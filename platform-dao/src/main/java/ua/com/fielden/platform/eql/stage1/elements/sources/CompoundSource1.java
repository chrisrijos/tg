package ua.com.fielden.platform.eql.stage1.elements.sources;

import java.util.Objects;

import ua.com.fielden.platform.entity.query.fluent.enums.JoinType;
import ua.com.fielden.platform.eql.stage1.elements.ITransformableToS2;
import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.conditions.Conditions1;
import ua.com.fielden.platform.eql.stage2.elements.conditions.Conditions2;
import ua.com.fielden.platform.eql.stage2.elements.sources.CompoundSource2;
import ua.com.fielden.platform.eql.stage2.elements.sources.IQrySource2;

public class CompoundSource1 implements ITransformableToS2<CompoundSource2>{
    public final IQrySource1<? extends IQrySource2<?>> source;
    public final JoinType joinType;
    public final Conditions1 joinConditions;

    public CompoundSource1(final IQrySource1<? extends IQrySource2<?>> source, final JoinType joinType, final Conditions1 joinConditions) {
        this.source = source;
        this.joinType = joinType;
        this.joinConditions = joinConditions;
    }

    @Override
    public CompoundSource2 transform(final PropsResolutionContext context) {
        final IQrySource2<?> source2 = source.transform(context);
        final Conditions2 joinConditions2 = joinConditions.transform(context.cloneWithAdded(source2));
        return new CompoundSource2(source2, joinType, joinConditions2);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((joinConditions == null) ? 0 : joinConditions.hashCode());
        result = prime * result + ((joinType == null) ? 0 : joinType.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CompoundSource1)) {
            return false;
        }
        
        final CompoundSource1 other = (CompoundSource1) obj;

        return Objects.equals(joinConditions, other.joinConditions) && Objects.equals(joinType, other.joinType) && Objects.equals(source, other.source);
    }
}