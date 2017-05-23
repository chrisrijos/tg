package ua.com.fielden.platform.test.domain.entities;

import ua.com.fielden.platform.entity.DynamicEntityKey;
import ua.com.fielden.platform.entity.annotation.CompositeKeyMember;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.annotation.Observable;
import ua.com.fielden.platform.entity.validation.annotation.DomainValidation;

/**
 * Represents slot in the wagon for fitting bogie rotable there.
 * 
 * @author nc
 * 
 */
@KeyType(DynamicEntityKey.class)
public class WagonSlot extends RotableLocation<DynamicEntityKey> {

    private static final long serialVersionUID = 1L;

    @IsProperty
    @CompositeKeyMember(1)
    private Wagon wagon;

    @IsProperty
    @CompositeKeyMember(2)
    private Integer position;

    @IsProperty
    private Bogie bogie;

    public Bogie getBogie() {
        return bogie;
    }

    @Observable
    @DomainValidation
    public void setBogie(final Bogie bogie) {
        this.bogie = bogie;
    }

    public WagonSlot(final Wagon wagon, final Integer position) {
        setWagon(wagon);
        setPosition(position);
    }

    public Wagon getWagon() {
        return wagon;
    }

    @Observable
    protected void setWagon(final Wagon wagon) {
        this.wagon = wagon;
    }

    public Integer getPosition() {
        return position;
    }

    @Observable
    protected void setPosition(final Integer position) {
        this.position = position;
    }

    /**
     * Retrieves full wagon slot number, which is composed of the wagon serial number, char 'B' and the slot index.
     * 
     * @return
     */
    public String getSlotNumber() {
        return wagon.getSerialNo() + "B" + getSlotIndex();
    }

    /**
     * Retrieves wagon slot index.
     * 
     * @return
     */
    public String getSlotIndex() {
        return String.format("%02d", getPosition());
    }
}
