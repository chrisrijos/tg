package ua.com.fielden.platform.eql.s2.elements;

public class EntValue2 implements ISingleOperand2 {
    private final Object value;
    private final boolean ignoreNull;

    public EntValue2(final Object value) {
        this(value, false);
    }

    public EntValue2(final Object value, final boolean ignoreNull) {
        super();
        this.value = value;
        this.ignoreNull = ignoreNull;
        if (!ignoreNull && value == null) {
            // TODO Uncomment when yieldNull() operator is implemented and all occurences of yield().val(null) are corrected.
            //	    throw new IllegalStateException("Value can't be null"); //
        }
    }

    @Override
    public boolean ignore() {
        return ignoreNull && value == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        if (!(obj instanceof EntValue2)) {
            return false;
        }
        final EntValue2 other = (EntValue2) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public Class type() {
        // TODO EQL
        return value != null ? value.getClass() : null;
    }
}