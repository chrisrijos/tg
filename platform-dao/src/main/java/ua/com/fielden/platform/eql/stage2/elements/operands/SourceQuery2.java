package ua.com.fielden.platform.eql.stage2.elements.operands;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.eql.stage2.elements.EntQueryBlocks2;
import ua.com.fielden.platform.eql.stage2.elements.ITransformableToS3;
import ua.com.fielden.platform.eql.stage2.elements.TransformationContext;
import ua.com.fielden.platform.eql.stage2.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage3.elements.EntQueryBlocks3;
import ua.com.fielden.platform.eql.stage3.elements.GroupBys3;
import ua.com.fielden.platform.eql.stage3.elements.OrderBys3;
import ua.com.fielden.platform.eql.stage3.elements.Yields3;
import ua.com.fielden.platform.eql.stage3.elements.conditions.Conditions3;
import ua.com.fielden.platform.eql.stage3.elements.operands.SourceQuery3;
import ua.com.fielden.platform.eql.stage3.elements.sources.IQrySources3;

public class SourceQuery2 extends AbstractQuery2 implements ITransformableToS3<SourceQuery3> {

    public SourceQuery2(final EntQueryBlocks2 queryBlocks, final Class<? extends AbstractEntity<?>> resultType) {
        super(queryBlocks, resultType);
    }

    @Override
    public TransformationResult<SourceQuery3> transform(final TransformationContext context) {
        final TransformationResult<IQrySources3> sourcesTr = sources.transform(context);
        final TransformationResult<Conditions3> conditionsTr = conditions.transform(sourcesTr.updatedContext);
        final TransformationResult<Yields3> yieldsTr = yields.transform(conditionsTr.updatedContext);
        final TransformationResult<GroupBys3> groupsTr = groups.transform(yieldsTr.updatedContext);
        final TransformationResult<OrderBys3> orderingsTr = orderings.transform(groupsTr.updatedContext);

        final EntQueryBlocks3 entQueryBlocks = new EntQueryBlocks3(sourcesTr.item, conditionsTr.item, yieldsTr.item, groupsTr.item, orderingsTr.item);

        return new TransformationResult<SourceQuery3>(new SourceQuery3(entQueryBlocks, resultType), orderingsTr.updatedContext);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = super.hashCode();
        return prime * result + SourceQuery2.class.getName().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj || super.equals(obj) && obj instanceof SourceQuery2;
    }
}