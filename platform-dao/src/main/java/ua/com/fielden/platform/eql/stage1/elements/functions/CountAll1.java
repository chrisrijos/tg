package ua.com.fielden.platform.eql.stage1.elements.functions;

import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage2.elements.functions.CountAll2;

public class CountAll1 extends AbstractFunction1<CountAll2> {

    @Override
    public TransformationResult<CountAll2> transform(final PropsResolutionContext context) {
        return new TransformationResult<CountAll2>(new CountAll2(), context);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = 1;
        return prime * result + CountAll1.class.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof CountAll1;
    } 
}