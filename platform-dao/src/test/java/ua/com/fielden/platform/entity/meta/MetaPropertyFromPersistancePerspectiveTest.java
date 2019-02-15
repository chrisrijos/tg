package ua.com.fielden.platform.entity.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;

import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.persistence.composite.EntityWithDynamicCompositeKey;
import ua.com.fielden.platform.persistence.types.EntityWithMoney;
import ua.com.fielden.platform.test_config.AbstractDaoTestCase;
import ua.com.fielden.platform.types.Money;

/**
 * A test case for testing essential {@link MetaProperty} functionality from perspective of a persisted entity.
 * 
 * @author TG Team
 * 
 */
public class MetaPropertyFromPersistancePerspectiveTest extends AbstractDaoTestCase {

    @Test
    public void all_properties_should_be_not_dirty_and_marked_as_assigned() {
        final EntityWithMoney entity = co$(EntityWithMoney.class).findByKey("key1");
        assertFalse(entity.getProperty("money").isDirty());
        assertTrue(entity.getProperty("money").isAssigned());
    }

    @Test
    public void original_value_for_property_should_not_be_null() {
        final EntityWithMoney entity = co$(EntityWithMoney.class).findByKey("key1");
        assertNotNull(entity.getProperty("money").getValue());
        assertNotNull(entity.getProperty("money").getOriginalValue());
    }

    @Test
    public void persisted_entities_shoul_have_original_values_of_their_properties_unchanged_regardless_of_property_changes() {
        final EntityWithMoney entity = co$(EntityWithMoney.class).findByKey("key1");
        assertEquals(new Money("20.00"), entity.getProperty("money").getOriginalValue());
        entity.setMoney(new Money("30.00"));
        assertEquals(new Money("30.00"), entity.getProperty("money").getValue());
        assertEquals(new Money("20.00"), entity.getProperty("money").getOriginalValue());

        entity.setMoney(new Money("40.00"));
        assertEquals(new Money("40.00"), entity.getProperty("money").getValue());
        assertEquals(new Money("20.00"), entity.getProperty("money").getOriginalValue());
    }

    @Test
    public void last_attempted_value_should_match_original() {
        final EntityWithMoney entity = co$(EntityWithMoney.class).findByKey("key1");
        assertEquals(new Money("20.00"), entity.getProperty("money").getLastAttemptedValue());
        assertEquals(new Money("20.00"), entity.getProperty("money").getOriginalValue());
    }

    @Test
    public void resetting_invalid_property_changes_should_null_out_last_invalid_value_information() {
        final EntityWithMoney entity = co$(EntityWithMoney.class).findByKey("key1");
        entity.setMoney(null);
        assertNull(entity.getProperty("money").getLastAttemptedValue());
        assertNull(entity.getProperty("money").getLastInvalidValue());

        entity.setMoney(new Money("30.00"));
        assertEquals(new Money("30.00"), entity.getProperty("money").getLastAttemptedValue());
        assertNull(entity.getProperty("money").getLastInvalidValue());
    }

    @Override
    protected void populateDomain() {
        super.populateDomain();
        
        final EntityWithMoney ewm1 = save(new_(EntityWithMoney.class, "key1", "desc").setMoney(new Money("20.00")).setDateTimeProperty((new DateTime("2009-03-01T11:00:55Z")).toDate()));
        save(new_(EntityWithMoney.class, "key2", "desc").setMoney(new Money("30.00")));
        save(new_(EntityWithMoney.class, "key3", "desc").setMoney(new Money("40.00")).setDateTimeProperty((new DateTime("2009-03-01T00:00:00Z")).toDate()));
        save(new_(EntityWithMoney.class, "key4", "desc").setMoney(new Money("50.00")).setDateTimeProperty((new DateTime("2009-03-01T10:00:00Z")).toDate()));

        save(new_composite(EntityWithDynamicCompositeKey.class, "key-1-1", ewm1).setDesc("soem desc"));
    }

}