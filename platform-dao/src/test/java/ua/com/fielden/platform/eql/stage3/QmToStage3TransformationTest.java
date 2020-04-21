package ua.com.fielden.platform.eql.stage3;


import static org.junit.Assert.assertEquals;
import static ua.com.fielden.platform.entity.AbstractEntity.ID;
import static ua.com.fielden.platform.entity.AbstractEntity.KEY;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;

import java.math.BigInteger;
import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Test;

import ua.com.fielden.platform.entity.query.DbVersion;
import ua.com.fielden.platform.entity.query.model.AggregatedResultQueryModel;
import ua.com.fielden.platform.entity.query.model.PrimitiveResultQueryModel;
import ua.com.fielden.platform.eql.meta.EqlStage3TestCase;
import ua.com.fielden.platform.eql.stage3.elements.Yield3;
import ua.com.fielden.platform.eql.stage3.elements.Yields3;
import ua.com.fielden.platform.eql.stage3.elements.conditions.Conditions3;
import ua.com.fielden.platform.eql.stage3.elements.operands.EntQuery3;
import ua.com.fielden.platform.eql.stage3.elements.operands.ISingleOperand3;
import ua.com.fielden.platform.eql.stage3.elements.sources.IQrySources3;
import ua.com.fielden.platform.eql.stage3.elements.sources.QrySource3BasedOnSubqueries;
import ua.com.fielden.platform.eql.stage3.elements.sources.QrySource3BasedOnTable;
import ua.com.fielden.platform.sample.domain.TeVehicle;
import ua.com.fielden.platform.sample.domain.TeVehicleModel;

public class QmToStage3TransformationTest extends EqlStage3TestCase {

    @Test
    public void yielding_entity_id_under_different_alias_preserves_entity_type_info() {
        final AggregatedResultQueryModel qry = select(TeVehicleModel.class).
                yield().prop("id").as("model").
                yield().prop("make").as("make").
                modelAsAggregate();
        
        final EntQuery3 actQry = qry(qry);
        
        final QrySource3BasedOnTable source = source(MODEL, "1");
        
        final Yield3 modelYield = yieldPropExpr("id", source, "model", MODEL, H_LONG);
        final Yield3 makeYield = yieldPropExpr("make", source, "make", MAKE, H_LONG);
        final Yields3 yields = yields(modelYield, makeYield);
        
        final EntQuery3 expQry = qry(sources(source), yields);
        assertEquals(expQry, actQry);
    }
    
    @Test
    public void correlated_source_query_works() {
        final AggregatedResultQueryModel sourceSubQry = select(TeVehicle.class).where().prop("model").eq().extProp("id").yield().countAll().as("qty").modelAsAggregate();
        final PrimitiveResultQueryModel qtySubQry = select(sourceSubQry).yield().prop("qty").modelAsPrimitive();
        final AggregatedResultQueryModel qry = select(TeVehicleModel.class).yield().model(qtySubQry).as("qty").modelAsAggregate();

        final EntQuery3 actQry = qry(qry);
        
        final QrySource3BasedOnTable modelSource = source(MODEL, "3");
        
        final QrySource3BasedOnTable vehSource = source(VEHICLE, "1");
        final IQrySources3 vehSources = sources(vehSource);
        final ISingleOperand3 vehModelProp = entityProp("model", vehSource, MODEL);
        final ISingleOperand3 modelIdProp = entityProp("id", modelSource, MODEL);
        final Conditions3 vehConditions = or(eq(expr(vehModelProp), expr(modelIdProp)));
        final Yields3 vehYields = yields(yieldCountAll("qty"));

        final EntQuery3 vehSourceSubQry = srcqry(vehSources, vehConditions, vehYields);
        
        final QrySource3BasedOnSubqueries qtyQrySource = source("2", vehSourceSubQry);
        final IQrySources3 qtyQrySources = sources(qtyQrySource);
        final Yields3 qtyQryYields = yields(yieldPropExpr("qty", qtyQrySource, "", BigInteger.class, H_BIG_INTEGER));
        
        final Yields3 modelQryYields = yields(yieldModel(subqry(qtyQrySources, qtyQryYields, BigInteger.class), "qty"));
        
        final EntQuery3 expQry = qry(sources(modelSource), modelQryYields);

        assertEquals(expQry, actQry);
    }
    
    @Test
    public void calc_prop_is_correctly_transformed_10() {
        final EntQuery3 actQry = qryCountAll(select(WORK_ORDER).where().anyOfProps("vehicle.modelMakeKey", "vehicle.model.make.key").isNotNull());
        final String wo1 = "1";
        final QrySource3BasedOnTable wo = source(WORK_ORDER, wo1);
        final QrySource3BasedOnTable veh = source(VEHICLE, wo1, "vehicle");
        final QrySource3BasedOnTable model = source(MODEL, wo1, "vehicle_model");
        final QrySource3BasedOnTable make = source(MAKE, wo1, "vehicle_model_make");
        final IQrySources3 sources = 
                lj(
                        wo,
                        ij(
                                veh,
                                ij(
                                        model,
                                        make,
                                        eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                                  ),
                                eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                          ),
                        eq(entityProp("vehicle", wo, VEHICLE), entityProp(ID, veh, VEHICLE))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(expr(stringProp(KEY, make)))), isNotNull(expr(stringProp(KEY, make))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }

    @Test
    public void calc_prop_is_correctly_transformed_13() {
        final EntQuery3 actQry = qryCountAll(select(WORK_ORDER).where().anyOfProps("vehicle.modelKey", "vehicle.model.key").isNotNull());
        final String wo1 = "1";
        
        final QrySource3BasedOnTable wo = source(WORK_ORDER, wo1);
        final QrySource3BasedOnTable veh = source(VEHICLE, wo1, "vehicle");
        final QrySource3BasedOnTable model = source(MODEL, wo1, "vehicle_model");
        
        final IQrySources3 sources = 
                lj(
                        wo,
                        ij(
                                veh,
                                model,
                                eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                          ),
                        eq(entityProp("vehicle", wo, VEHICLE), entityProp(ID, veh, VEHICLE))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(expr(stringProp(KEY, model)))), isNotNull(expr(stringProp(KEY, model))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);    
    }
    
    @Test
    @Ignore
    public void calc_prop_is_correctly_transformed_12() {
//        //protected static final ExpressionModel TgVehicle.modelMakeKey6_ = expr().model(select(TgVehicleModel.class).where().prop("id").eq().extProp("model").yield().prop("model.makeKey2").modelAsPrimitive()).model();
//        //protected static final ExpressionModel makeKey2_ = expr().model(select(TgVehicleMake.class).where().prop("id").eq().extProp("make").yield().prop(KEY).modelAsPrimitive()).model();

        final EntQuery3 actQry = qryCountAll(select(WORK_ORDER).where().anyOfProps("vehicle.modelMakeKey6").isNotNull());
        final String wo1 = "1";
        final QrySource3BasedOnTable wo = source(WORK_ORDER, wo1);
        final QrySource3BasedOnTable veh = source(VEHICLE, wo1, "vehicle");
        final QrySource3BasedOnTable model = source(MODEL, wo1, "vehicleModel");
        
        final IQrySources3 sources = 
                lj(
                        lj(
                                wo, 
                                veh, 
                                eq(prop("vehicle", wo), prop(ID, veh))
                          ),
                        model,
                        eq(expr(expr(prop("model", veh))), prop(ID, model))                
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(prop(KEY, model))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        assertEquals(expQry, actQry);
    }

    @Test
    @Ignore
    public void calc_prop_is_correctly_transformed_09() {
//        //protected static final ExpressionModel TgWorkOrder.makeKey2_ = expr().model(select(TgVehicle.class).where().prop("id").eq().extProp("vehicle").yield().prop("modelMakeKey4").modelAsPrimitive()).model();
//        //protected static final ExpressionModel TgVehicle.modelMakeKey4_ = expr().model(select(TgVehicleModel.class).where().prop("id").eq().extProp("model").yield().prop("model.makeKey2").modelAsPrimitive()).model();
//        //protected static final ExpressionModel makeKey2_ = expr().model(select(TgVehicleMake.class).where().prop("id").eq().extProp("make").yield().prop(KEY).modelAsPrimitive()).model();
        
        final EntQuery3 actQry = qryCountAll(select(WORK_ORDER).where().anyOfProps("makeKey2").isNotNull());
        final String wo1 = "1";
        
        final QrySource3BasedOnTable wo = source(WORK_ORDER, wo1);
        final QrySource3BasedOnTable veh = source(VEHICLE, wo1, "vehicle");
        final QrySource3BasedOnTable model = source(MODEL, wo1, "vehicleModel");
        
        final IQrySources3 sources = 
                lj(
                        lj(
                                wo, 
                                veh, 
                                eq(prop("vehicle", wo), prop(ID, veh))
                          ),
                        model,
                        eq(expr(expr(prop("model", veh))), prop(ID, model))                
                  );
        final Conditions3 conditions = or(isNotNull(expr(prop(KEY, model))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        assertEquals(expQry, actQry);
    }
    
    
    @Test
    public void calc_prop_is_correctly_transformed_08() {
        final EntQuery3 actQry = qryCountAll(select(WORK_ORDER).where().prop("make.key").isNotNull());
        final String wo1 = "1";

        final QrySource3BasedOnTable wo = source(WORK_ORDER, wo1);

        final QrySource3BasedOnTable veh = source(VEHICLE, wo, "make_1");
        final QrySource3BasedOnTable model = source(MODEL, veh, "model");
        final QrySource3BasedOnTable make = source(MAKE, wo1, "make");
        
        final IQrySources3 subQrySources = 
                ij(
                        veh,  
                        model, 
                        eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                  );
        
        final Conditions3 subQryConditions = cond(eq(expr(entityProp(ID, veh, VEHICLE)), expr(entityProp("vehicle", wo, VEHICLE))));
        
        final EntQuery3 expSubQry = subqry(subQrySources, subQryConditions, yields(yieldSingleExpr("make", model, MAKE)), MAKE);

        final IQrySources3 sources = 
                lj(
                        wo,
                        make,
                        eq(expr(expSubQry), entityProp(ID, make, MAKE))                
                  );
        final Conditions3 conditions = or(isNotNull(expr(stringProp(KEY, make))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }
    
    @Test
    public void calc_prop_is_correctly_transformed_07() {
        final EntQuery3 actQry = qryCountAll(select(WORK_ORDER).where().prop("make").isNotNull());
        final String wo1 = "1";

        final QrySource3BasedOnTable wo = source(WORK_ORDER, wo1);

        final QrySource3BasedOnTable veh = source(VEHICLE, wo, "make_1");
        final QrySource3BasedOnTable model = source(MODEL, veh, "model");
        
        final IQrySources3 subQrySources = 
                ij(
                        veh,  
                        model, 
                        eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                  );
        
        final Conditions3 subQryConditions = cond(eq(expr(entityProp(ID, veh, VEHICLE)), expr(entityProp("vehicle", wo, VEHICLE))));
        
        final EntQuery3 expSubQry = subqry(subQrySources, subQryConditions, yields(yieldSingleExpr("make", model, MAKE)), MAKE);

        final IQrySources3 sources = sources(wo);
        final Conditions3 conditions = or(isNotNull(expr(expSubQry)));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }

    @Test
    public void calc_prop_is_correctly_transformed_06() {
        final EntQuery3 actQry = qryCountAll(select(WORK_ORDER).where().prop("makeKey").isNotNull());
        final String wo1 = "1";
        
        final QrySource3BasedOnTable wo = source(WORK_ORDER, wo1);

        final QrySource3BasedOnTable veh = source(VEHICLE, wo1, "makeKey_1");
        final QrySource3BasedOnTable model = source(MODEL, veh, "model");
        final QrySource3BasedOnTable make = source(MAKE, veh, "model_make");
        
        final IQrySources3 subQrySources = 
                ij(
                        veh,  
                        ij(
                                model, 
                                make, 
                                eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                          ), 
                        eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                  );
        
        final Conditions3 subQryConditions = cond(eq(expr(entityProp(ID, veh, VEHICLE)), expr(entityProp("vehicle", wo, VEHICLE))));
        
        final EntQuery3 expSubQry = subqry(subQrySources, subQryConditions, yields(yieldSingleStringExpr(KEY, make)), String.class);

        final IQrySources3 sources = sources(wo);
        final Conditions3 conditions = or(isNotNull(expr(expSubQry)));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
        System.out.println(actQry.sql(DbVersion.H2));
    }
    
    @Test
    public void calc_prop_is_correctly_transformed_05() {
        final EntQuery3 actQry = qryCountAll(select(WORK_ORDER).where().prop("vehicleModel.key").isNotNull());
        final String wo1 = "1";
        
        final QrySource3BasedOnTable wo = source(WORK_ORDER, wo1);
        final QrySource3BasedOnTable veh = source(VEHICLE, wo1, "vehicle");
        final QrySource3BasedOnTable model = source(MODEL, wo1, "vehicleModel");
        
        final IQrySources3 sources = 
                lj(
                        lj(
                                wo, 
                                veh, 
                                eq(entityProp("vehicle", wo, VEHICLE), entityProp(ID, veh, VEHICLE))
                          ),
                        model,
                        eq(expr(expr(entityProp("model", veh, MODEL))), entityProp(ID, model, MODEL))                
                  );
        final Conditions3 conditions = or(isNotNull(expr(stringProp(KEY, model))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
        System.out.println(actQry.sql(DbVersion.H2));
    }

    @Test
    public void calc_prop_is_correctly_transformed_04() {
        final EntQuery3 actQry = qryCountAll(select(WORK_ORDER).where().prop("vehicleModel.makeKey").isNotNull());
        final String wo1 = "1";
        
        final QrySource3BasedOnTable wo = source(WORK_ORDER, wo1);
        final QrySource3BasedOnTable veh = source(VEHICLE, wo1, "vehicle");
        final QrySource3BasedOnTable model = source(MODEL, wo1, "vehicleModel");
        final QrySource3BasedOnTable make = source(MAKE, wo1, "vehicleModel_make");
        
        final IQrySources3 sources = 
                lj(
                        lj(
                                wo,
                                veh,
                                eq(entityProp("vehicle", wo, VEHICLE), entityProp(ID, veh, VEHICLE))
                          ),  
                        ij(
                                model,
                                make,
                                eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                          ), 
                        eq(expr(expr(entityProp("model", veh, MODEL))), entityProp(ID, model, MODEL))                
                  );
        final Conditions3 conditions = or(isNotNull(expr(expr(stringProp(KEY, make)))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
        System.out.println(actQry.sql(DbVersion.H2));
        
    }

    @Test
    public void calc_prop_is_correctly_transformed_03() {
        final EntQuery3 actQry = qryCountAll(select(WORK_ORDER).where().prop("vehicle.modelMakeKey2").isNotNull());
        final String wo1 = "1";
        
        final QrySource3BasedOnTable wo = source(WORK_ORDER, wo1);
        final QrySource3BasedOnTable veh = source(VEHICLE, wo1, "vehicle");
        final QrySource3BasedOnTable model = source(MODEL, wo1, "vehicle_model");
        final QrySource3BasedOnTable make = source(MAKE, wo1, "vehicle_model_make");
        
        final IQrySources3 sources = 
                lj(
                        wo,
                        ij(
                                veh,  
                                ij(
                                        model,
                                        make,
                                        eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                                  ), 
                                eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                          ),
                        eq(entityProp("vehicle", wo, VEHICLE), entityProp(ID, veh, VEHICLE))
                  );
        final Conditions3 conditions = or(isNotNull(expr(expr(expr(stringProp(KEY, make))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }

    @Test
    public void calc_prop_is_correctly_transformed_02() {
        final EntQuery3 actQry = qryCountAll(select(WORK_ORDER).where().prop("vehicle.modelMakeKey").isNotNull());
        final String wo1 = "1";
        
        final QrySource3BasedOnTable wo = source(WORK_ORDER, wo1);
        final QrySource3BasedOnTable veh = source(VEHICLE, wo1, "vehicle");
        final QrySource3BasedOnTable model = source(MODEL, wo1, "vehicle_model");
        final QrySource3BasedOnTable make = source(MAKE, wo1, "vehicle_model_make");
        
        final IQrySources3 sources = 
                lj(
                        wo,
                        ij(
                                veh,
                                ij(
                                        model,
                                        make,
                                        eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                                  ),
                                eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                          ),
                        eq(entityProp("vehicle", wo, VEHICLE), entityProp(ID, veh, VEHICLE))
                  );
        final Conditions3 conditions = or(isNotNull(expr(expr(stringProp(KEY, make)))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }
    
    @Test
    public void veh_calc_prop_is_correctly_transformed_08() {
        final EntQuery3 actQry = qryCountAll(select(VEHICLE).where().anyOfProps("modelMakeKey2", "make.key", "model.make.key").isNotNull());
        final String veh1 = "1";
        
        final QrySource3BasedOnTable veh = source(VEHICLE, veh1);
        final QrySource3BasedOnTable model = source(MODEL, veh1, "model");
        final QrySource3BasedOnTable makeA = source(MAKE, veh1, "make");
        final QrySource3BasedOnTable make = source(MAKE, veh1, "model_make");
        
        final IQrySources3 sources =
                ij(
                        lj(
                                veh,
                                makeA,
                                eq(entityProp("make", veh, MAKE), entityProp(ID, makeA, MAKE))
                          ),
                        ij(
                                model,
                                make,
                                eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                          ),
                        eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(expr(expr(stringProp(KEY, make))))), isNotNull(expr(stringProp(KEY, makeA))), isNotNull(expr(stringProp(KEY, make))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);   
    }

    @Test
    public void veh_calc_prop_is_correctly_transformed_07() {
        final EntQuery3 actQry = qryCountAll(select(VEHICLE).where().anyOfProps("modelMakeKey2", "make.key").isNotNull());
        final String veh1 = "1";
        
        final QrySource3BasedOnTable veh = source(VEHICLE, veh1);
        final QrySource3BasedOnTable model = source(MODEL, veh1, "model");
        final QrySource3BasedOnTable make = source(MAKE, veh1, "model_make");
        final QrySource3BasedOnTable makeA = source(MAKE, veh1, "make");
        
        final IQrySources3 sources =
                ij(
                        lj(
                                veh,
                                makeA,
                                eq(entityProp("make", veh, MAKE), entityProp(ID, makeA, MAKE))
                          ),
                        ij(
                                model,
                                make,
                                eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                          ),
                        eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(expr(expr(stringProp(KEY, make))))), isNotNull(expr(stringProp(KEY, makeA))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }
    
    @Test
    public void veh_calc_prop_is_correctly_transformed_06() {
        final EntQuery3 actQry = qryCountAll(select(VEHICLE).where().anyOfProps("modelMakeKey2", "make.key").isNotNull());
        final String veh1 = "1";
        
        final QrySource3BasedOnTable veh = source(VEHICLE, veh1);
        final QrySource3BasedOnTable model = source(MODEL, veh1, "model");
        final QrySource3BasedOnTable makeA = source(MAKE, veh1, "make");
        final QrySource3BasedOnTable make = source(MAKE, veh1, "model_make");
        
        final IQrySources3 sources =
                ij(
                        lj(
                                veh,
                                makeA,
                                eq(entityProp("make", veh, MAKE), entityProp(ID, makeA, MAKE))
                          ),
                        ij(
                                model,
                                make,
                                eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                          ),
                        eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(expr(expr(stringProp(KEY, make))))), isNotNull(expr(stringProp(KEY, makeA))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }
    
    @Test
    public void veh_calc_prop_is_correctly_transformed_05() {
        final EntQuery3 actQry = qryCountAll(select(VEHICLE).where().anyOfProps("model.make.key", "make.key").isNotNull());
        final String veh1 = "1";
        
        final QrySource3BasedOnTable veh = source(VEHICLE, veh1);
        final QrySource3BasedOnTable model = source(MODEL, veh1, "model");
        final QrySource3BasedOnTable make = source(MAKE, veh1, "model_make");
        final QrySource3BasedOnTable makeA = source(MAKE, veh1, "make");
        
        final IQrySources3 sources = 
                ij(
                        lj(
                                veh,
                                makeA,
                                eq(entityProp("make", veh, MAKE), entityProp(ID, makeA, MAKE))
                          ),
                        ij(
                                model,
                                make,
                                eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                          ),
                        eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(stringProp(KEY, make))), isNotNull(expr(stringProp(KEY, makeA))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);    
    }
    
    @Test
    public void veh_calc_prop_is_correctly_transformed_04() {
        final EntQuery3 actQry = qryCountAll(select(VEHICLE).where().anyOfProps("modelMakeKey2", "model.make.key").isNotNull());
        final String veh1 = "1";
        
        final QrySource3BasedOnTable veh = source(VEHICLE, veh1);
        final QrySource3BasedOnTable model = source(MODEL, veh1, "model");
        final QrySource3BasedOnTable make = source(MAKE, veh1, "model_make");
        
        final IQrySources3 sources =
                ij(
                        veh,
                        ij(
                                model,
                                make,
                                eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                          ),
                        eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(expr(expr(stringProp(KEY, make))))), isNotNull(expr(stringProp(KEY, make))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }
    
    @Test
    public void veh_calc_prop_is_correctly_transformed_03() {
        final EntQuery3 actQry = qryCountAll(select(VEHICLE).where().anyOfProps("modelMakeKey", "modelMakeDesc").isNotNull());
        final String veh1 = "1";
        
        final QrySource3BasedOnTable veh = source(VEHICLE, veh1);
        final QrySource3BasedOnTable model = source(MODEL, veh1, "model");
        final QrySource3BasedOnTable make = source(MAKE, veh1, "model_make");
        
        final IQrySources3 sources = 
                ij(
                        veh,
                        ij(
                                model,
                                make,
                                eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                          ),
                        eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(expr(stringProp(KEY, make)))), isNotNull(expr(expr(stringProp("desc", make)))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }
    
    @Test
    public void veh_calc_prop_is_correctly_transformed_02() {
        final EntQuery3 actQry = qryCountAll(select(VEHICLE).where().anyOfProps("modelKey", "modelDesc").isNotNull());
        final String veh1 = "1";
        
        final QrySource3BasedOnTable veh = source(VEHICLE, veh1);
        final QrySource3BasedOnTable model = source(MODEL, veh1, "model");
        
        final IQrySources3 sources = 
                ij(
                        veh,
                        model,
                        eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(expr(stringProp(KEY, model)))), isNotNull(expr(expr(stringProp("desc", model)))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }

    @Test
    public void veh_calc_prop_is_correctly_transformed_01() {
        final EntQuery3 actQry = qryCountAll(select(VEHICLE).where().prop("modelMakeKey").isNotNull());
        final String veh1 = "1";
        
        final QrySource3BasedOnTable veh = source(VEHICLE, veh1);
        final QrySource3BasedOnTable model = source(MODEL, veh1, "model");
        final QrySource3BasedOnTable make = source(MAKE, veh1, "model_make");
        
        final IQrySources3 sources = 
                ij(
                        veh,
                        ij(
                                model,
                                make,
                                eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                          ),
                        eq(entityProp("model", veh, MODEL), entityProp(ID, model, MODEL))
                  );
        final Conditions3 conditions = or(isNotNull(expr(expr(stringProp(KEY, make)))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }


    @Test
    public void veh_model_calc_prop_is_correctly_transformed_05() {
        final EntQuery3 actQry = qryCountAll(select(MODEL).where().anyOfProps("makeKey", "makeKey2", "make.key").isNotNull());
        final String model1 = "1";
        
        final QrySource3BasedOnTable model = source(MODEL, model1);
        final QrySource3BasedOnTable make = source(MAKE, model1, "make");
        
        final QrySource3BasedOnTable subQryMake = source(MAKE, model1, "makeKey2_1");
        
        final IQrySources3 subQrySources = sources(subQryMake);
        
        final Conditions3 subQryConditions = cond(eq(expr(entityProp(ID, subQryMake, MAKE)), expr(entityProp("make", model, MAKE))));
        
        final EntQuery3 expSubQry = subqry(subQrySources, subQryConditions, yields(yieldSingleStringExpr(KEY, subQryMake)), String.class);

        
        final IQrySources3 sources = 
                ij(
                        model,
                        make,
                        eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(expr(stringProp(KEY, make)))), isNotNull(expr(expSubQry)), isNotNull(expr(stringProp(KEY, make))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }

    @Test
    public void veh_model_calc_prop_is_correctly_transformed_04() {
        final EntQuery3 actQry = qryCountAll(select(MODEL).where().anyOfProps("makeKey", "makeKey2").isNotNull());
        final String model1 = "1";
        
        final QrySource3BasedOnTable model = source(MODEL, model1);
        final QrySource3BasedOnTable make = source(MAKE, model1, "make");
        
        final QrySource3BasedOnTable subQryMake = source(MAKE, model1, "makeKey2_1");
        
        final IQrySources3 subQrySources = sources(subQryMake);
        
        final Conditions3 subQryConditions = cond(eq(expr(entityProp(ID, subQryMake, MAKE)), expr(entityProp("make", model, MAKE))));
        
        final EntQuery3 expSubQry = subqry(subQrySources, subQryConditions, yields(yieldSingleStringExpr(KEY, subQryMake)), String.class);

        
        final IQrySources3 sources = 
                ij(
                        model,
                        make,
                        eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(expr(stringProp(KEY, make)))), isNotNull(expr(expSubQry)))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }

    @Test
    public void veh_model_calc_prop_is_correctly_transformed_03() {
        final EntQuery3 actQry = qryCountAll(select(MODEL).where().anyOfProps("makeKey", "make.key").isNotNull());
        final String model1 = "1";
        
        final QrySource3BasedOnTable model = source(MODEL, model1);
        final QrySource3BasedOnTable make = source(MAKE, model1, "make");
        
        final IQrySources3 sources = 
                ij(
                        model,
                        make,
                        eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(expr(stringProp(KEY, make)))), isNotNull(expr(stringProp(KEY, make))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }
    
    @Test
    public void veh_model_calc_prop_is_correctly_transformed_02() {
        final EntQuery3 actQry = qryCountAll(select(MODEL).where().prop("makeKey2").isNotNull());
        final String model1 = "1";

        final QrySource3BasedOnTable model = source(MODEL, model1);

        final QrySource3BasedOnTable subQryMake = source(MAKE, model1, "makeKey2_1");
        
        final IQrySources3 subQrySources = sources(subQryMake);
        
        final Conditions3 subQryConditions = cond(eq(expr(entityProp(ID, subQryMake, MAKE)), expr(entityProp("make", model, MAKE))));
        
        final EntQuery3 expSubQry = subqry(subQrySources, subQryConditions, yields(yieldSingleStringExpr(KEY, subQryMake)), String.class);

        final IQrySources3 sources = sources(model);
        final Conditions3 conditions = or(isNotNull(expr(expSubQry)));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }

    @Test
    public void veh_model_calc_prop_is_correctly_transformed_01() {
        final EntQuery3 actQry = qryCountAll(select(MODEL).where().prop("makeKey").isNotNull());
        final String model1 = "1";
        
        final QrySource3BasedOnTable model = source(MODEL, model1);
        final QrySource3BasedOnTable make = source(MAKE, model1, "make");
        
        final IQrySources3 sources = 
                ij(
                        model,
                        make,
                        eq(entityProp("make", model, MAKE), entityProp(ID, make, MAKE))
                  );
        final Conditions3 conditions = or(isNotNull(expr(expr(stringProp(KEY, make)))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }

    @Test
    public void dot_notated_props_are_correctly_transformed_01() {
        final EntQuery3 actQry = qryCountAll(select(VEHICLE).where().anyOfProps(KEY, "replacedBy.key").isNotNull());
        final String veh1 = "1"; 
        
        final QrySource3BasedOnTable veh = source(VEHICLE, veh1);
        final QrySource3BasedOnTable repVeh = source(VEHICLE, veh1, "replacedBy");
        
        final IQrySources3 sources = 
                lj(
                        veh,
                        repVeh,
                        eq(entityProp("replacedBy", veh, VEHICLE), entityProp(ID, repVeh, VEHICLE))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(stringProp(KEY, veh))), isNotNull(expr(stringProp(KEY, repVeh))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }

    @Test
    public void dot_notated_props_are_correctly_transformed_02() {
        final EntQuery3 actQry = qryCountAll(select(VEHICLE).where().anyOfProps("initDate", "station.name", "station.parent.name", "replacedBy.initDate").isNotNull());
        final String veh1 = "1";
        
        final QrySource3BasedOnTable veh = source(VEHICLE, veh1);
        final QrySource3BasedOnTable repVeh = source(VEHICLE, veh1, "replacedBy");
        final QrySource3BasedOnTable org5 = source(ORG5, veh1, "station");
        final QrySource3BasedOnTable org4 = source(ORG4, veh1, "station_parent");
        
        final IQrySources3 sources = 
                lj(
                        lj(
                                veh,
                                repVeh,
                                eq(entityProp("replacedBy", veh, VEHICLE), entityProp(ID, repVeh, VEHICLE))
                          ),
                        ij(
                                org5,
                                org4,
                                eq(entityProp("parent", org5, ORG4), entityProp(ID, org4, ORG4))
                          ),
                        eq(entityProp("station", veh, ORG5), entityProp(ID, org5, ORG5))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(dateProp("initDate", veh))), isNotNull(expr(stringProp("name", org5))), isNotNull(expr(stringProp("name", org4))), isNotNull(expr(dateProp("initDate", repVeh))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);
        
        assertEquals(expQry, actQry);
    }
    
    @Test
    public void dot_notated_props_are_correctly_transformed_03() {
        final EntQuery3 actQry = qryCountAll(select(VEHICLE).as("veh").join(ORG5).as("ou5e").on().prop("veh.station").eq().prop("ou5e.id").where().anyOfProps("veh.key", "veh.replacedBy.key").isNotNull());
        final String veh1 = "1";
        final String ou5e1 = "2";
        
        final QrySource3BasedOnTable veh = source(VEHICLE, veh1);
        final QrySource3BasedOnTable repVeh = source(VEHICLE, veh1, "replacedBy");
        final QrySource3BasedOnTable ou5e = source(ORG5, ou5e1);

        final IQrySources3 sources = 
                ij(
                        lj(
                                veh,
                                repVeh,
                                eq(entityProp("replacedBy", veh, VEHICLE), entityProp(ID, repVeh, VEHICLE))
                          ),
                        ou5e,
                        eq(expr(entityProp("station", veh, ORG5)), expr(entityProp(ID, ou5e, ORG5)))
                  );
        final Conditions3 conditions = or(and(or(isNotNull(expr(stringProp(KEY, veh))), isNotNull(expr(stringProp(KEY, repVeh))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }
    
    @Test
    public void dot_notated_props_are_correctly_transformed_04() {
        final EntQuery3 actQry = qryCountAll(select(VEHICLE).as("veh").join(ORG5).as("ou5e").on().prop("station").eq().prop("ou5e.id").
                where().anyOfProps("veh.key", "replacedBy.key", "initDate", "station.name", "station.parent.name", "ou5e.parent.name").isNotNull());
        final String veh1 = "1";
        final String ou5e1 = "2";

        final QrySource3BasedOnTable veh = source(VEHICLE, veh1);
        final QrySource3BasedOnTable repVeh = source(VEHICLE, veh1, "replacedBy");
        final QrySource3BasedOnTable ou5e = source(ORG5, ou5e1);
        final QrySource3BasedOnTable ou5eou4 = source(ORG4, ou5e1, "parent");
        final QrySource3BasedOnTable ou5 = source(ORG5, veh1, "station");
        final QrySource3BasedOnTable ou4 = source(ORG4, veh1, "station_parent");

        final IQrySources3 sources = 
                ij(
                        lj(
                                lj(
                                        veh,
                                        repVeh,
                                        eq(entityProp("replacedBy", veh, VEHICLE), entityProp(ID, repVeh, VEHICLE))
                                  ),
                                ij(
                                        ou5,
                                        ou4,
                                        eq(entityProp("parent", ou5, ORG4), entityProp(ID, ou4, ORG4))
                                  ),
                                eq(entityProp("station", veh, ORG5), entityProp(ID, ou5, ORG5))
                          ),
                        ij(
                                ou5e,
                                ou5eou4,
                                eq(entityProp("parent", ou5e, ORG4), entityProp(ID, ou5eou4, ORG4))
                          ),
                        eq(expr(entityProp("station", veh, ORG5)), expr(entityProp(ID, ou5e, ORG5)))
                  );
        final Conditions3 conditions = or(and(or(
                isNotNull(expr(stringProp(KEY, veh))),
                isNotNull(expr(stringProp(KEY, repVeh))), 
                isNotNull(expr(dateProp("initDate", veh))), 
                isNotNull(expr(stringProp("name", ou5))), 
                isNotNull(expr(stringProp("name", ou4))),
                isNotNull(expr(stringProp("name", ou5eou4))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }

    @Test
    public void dot_notated_props_are_correctly_transformed_05() {
        final EntQuery3 actQry = qryCountAll(select(VEHICLE).
                join(ORG2).as("ou2e").on().prop("station.parent.parent.parent").eq().prop("ou2e.id").
                where().anyOfProps("initDate", "replacedBy.initDate", "station.name", "station.parent.name", "ou2e.parent.key").isNotNull());
        final String veh1 = "1";
        final String ou2e1 = "2";
        
        final QrySource3BasedOnTable veh = source(VEHICLE, veh1);
        final QrySource3BasedOnTable repVeh = source(VEHICLE, veh1, "replacedBy");
        final QrySource3BasedOnTable ou5 = source(ORG5, veh1, "station");
        final QrySource3BasedOnTable ou4 = source(ORG4, veh1, "station_parent");
        final QrySource3BasedOnTable ou3 = source(ORG3, veh1, "station_parent_parent");
        final QrySource3BasedOnTable ou2e = source(ORG2, ou2e1);
        final QrySource3BasedOnTable ou2eou1 = source(ORG1, ou2e1, "parent");

        final IQrySources3 sources = 
                ij(
                        lj(
                                lj(
                                        veh,
                                        repVeh,
                                        eq(entityProp("replacedBy", veh, VEHICLE), entityProp(ID, repVeh, VEHICLE))
                                  ),
                                ij(
                                        ou5,
                                        ij(
                                                ou4,
                                                ou3,
                                                eq(entityProp("parent", ou4, ORG3), entityProp(ID, ou3, ORG3))
                                          ),
                                        eq(entityProp("parent", ou5, ORG4), entityProp(ID, ou4, ORG4))
                                  ),
                                eq(entityProp("station", veh, ORG5), entityProp(ID, ou5, ORG5))
                          ),
                        ij(
                                ou2e,
                                ou2eou1,
                                eq(entityProp("parent", ou2e, ORG1), entityProp(ID, ou2eou1, ORG1))
                          ),
                        eq(expr(entityProp("parent", ou3, ORG2)), expr(entityProp(ID, ou2e, ORG2)))
                  );
        final Conditions3 conditions = or(and(or(
                isNotNull(expr(dateProp("initDate", veh))),
                isNotNull(expr(dateProp("initDate", repVeh))), 
                isNotNull(expr(stringProp("name", ou5))), 
                isNotNull(expr(stringProp("name", ou4))),
                isNotNull(expr(stringProp(KEY, ou2eou1))))));
        final EntQuery3 expQry = qryCountAll(sources, conditions);

        assertEquals(expQry, actQry);
    }
    
    @Test
    public void condition_is_correctly_ignored_01() {
        final EntQuery3 actQry = qryCountAll(select(MODEL).where().prop(KEY).eq().iVal(null));
        final String model1 = "1";

        final QrySource3BasedOnTable model = source(MODEL, model1);

        final IQrySources3 sources = sources(model);
        final EntQuery3 expQry = qryCountAll(sources);
        
        assertEquals(expQry, actQry);
    }
    
    @Test
    public void condition_is_correctly_ignored_02() {
        final HashMap<String,Object> paramValues = new HashMap<String, Object>();
        paramValues.put(KEY, null);
        
        final EntQuery3 actQry = qryCountAll(select(MODEL).where().prop(KEY).eq().iParam("keyValue"), paramValues);
        final String model1 = "1";

        final QrySource3BasedOnTable model = source(MODEL, model1);

        final IQrySources3 sources = sources(model);
        final EntQuery3 expQry = qryCountAll(sources);
        
        assertEquals(expQry, actQry);
    }
 }