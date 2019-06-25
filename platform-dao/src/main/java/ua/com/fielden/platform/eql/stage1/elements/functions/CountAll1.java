package ua.com.fielden.platform.eql.stage1.elements.functions;

import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage2.elements.functions.CountAll2;

public class CountAll1 extends ZeroOperandFunction1<CountAll2> {

    public CountAll1() {
        super("COUNT(*)");
    }

    @Override
    public TransformationResult<CountAll2> transform(final PropsResolutionContext resolutionContext) {
        return new TransformationResult<CountAll2>(new CountAll2(), resolutionContext.cloneNew());
    }
}