package ua.com.fielden.platform.eql.stage1;

import static org.junit.Assert.assertEquals;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;

import org.junit.Test;

import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.eql.meta.EqlStage1TestCase;
import ua.com.fielden.platform.eql.stage1.elements.EntQueryBlocks1;
import ua.com.fielden.platform.eql.stage1.elements.conditions.Conditions1;
import ua.com.fielden.platform.eql.stage1.elements.operands.ResultQuery1;
import ua.com.fielden.platform.eql.stage1.elements.sources.Sources1;
import ua.com.fielden.platform.sample.domain.TeVehicleModel;

public class QmToStage1TransformationTest extends EqlStage1TestCase {
    
    @Test
    public void test01() {
        final EntityResultQueryModel<TeVehicleModel> qry = select(MODEL).where().prop("make.key").isNotNull().model();
        
        final Sources1 sources1 = sources(MODEL);
        final Conditions1 conditions1 = conditions(isNotNull(prop("make.key")));
        final EntQueryBlocks1 parts1 = qb1(sources1, conditions1);
        final ResultQuery1 expQry1 = new ResultQuery1(parts1, MODEL, null);

        assertEquals(expQry1, resultQry(qry));
    }
    
    @Test
    public void test02() {
        final EntityResultQueryModel<TeVehicleModel> qry = select(MODEL).where().prop("make").isNotNull().model();
        
        final Sources1 sources1 = sources(MODEL);
        final Conditions1 conditions1 = conditions(isNotNull(prop("make")));
        final EntQueryBlocks1 parts1 = qb1(sources1, conditions1);
        final ResultQuery1 expQry1 = new ResultQuery1(parts1, MODEL, null);

        assertEquals(expQry1, resultQry(qry));
    }
}