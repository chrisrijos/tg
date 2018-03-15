package ua.com.fielden.platform.sample.domain;

import org.junit.Ignore;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.CompanionObject;
import ua.com.fielden.platform.entity.annotation.DescTitle;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.annotation.MapEntityTo;
import ua.com.fielden.platform.entity.annotation.MapTo;
import ua.com.fielden.platform.entity.annotation.Observable;
import ua.com.fielden.platform.entity.annotation.Required;
import ua.com.fielden.platform.entity.annotation.Title;

@KeyType(String.class)
@MapEntityTo
@DescTitle("Description")
@Ignore
@CompanionObject(ITgVehicleModel.class)
public class TgVehicleModel extends AbstractEntity<String> {

    @IsProperty
    @Required
    @MapTo
    @Title(value = "Test vehicle model", desc = "Test vehicle model")
    private TgVehicleMake make;
    
    @IsProperty
    @Title("Ordinary property")
    private Integer ordinaryIntProp;

    @Observable
    public TgVehicleModel setOrdinaryIntProp(final Integer ordinaryIntProp) {
        this.ordinaryIntProp = ordinaryIntProp;
        return this;
    }

    public Integer getOrdinaryIntProp() {
        return ordinaryIntProp;
    }

    @Observable
    public TgVehicleModel setMake(final TgVehicleMake make) {
        this.make = make;
        return this;
    }

    public TgVehicleMake getMake() {
        return make;
    }

    /**
     * Constructor for (@link EntityFactory}.
     */
    protected TgVehicleModel() {
    }
}
