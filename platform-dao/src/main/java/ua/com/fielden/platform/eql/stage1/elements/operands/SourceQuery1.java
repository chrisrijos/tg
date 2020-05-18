package ua.com.fielden.platform.eql.stage1.elements.operands;

import static ua.com.fielden.platform.utils.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.eql.meta.AbstractPropInfo;
import ua.com.fielden.platform.eql.stage1.elements.EntQueryBlocks1;
import ua.com.fielden.platform.eql.stage1.elements.ITransformableToS2;
import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage2.elements.EntQueryBlocks2;
import ua.com.fielden.platform.eql.stage2.elements.GroupBys2;
import ua.com.fielden.platform.eql.stage2.elements.OrderBys2;
import ua.com.fielden.platform.eql.stage2.elements.Yield2;
import ua.com.fielden.platform.eql.stage2.elements.Yields2;
import ua.com.fielden.platform.eql.stage2.elements.conditions.Conditions2;
import ua.com.fielden.platform.eql.stage2.elements.operands.EntProp2;
import ua.com.fielden.platform.eql.stage2.elements.operands.SourceQuery2;
import ua.com.fielden.platform.eql.stage2.elements.sources.Sources2;
import ua.com.fielden.platform.types.tuples.T2;

public class SourceQuery1 extends AbstractQuery1 implements ITransformableToS2<SourceQuery2> {

    public final boolean isCorrelated;
    
    public SourceQuery1(final EntQueryBlocks1 queryBlocks, final Class<? extends AbstractEntity<?>> resultType, final boolean isCorrelated) {
        super(queryBlocks, resultType);
        this.isCorrelated = isCorrelated;
        assert (resultType != null);
    }

    @Override
    public SourceQuery2 transform(final PropsResolutionContext context) {
        final PropsResolutionContext localResolutionContext = isCorrelated ? context.produceForCorrelatedSubquery() : context.produceForUncorrelatedSubquery();
        // .produceForUncorrelatedSubquery() should be used only for cases of synthetic entities (where source query can only be uncorrelated) -- simple queries as source queries are accessible for correlation
        final T2<Sources2,PropsResolutionContext> sourcesTr = sources.transform(localResolutionContext);
        final PropsResolutionContext enhancedContext = sourcesTr._2; 
        final Sources2 sources2 = sourcesTr._1;
        final Conditions2 conditions2 = conditions.transform(enhancedContext);
        final Yields2 yields2 = yields.transform(enhancedContext);
        final GroupBys2 groups2 = groups.transform(enhancedContext);
        final OrderBys2 orderings2 = orderings.transform(enhancedContext);
        final Yields2 enhancedYields2 = enhanceYields(yields2, sources2);
        final EntQueryBlocks2 entQueryBlocks = new EntQueryBlocks2(sources2, conditions2, enhancedYields2, groups2, orderings2);

        return new SourceQuery2(entQueryBlocks, resultType);
    }

    private Yields2 enhanceYields(final Yields2 yields, final Sources2 sources2) {
        if (yields.getYields().isEmpty() || yieldAll) {
            final List<Yield2> enhancedYields = new ArrayList<>(yields.getYields());
            for (final Entry<String, AbstractPropInfo<?>> el : sources2.main.entityInfo().getProps().entrySet()) {
                if (!el.getValue().hasExpression()) {
                    enhancedYields.add(new Yield2(new EntProp2(sources2.main, listOf(el.getValue())), el.getKey(), false));
                }
            }
            return new Yields2(enhancedYields);
        }
        return yields;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (isCorrelated ? 1231 : 1237);
        return prime * result + SourceQuery1.class.getName().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj || super.equals(obj) && obj instanceof SourceQuery1 && ((SourceQuery1) obj).isCorrelated == isCorrelated;
    }
}