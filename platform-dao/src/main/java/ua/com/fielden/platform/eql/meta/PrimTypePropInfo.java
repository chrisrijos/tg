package ua.com.fielden.platform.eql.meta;

import ua.com.fielden.platform.eql.s1.elements.Expression1;

public class PrimTypePropInfo extends AbstractPropInfo {
    private final Class propType;

    @Override
    public String toString() {
        return super.toString() + ": " + propType.getSimpleName();
    }

    public PrimTypePropInfo(final String name, final EntityInfo parent, final Class propType, final Expression1 expression) {
        super(name, parent, expression);
        this.propType = propType;
    }

    protected Class getPropType() {
        return propType;
    }

    @Override
    public AbstractPropInfo resolve(final String dotNotatedSubPropName) {
        if (dotNotatedSubPropName != null) {
            throw new IllegalStateException("Resolve method should get [null] as parameter instead of [" + dotNotatedSubPropName + "].\nAdditional info: name = " + getName() + "; propType = " + getPropType() + ";");
        }
        return this;
    }

    @Override
    public Class javaType() {
        return getPropType();
    }
}