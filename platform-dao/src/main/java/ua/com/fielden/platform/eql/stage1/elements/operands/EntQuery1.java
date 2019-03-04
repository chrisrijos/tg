package ua.com.fielden.platform.eql.stage1.elements.operands;

import static ua.com.fielden.platform.eql.meta.QueryCategory.SOURCE_QUERY;
import static ua.com.fielden.platform.eql.meta.QueryCategory.SUB_QUERY;

import com.google.common.base.Objects;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.IRetrievalModel;
import ua.com.fielden.platform.eql.meta.PropsResolutionContext;
import ua.com.fielden.platform.eql.meta.QueryCategory;
import ua.com.fielden.platform.eql.meta.TransformationResult;
import ua.com.fielden.platform.eql.stage1.builders.EntQueryBlocks;
import ua.com.fielden.platform.eql.stage1.elements.AbstractElement1;
import ua.com.fielden.platform.eql.stage1.elements.GroupBys1;
import ua.com.fielden.platform.eql.stage1.elements.OrderBys1;
import ua.com.fielden.platform.eql.stage1.elements.Yields1;
import ua.com.fielden.platform.eql.stage1.elements.conditions.Conditions1;
import ua.com.fielden.platform.eql.stage1.elements.sources.Sources1;
import ua.com.fielden.platform.eql.stage2.elements.Conditions2;
import ua.com.fielden.platform.eql.stage2.elements.EntQuery2;
import ua.com.fielden.platform.eql.stage2.elements.EntQueryBlocks2;
import ua.com.fielden.platform.eql.stage2.elements.GroupBys2;
import ua.com.fielden.platform.eql.stage2.elements.OrderBys2;
import ua.com.fielden.platform.eql.stage2.elements.Sources2;
import ua.com.fielden.platform.eql.stage2.elements.Yields2;

public class EntQuery1 extends AbstractElement1 implements ISingleOperand1<EntQuery2> {

    public final Sources1 sources;
    public final Conditions1 conditions;
    public final Yields1 yields;
    public final GroupBys1 groups;
    public final OrderBys1 orderings;

    public final Class<? extends AbstractEntity<?>> resultType;
    public final QueryCategory category;

    public final boolean filterable;
    public final IRetrievalModel fetchModel;

    public EntQuery1(final EntQueryBlocks queryBlocks, final Class resultType, final QueryCategory category, //
            final boolean filterable, final IRetrievalModel fetchModel, final int contextId) {
       super(contextId);
       this.filterable = filterable;
       this.category = category;
       this.sources = queryBlocks.getSources();
       this.conditions = queryBlocks.getConditions();
       this.yields = queryBlocks.getYields();
       this.groups = queryBlocks.getGroups();
       this.orderings = queryBlocks.getOrderings();
       this.fetchModel = fetchModel;
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
    public TransformationResult<EntQuery2> transform(final PropsResolutionContext resolutionContext) {
        final PropsResolutionContext localResolutionContext = isSubQuery() ? resolutionContext.produceForCorrelatedSubquery() : resolutionContext.produceForUncorrelatedSubquery();
        // .produceForUncorrelatedSubquery() should be used only for cases of synthetic entities (where source query can only be uncorrelated) -- simple queries as source queries are accessible for correlation
        final TransformationResult<Sources2> sourcesTransformationResult =  sources.transform(localResolutionContext);
        final TransformationResult<Conditions2> conditionsTransformationResult =  conditions.transform(sourcesTransformationResult.getUpdatedContext());
        final TransformationResult<Yields2> yieldsTransformationResult =  yields.transform(conditionsTransformationResult.getUpdatedContext());
        final TransformationResult<GroupBys2> groupsTransformationResult =  groups.transform(yieldsTransformationResult.getUpdatedContext());
        final TransformationResult<OrderBys2> orderingsTransformationResult =  orderings.transform(groupsTransformationResult.getUpdatedContext());

        final EntQueryBlocks2 entQueryBlocks = new EntQueryBlocks2(
                sourcesTransformationResult.getItem(), 
                conditionsTransformationResult.getItem(), 
                yieldsTransformationResult.getItem(), 
                groupsTransformationResult.getItem(), 
                orderingsTransformationResult.getItem());

        final PropsResolutionContext resultResolutionContext = (isSubQuery() || isSourceQuery()) ? 
                new PropsResolutionContext(resolutionContext.getDomainInfo(), resolutionContext.getSources(), orderingsTransformationResult.getUpdatedContext().getResolvedProps()) :
                    orderingsTransformationResult.getUpdatedContext();
               
        return new TransformationResult<EntQuery2>(new EntQuery2(entQueryBlocks, type(), category, fetchModel), resultResolutionContext);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((conditions == null) ? 0 : conditions.hashCode());
        result = prime * result + ((groups == null) ? 0 : groups.hashCode());
        result = prime * result + ((orderings == null) ? 0 : orderings.hashCode());
        result = prime * result + ((resultType == null) ? 0 : resultType.hashCode());
        result = prime * result + ((sources == null) ? 0 : sources.hashCode());
        result = prime * result + ((yields == null) ? 0 : yields.hashCode());
        result = prime * result + ((fetchModel == null) ? 0 : fetchModel.hashCode());
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
        
        return Objects.equal(category, other.category) &&
                Objects.equal(resultType, other.resultType) &&
                Objects.equal(sources, other.sources) &&
                Objects.equal(yields, other.yields) &&
                Objects.equal(conditions, other.conditions) &&
                Objects.equal(groups, other.groups) &&
                Objects.equal(orderings, other.orderings) &&
                Objects.equal(fetchModel, other.fetchModel);
   }
}