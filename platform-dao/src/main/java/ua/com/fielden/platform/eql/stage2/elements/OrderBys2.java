package ua.com.fielden.platform.eql.stage2.elements;

import java.util.List;
import java.util.Objects;

import ua.com.fielden.platform.eql.stage3.elements.OrderBys3;

public class OrderBys2 {
    private final List<OrderBy2> models;

    public OrderBys2(final List<OrderBy2> models) {
        this.models = models;
    }

    public List<OrderBy2> getModels() {
        return models;
    }

    public TransformationResult<OrderBys3> transform(final TransformationContext transformationContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((models == null) ? 0 : models.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
    
        if (!(obj instanceof OrderBys2)) {
            return false;
        }
        
        final OrderBys2 other = (OrderBys2) obj;
        
        return Objects.equals(models, other.models);
    }
}