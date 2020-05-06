package ua.com.fielden.platform.eql.stage1.elements.functions;

import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.operands.ISingleOperand1;
import ua.com.fielden.platform.eql.stage2.elements.functions.IfNull2;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;
import ua.com.fielden.platform.eql.stage3.elements.operands.ISingleOperand3;

public class IfNull1 extends TwoOperandsFunction1<IfNull2> {

    public IfNull1(final ISingleOperand1<? extends ISingleOperand2<? extends ISingleOperand3>> operand1, final ISingleOperand1<? extends ISingleOperand2<? extends ISingleOperand3>> operand2) {
        super(operand1, operand2);
    }

    @Override
    public IfNull2 transform(final PropsResolutionContext context) {
        return new IfNull2(operand1.transform(context), operand2.transform(context));
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = super.hashCode();
        return prime * result + IfNull1.class.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || super.equals(obj) && obj instanceof IfNull1;
    } 
}