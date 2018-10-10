package ua.com.fielden.platform.test.domain.entities;

import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.annotation.Observable;

/**
 * Rotable business entity.
 *
 * @author 01es
 */
@KeyType(String.class)
public abstract class Rotable extends Equipment<String> {
    private static final long serialVersionUID = 1L;

    @IsProperty
    private RotableStatus status = RotableStatus.S; // TODO do we need to keep the history of status changes?
    @IsProperty
    private RotableClass rotableClass;
    /**
     * This property can point to any descendant of RotableLocation with any type parameter.
     *
     * Using RotableLocation<?> upsets javac when trying to use instanceof or explicit type casting. Thus, the need to suppress warning unchecked.
     */
    @SuppressWarnings("unchecked")
    @IsProperty
    private RotableLocation location;

    protected Rotable() {
    }

    public Rotable(final String name, final String desc) {
        super(null, name, desc);
    }

    public RotableStatus getStatus() {
        return status;
    }

    @Observable
    public Rotable setStatus(final RotableStatus status) {
        this.status = status;
        return this;
    }

    public RotableClass getRotableClass() {
        return rotableClass;
    }

    public Rotable setRotableClass(final RotableClass klass) {
        this.rotableClass = klass;
        return this;
    }

    @Observable
    @SuppressWarnings("unchecked")
    public Rotable setLocation(final RotableLocation location) {
        this.location = location;
        return this;
    }

    @SuppressWarnings("unchecked")
    public RotableLocation getLocation() {
        return location;
    }
}
