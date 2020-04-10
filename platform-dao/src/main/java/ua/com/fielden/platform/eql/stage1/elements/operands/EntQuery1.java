package ua.com.fielden.platform.eql.stage1.elements.operands;

import static ua.com.fielden.platform.eql.meta.QueryCategory.SOURCE_QUERY;
import static ua.com.fielden.platform.eql.meta.QueryCategory.SUB_QUERY;

import java.util.Objects;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.eql.meta.QueryCategory;
import ua.com.fielden.platform.eql.stage1.elements.EntQueryBlocks1;
import ua.com.fielden.platform.eql.stage1.elements.GroupBys1;
import ua.com.fielden.platform.eql.stage1.elements.OrderBys1;
import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage1.elements.Yields1;
import ua.com.fielden.platform.eql.stage1.elements.conditions.Conditions1;
import ua.com.fielden.platform.eql.stage1.elements.sources.Sources1;
import ua.com.fielden.platform.eql.stage2.elements.EntQueryBlocks2;
import ua.com.fielden.platform.eql.stage2.elements.GroupBys2;
import ua.com.fielden.platform.eql.stage2.elements.OrderBys2;
import ua.com.fielden.platform.eql.stage2.elements.Yields2;
import ua.com.fielden.platform.eql.stage2.elements.conditions.Conditions2;
import ua.com.fielden.platform.eql.stage2.elements.operands.EntQuery2;
import ua.com.fielden.platform.eql.stage2.elements.sources.Sources2;

public class EntQuery1 implements ISingleOperand1<EntQuery2> {

    public final Sources1 sources;
    public final Conditions1 conditions;
    public final Yields1 yields;
    public final GroupBys1 groups;
    public final OrderBys1 orderings;

    public final Class<? extends AbstractEntity<?>> resultType;
    public final QueryCategory category;

    public EntQuery1(final EntQueryBlocks1 queryBlocks, final Class<? extends AbstractEntity<?>> resultType, final QueryCategory category) {
       this.category = category;
       this.sources = queryBlocks.sources;
       this.conditions = queryBlocks.conditions;
       this.yields = queryBlocks.yields;
       this.groups = queryBlocks.groups;
       this.orderings = queryBlocks.orderings;
       this.resultType = resultType;
       
       if (this.resultType == null && category != SUB_QUERY) { // only primitive result queries have result type not assigned
           throw new IllegalStateException("This query is not subquery, thus its result type shouldn't be null!");
       }
   }
    
    public boolean isSubQuery() {
        return category == SUB_QUERY;
    }
    
    public boolean isSourceQuery() {
        return category == SOURCE_QUERY;
    }

    public Class<? extends AbstractEntity<?>> type() {
        return resultType;
    }

    @Override
    public TransformationResult<EntQuery2> transform(final PropsResolutionContext context) {
        final PropsResolutionContext localResolutionContext = context.produceForCorrelatedSubquery();//isSubQuery() ? context.produceForCorrelatedSubquery() : context.produceForUncorrelatedSubquery();
        // .produceForUncorrelatedSubquery() should be used only for cases of synthetic entities (where source query can only be uncorrelated) -- simple queries as source queries are accessible for correlation
        final TransformationResult<Sources2> sourcesTr =  sources != null ? sources.transform(localResolutionContext) : null;
        final TransformationResult<Conditions2> conditionsTr =  conditions.transform(sourcesTr != null ? sourcesTr.updatedContext : localResolutionContext);
        final TransformationResult<Yields2> yieldsTr =  yields.transform(conditionsTr.updatedContext);
        final TransformationResult<GroupBys2> groupsTr =  groups.transform(yieldsTr.updatedContext);
        final TransformationResult<OrderBys2> orderingsTr =  orderings.transform(groupsTr.updatedContext);

        final EntQueryBlocks2 entQueryBlocks = new EntQueryBlocks2(sourcesTr != null ? sourcesTr.item : null, conditionsTr.item, yieldsTr.item, groupsTr.item, orderingsTr.item);

        final PropsResolutionContext resultResolutionContext = (isSubQuery() || isSourceQuery()) ? 
                new PropsResolutionContext(orderingsTr.updatedContext.getDomainInfo(), orderingsTr.updatedContext.getSources().subList(1, orderingsTr.updatedContext.getSources().size()), orderingsTr.updatedContext.sourceId) :
                    orderingsTr.updatedContext;
               
        return new TransformationResult<EntQuery2>(new EntQuery2(entQueryBlocks, type(), category), resultResolutionContext);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + category.hashCode();
        result = prime * result + conditions.hashCode();
        result = prime * result + groups.hashCode();
        result = prime * result + orderings.hashCode();
        result = prime * result + ((resultType == null) ? 0 : resultType.hashCode());
        result = prime * result + ((sources == null) ? 0 : sources.hashCode());
        result = prime * result + yields.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof EntQuery1)) {
            return false;
        }
        
        final EntQuery1 other = (EntQuery1) obj;
        
        return Objects.equals(category, other.category) &&
                Objects.equals(resultType, other.resultType) &&
                Objects.equals(sources, other.sources) &&
                Objects.equals(yields, other.yields) &&
                Objects.equals(conditions, other.conditions) &&
                Objects.equals(groups, other.groups) &&
                Objects.equals(orderings, other.orderings);
   }
}