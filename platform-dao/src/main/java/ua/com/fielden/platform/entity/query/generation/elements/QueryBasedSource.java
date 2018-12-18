package ua.com.fielden.platform.entity.query.generation.elements;

import static java.lang.String.format;
import static java.lang.String.join;
import static ua.com.fielden.platform.utils.Pair.pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ua.com.fielden.platform.entity.query.DbVersion;
import ua.com.fielden.platform.entity.query.metadata.DomainMetadataAnalyser;
import ua.com.fielden.platform.entity.query.metadata.PropertyMetadata;
import ua.com.fielden.platform.utils.Pair;

public class QueryBasedSource extends AbstractSource {
    private final List<EntQuery> models;
    private final Map<String, List<Yield>> yieldsMatrix = new HashMap<>();

    private EntQuery firstModel() {
        return models.get(0);
    }

    public QueryBasedSource(final String alias, final DomainMetadataAnalyser domainMetadataAnalyser, final EntQuery... models) {
        super(alias, domainMetadataAnalyser);
        if (models == null || models.length == 0) {
            throw new IllegalArgumentException("Couldn't produce instance of QueryBasedSource due to zero models passed to constructor!");
        }
        this.models = Arrays.asList(models);
        populateYieldMatrixFromQueryModels(models);
        validateYieldsMatrix();
    }

    private void populateYieldMatrixFromQueryModels(final EntQuery... models) {
        for (final EntQuery entQuery : models) {
            for (final Yield yield : entQuery.getYields().getYields()) {
                final List<Yield> foundYields = yieldsMatrix.get(yield.getAlias());
                if (foundYields != null) {
                    foundYields.add(yield);
                } else {
                    final List<Yield> newList = new ArrayList<>();
                    newList.add(yield);
                    yieldsMatrix.put(yield.getAlias(), newList);
                }
            }
        }
    }
    
    private Yield getYield(final String yieldAlias) {
        final List<Yield> yields = yieldsMatrix.get(yieldAlias);
        
        if (yields != null) {
            if (yields.size() == 1) {
                return yields.get(0);
            }
            
            for (final Yield yield : yieldsMatrix.get(yieldAlias)) {
                if (yield.getInfo().getJavaType() != null) {
                    return yield;
                }
            }
            
            return yields.get(0);
        }
        return null;
    }
    
    private boolean getYieldNullability(final String yieldAlias) {
        final boolean result = false;
        for (final Yield yield : yieldsMatrix.get(yieldAlias)) {
            if (yield.getInfo().isNullable()) {
                return true;
            }
        }
        return result;
    }

    private void validateYieldsMatrix() {
        for (final Map.Entry<String, List<Yield>> entry : yieldsMatrix.entrySet()) {
            if (entry.getValue().size() != models.size()) {
                throw new IllegalStateException("Incorrect models used as query source - their result types are different!");
            }
        }
    }

    @Override
    public void populateSourceItems(final boolean parentLeftJoinLegacy) {
        for (final Yield yield : firstModel().getYields().getYields()) {
            final Yield properYield = getYield(yield.getAlias());
            sourceItems.put(yield.getAlias(), new ResultQueryYieldDetails(yield.getInfo().getName(), properYield.getInfo().getJavaType(), properYield.getInfo().getHibType(), yield.getInfo().getColumn(), //
            getYieldNullability(yield.getInfo().getName()) || parentLeftJoinLegacy, yield.getInfo().getYieldDetailsType()));
        }
    }

    @Override
    public Class sourceType() {
        return firstModel().type();
    }

    @Override
    public boolean generated() {
        return false;
    }

    @Override
    protected Pair<PurePropInfo, PurePropInfo> lookForProp(final String dotNotatedPropName) {
        for (final Pair<String, String> candidate : prepareCandidates(dotNotatedPropName)) {
            final Pair<PurePropInfo, PurePropInfo> candidateResult = validateCandidate(dotNotatedPropName, candidate.getKey(), candidate.getValue());
            if (candidateResult != null) {
                return candidateResult;
            }
        }

        return null;
    }

    private Pair<PurePropInfo, PurePropInfo> validateCandidate(final String dotNotatedPropName, final String first, final String rest) {
        final Yield firstLevelPropYield = getYield(first);

        if (firstLevelPropYield == null || firstLevelPropYield.isCompositePropertyHeader()) { // there are no such first level prop at all within source query yields
            final PropertyMetadata explicitPropMetadata = getDomainMetadataAnalyser().getInfoForDotNotatedProp(sourceType(), first);

            if (explicitPropMetadata == null) {
                return null;
            } else {
                if (explicitPropMetadata.isCalculated()) {
                    if (explicitPropMetadata.getJavaType() == null) {
                        return StringUtils.isEmpty(rest) ? pair(new PurePropInfo(first, null, null, true), new PurePropInfo(first, null, null, true))
                                : null;
                    } else if (!StringUtils.isEmpty(rest)) {
                        final PropertyMetadata propInfo = getDomainMetadataAnalyser().getInfoForDotNotatedProp(explicitPropMetadata.getJavaType(), rest);
                        if (propInfo == null) {
                            return null;
                        } else {
                            final boolean propNullability = getDomainMetadataAnalyser().isNullable(explicitPropMetadata.getJavaType(), rest);
                            final boolean explicitPartNullability = explicitPropMetadata.isNullable() || isNullable();
                            return pair(new PurePropInfo(first, explicitPropMetadata.getJavaType(), explicitPropMetadata.getHibType(), explicitPartNullability), 
                                        new PurePropInfo(dotNotatedPropName, propInfo.getJavaType(), propInfo.getHibType(), propNullability || explicitPartNullability));
                        }
                    } else {

                        final PurePropInfo ppi = new PurePropInfo(first, explicitPropMetadata.getJavaType(), explicitPropMetadata.getHibType(), explicitPropMetadata.isNullable()
                                || isNullable());
                        ppi.setExpressionModel(explicitPropMetadata.getExpressionModel());
                        return pair(ppi, ppi);
                    }
                    //		    throw new RuntimeException("Implementation pending! Additional info: " + dotNotatedPropName + " " + explicitPropMetadata);
                } else if (explicitPropMetadata.isCompositeProperty()) {
                    final String singleSubProp = explicitPropMetadata.getSinglePropertyOfCompositeUserType();
                    if (singleSubProp != null) {
                        return validateCandidate(dotNotatedPropName, first + "." + singleSubProp, rest);
                    }
                    return null;
                } else {
                    return null;
                }
            }
        } else if (firstLevelPropYield.getInfo().getJavaType() == null) { //such property is present, but its type is definitely not entity, that's why it can't have subproperties
            return StringUtils.isEmpty(rest) ? pair(new PurePropInfo(first, null, null, true), new PurePropInfo(first, null, null, true)) : null;
        } else if (!StringUtils.isEmpty(rest)) {
            final PropertyMetadata propInfo = getDomainMetadataAnalyser().getInfoForDotNotatedProp(firstLevelPropYield.getInfo().getJavaType(), rest);
            if (propInfo == null) {
                return null;
            } else {
                final boolean propNullability = getDomainMetadataAnalyser().isNullable(firstLevelPropYield.getInfo().getJavaType(), rest);
                final boolean explicitPartNullability = getYieldNullability(firstLevelPropYield.getAlias())/*firstLevelPropYield.getInfo().isNullable()*/|| isNullable();
                return pair(new PurePropInfo(first, firstLevelPropYield.getInfo().getJavaType(), firstLevelPropYield.getInfo().getHibType(), explicitPartNullability),
                            new PurePropInfo(dotNotatedPropName, propInfo.getJavaType(), propInfo.getHibType(), propNullability || explicitPartNullability));
            }
        } else {
            final PurePropInfo ppi = new PurePropInfo(first, firstLevelPropYield.getInfo().getJavaType(), firstLevelPropYield.getInfo().getHibType(), getYieldNullability(firstLevelPropYield.getAlias())/*firstLevelPropYield.getInfo().isNullable()*/
                    || isNullable());
            return pair(ppi, ppi);
        }
    }

    private static List<Pair<String, String>> prepareCandidates(final String dotNotatedPropName) {
        final List<Pair<String, String>> result = new ArrayList<>();
        final List<String> parts = Arrays.asList(dotNotatedPropName.split("\\."));

        for (int i = parts.size(); i >= 1; i--) {
            result.add(pair(join(".", parts.subList(0, i)), join(".", parts.subList(i, parts.size()))));
        }

        return result;
    }

    @Override
    public List<EntValue> getValues() {
        final List<EntValue> result = new ArrayList<>();
        for (final EntQuery entQry : models) {
            result.addAll(entQry.getAllValues());
        }
        return result;
    }

    @Override
    public String sql() {
        final StringBuilder sb = new StringBuilder().append("(");
        for (final Iterator<EntQuery> iterator = models.iterator(); iterator.hasNext();) {
            sb.append(iterator.next().sql());
            sb.append(iterator.hasNext() ? "\nUNION ALL\n" : "");
        }
        // AS alias is not applicable for Oracle
        sb.append(format(")%s%s ", dbVersion.AS, sqlAlias)); // /*%s*/  , alias        
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((models == null) ? 0 : models.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof QueryBasedSource)) {
            return false;
        }
        final QueryBasedSource other = (QueryBasedSource) obj;
        if (models == null) {
            if (other.models != null) {
                return false;
            }
        } else if (!models.equals(other.models)) {
            return false;
        }
        return true;
    }
}