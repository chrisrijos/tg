package ua.com.fielden.platform.eql.stage2.elements.operands;

import static java.util.Collections.emptySet;
import static ua.com.fielden.platform.utils.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.eql.meta.AbstractPropInfo;
import ua.com.fielden.platform.eql.meta.QueryCategory;
import ua.com.fielden.platform.eql.stage2.elements.EntQueryBlocks2;
import ua.com.fielden.platform.eql.stage2.elements.GroupBys2;
import ua.com.fielden.platform.eql.stage2.elements.OrderBys2;
import ua.com.fielden.platform.eql.stage2.elements.TransformationContext;
import ua.com.fielden.platform.eql.stage2.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage2.elements.Yield2;
import ua.com.fielden.platform.eql.stage2.elements.Yields2;
import ua.com.fielden.platform.eql.stage2.elements.conditions.Conditions2;
import ua.com.fielden.platform.eql.stage2.elements.sources.IQrySource2;
import ua.com.fielden.platform.eql.stage2.elements.sources.Sources2;
import ua.com.fielden.platform.eql.stage3.elements.EntQueryBlocks3;
import ua.com.fielden.platform.eql.stage3.elements.GroupBys3;
import ua.com.fielden.platform.eql.stage3.elements.OrderBys3;
import ua.com.fielden.platform.eql.stage3.elements.Yields3;
import ua.com.fielden.platform.eql.stage3.elements.conditions.Conditions3;
import ua.com.fielden.platform.eql.stage3.elements.operands.EntQuery3;
import ua.com.fielden.platform.eql.stage3.elements.sources.IQrySource3;
import ua.com.fielden.platform.eql.stage3.elements.sources.IQrySources3;

public class EntQuery2 implements ISingleOperand2<EntQuery3> {

    public final Sources2 sources;
    public final Conditions2 conditions;
    public final Yields2 yields;
    public final GroupBys2 groups;
    public final OrderBys2 orderings;
    public final Class<? extends AbstractEntity<?>> resultType;
    public final QueryCategory category;

    public EntQuery2(final EntQueryBlocks2 queryBlocks, final Class<? extends AbstractEntity<?>> resultType, final QueryCategory category) {
        this.category = category;
        this.sources = queryBlocks.sources;
        this.conditions = queryBlocks.conditions;
        this.yields = enhancedYields(queryBlocks.yields, queryBlocks.sources.main);
        this.groups = queryBlocks.groups;
        this.orderings = queryBlocks.orderings;
        this.resultType = resultType;
    }

    private Yields2 enhancedYields(final Yields2 yields, final IQrySource2<? extends IQrySource3> mainSource) {
        if (yields.getYields().isEmpty()) {
            final List<Yield2> enhancedYields = new ArrayList<>();
            for (final Entry<String, AbstractPropInfo<?>> el : mainSource.entityInfo().getProps().entrySet()) {
                if (el.getValue().expression == null) {
                    //final String yieldedPropAliasedName = sourcesTr.item.main.alias() != null ?  sourcesTr.item.main.alias() + "." + el.getKey() : el.getKey();
                    enhancedYields.add(new Yield2(new EntProp2(mainSource, "0", listOf(el.getValue())), el.getKey(), false));
                }
            }
            return new Yields2(enhancedYields);
        }
        return yields;
    }
    
    @Override
    public TransformationResult<EntQuery3> transform(final TransformationContext context) {


        
        final TransformationResult<IQrySources3> sourcesTr = sources != null ? sources.transform(context) : null;
        final TransformationResult<Conditions3> conditionsTr = conditions.transform(sourcesTr != null ? sourcesTr.updatedContext : context);

        
        final TransformationResult<Yields3> yieldsTr = yields.transform(conditionsTr.updatedContext);
        final TransformationResult<GroupBys3> groupsTr = groups.transform(yieldsTr.updatedContext);
        final TransformationResult<OrderBys3> orderingsTr = orderings.transform(groupsTr.updatedContext);

        final EntQueryBlocks3 entQueryBlocks = new EntQueryBlocks3(sourcesTr != null ? sourcesTr.item : null, conditionsTr.item, yieldsTr.item, groupsTr.item, orderingsTr.item);

        return new TransformationResult<EntQuery3>(new EntQuery3(entQueryBlocks, category), orderingsTr.updatedContext);
    }

    @Override
    public Set<EntProp2> collectProps() {
        final Set<EntProp2> result = new HashSet<>();
        result.addAll(sources != null ? sources.collectProps() : emptySet());
        result.addAll(conditions.collectProps());
        result.addAll(yields.collectProps());
        result.addAll(groups.collectProps());
        result.addAll(orderings.collectProps());
        
        return result;
    }

    @Override
    public Class<? extends AbstractEntity<?>> type() {
        return resultType;
    }

    @Override
    public boolean ignore() {
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + conditions.hashCode();
        result = prime * result + groups.hashCode();
        result = prime * result + category.hashCode();
        result = prime * result + ((resultType == null) ? 0 : resultType.hashCode());
        result = prime * result + ((sources == null) ? 0 : sources.hashCode());
        result = prime * result + yields.hashCode();
        result = prime * result + orderings.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof EntQuery2)) {
            return false;
        }

        final EntQuery2 other = (EntQuery2) obj;

        return Objects.equals(category, other.category) &&
                Objects.equals(resultType, other.resultType) &&
                Objects.equals(sources, other.sources) &&
                Objects.equals(yields, other.yields) &&
                Objects.equals(conditions, other.conditions) &&
                Objects.equals(groups, other.groups) &&
                Objects.equals(orderings, other.orderings);
    }
}