package ua.com.fielden.platform.basic.autocompleter;

import org.junit.Test;

import ua.com.fielden.platform.dao.IEntityAggregatesOperations;
import ua.com.fielden.platform.test.DbDrivenTestCase;
import ua.com.fielden.platform.test.entities.ComplexSyntheticEntity;
import ua.com.fielden.platform.test.entities.SimpleSyntheticEntity;

public class SyntheticEntityValueMatcherTestCase extends DbDrivenTestCase {

    @Test
    public void testSyntheticValueMatcherWithSimpleEntities() {
        hibernateUtil.getSessionFactory().getCurrentSession().close();
        final SyntheticEntityValueMatcher valueMatcher = new SyntheticEntityValueMatcher(injector.getInstance(IEntityAggregatesOperations.class), SimpleSyntheticEntity.class);

        assertEquals("The number of entities for %entity% param value must be 6", 6, valueMatcher.findMatches("%entity%").size());
        assertEquals("The number of entities for centity% param value must be 3", 3, valueMatcher.findMatches("centity%").size());
        assertEquals("The number of entities for sentity% param value must be 3", 3, valueMatcher.findMatches("sentity%").size());
        assertEquals("The number of entities for %other1 param value must be 2", 2, valueMatcher.findMatches("%other1").size());
        assertEquals("The number of entities for %entity1 param value must be 2", 2, valueMatcher.findMatches("%entity1").size());
    }

    @Test
    public void testSyntheticvalueMatcherWithComplexEntity() {
        hibernateUtil.getSessionFactory().getCurrentSession().close();
        final SyntheticEntityValueMatcher valueMatcher = new SyntheticEntityValueMatcher(injector.getInstance(IEntityAggregatesOperations.class), ComplexSyntheticEntity.class);

        assertEquals("The number of entities for %entity% param value must be 6", 6, valueMatcher.findMatches("%entity%").size());
        assertEquals("The number of entities for centity% param value must be 3", 3, valueMatcher.findMatches("centity%").size());
        assertEquals("The number of entities for sentity% param value must be 3", 3, valueMatcher.findMatches("sentity%").size());
        assertEquals("The number of entities for %other1 param value must be 2", 2, valueMatcher.findMatches("%other1").size());
        assertEquals("The number of entities for %entity1 param value must be 2", 2, valueMatcher.findMatches("%entity1").size());
    }

    @Override
    protected String[] getDataSetPathsForInsert() {
        return new String[] { "src/test/resources/data-files/SyntheticEntityValueMatcherTest.flat.xml" };
    }

}
