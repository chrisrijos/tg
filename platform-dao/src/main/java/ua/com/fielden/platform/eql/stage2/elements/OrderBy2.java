package ua.com.fielden.platform.eql.stage2.elements;

import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;

public class OrderBy2 {
    private final ISingleOperand2 operand;
    private final String yieldName;
    private Yield2 yield;
    private final boolean desc;

    @Override
    public String toString() {
        return (yieldName == null ? operand : yieldName) + (desc ? " DESC" : " ASC");
    }

    public OrderBy2(final ISingleOperand2 operand, final boolean desc) {
        super();
        this.operand = operand;
        this.yieldName = null;
        this.desc = desc;
    }

    public OrderBy2(final String yieldName, final boolean desc) {
        super();
        this.operand = null;
        this.yieldName = yieldName;
        this.desc = desc;
    }

    public ISingleOperand2 getOperand() {
        return operand;
    }

    public String getYieldName() {
        return yieldName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (desc ? 1231 : 1237);
        result = prime * result + ((operand == null) ? 0 : operand.hashCode());
        result = prime * result + ((yieldName == null) ? 0 : yieldName.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OrderBy2)) {
            return false;
        }
        final OrderBy2 other = (OrderBy2) obj;
        if (desc != other.desc) {
            return false;
        }
        if (operand == null) {
            if (other.operand != null) {
                return false;
            }
        } else if (!operand.equals(other.operand)) {
            return false;
        }
        if (yieldName == null) {
            if (other.yieldName != null) {
                return false;
            }
        } else if (!yieldName.equals(other.yieldName)) {
            return false;
        }
        return true;
    }

    public Yield2 getYield() {
        return yield;
    }

    public void setYield(final Yield2 yield) {
        this.yield = yield;
    }

    public boolean isDesc() {
        return desc;
    }
}