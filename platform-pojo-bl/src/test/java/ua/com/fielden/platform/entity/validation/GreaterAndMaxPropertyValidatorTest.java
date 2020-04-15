package ua.com.fielden.platform.entity.validation;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.google.inject.Injector;

import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.entity.validation.test_entities.EntityWithGreaterAndMaxValidations;
import ua.com.fielden.platform.ioc.ApplicationInjectorFactory;
import ua.com.fielden.platform.test.CommonTestEntityModuleWithPropertyFactory;
import ua.com.fielden.platform.test.EntityModuleWithPropertyFactory;
import ua.com.fielden.platform.types.Money;

/**
 * A test case for validation of range properties.
 * 
 * @author TG Team
 * 
 */
public class GreaterAndMaxPropertyValidatorTest {

    private final EntityModuleWithPropertyFactory module = new CommonTestEntityModuleWithPropertyFactory();
    private final Injector injector = new ApplicationInjectorFactory().add(module).getInjector();
    private final EntityFactory factory = injector.getInstance(EntityFactory.class);

    @Test
    public void integer_property_must_be_greater_than_limit() {
        final Integer limit = 0;
        final EntityWithGreaterAndMaxValidations entity = factory.newEntity(EntityWithGreaterAndMaxValidations.class);
        
        final MetaProperty<Integer> mpIntProp = entity.getProperty("intProp");
        entity.setIntProp(limit + 1);
        assertTrue(mpIntProp.isValid());
        entity.setIntProp(limit - 1);
        assertFalse(mpIntProp.isValid());
        assertEquals(format(GreaterValidator.ERR_VALUE_SHOULD_BE_GREATER_THAN, limit), mpIntProp.getFirstFailure().getMessage());
        entity.setIntProp(limit);
        assertFalse(mpIntProp.isValid());
        assertEquals(format(GreaterValidator.ERR_VALUE_SHOULD_BE_GREATER_THAN, limit), mpIntProp.getFirstFailure().getMessage());
    }

    @Test
    public void integer_property_must_not_exceed_max_limit() {
        final Integer limit = 300;
        final EntityWithGreaterAndMaxValidations entity = factory.newEntity(EntityWithGreaterAndMaxValidations.class);
        
        final MetaProperty<Integer> mpIntProp = entity.getProperty("intProp");
        entity.setIntProp(limit - 1);
        assertTrue(mpIntProp.isValid());
        entity.setIntProp(limit);
        assertTrue(mpIntProp.isValid());
        entity.setIntProp(limit + 1);
        assertFalse(mpIntProp.isValid());
        assertEquals(format(MaxValueValidator.ERR_VALUE_SHOULD_NOT_EXCEED_MAX, limit), mpIntProp.getFirstFailure().getMessage());
    }

    @Test
    public void decimal_property_must_be_greater_than_limit() {
        final BigDecimal limit = new BigDecimal("0.50");
        final EntityWithGreaterAndMaxValidations entity = factory.newEntity(EntityWithGreaterAndMaxValidations.class);
        
        final MetaProperty<BigDecimal> mpDecimalProp = entity.getProperty("decimalProp");
        entity.setDecimalProp(limit.add(new BigDecimal("0.01")));
        assertTrue(mpDecimalProp.isValid());
        entity.setDecimalProp(limit.subtract(new BigDecimal("0.01")));
        assertFalse(mpDecimalProp.isValid());
        assertEquals(format(GreaterValidator.ERR_VALUE_SHOULD_BE_GREATER_THAN, limit), mpDecimalProp.getFirstFailure().getMessage());
        entity.setDecimalProp(limit);
        assertFalse(mpDecimalProp.isValid());
        assertEquals(format(GreaterValidator.ERR_VALUE_SHOULD_BE_GREATER_THAN, limit), mpDecimalProp.getFirstFailure().getMessage());
    }

    @Test
    public void decimal_property_must_not_exceed_max_limit() {
        final BigDecimal limit = new BigDecimal("1.50");
        final EntityWithGreaterAndMaxValidations entity = factory.newEntity(EntityWithGreaterAndMaxValidations.class);
        
        final MetaProperty<BigDecimal> mpDecimalProp = entity.getProperty("decimalProp");
        entity.setDecimalProp(limit.subtract(new BigDecimal("0.01")));
        assertTrue(mpDecimalProp.isValid());
        entity.setDecimalProp(limit);
        assertTrue(mpDecimalProp.isValid());
        entity.setDecimalProp(limit.add(new BigDecimal("0.01")));
        assertFalse(mpDecimalProp.isValid());
        assertEquals(format(MaxValueValidator.ERR_VALUE_SHOULD_NOT_EXCEED_MAX, limit), mpDecimalProp.getFirstFailure().getMessage());
    }

    @Test
    public void money_property_must_be_greater_than_limit() {
        final BigDecimal limit = new BigDecimal("-1.50");
        final EntityWithGreaterAndMaxValidations entity = factory.newEntity(EntityWithGreaterAndMaxValidations.class);
        
        final MetaProperty<Money> mpMoneyProp = entity.getProperty("moneyProp");
        entity.setMoneyProp(new Money(limit.add(new BigDecimal("0.01"))));
        assertTrue(mpMoneyProp.isValid());
        entity.setMoneyProp(new Money(limit.subtract(new BigDecimal("0.01"))));
        assertFalse(mpMoneyProp.isValid());
        assertEquals(format(GreaterValidator.ERR_VALUE_SHOULD_BE_GREATER_THAN, limit), mpMoneyProp.getFirstFailure().getMessage());
        entity.setMoneyProp(new Money(limit));
        assertFalse(mpMoneyProp.isValid());
        assertEquals(format(GreaterValidator.ERR_VALUE_SHOULD_BE_GREATER_THAN, limit), mpMoneyProp.getFirstFailure().getMessage());
    }

    @Test
    public void money_property_must_not_exceed_max_limit() {
        final BigDecimal limit = new BigDecimal("1.50");
        final EntityWithGreaterAndMaxValidations entity = factory.newEntity(EntityWithGreaterAndMaxValidations.class);
        
        final MetaProperty<Money> mpMoneyProp = entity.getProperty("moneyProp");
        entity.setMoneyProp(new Money(limit.subtract(new BigDecimal("0.01"))));
        assertTrue(mpMoneyProp.isValid());
        entity.setMoneyProp(new Money(limit));
        assertTrue(mpMoneyProp.isValid());
        entity.setMoneyProp(new Money(limit.add(new BigDecimal("0.01"))));
        assertFalse(mpMoneyProp.isValid());
        assertEquals(format(MaxValueValidator.ERR_VALUE_SHOULD_NOT_EXCEED_MAX, limit), mpMoneyProp.getFirstFailure().getMessage());
    }

}