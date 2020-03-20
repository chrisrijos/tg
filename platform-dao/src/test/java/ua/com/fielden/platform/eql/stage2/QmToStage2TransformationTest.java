package ua.com.fielden.platform.eql.stage2;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;
import static ua.com.fielden.platform.entity.query.fluent.enums.ComparisonOperator.EQ;
import static ua.com.fielden.platform.entity.query.fluent.enums.JoinType.LJ;
import static ua.com.fielden.platform.eql.meta.QueryCategory.RESULT_QUERY;
import static ua.com.fielden.platform.eql.meta.QueryCategory.SUB_QUERY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import ua.com.fielden.platform.entity.query.fluent.enums.JoinType;
import ua.com.fielden.platform.entity.query.model.AggregatedResultQueryModel;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.eql.meta.EqlStage2TestCase;
import ua.com.fielden.platform.eql.stage2.elements.EntQueryBlocks2;
import ua.com.fielden.platform.eql.stage2.elements.GroupBy2;
import ua.com.fielden.platform.eql.stage2.elements.GroupBys2;
import ua.com.fielden.platform.eql.stage2.elements.OrderBys2;
import ua.com.fielden.platform.eql.stage2.elements.conditions.ComparisonTest2;
import ua.com.fielden.platform.eql.stage2.elements.conditions.Conditions2;
import ua.com.fielden.platform.eql.stage2.elements.conditions.ICondition2;
import ua.com.fielden.platform.eql.stage2.elements.conditions.NullTest2;
import ua.com.fielden.platform.eql.stage2.elements.operands.EntProp2;
import ua.com.fielden.platform.eql.stage2.elements.operands.EntQuery2;
import ua.com.fielden.platform.eql.stage2.elements.operands.EntValue2;
import ua.com.fielden.platform.eql.stage2.elements.operands.Expression2;
import ua.com.fielden.platform.eql.stage2.elements.sources.CompoundSource2;
import ua.com.fielden.platform.eql.stage2.elements.sources.QrySource2BasedOnPersistentType;
import ua.com.fielden.platform.eql.stage2.elements.sources.Sources2;
import ua.com.fielden.platform.sample.domain.TgAuthorRoyalty;
import ua.com.fielden.platform.sample.domain.TgAuthorship;
import ua.com.fielden.platform.sample.domain.TgAverageFuelUsage;
import ua.com.fielden.platform.sample.domain.TgEntityWithLoopedCalcProps;
import ua.com.fielden.platform.sample.domain.TgOrgUnit2;
import ua.com.fielden.platform.sample.domain.TgPersonName;
import ua.com.fielden.platform.sample.domain.TgWorkshop;

public class QmToStage2TransformationTest extends EqlStage2TestCase {
    
    @Test
    public void test01() {
        final EntQuery2 actQry = qryCountAll(select(MODEL).where().prop("make").isNotNull());
        
        final QrySource2BasedOnPersistentType source = source("1", MODEL);
        final Sources2 sources = sources(source);
        final EntProp2 makeProp = prop(source, pi(MODEL, "make"));
        final Conditions2 conditions = cond(isNotNull(makeProp));
        final EntQuery2 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }

    @Test
    public void test02() {
        final EntQuery2 actQry = qryCountAll(select(MODEL).where().prop("make.id").isNotNull());
        
        final QrySource2BasedOnPersistentType source = source("1", MODEL);
        final Sources2 sources = sources(source);
        final EntProp2 makeProp = prop(source, pi(MODEL, "make"));
        final Conditions2 conditions = cond(isNotNull(makeProp));
        final EntQuery2 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }
    
    @Test
    public void test03() {
        final EntQuery2 actQry = qryCountAll(select(MODEL).where().prop("make.key").isNotNull());
        
        final QrySource2BasedOnPersistentType source = source("1", MODEL);
        final Sources2 sources = sources(source);
        final EntProp2 makeProp = prop(source, pi(MODEL, "make"), pi(MAKE, "key"));
        final Conditions2 conditions = cond(isNotNull(makeProp));
        final EntQuery2 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }
    
    @Test
    public void prop_paths_are_correctly_resolved() {
        final EntQuery2 actQry = qryCountAll(select(VEHICLE).where().anyOfProps("initDate", "station.name", "station.parent.name", "replacedBy.initDate").isNotNull());

        final QrySource2BasedOnPersistentType source = source("1", VEHICLE);
        final Sources2 sources = sources(source);
        final EntProp2 initDate = prop(source, pi(VEHICLE, "initDate"));
        final EntProp2 station_name = prop(source, pi(VEHICLE, "station"), pi(ORG5, "name"));
        final EntProp2 station_parent_name = prop(source, pi(VEHICLE, "station"), pi(ORG5, "parent"), pi(ORG4, "name"));
        final EntProp2 replacedBy_initDate = prop(source, pi(VEHICLE, "replacedBy"), pi(VEHICLE, "initDate"));
        
        final Conditions2 conditions = or(and(or(
                isNotNull(initDate), 
                isNotNull(station_name), 
                isNotNull(station_parent_name), 
                isNotNull(replacedBy_initDate)
                )));
        final EntQuery2 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }
    
    @Test
    public void prop_paths_without_aliases_with_aliased_source_are_correctly_resolved() {
        final EntQuery2 actQry = qryCountAll(select(VEHICLE).as("v").where().anyOfProps("initDate", "station.name", "station.parent.name", "replacedBy.initDate").isNotNull());
        
        final QrySource2BasedOnPersistentType source = source("1", VEHICLE, "v");
        final Sources2 sources = sources(source);
        final EntProp2 initDate = prop(source, pi(VEHICLE, "initDate"));
        final EntProp2 station_name = prop(source, pi(VEHICLE, "station"), pi(ORG5, "name"));
        final EntProp2 station_parent_name = prop(source, pi(VEHICLE, "station"), pi(ORG5, "parent"), pi(ORG4, "name"));
        final EntProp2 replacedBy_initDate = prop(source, pi(VEHICLE, "replacedBy"), pi(VEHICLE, "initDate"));
        
        final Conditions2 conditions = or(and(or(
                isNotNull(initDate), 
                isNotNull(station_name), 
                isNotNull(station_parent_name), 
                isNotNull(replacedBy_initDate)
                )));
        final EntQuery2 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }

    @Test
    public void prop_paths_with_some_aliases_with_aliased_source_are_correctly_resolved() {
        final EntQuery2 actQry = qryCountAll(select(VEHICLE).as("v").where().anyOfProps("v.initDate", "station.name", "station.parent.name", "v.replacedBy.initDate").isNotNull());
        
        final QrySource2BasedOnPersistentType source = source("1", VEHICLE, "v");
        final Sources2 sources = sources(source);
        final EntProp2 initDate = prop(source, pi(VEHICLE, "initDate"));
        final EntProp2 station_name = prop(source, pi(VEHICLE, "station"), pi(ORG5, "name"));
        final EntProp2 station_parent_name = prop(source, pi(VEHICLE, "station"), pi(ORG5, "parent"), pi(ORG4, "name"));
        final EntProp2 replacedBy_initDate = prop(source, pi(VEHICLE, "replacedBy"), pi(VEHICLE, "initDate"));
        
        final Conditions2 conditions = or(and(or(
                isNotNull(initDate), 
                isNotNull(station_name), 
                isNotNull(station_parent_name), 
                isNotNull(replacedBy_initDate)
                )));
        final EntQuery2 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }
    
    @Test
    public void prop_paths_in_qry_with_two_sources_are_correctly_resolved() {
        final EntQuery2 actQry = qryCountAll(select(VEHICLE).as("v").join(VEHICLE).as("rv").on().prop("v.replacedBy").eq().prop("rv.id").
                where().anyOfProps("v.initDate", "rv.station.name", "v.station.parent.name", "rv.replacedBy.initDate").isNotNull());

        final QrySource2BasedOnPersistentType source = source("1", VEHICLE, "v");
        final QrySource2BasedOnPersistentType source2 = source("2", VEHICLE, "rv");
        final Sources2 sources = sources(source, ij(source2, or(eq(prop(source, pi(VEHICLE, "replacedBy")), prop(source2, pi(VEHICLE, "id"))))));
        final EntProp2 initDate = prop(source, pi(VEHICLE, "initDate"));
        final EntProp2 station_name = prop(source2, pi(VEHICLE, "station"), pi(ORG5, "name"));
        final EntProp2 station_parent_name = prop(source, pi(VEHICLE, "station"), pi(ORG5, "parent"), pi(ORG4, "name"));
        final EntProp2 replacedBy_initDate = prop(source2, pi(VEHICLE, "replacedBy"), pi(VEHICLE, "initDate"));
        
        final Conditions2 conditions = or(and(or(
                isNotNull(initDate), 
                isNotNull(station_name), 
                isNotNull(station_parent_name), 
                isNotNull(replacedBy_initDate)
                )));
        final EntQuery2 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }
    
    @Test
    public void test05() {
        final EntQuery2 actQry = qryCountAll(select(ORG1).where().exists(select(ORG2).where().prop("parent").eq().extProp("id").model()));  

        final QrySource2BasedOnPersistentType source1 = source("2", ORG1);
        final QrySource2BasedOnPersistentType source2 = source("1", ORG2);

        final Sources2 sources1 = sources(source1);
        final Sources2 sources2 = sources(source2);
        final Conditions2 conditions2 = or(eq(prop(source2, pi(ORG2, "parent")), prop(source1, pi(ORG1, "id"))));
        final Conditions2 conditions1 = or(exists(sources2, conditions2, ORG2));

        final EntQuery2 expQry = qryCountAll(sources1, conditions1);

        assertEquals(expQry, actQry);
    }
    
    @Test
    public void test06() {
        final EntityResultQueryModel<TgOrgUnit2> subqry = select(ORG2).where().prop("parent").eq().extProp("id").model();
        final EntQuery2 actQry = qryCountAll(select(ORG1).where().exists(subqry).or().notExists(subqry));  

        final QrySource2BasedOnPersistentType source = source("3", ORG1);
        final QrySource2BasedOnPersistentType subQrySource1 = source("1", ORG2);
        final QrySource2BasedOnPersistentType subQrySource2 = source("2", ORG2);

        final Sources2 sources = sources(source);
        final Sources2 subQrySources1 = sources(subQrySource1);
        final Sources2 subQrySources2 = sources(subQrySource2);
        final Conditions2 subQryConditions1 = or(eq(prop(subQrySource1, pi(ORG2, "parent")), prop(source, pi(ORG1, "id"))));
        final Conditions2 subQryConditions2 = or(eq(prop(subQrySource2, pi(ORG2, "parent")), prop(source, pi(ORG1, "id"))));
        final Conditions2 conditions = or(exists(subQrySources1, subQryConditions1, ORG2), notExists(subQrySources2, subQryConditions2, ORG2));

        final EntQuery2 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }
    
    @Test
    public void test07() {
        final EntQuery2 actQry = qryCountAll(//
        select(ORG1).where().exists( //
        select(ORG2).where().prop("parent").eq().extProp("id").and().exists( //
        select(ORG3).where().prop("parent").eq().extProp("id").and().exists( // 
        select(ORG4).where().prop("parent").eq().extProp("id").and().exists( //
        select(ORG5).where().prop("parent").eq().extProp("id").and().prop("key").isNotNull(). //
        model()). //
        model()). //
        model()). //
        model()));

        final QrySource2BasedOnPersistentType source = source("5", ORG1);
        final QrySource2BasedOnPersistentType sub1QrySource = source("4", ORG2);
        final QrySource2BasedOnPersistentType sub2QrySource = source("3", ORG3);
        final QrySource2BasedOnPersistentType sub3QrySource = source("2", ORG4);
        final QrySource2BasedOnPersistentType sub4QrySource = source("1", ORG5);

        final Sources2 sources = sources(source);
        final Sources2 sub1QrySources = sources(sub1QrySource);
        final Sources2 sub2QrySources = sources(sub2QrySource);
        final Sources2 sub3QrySources = sources(sub3QrySource);
        final Sources2 sub4QrySources = sources(sub4QrySource);


        final Conditions2 subQryConditions4 = or(and(eq(prop(sub4QrySource, pi(ORG5, "parent")), prop(sub3QrySource, pi(ORG4, "id"))), isNotNull(prop(sub4QrySource, pi(ORG5, "key")))));
        final Conditions2 subQryConditions3 = or(and(eq(prop(sub3QrySource, pi(ORG4, "parent")), prop(sub2QrySource, pi(ORG3, "id"))), exists(sub4QrySources, subQryConditions4, ORG5)));
        final Conditions2 subQryConditions2 = or(and(eq(prop(sub2QrySource, pi(ORG3, "parent")), prop(sub1QrySource, pi(ORG2, "id"))), exists(sub3QrySources, subQryConditions3, ORG4)));
        final Conditions2 subQryConditions1 = or(and(eq(prop(sub1QrySource, pi(ORG2, "parent")), prop(source, pi(ORG1, "id"))), exists(sub2QrySources, subQryConditions2, ORG3)));
        
        final Conditions2 conditions = or(exists(sub1QrySources, subQryConditions1, ORG2));

        final EntQuery2 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }

    @Test
    public void test08() {
        final EntQuery2 actQry = qryCountAll(//
        select(ORG1).as("L1").where().exists( //
        select(ORG2).as("L2").where().prop("parent").eq().prop("L1.id").and().exists( //
        select(ORG3).as("L3").where().prop("parent").eq().prop("L2.id").and().exists( // 
        select(ORG4).as("L4").where().prop("parent").eq().prop("L3.id").and().exists( //
        select(ORG5).as("L5").where().prop("parent").eq().prop("L4.id").and().prop("key").isNotNull(). //
        model()). //
        model()). //
        model()). //
        model()));
        
        final QrySource2BasedOnPersistentType source = source("5", ORG1, "L1");
        final QrySource2BasedOnPersistentType sub1QrySource = source("4", ORG2, "L2");
        final QrySource2BasedOnPersistentType sub2QrySource = source("3", ORG3, "L3");
        final QrySource2BasedOnPersistentType sub3QrySource = source("2", ORG4, "L4");
        final QrySource2BasedOnPersistentType sub4QrySource = source("1", ORG5, "L5");

        final Sources2 sources = sources(source);
        final Sources2 sub1QrySources = sources(sub1QrySource);
        final Sources2 sub2QrySources = sources(sub2QrySource);
        final Sources2 sub3QrySources = sources(sub3QrySource);
        final Sources2 sub4QrySources = sources(sub4QrySource);


        final Conditions2 subQryConditions4 = or(and(eq(prop(sub4QrySource, pi(ORG5, "parent")), prop(sub3QrySource, pi(ORG4, "id"))), isNotNull(prop(sub4QrySource, pi(ORG5, "key")))));
        final Conditions2 subQryConditions3 = or(and(eq(prop(sub3QrySource, pi(ORG4, "parent")), prop(sub2QrySource, pi(ORG3, "id"))), exists(sub4QrySources, subQryConditions4, ORG5)));
        final Conditions2 subQryConditions2 = or(and(eq(prop(sub2QrySource, pi(ORG3, "parent")), prop(sub1QrySource, pi(ORG2, "id"))), exists(sub3QrySources, subQryConditions3, ORG4)));
        final Conditions2 subQryConditions1 = or(and(eq(prop(sub1QrySource, pi(ORG2, "parent")), prop(source, pi(ORG1, "id"))), exists(sub2QrySources, subQryConditions2, ORG3)));
        
        final Conditions2 conditions = or(exists(sub1QrySources, subQryConditions1, ORG2));

        final EntQuery2 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }

    @Test
    public void test09() {
        final EntQuery2 actQry = qryCountAll(//
        select(ORG1).as("L1").where().exists( //
        select(ORG2).as("L2").where().prop("parent").eq().prop("L1").and().exists( //
        select(ORG3).as("L3").where().prop("parent").eq().prop("L2").and().exists( // 
        select(ORG4).as("L4").where().prop("parent").eq().prop("L3").and().exists( //
        select(ORG5).as("L5").where().prop("parent").eq().prop("L4").and().prop("key").isNotNull(). //
        model()). //
        model()). //
        model()). //
        model()));
        
        final QrySource2BasedOnPersistentType source = source("5", ORG1, "L1");
        final QrySource2BasedOnPersistentType sub1QrySource = source("4", ORG2, "L2");
        final QrySource2BasedOnPersistentType sub2QrySource = source("3", ORG3, "L3");
        final QrySource2BasedOnPersistentType sub3QrySource = source("2", ORG4, "L4");
        final QrySource2BasedOnPersistentType sub4QrySource = source("1", ORG5, "L5");

        final Sources2 sources = sources(source);
        final Sources2 sub1QrySources = sources(sub1QrySource);
        final Sources2 sub2QrySources = sources(sub2QrySource);
        final Sources2 sub3QrySources = sources(sub3QrySource);
        final Sources2 sub4QrySources = sources(sub4QrySource);


        final Conditions2 subQryConditions4 = or(and(eq(prop(sub4QrySource, pi(ORG5, "parent")), prop(sub3QrySource, pi(ORG4, "id"))), isNotNull(prop(sub4QrySource, pi(ORG5, "key")))));
        final Conditions2 subQryConditions3 = or(and(eq(prop(sub3QrySource, pi(ORG4, "parent")), prop(sub2QrySource, pi(ORG3, "id"))), exists(sub4QrySources, subQryConditions4, ORG5)));
        final Conditions2 subQryConditions2 = or(and(eq(prop(sub2QrySource, pi(ORG3, "parent")), prop(sub1QrySource, pi(ORG2, "id"))), exists(sub3QrySources, subQryConditions3, ORG4)));
        final Conditions2 subQryConditions1 = or(and(eq(prop(sub1QrySource, pi(ORG2, "parent")), prop(source, pi(ORG1, "id"))), exists(sub2QrySources, subQryConditions2, ORG3)));
        
        final Conditions2 conditions = or(exists(sub1QrySources, subQryConditions1, ORG2));

        final EntQuery2 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }

    @Test
    public void test10() {
        final EntQuery2 actQry = qryCountAll(//
        select(ORG1).as("L1").where().exists( //
        select(ORG2).as("L2").where().prop("parent").eq().extProp("L1.id").and().exists( //
        select(ORG3).as("L3").where().prop("parent").eq().prop("L1.id").and().exists( // 
        select(ORG4).as("L4").where().prop("parent").eq().extProp("L1").and().exists( //
        select(ORG5).as("L5").where().prop("parent").eq().prop("L1.id").and().prop("key").isNotNull(). //
        model()). //
        model()). //
        model()). //
        model()));
        
        final QrySource2BasedOnPersistentType source = source("5", ORG1, "L1");
        final QrySource2BasedOnPersistentType sub1QrySource = source("4", ORG2, "L2");
        final QrySource2BasedOnPersistentType sub2QrySource = source("3", ORG3, "L3");
        final QrySource2BasedOnPersistentType sub3QrySource = source("2", ORG4, "L4");
        final QrySource2BasedOnPersistentType sub4QrySource = source("1", ORG5, "L5");

        final Sources2 sources = sources(source);
        final Sources2 sub1QrySources = sources(sub1QrySource);
        final Sources2 sub2QrySources = sources(sub2QrySource);
        final Sources2 sub3QrySources = sources(sub3QrySource);
        final Sources2 sub4QrySources = sources(sub4QrySource);


        final Conditions2 subQryConditions4 = or(and(eq(prop(sub4QrySource, pi(ORG5, "parent")), prop(source, pi(ORG1, "id"))), isNotNull(prop(sub4QrySource, pi(ORG5, "key")))));
        final Conditions2 subQryConditions3 = or(and(eq(prop(sub3QrySource, pi(ORG4, "parent")), prop(source, pi(ORG1, "id"))), exists(sub4QrySources, subQryConditions4, ORG5)));
        final Conditions2 subQryConditions2 = or(and(eq(prop(sub2QrySource, pi(ORG3, "parent")), prop(source, pi(ORG1, "id"))), exists(sub3QrySources, subQryConditions3, ORG4)));
        final Conditions2 subQryConditions1 = or(and(eq(prop(sub1QrySource, pi(ORG2, "parent")), prop(source, pi(ORG1, "id"))), exists(sub2QrySources, subQryConditions2, ORG3)));
        
        final Conditions2 conditions = or(exists(sub1QrySources, subQryConditions1, ORG2));

        final EntQuery2 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }

    @Test
    public void test11() {
        final EntQuery2 actQry = qryCountAll(select(MODEL).where().prop("make").eq().iVal(null));
        
        final QrySource2BasedOnPersistentType source = source("1", MODEL);
        final Sources2 sources = sources(source);
        final EntQuery2 expQry = qryCountAll(sources);

        assertEquals(expQry, actQry);
    }
    
    @Test
    public void test12() {
        final EntQuery2 actQry = qryCountAll(select(ORG1).where().exists(select(ORG2).where().prop("parent").isNotNull().model()));  

        final QrySource2BasedOnPersistentType source1 = source("2", ORG1);
        final QrySource2BasedOnPersistentType source2 = source("1", ORG2);

        final Sources2 sources1 = sources(source1);
        final Sources2 sources2 = sources(source2);
        final Conditions2 conditions2 = or(isNotNull(prop(source2, pi(ORG2, "parent"))));
        final Conditions2 conditions1 = or(exists(sources2, conditions2, ORG2));

        final EntQuery2 expQry = qryCountAll(sources1, conditions1);

        assertEquals(expQry, actQry);
    }

    @Test
    public void test_05() {
        qryCountAll(select(AUTHOR).where().exists(select(TgAuthorRoyalty.class).where().prop("authorship.author").eq().extProp("id").model()));
    }

    @Test
    public void test_06() {
        qryCountAll(select(TgAuthorRoyalty.class).as("ar").where().exists(select(TgAuthorship.class).where().prop("id").eq().extProp("authorship").and().prop("author.surname").eq().val("Date").or().prop("author.name.key").eq().val("Chris").model()));
    }

    @Test
    public void test_07() {
        qryCountAll(select(select(TgAuthorship.class).where().prop("title").isNotNull().model()).where().prop("author.surname").eq().val("Date").or().prop("author.name.key").eq().val("Chris"));
    }

    @Test
    public void test_08() {
        qryCountAll(select(select(TgAuthorRoyalty.class).where().prop("authorship.author.surname").isNotNull().model()).as("ar").where().prop("authorship.author.surname").eq().val("Date").or().prop("ar.authorship.author.name.key").eq().val("Chris"));
    }

    @Test
    public void test_09() {
        qryCountAll(select(select(TgAuthorRoyalty.class).where().prop("authorship.author.surname").isNotNull().yield().prop("authorship.author").modelAsEntity(TgAuthorship.class)).where().prop("author.surname").eq().val("Date").or().prop("author.name.key").eq().val("Chris"));
    }

    @Test
    public void test_10() {
        qryCountAll(select(select(TgAuthorship.class).where().prop("title").isNotNull().yield().prop("author").as("author").yield().prop("title").as("bookTitle").modelAsAggregate()).where().prop("bookTitle").isNotNull().or().begin().prop("author.surname").eq().val("Date").or().prop("author.name.key").eq().val("Chris").end());
    }

    @Test
    public void test_11() {
        qryCountAll(select(TgAuthorship.class).where().beginExpr().val(100).mult().model(select(AUTHOR).yield().countAll().modelAsPrimitive()).endExpr().ge().val(1000));
    }

    @Test
    @Ignore
    public void test_13() {
        qryCountAll(select(TgAverageFuelUsage.class).where().prop("key.key").eq().val("CAR2"));
    }

    @Test
    public void test_14() {
        qryCountAll(select(AUTHOR).where().prop("hasMultiplePublications").eq().val(true));
    }

    @Test
    @Ignore
    public void test_15() {
        // TODO EQL.3
        qryCountAll(select(TgEntityWithLoopedCalcProps.class).where().prop("calc1").gt().val(25));
    }

    @Test
    public void test_16() {
        qryCountAll(select(AUTHOR).leftJoin(TgPersonName.class).as("pn").on().prop("name").eq().prop("pn").where().prop("lastRoyalty").eq().val(1));
    }

    @Test
    public void test_17() {
        //transform(select(AUTHOR).where().prop("name").isNotNull().groupBy().prop("name").yield().prop("name").modelAsEntity(TgPersonName.class));
    }

    @Test
    public void test_22() {
        qryCountAll(select(TgAuthorRoyalty.class).where().prop("payment").isNotNull());
    }

    @Test
    @Ignore
    public void test_23() {
        qryCountAll(select(TgAuthorRoyalty.class).where().prop("payment.amount").isNotNull());
    }

    @Test
    public void test_24() {
        qryCountAll(select(select(VEHICLE).yield().prop("key").as("key").yield().prop("desc").as("desc").yield().prop("model.make").as("model-make").modelAsAggregate()).where().prop("model-make").isNotNull());
    }
    
    @Test
    public void test_q21_s1s3() {
        final AggregatedResultQueryModel qry = select(VEHICLE).as("veh").leftJoin(VEHICLE).as("rbv").
                on().prop("veh.replacedBy").eq().prop("rbv.id").or().prop("veh.replacedBy").ne().prop("rbv.id").
                yield().prop("veh.key").as("vehicle-key").
                yield().prop("rbv.key").as("replacedByVehicle-key").
                modelAsAggregate();
//        final ua.com.fielden.platform.eql.stage2.elements.TransformationResult<EntQuery3> qry3 = entResultQry3(qry,  new PropsResolutionContext(metadata), tables);
//        System.out.println(qry3.item.sql(DbVersion.H2));
    }
    

    @Test
    @Ignore
    public void test_that_prop_name_is_without_alias_at_stage2() {
        final EntQuery2 actQry = qryCountAll(select(TgWorkshop.class).as("w").where().prop("w.key").isNotNull());

        final QrySource2BasedOnPersistentType source = new QrySource2BasedOnPersistentType(TgWorkshop.class, metadata.get(WORKSHOP), "w", "0");
        
        final List<List<? extends ICondition2<?>>> allConditions = new ArrayList<>();
        final List<ICondition2<?>> firstAndConditionsGroup = new ArrayList<>();
        // TODO make utility method for easy creation of Conditions2 with only one condition
        firstAndConditionsGroup.add(new NullTest2(new EntProp2(source, asList(pi(WORKSHOP, "key"))), true));
        allConditions.add(firstAndConditionsGroup);

        final Conditions2 conditions = new Conditions2(false, allConditions);
        final EntQueryBlocks2 parts = new EntQueryBlocks2(new Sources2(source, emptyList()), conditions, emptyYields2, emptyGroupBys2, emptyOrderBys2);
        final EntQuery2 exp = new EntQuery2(parts, TgWorkshop.class, RESULT_QUERY);
        assertEquals(exp, actQry);
    }

    @Test
    @Ignore
    public void test_q2() {
        final EntQuery2 actQry = qryCountAll(select(AUTHOR).where().prop("surname").isNotNull());

        final QrySource2BasedOnPersistentType source = new QrySource2BasedOnPersistentType(AUTHOR, metadata.get(AUTHOR), null, "0");
        final Sources2 sources = new Sources2(source, emptyList());
        final List<List<? extends ICondition2<?>>> allConditions = new ArrayList<>();
        final List<ICondition2<?>> firstAndConditionsGroup = new ArrayList<>();
        firstAndConditionsGroup.add(new NullTest2(new EntProp2(source, asList(pi(AUTHOR, "surname"))), true));
        allConditions.add(firstAndConditionsGroup);
        final Conditions2 conditions = new Conditions2(false, allConditions);

        final EntQueryBlocks2 parts = new EntQueryBlocks2(sources, conditions, emptyYields2, emptyGroupBys2, emptyOrderBys2);
        final EntQuery2 exp = new EntQuery2(parts, AUTHOR, RESULT_QUERY);
        
        assertEquals(exp, actQry);
    }
    
    @Test
    @Ignore
    public void test_q3() {
        final EntQuery2 actQry = qryCountAll(select(AUTHOR).as("a").where().prop("a.surname").isNotNull());

        final QrySource2BasedOnPersistentType source = new QrySource2BasedOnPersistentType(AUTHOR, metadata.get(AUTHOR), "a", "0");
        final Sources2 sources = new Sources2(source, Collections.<CompoundSource2> emptyList());
        final List<List<? extends ICondition2<?>>> allConditions = new ArrayList<>();
        final List<ICondition2<?>> firstAndConditionsGroup = new ArrayList<>();
        firstAndConditionsGroup.add(new NullTest2(new EntProp2(source, asList(pi(AUTHOR, "surname"))), true));
        allConditions.add(firstAndConditionsGroup);
        final Conditions2 conditions = new Conditions2(false, allConditions);

        final EntQueryBlocks2 parts = new EntQueryBlocks2(sources, conditions, emptyYields2, emptyGroupBys2, emptyOrderBys2);
        final EntQuery2 exp = new EntQuery2(parts, AUTHOR, RESULT_QUERY);

        assertEquals(exp, actQry); 
    }

    @Test
    @Ignore
    public void test_q4() {
        final EntQuery2 actQry = qryCountAll(select(AUTHOR).where().prop("surname").isNotNull().and().prop("name").eq().iVal(null));

        final QrySource2BasedOnPersistentType source = new QrySource2BasedOnPersistentType(AUTHOR, metadata.get(AUTHOR), null, "0");
        final Sources2 sources = new Sources2(source, Collections.<CompoundSource2> emptyList());
        final List<List<? extends ICondition2<?>>> allConditions = new ArrayList<>();
        final List<ICondition2<?>> firstAndConditionsGroup = new ArrayList<>();
        firstAndConditionsGroup.add(new NullTest2(new EntProp2(source, asList(pi(AUTHOR, "surname"))), true));
        allConditions.add(firstAndConditionsGroup);
        final Conditions2 conditions = new Conditions2(false, allConditions);

        final EntQueryBlocks2 parts = new EntQueryBlocks2(sources, conditions, emptyYields2, emptyGroupBys2, emptyOrderBys2);
        final EntQuery2 exp = new EntQuery2(parts, AUTHOR, RESULT_QUERY);

        assertEquals(exp, actQry); 
    }

    @Test
    @Ignore
    public void test_q5() {
        final EntQuery2 actQry = qryCountAll(select(AUTHOR).where().prop("surname").isNotNull().and().prop("name").eq().iParam("param"));

        final QrySource2BasedOnPersistentType source = new QrySource2BasedOnPersistentType(AUTHOR, metadata.get(AUTHOR), null, "0");
        final Sources2 sources = new Sources2(source, Collections.<CompoundSource2> emptyList());
        final List<List<? extends ICondition2<?>>> allConditions = new ArrayList<>();
        final List<ICondition2<?>> firstAndConditionsGroup = new ArrayList<>();
        firstAndConditionsGroup.add(new NullTest2(new EntProp2(source, asList(pi(AUTHOR, "surname"))), true));
        allConditions.add(firstAndConditionsGroup);
        final Conditions2 conditions = new Conditions2(false, allConditions);

        final EntQueryBlocks2 parts = new EntQueryBlocks2(sources, conditions, emptyYields2, emptyGroupBys2, emptyOrderBys2);
        final EntQuery2 exp = new EntQuery2(parts, AUTHOR, RESULT_QUERY);

        assertEquals(exp, actQry); 
    }

    @Test
    @Ignore
    public void test_q6() {
        final EntQuery2 actQry = qryCountAll(select(AUTHOR).where().prop("surname").eq().param("param"));
        final Map<String, Object> params = new HashMap<>();
        params.put("param", 1);

        final QrySource2BasedOnPersistentType source = new QrySource2BasedOnPersistentType(AUTHOR, metadata.get(AUTHOR), null, "0");
        final Sources2 sources = new Sources2(source, Collections.<CompoundSource2> emptyList());
        final List<List<? extends ICondition2<?>>> allConditions = new ArrayList<>();
        final List<ICondition2<?>> firstAndConditionsGroup = new ArrayList<>();
        firstAndConditionsGroup.add(new ComparisonTest2(new EntProp2(source, asList(pi(AUTHOR, "surname"))), EQ, new EntValue2(1)));
        allConditions.add(firstAndConditionsGroup);
        final Conditions2 conditions = new Conditions2(false, allConditions);

        final EntQueryBlocks2 parts = new EntQueryBlocks2(sources, conditions, emptyYields2, emptyGroupBys2, emptyOrderBys2);
        final EntQuery2 exp = new EntQuery2(parts, AUTHOR, RESULT_QUERY);

        assertEquals(exp, actQry); 
    }

    @Test
    @Ignore
    public void test_q7() {
        final EntQuery2 actQry = qryCountAll(select(AUTHOR).leftJoin(TgPersonName.class).as("pn").on().prop("name").eq().prop("pn.id"). //
        where().prop("surname").eq().val(1));

        final QrySource2BasedOnPersistentType source = new QrySource2BasedOnPersistentType(AUTHOR, metadata.get(AUTHOR), null, "0");
        final QrySource2BasedOnPersistentType source2 = new QrySource2BasedOnPersistentType(TgPersonName.class, metadata.get(TgPersonName.class), "pn", "0");

        final List<List<? extends ICondition2<?>>> allConditions = new ArrayList<>();
        final List<ICondition2<?>> firstAndConditionsGroup = new ArrayList<>();
        firstAndConditionsGroup.add(new ComparisonTest2(new EntProp2(source, asList(pi(AUTHOR, "name"))), //
                EQ, // 
                new EntProp2(source2, asList(metadata.get(TgPersonName.class).getProps().get("id")))));
        allConditions.add(firstAndConditionsGroup);
        final Conditions2 joinConditions = new Conditions2(false, allConditions);

        final CompoundSource2 compound = new CompoundSource2(source2, JoinType.LJ, joinConditions);
        final List<CompoundSource2> compounds = new ArrayList<>();
        compounds.add(compound);
        final Sources2 sources = new Sources2(source, compounds);
        final List<List<? extends ICondition2<?>>> allConditions2 = new ArrayList<>();
        final List<ICondition2<?>> firstAndConditionsGroup2 = new ArrayList<>();
        firstAndConditionsGroup2.add(new ComparisonTest2(new EntProp2(source, asList(pi(AUTHOR, "surname"))), EQ, new EntValue2(1)));
        allConditions2.add(firstAndConditionsGroup2);
        final Conditions2 conditions = new Conditions2(false, allConditions2);

        final EntQueryBlocks2 parts = new EntQueryBlocks2(sources, conditions, emptyYields2, emptyGroupBys2, emptyOrderBys2);
        final EntQuery2 exp = new EntQuery2(parts, AUTHOR, RESULT_QUERY);

        assertEquals(exp, actQry); 
    }

    @Test
    @Ignore
    public void test_q8() {
        final EntQuery2 actQry = qryCountAll(select(AUTHOR).leftJoin(TgPersonName.class).as("pn").on().prop("name").eq().prop("pn.id"). //
        where().prop("lastRoyalty").eq().val(1));

        final QrySource2BasedOnPersistentType sourceAuthor = new QrySource2BasedOnPersistentType(AUTHOR, metadata.get(AUTHOR), null, "0");

        final QrySource2BasedOnPersistentType sourceAuthorRoyalty = new QrySource2BasedOnPersistentType(TgAuthorRoyalty.class, metadata.get(TgAuthorRoyalty.class), null, "0");

        final List<List<? extends ICondition2<?>>> lrAllConditions2 = new ArrayList<>();
        final List<ICondition2<?>> lrFirstAndConditionsGroup2 = new ArrayList<>();
        lrFirstAndConditionsGroup2.add(new ComparisonTest2(new EntProp2(sourceAuthorRoyalty, asList(null)), EQ,
                new EntProp2(sourceAuthor, asList(null))));
        lrAllConditions2.add(lrFirstAndConditionsGroup2);
        final Conditions2 lrConditions = new Conditions2(false, lrAllConditions2);

        final EntQueryBlocks2 lastRoyaltyParts = new EntQueryBlocks2(new Sources2(sourceAuthorRoyalty, emptyList()), lrConditions, emptyYields2, new GroupBys2(Collections.<GroupBy2> emptyList()), new OrderBys2(null));
        final EntQuery2 lastRoyaltySubqry = new EntQuery2(lastRoyaltyParts, TgAuthorRoyalty.class, SUB_QUERY);

        final QrySource2BasedOnPersistentType sourcePersonName = new QrySource2BasedOnPersistentType(TgPersonName.class, metadata.get(TgPersonName.class), "pn", "0");

        final List<List<? extends ICondition2<?>>> allConditions = new ArrayList<>();
        final List<ICondition2<?>> firstAndConditionsGroup = new ArrayList<>();
        firstAndConditionsGroup.add(new ComparisonTest2(new EntProp2(sourceAuthor, asList(null)), EQ, new EntProp2(sourcePersonName, asList(null))));
        allConditions.add(firstAndConditionsGroup);
        final Conditions2 joinConditions = new Conditions2(false, allConditions);

        final CompoundSource2 compound = new CompoundSource2(sourcePersonName, LJ, joinConditions);
        final List<CompoundSource2> compounds = new ArrayList<>();
        compounds.add(compound);
        final Sources2 sources = new Sources2(sourceAuthor, compounds);
        final List<List<? extends ICondition2<?>>> allConditions2 = new ArrayList<>();
        final List<ICondition2<?>> firstAndConditionsGroup2 = new ArrayList<>();

        final Expression2 lrExpr = new Expression2(lastRoyaltySubqry, emptyList());
        firstAndConditionsGroup2.add(new ComparisonTest2(new EntProp2(sourceAuthor, asList(null)), EQ, new EntValue2(1)));
        allConditions2.add(firstAndConditionsGroup2);
        final Conditions2 conditions = new Conditions2(false, allConditions2);

        final EntQueryBlocks2 parts = new EntQueryBlocks2(sources, conditions, emptyYields2, emptyGroupBys2, emptyOrderBys2);
        final EntQuery2 exp = new EntQuery2(parts, AUTHOR, RESULT_QUERY);

        assertEquals(exp, actQry); 
    }
}