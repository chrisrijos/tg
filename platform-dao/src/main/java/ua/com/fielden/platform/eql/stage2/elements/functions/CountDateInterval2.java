package ua.com.fielden.platform.eql.stage2.elements.functions;

import java.util.Objects;

import org.hibernate.type.IntegerType;

import ua.com.fielden.platform.entity.query.fluent.enums.DateIntervalUnit;
import ua.com.fielden.platform.eql.stage2.elements.TransformationContext;
import ua.com.fielden.platform.eql.stage2.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;
import ua.com.fielden.platform.eql.stage3.elements.functions.CountDateInterval3;
import ua.com.fielden.platform.eql.stage3.elements.operands.ISingleOperand3;

public class CountDateInterval2 extends TwoOperandsFunction2<CountDateInterval3> {

    private DateIntervalUnit intervalUnit;

    public CountDateInterval2(final DateIntervalUnit intervalUnit, final ISingleOperand2<? extends ISingleOperand3> periodEndDate, final ISingleOperand2<? extends ISingleOperand3> periodStartDate) {
        super(periodEndDate, periodStartDate);
        this.intervalUnit = intervalUnit;
    }

    @Override
    public Class<Integer> type() {
        return Integer.class; 
    }

    @Override
    public Object hibType() {
        return IntegerType.INSTANCE;
    }

    @Override
    public TransformationResult<CountDateInterval3> transform(final TransformationContext context) {
        final TransformationResult<? extends ISingleOperand3> firstOperandTr = operand1.transform(context);
        final TransformationResult<? extends ISingleOperand3> secondOperandTr = operand2.transform(firstOperandTr.updatedContext);
        return new TransformationResult<CountDateInterval3>(new CountDateInterval3(intervalUnit, firstOperandTr.item, secondOperandTr.item), secondOperandTr.updatedContext);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = super.hashCode();
        return prime * result + intervalUnit.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (!super.equals(obj)) {
            return false;
        }
        
        if (!(obj instanceof CountDateInterval2)) {
            return false;
        }
        
        final CountDateInterval2 other = (CountDateInterval2) obj;
        
        return Objects.equals(intervalUnit, other.intervalUnit);
    }
}