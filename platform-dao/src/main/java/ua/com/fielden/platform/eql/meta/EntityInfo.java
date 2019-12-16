package ua.com.fielden.platform.eql.meta;

import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import ua.com.fielden.platform.entity.AbstractEntity;

public class EntityInfo<T extends AbstractEntity<?>> implements IResolvable<T> {
    private final Class<T> javaType;
    private final SortedMap<String, AbstractPropInfo<?>> props = new TreeMap<>();
    private final EntityCategory category;

    public EntityInfo(final Class<T> javaType, final EntityCategory category) {
        this.javaType = javaType;
        this.category = category;
    }

    @Override
    public ResolutionContext resolve(final ResolutionContext context) {
        if (context.isSuccessful()) {
            return context;
        } else {
            final AbstractPropInfo<?> foundPart = props.get(context.getNextPending());
            return foundPart == null ? context : foundPart.resolve(context.registerResolutionAndClone(foundPart));
        }
    }

    public EntityInfo<T> addProp(final AbstractPropInfo<?> propInfo) { 
        props.put(propInfo.name, propInfo);
        return this;
    }
    
    public SortedMap<String, AbstractPropInfo<?>> getProps() {
        return props;
    }
    
    @Override
    public String toString() {
        return javaType.getSimpleName();
    }

    @Override
    public Class<T> javaType() {
        return javaType;
    }

    public EntityCategory getCategory() {
        return category;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + category.hashCode();
        result = prime * result + javaType.hashCode();
        result = prime * result + props.keySet().hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof EntityInfo)) {
            return false;
        }

        final EntityInfo other = (EntityInfo) obj;

        return Objects.equals(javaType, other.javaType) && Objects.equals(category, other.category) && Objects.equals(props.keySet(), other.props.keySet());
    }
}