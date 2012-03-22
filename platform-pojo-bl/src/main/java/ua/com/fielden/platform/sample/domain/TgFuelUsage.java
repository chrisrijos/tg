package ua.com.fielden.platform.sample.domain;

import java.math.BigDecimal;
import java.util.Date;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.DynamicEntityKey;
import ua.com.fielden.platform.entity.annotation.CompositeKeyMember;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.KeyTitle;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.annotation.MapEntityTo;
import ua.com.fielden.platform.entity.annotation.MapTo;
import ua.com.fielden.platform.entity.annotation.Observable;
import ua.com.fielden.platform.entity.annotation.Title;
import ua.com.fielden.platform.entity.validation.annotation.DefaultController2;
import ua.com.fielden.platform.entity.validation.annotation.EntityExists;
import ua.com.fielden.platform.sample.domain.controller.ITgFuelUsage;

@KeyTitle("Fuel Usages")
@KeyType(DynamicEntityKey.class)
@MapEntityTo
@DefaultController2(ITgFuelUsage.class)
public class TgFuelUsage extends AbstractEntity<DynamicEntityKey> {
    private static final long serialVersionUID = 1L;

    @IsProperty
    @Title("Vehicle")
    @CompositeKeyMember(1)
    @MapTo
    private TgVehicle vehicle;
    @IsProperty
    @Title("Purchase Date")
    @CompositeKeyMember(2)
    @MapTo
    private Date date;

    @IsProperty
    @MapTo
    @Title(value = "Fuel Qty", desc = "Fuel Qty")
    private BigDecimal qty;

    public BigDecimal getQty() {
	return qty;
    }

    public TgVehicle getVehicle() {
        return vehicle;
    }

    public Date getDate() {
        return date;
    }

    @Observable
    @EntityExists(TgVehicle.class)
    public void setVehicle(final TgVehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Observable
    public void setDate(final Date date) {
        this.date = date;
    }

    @Observable
    public TgFuelUsage setQty(final BigDecimal qty) {
	this.qty = qty;
	return this;
    }
}