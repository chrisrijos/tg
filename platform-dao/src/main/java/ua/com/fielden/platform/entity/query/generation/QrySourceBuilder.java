package ua.com.fielden.platform.entity.query.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ua.com.fielden.platform.entity.query.fluent.TokenCategory;
import ua.com.fielden.platform.entity.query.generation.elements.EntQuery;
import ua.com.fielden.platform.entity.query.generation.elements.TypeBasedSource;
import ua.com.fielden.platform.entity.query.generation.elements.QueryBasedSource;
import ua.com.fielden.platform.entity.query.model.QueryModel;
import ua.com.fielden.platform.utils.Pair;

public class QrySourceBuilder extends AbstractTokensBuilder {

    protected QrySourceBuilder(final AbstractTokensBuilder parent, final EntQueryGenerator queryBuilder, final Map<String, Object> paramValues) {
	super(parent, queryBuilder, paramValues);
    }

    private boolean isEntityTypeAsSourceTest() {
	return getSize() == 2 && TokenCategory.ENTITY_TYPE_AS_QRY_SOURCE.equals(firstCat()) && TokenCategory.QRY_SOURCE_ALIAS.equals(secondCat());
    }

    private boolean isEntityTypeAsSourceWithoutAliasTest() {
	return getSize() == 1 && TokenCategory.ENTITY_TYPE_AS_QRY_SOURCE.equals(firstCat());
    }

    private boolean isEntityModelAsSourceTest() {
	return getSize() == 2 && TokenCategory.QRY_MODELS_AS_QRY_SOURCE.equals(firstCat()) && TokenCategory.QRY_SOURCE_ALIAS.equals(secondCat());
    }

    private boolean isEntityModelAsSourceWithoutAliasTest() {
	return getSize() == 1 && TokenCategory.QRY_MODELS_AS_QRY_SOURCE.equals(firstCat());
    }

    @Override
    public boolean isClosing() {
	return false;
    }

    @Override
    public boolean canBeClosed() {
	return isEntityTypeAsSourceTest() || isEntityModelAsSourceTest() || isEntityModelAsSourceWithoutAliasTest() || isEntityTypeAsSourceWithoutAliasTest();
    }

    private Pair<TokenCategory, Object> getResultForEntityTypeAsSource() {
	return new Pair<TokenCategory, Object>(TokenCategory.QRY_SOURCE, new TypeBasedSource((Class) firstValue(), (String) secondValue(), getQueryBuilder().getDomainPersistenceMetadata()));
    }

    private Pair<TokenCategory, Object> getResultForEntityModelAsSource() {
	final List<QueryModel> models = (List<QueryModel>) firstValue();
	final List<EntQuery> queries = new ArrayList<EntQuery>();
	for (final QueryModel qryModel : models) {
	    queries.add(getQueryBuilder().generateEntQueryAsSourceQuery(qryModel, getParamValues()));
	}

	return new Pair<TokenCategory, Object>(TokenCategory.QRY_SOURCE, new QueryBasedSource((String) secondValue(), getQueryBuilder().getDomainPersistenceMetadata(), queries.toArray(new EntQuery[]{})));
    }

    @Override
    public Pair<TokenCategory, Object> getResult() {
	if (isEntityTypeAsSourceTest() || isEntityTypeAsSourceWithoutAliasTest()) {
	    return getResultForEntityTypeAsSource();
	} else if (isEntityModelAsSourceTest() || isEntityModelAsSourceWithoutAliasTest()) {
	    return getResultForEntityModelAsSource();
	} else {
	    throw new RuntimeException("Unable to get result - unrecognised state.");
	}
    }
}