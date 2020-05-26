package ua.com.fielden.platform.eql.meta;

import java.util.Objects;

import ua.com.fielden.platform.entity.AbstractUnionEntity;

/**
 * A structure that captures a query source yield-able entity-typed-property resolution related info within a query source of type <code>PARENT</code>.
 * 
 * @author TG Team
 *
 * @param <T>
 * @param <PARENT>
 */
public class UnionTypePropInfo<T extends AbstractUnionEntity> extends AbstractPropInfo<T> {
    public final EntityInfo<T> propEntityInfo;
    public final boolean required;

    public UnionTypePropInfo(final String name, final EntityInfo<T> propEntityInfo, final Object hibType, final boolean required) {
        super(name, hibType, null);
        this.propEntityInfo = propEntityInfo;
        this.required = required;
    }

    @Override
    public ResolutionContext resolve(final ResolutionContext context) {
        return propEntityInfo.resolve(context);
    }

    @Override
    public Class<T> javaType() {
        return propEntityInfo.javaType();
    }

    @Override
    public String toString() {
        return String.format("%20s %20s", name, propEntityInfo.javaType().getSimpleName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + propEntityInfo.hashCode();
        result = prime * result + (required ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof UnionTypePropInfo)) {
            return false;
        }

        final UnionTypePropInfo other = (UnionTypePropInfo) obj;

        return Objects.equals(propEntityInfo, other.propEntityInfo) && Objects.equals(required, other.required);
    }
}