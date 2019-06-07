package ua.com.fielden.platform.eql.stage1.elements.sources;

import ua.com.fielden.platform.eql.stage1.elements.AbstractElement1;
import ua.com.fielden.platform.eql.stage2.elements.IQrySource2;

public abstract class AbstractQrySource1<S2 extends IQrySource2> extends AbstractElement1 implements IQrySource1<S2> {

    /**
     * Business name for query source. Can be also dot.notated, but should stick to property alias naming rules (e.g. no dots in beginning/end).
     */
    protected final String alias;

    public AbstractQrySource1(final String alias, final int contextId) {
        super(contextId);
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alias == null) ? 0 : alias.hashCode());
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
        if (!(obj instanceof AbstractQrySource1)) {
            return false;
        }
        final AbstractQrySource1 other = (AbstractQrySource1) obj;
        if (alias == null) {
            if (other.alias != null) {
                return false;
            }
        } else if (!alias.equals(other.alias)) {
            return false;
        }
        return true;
    }
}