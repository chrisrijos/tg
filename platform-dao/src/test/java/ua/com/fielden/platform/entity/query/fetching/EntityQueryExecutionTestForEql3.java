package ua.com.fielden.platform.entity.query.fetching;

import static org.junit.Assert.assertEquals;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import ua.com.fielden.platform.dao.IEntityAggregatesOperations;
import ua.com.fielden.platform.entity.query.EntityAggregates;
import ua.com.fielden.platform.entity.query.model.AggregatedResultQueryModel;
import ua.com.fielden.platform.sample.domain.TgOrgUnit1;
import ua.com.fielden.platform.sample.domain.TgOrgUnit2;
import ua.com.fielden.platform.sample.domain.TgOrgUnit3;
import ua.com.fielden.platform.sample.domain.TgOrgUnit4;
import ua.com.fielden.platform.sample.domain.TgOrgUnit5;
import ua.com.fielden.platform.sample.domain.TgVehicle;
import ua.com.fielden.platform.sample.domain.TgVehicleMake;
import ua.com.fielden.platform.sample.domain.TgVehicleModel;
import ua.com.fielden.platform.sample.domain.TgWorkOrder;
import ua.com.fielden.platform.test_config.AbstractDaoTestCase;
import ua.com.fielden.platform.types.Money;

public class EntityQueryExecutionTestForEql3 extends AbstractDaoTestCase {
    private final IEntityAggregatesOperations aggregateDao = getInstance(IEntityAggregatesOperations.class);

    @Test
    public void eql3_query_executes_correctly() {
        final AggregatedResultQueryModel qry = select(TgVehicle.class).as("veh").leftJoin(TgVehicle.class).as("rbv").
                on().prop("veh.replacedBy").eq().prop("rbv.id")./*or().prop("veh.replacedBy").ne().prop("rbv.id").*/
                where().prop("veh.replacedBy").isNotNull().and().notExists(
                        select(TgVehicle.class).where().prop("replacedBy").eq().extProp("veh.id").model()).
                yield().prop("veh.key").as("vehiclekey").
                yield().prop("rbv.key").as("replacedByVehiclekey").
                yield().countAll().as("allCount").
                yield().caseWhen().prop("veh.key").eq().prop("rbv.key").then().prop("veh.key").otherwise().prop("rbv.key").endAsStr(5).as("cwts").
                modelAsAggregate();
        
        final List<EntityAggregates> vehicles = aggregateDao.getAllEntities(from(qry).with("EQL3", null).model());
        
        assertEquals("CAR2", vehicles.get(0).get("vehiclekey"));
        assertEquals("CAR1", vehicles.get(0).get("replacedByVehiclekey"));
        assertEquals("1", vehicles.get(0).get("allCount").toString());
        assertEquals("CAR1", vehicles.get(0).get("cwts").toString());
    }
    
    @Test
    public void eql3_query_executes_correctly2() {
        final AggregatedResultQueryModel qry = select(TgVehicle.class).as("veh").
                where().prop("veh.replacedBy").isNotNull().and().notExists(
                        select(TgVehicle.class).where().prop("replacedBy").eq().extProp("veh.id").model()).
                yield().prop("veh.key").as("vehiclekey").
                yield().prop("veh.replacedBy.key").as("replacedByVehiclekey").
                yield().countAll().as("allCount").
                yield().caseWhen().prop("veh.key").eq().prop("veh.replacedBy.key").then().prop("veh.key").otherwise().prop("veh.replacedBy.key").endAsStr(5).as("cwts").
                modelAsAggregate();
        
        final List<EntityAggregates> vehicles = aggregateDao.getAllEntities(from(qry).with("EQL3", null).model());
        
        assertEquals("CAR2", vehicles.get(0).get("vehiclekey"));
        assertEquals("CAR1", vehicles.get(0).get("replacedByVehiclekey"));
        assertEquals("1", vehicles.get(0).get("allCount").toString());
        assertEquals("CAR1", vehicles.get(0).get("cwts").toString());
    }
    
    @Test
    public void eql3_query_executes_correctly3() {
        final AggregatedResultQueryModel qry = select(TgVehicle.class).as("veh").
                join(TgOrgUnit1.class).as("st").on().prop("station.parent.parent.parent.parent").eq().prop("st.id").
                where().anyOfProps("veh.key", "replacedBy.key", "initDate", "station.name", "station.parent.name", "st.key", "st.id", "model.make.key", "replacedBy.model.make.key").isNotNull().
                yield().prop("veh.key").as("vehiclekey").
                yield().prop("veh.replacedBy.key").as("replacedByVehiclekey").
                yield().countAll().as("allCount").
                yield().caseWhen().prop("veh.key").eq().prop("veh.replacedBy.key").then().prop("veh.key").otherwise().prop("veh.replacedBy.key").endAsStr(5).as("cwts").
                modelAsAggregate();
        
        final List<EntityAggregates> vehicles = aggregateDao.getAllEntities(from(qry).with("EQL3", null).model());
        
        assertEquals("CAR2", vehicles.get(0).get("vehiclekey"));
        assertEquals("CAR1", vehicles.get(0).get("replacedByVehiclekey"));
        assertEquals("1", vehicles.get(0).get("allCount").toString());
        assertEquals("CAR1", vehicles.get(0).get("cwts").toString());
    }
    
    @Test
    public void eql3_query_executes_correctly4() {
        final AggregatedResultQueryModel qry = select(TgVehicle.class).as("veh").
                join(TgOrgUnit1.class).as("st").on().prop("station.parent.parent.parent.parent").eq().prop("st.id").
                where().anyOfProps("veh.key", "replacedBy.key", "initDate", "station.name", "station.parent.name", "st.key", "st.id", "model.make.key", "replacedBy.model.make.key").isNotNull().
                yield().prop("veh.key").as("vehicleKey").
                yield().prop("veh.replacedBy.key").as("replacedByVehiclekey").
                yield().countAll().as("allCount").
                yield().caseWhen().prop("veh.key").eq().prop("veh.replacedBy.key").then().prop("veh.key").otherwise().prop("veh.replacedBy.key").endAsStr(5).as("cwts").
                modelAsAggregate();
        
        final AggregatedResultQueryModel qry2 = select(qry).yield().prop("vehicleKey").as("vk").modelAsAggregate();
        
        final List<EntityAggregates> vehicles = aggregateDao.getAllEntities(from(qry2).with("EQL3", null).model());
        
        assertEquals("CAR2", vehicles.get(0).get("vk"));
    }

    @Test
    public void eql3_query_executes_correctly5() {
        final AggregatedResultQueryModel qry = select(TgVehicle.class).as("veh").
                join(TgOrgUnit1.class).as("st").on().prop("station.parent.parent.parent.parent").eq().prop("st.id").
                where().anyOfProps("veh.key", "replacedBy.key", "initDate", "station.name", "station.parent.name", "st.key", "st.id", "model.make.key", "replacedBy.model.make.key").isNotNull().
                yield().prop("veh.replacedBy").as("vehicle").
                yield().prop("veh.replacedBy.key").as("replacedByVehiclekey").
                yield().countAll().as("allCount").
                yield().caseWhen().prop("veh.key").eq().prop("veh.replacedBy.key").then().prop("veh.key").otherwise().prop("veh.replacedBy.key").endAsStr(5).as("cwts").
                modelAsAggregate();
        
        final AggregatedResultQueryModel qry2 = select(qry).where().prop("vehicle").isNotNull().yield().prop("vehicle.key").as("vk").modelAsAggregate();
        
        final List<EntityAggregates> vehicles = aggregateDao.getAllEntities(from(qry2).with("EQL3", null).model());
        
        assertEquals("CAR1", vehicles.get(0).get("vk"));
    }

    @Test
    public void eql3_query_executes_correctly6() {
        final AggregatedResultQueryModel qry = select(TgVehicle.class).as("veh").
                where().prop("veh.replacedBy").isNotNull().and().notExists(
                        select(TgVehicle.class).where().prop("replacedBy").eq().extProp("veh.id").model()).
                yield().prop("veh.replacedBy").as("vehicle").
                modelAsAggregate();
        
        final AggregatedResultQueryModel qry2 = select(qry).where().prop("vehicle").isNotNull().yield().prop("vehicle.key").as("vk").modelAsAggregate();
        
        final List<EntityAggregates> vehicles = aggregateDao.getAllEntities(from(qry2).with("EQL3", null).model());
        
        assertEquals("CAR1", vehicles.get(0).get("vk"));
    }

    @Test
    public void eql3_query_executes_correctly7() {
        final AggregatedResultQueryModel qry = select(TgVehicle.class).as("veh").
                where().prop("veh.replacedBy").isNotNull().and().notExists(
                        select(TgVehicle.class).where().prop("replacedBy").eq().extProp("veh.id").model()).
                yield().prop("veh.replacedBy").as("vehicle").
                modelAsAggregate();
        
        final AggregatedResultQueryModel qry2 = select(qry).where().prop("vehicle.station.name").isNull().yield().prop("vehicle").as("vk").modelAsAggregate();
        
        final AggregatedResultQueryModel qry3 = select(qry2).where().prop("vk.model").isNotNull().yield().prop("vk.model.make.key").as("makeKey").modelAsAggregate();
        
        final List<EntityAggregates> vehicles = aggregateDao.getAllEntities(from(qry3).with("EQL3", null).model());
        
        assertEquals("AUDI", vehicles.get(0).get("makeKey"));
    }

    @Test
    public void eql3_query_executes_correctly8() {
        final AggregatedResultQueryModel qry = select(TgWorkOrder.class).where().prop("zMakeKey").isNotNull().yield().countAll().as("KOUNT").
                modelAsAggregate();
        
        final List<EntityAggregates> vehicles = aggregateDao.getAllEntities(from(qry).with("EQL3", null).model());
        
        assertEquals("1", vehicles.get(0).get("KOUNT").toString());
    }

    @Test
    public void eql3_query_executes_correctly9() {
        final AggregatedResultQueryModel qry = select(TgWorkOrder.class).where().prop("zMake.key").isNotNull().yield().countAll().as("KOUNT").
                modelAsAggregate();
        
        final List<EntityAggregates> vehicles = aggregateDao.getAllEntities(from(qry).with("EQL3", null).model());
        
        assertEquals("1", vehicles.get(0).get("KOUNT").toString());
    }

    
    @Override
    protected void populateDomain() {
        super.populateDomain();
        
        final TgOrgUnit1 orgUnit1 = save(new_(TgOrgUnit1.class, "orgunit1", "desc orgunit1"));
        final TgOrgUnit2 orgUnit2 = save(new_composite(TgOrgUnit2.class, orgUnit1, "orgunit2"));
        final TgOrgUnit3 orgUnit3 = save(new_composite(TgOrgUnit3.class, orgUnit2, "orgunit3"));
        final TgOrgUnit4 orgUnit4 = save(new_composite(TgOrgUnit4.class, orgUnit3, "orgunit4"));
        final TgOrgUnit5 orgUnit5 = save(new_composite(TgOrgUnit5.class, orgUnit4, "orgunit5"));

        final TgVehicleMake merc = save(new_(TgVehicleMake.class, "MERC", "Mercedes"));
        final TgVehicleMake audi = save(new_(TgVehicleMake.class, "AUDI", "Audi"));
        final TgVehicleMake bmw = save(new_(TgVehicleMake.class, "BMW", "BMW"));
        final TgVehicleMake subaro = save(new_(TgVehicleMake.class, "SUBARO", "Subaro"));

        final TgVehicleModel m316 = save(new_(TgVehicleModel.class, "316", "316").setMake(merc));
        final TgVehicleModel m317 = save(new_(TgVehicleModel.class, "317", "317").setMake(audi));
        final TgVehicleModel m318 = save(new_(TgVehicleModel.class, "318", "318").setMake(audi));
        final TgVehicleModel m319 = save(new_(TgVehicleModel.class, "319", "319").setMake(bmw));
        final TgVehicleModel m320 = save(new_(TgVehicleModel.class, "320", "320").setMake(bmw));
        final TgVehicleModel m321 = save(new_(TgVehicleModel.class, "321", "321").setMake(bmw));
        final TgVehicleModel m322 = save(new_(TgVehicleModel.class, "322", "322").setMake(bmw));

        final TgVehicle car1 = save(new_(TgVehicle.class, "CAR1", "CAR1 DESC").setInitDate(date("2001-01-01 00:00:00")).setModel(m318).setPrice(new Money("20")).setPurchasePrice(new Money("10")).setActive(true).setLeased(false));
        final TgVehicle car2 = save(new_(TgVehicle.class, "CAR2", "CAR2 DESC").setInitDate(date("2007-01-01 00:00:00")).setModel(m316).setPrice(new Money("200")).setPurchasePrice(new Money("100")).setActive(false).setLeased(true).setLastMeterReading(new BigDecimal("105")).setStation(orgUnit5).setReplacedBy(car1));

        final TgWorkOrder wo1 = save(new_(TgWorkOrder.class, "wo0001", "wo0001 desc").setVehicle(car2));

        
    }
}
