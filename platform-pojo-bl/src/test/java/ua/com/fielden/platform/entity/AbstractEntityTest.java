package ua.com.fielden.platform.entity;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static ua.com.fielden.platform.types.try_wrapper.TryWrapper.Try;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;

import ua.com.fielden.platform.associations.one2many.incorrect.MasterEntity1;
import ua.com.fielden.platform.associations.one2many.incorrect.MasterEntity2;
import ua.com.fielden.platform.associations.one2many.incorrect.MasterEntity3;
import ua.com.fielden.platform.associations.one2many.incorrect.MasterEntity4;
import ua.com.fielden.platform.associations.one2many.incorrect.MasterEntity6;
import ua.com.fielden.platform.entity.exceptions.EntityDefinitionException;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.factory.IMetaPropertyFactory;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.entity.proxy.EntityProxyContainer;
import ua.com.fielden.platform.entity.validation.HappyValidator;
import ua.com.fielden.platform.entity.validation.annotation.ValidationAnnotation;
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.error.Warning;
import ua.com.fielden.platform.ioc.ApplicationInjectorFactory;
import ua.com.fielden.platform.reflection.Finder;
import ua.com.fielden.platform.reflection.TitlesDescsGetter;
import ua.com.fielden.platform.reflection.test_entities.SecondLevelEntity;
import ua.com.fielden.platform.reflection.test_entities.SimplePartEntity;
import ua.com.fielden.platform.reflection.test_entities.UnionEntityForReflector;
import ua.com.fielden.platform.test.CommonTestEntityModuleWithPropertyFactory;
import ua.com.fielden.platform.test.EntityModuleWithPropertyFactory;
import ua.com.fielden.platform.types.Money;
import ua.com.fielden.platform.types.either.Either;
import ua.com.fielden.platform.types.either.Left;
import ua.com.fielden.platform.utils.PropertyChangeSupportEx.PropertyChangeOrIncorrectAttemptListener;

/**
 * Unit test for :
 * <ul>
 * <li>{@link AbstractEntity}'s change support and entity instantiation with {@link EntityFactory}.</li>
 * <li>{@link AbstractEntity}'s support for properties and validation.</li>
 * <li>{@link AbstractEntity}'s meta-property support.</li>
 * </ul>
 *
 * @author TG Team
 *
 */
public class AbstractEntityTest {
    private boolean observed = false; // used
    private boolean observedForIncorrectAttempt = false; // used
    private final EntityModuleWithPropertyFactory module = new CommonTestEntityModuleWithPropertyFactory();
    {
        module.getDomainValidationConfig().setValidator(Entity.class, "firstProperty", new HappyValidator());
        module.getDomainValidationConfig().setValidator(Entity.class, "doubles", new HappyValidator());
        module.getDomainValidationConfig().setValidator(Entity.class, "number", new HappyValidator() {
            @Override
            public Result handle(final MetaProperty<Object> property, final Object newValue, final Set<Annotation> mutatorAnnotations) {
                if (newValue != null && newValue.equals(35)) {
                    return new Result(property, new Exception("Domain : Value 35 is not permitted."));
                } else if (newValue != null && newValue.equals(77)) {
                    return new Warning("DOMAIN validation : The value of 77 is dangerous.");
                }
                return super.handle(property, newValue, mutatorAnnotations);
            }
        });
    }

    private final Injector injector = new ApplicationInjectorFactory().add(module).getInjector();
    private final EntityFactory factory = injector.getInstance(EntityFactory.class);
    private Entity entity;

    @Before
    public void setUp() {
        observed = false;
        entity = factory.newEntity(Entity.class, "key", "description"); // this ensures all listeners are removed
    }

    @Test
    public void testEntityFactoryReferenceIsSet() {
        assertEquals(factory, entity.getEntityFactory());
    }

    @Test
    public void testThatObservablePropertyIsObserved() {
        entity.addPropertyChangeListener("observableProperty", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                observed = true;
            }
        });
        entity.setObservableProperty(22.0);
        assertEquals("Property should have been observed.", true, observed);
    }

    @Test
    public void testThatObservablePropertyMaintainsOriginalAndPrevValue() {
        entity.getProperty("observableProperty").setPrevValue(new Double("0.0"));
        assertEquals("current value must be changed (original value 0)", true, entity.getProperty("observableProperty").isChangedFromOriginal());
        assertEquals("current value must be changed (previous value 0)", false, entity.getProperty("observableProperty").isChangedFromPrevious());
        final Double newValue = new Double("22.0");
        entity.setObservableProperty(newValue);
        assertEquals("Prev property value is incorrect", new Double("0.0"), entity.getProperty("observableProperty").getPrevValue());
        assertNull("Original property value is incorrect", entity.getProperty("observableProperty").getOriginalValue());
        assertEquals("Property change count is incorrect", 2, entity.getProperty("observableProperty").getValueChangeCount());
        assertEquals("current value must be changed (original value 1)", true, entity.getProperty("observableProperty").isChangedFromOriginal());
        assertEquals("current value must be changed (previous value 1)", true, entity.getProperty("observableProperty").isChangedFromPrevious());

        entity.setObservableProperty(23.0);
        assertEquals("Prev property value is incorrect", newValue, entity.getProperty("observableProperty").getPrevValue());
        assertNull("Original property value is incorrect", entity.getProperty("observableProperty").getOriginalValue());
        assertEquals("Property change count is incorrect", 3, entity.getProperty("observableProperty").getValueChangeCount());
        assertEquals("current value must be changed (original value 2)", true, entity.getProperty("observableProperty").isChangedFromOriginal());
        assertEquals("current value must be changed (previous value 2)", true, entity.getProperty("observableProperty").isChangedFromPrevious());
    }

    @Test
    public void testThatNotObservablePropertyIsNotObserved() {
        entity.addPropertyChangeListener("observableProperty", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                observed = true;
            }
        });
        entity.setFirstProperty(22);
        assertEquals("Property should not have been observed.", false, observed);
    }

    @Test
    public void testThatPropertyValidationWorksForNonObservable() {
        // test validation of the firstProperty
        entity.setFirstProperty(60);
        assertTrue("Property firstProperty validation failed.", entity.getProperty("firstProperty").isValid());
        assertNull("Incorrect value for last invalid value.", entity.getProperty("firstProperty").getLastInvalidValue());
        // try to assign null
        entity.setFirstProperty(null);
        assertFalse("Property validation failed.", entity.getProperty("firstProperty").isValid());
        assertEquals("Value assignment should have been prevented by the validator,", new Integer(60), entity.getFirstProperty());
        // try to assign value less than 50
        entity.setFirstProperty(23);
        assertFalse("Property validation failed.", entity.getProperty("firstProperty").isValid());
        assertEquals("Value assignment should have been prevented by the validator,", new Integer(60), entity.getFirstProperty());
        assertEquals("Incorrect value for last invalid value.", new Integer(23), entity.getProperty("firstProperty").getLastInvalidValue());
    }

    @Test
    public void testThatPropertyHasDomainValidator() {
        // test validation of the firstProperty
        entity.setFirstProperty(60);
        final Result result = entity.getProperty("firstProperty").getValidationResult(ValidationAnnotation.DOMAIN);
        assertTrue("Domain validator for property firstProperty should be successful.", result.isSuccessful());
        assertTrue("Entity and result's instance should match.", result.getInstance() == entity);
        assertNull("Incorrect value for last invalid value.", entity.getProperty("firstProperty").getLastInvalidValue());
    }

    /**
     * This test ensures that validation happens before observing.
     */
    @Test
    public void testThatPropertyValidationWorksForObservableProperty() {
        // test validation of the observableProperty
        entity.addPropertyChangeListener("observableProperty", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                observed = true;
            }
        });
        final Double value = 23.0;
        entity.setObservableProperty(value);
        assertTrue("Property observableProperty validation failed.", entity.getProperty("observableProperty").isValid());
        assertEquals("Property change should have been observed.", true, observed);
        observed = false;
        // try to assign null
        entity.setObservableProperty(null);
        assertFalse("Property observableProperty validation failed.", entity.getProperty("observableProperty").isValid());
        assertEquals("Incorrect validation error message.", Entity.NOT_NULL_MSG, entity.getProperty("observableProperty").getFirstFailure().getMessage());
        assertEquals("Value assignment should have been prevented by the validator,", value, entity.getObservableProperty());
        assertEquals("Property change should have not been observed.", false, observed);

    }

    @Test
    public void final_property_for_non_persistent_entity_can_only_be_assigned_once() {
        assertTrue(entity.getProperty("finalProperty").isEditable());
        entity.setFinalProperty(60.0);
        assertTrue(entity.getProperty("finalProperty").isValid());
        assertEquals(Double.valueOf(60.0), entity.getFinalProperty());
        assertFalse(entity.getProperty("finalProperty").isEditable());
        
        entity.setFinalProperty(31.0);
        assertFalse(entity.getProperty("finalProperty").isValid());
        assertEquals(Double.valueOf(60.0), entity.getFinalProperty());
        assertFalse(entity.getProperty("finalProperty").isEditable());
    }
    
    @Test
    public void persistentOnly_final_property_for_non_persistent_entity_yields_invalid_definition() {
        final Either<Exception, EntityInvalidDefinition> result = Try(() -> factory.newEntity(EntityInvalidDefinition.class, "key", "desc"));
        assertTrue(result instanceof Left);
        final Throwable rootCause = ExceptionUtils.getRootCause(((Left<Exception, EntityInvalidDefinition>) result).value);
        assertTrue(rootCause instanceof EntityDefinitionException);
        assertEquals(format("Non-persistent entity [%s] has property [%s], which is incorrectly annotated with @Final(persistentOnly = true).", EntityInvalidDefinition.class.getSimpleName(), "firstProperty"),
                rootCause.getMessage());
    }
    

    @Test
    public void testNewEntityWithDynamicKey() {
        final Integer key1 = Integer.valueOf(1);
        final String key2 = "StringCompositeKeyMember";
        final Entity key3 = entity;

        // testing if entity is created correctly
        final EntityWithDynamicEntityKey entityWithDynamicEntityKey = factory.newByKey(EntityWithDynamicEntityKey.class, key1, key2, key3);

        assertNotNull(entityWithDynamicEntityKey);
        assertNotNull(entityWithDynamicEntityKey.getKey());
        assertEquals(key1, entityWithDynamicEntityKey.getKey1());
        assertEquals(key2, entityWithDynamicEntityKey.getKey2());
        assertEquals(key3, entityWithDynamicEntityKey.getKey3());

        assertEquals(factory, entityWithDynamicEntityKey.getEntityFactory());

        // testing if exception is thrown in case of wrong number of parameters
        boolean exceptionWasThrown = false;
        try {
            factory.newByKey(EntityWithDynamicEntityKey.class, key1, key2);
        } catch (final Exception e) {
            exceptionWasThrown = true;
        }
        assertTrue(exceptionWasThrown);
    }

    @Test
    public void testThatMetaPropertiesForSimplEntityAreAssignedCorrectKeyAndActiveValues() {
        assertTrue("Meta-property for property 'key' should be marked as a key.", entity.getProperty("key").isKey());
        assertTrue("Meta-property for property 'key' should be marked active.", entity.getProperty("key").isVisible());

        assertFalse("Meta-property for property 'desc' should not be marked as a key.", entity.getProperty("desc").isKey());
        assertTrue("Meta-property for property 'desc' should be marked active.", entity.getProperty("desc").isVisible());
        assertFalse("Meta-property for property 'firstProperty' should not be marked as a key.", entity.getProperty("firstProperty").isKey());
        assertTrue("Meta-property for property 'firstProperty' should be marked active.", entity.getProperty("firstProperty").isVisible());
        assertFalse("Meta-property for property 'observableProperty' should not be marked as a key.", entity.getProperty("observableProperty").isKey());
        assertTrue("Meta-property for property 'observableProperty' should be marked active.", entity.getProperty("observableProperty").isVisible());
        assertFalse("Meta-property for property 'finalProperty' should not be marked as a key.", entity.getProperty("finalProperty").isKey());
        assertFalse("Meta-property for property 'finalProperty' should be marked invisible.", entity.getProperty("finalProperty").isVisible());
    }

    @Test
    public void testThatMetaPropertiesForCompositeEntityAreAssignedCorrectKeyAndActiveValues() {
        final Integer key1 = Integer.valueOf(1);
        final String key2 = "StringCompositeKeyMember";
        final Entity key3 = entity;
        final EntityWithDynamicEntityKey entityWithDynamicEntityKey = factory.newByKey(EntityWithDynamicEntityKey.class, key1, key2, key3);

        assertFalse("Meta-property for property 'key' should not be marked as a key.", entityWithDynamicEntityKey.getProperty("key").isKey());
        assertFalse("Meta-property for property 'key' should not be marked active.", entityWithDynamicEntityKey.getProperty("key").isVisible());
        assertTrue("Meta-property for property 'key1' should be marked as a key.", entityWithDynamicEntityKey.getProperty("key1").isKey());
        assertTrue("Meta-property for property 'key1' should be marked active.", entityWithDynamicEntityKey.getProperty("key1").isVisible());
        assertTrue("Meta-property for property 'key2' should be marked as a key.", entityWithDynamicEntityKey.getProperty("key2").isKey());
        assertTrue("Meta-property for property 'key2' should be marked active.", entityWithDynamicEntityKey.getProperty("key2").isVisible());
        assertTrue("Meta-property for property 'key3' should be marked as a key.", entityWithDynamicEntityKey.getProperty("key3").isKey());
        assertTrue("Meta-property for property 'key3' should be marked active.", entityWithDynamicEntityKey.getProperty("key3").isVisible());

        assertFalse("Meta-property for property 'desc' should not be marked as a key.", entityWithDynamicEntityKey.getProperty("desc").isKey());
        assertTrue("Meta-property for property 'desc' should be marked active.", entityWithDynamicEntityKey.getProperty("desc").isVisible());
    }

    @Test
    public void testThatMetaInformationForCollectionalPropertiesIsDeterminedCorrectly() {
        assertTrue("Property 'doubles' is not recognised as collectional.", entity.getProperty("doubles").isCollectional());
        assertEquals("Meta-property for 'doubles' has incorrect collectional type.", Double.class, entity.getProperty("doubles").getPropertyAnnotationType());

        assertTrue("Property 'entities' is not recognised as collectional.", entity.getProperty("entities").isCollectional());
        assertEquals("Meta-property for 'entities' has incorrect collectional type.", Entity.class, entity.getProperty("entities").getPropertyAnnotationType());
    }

    /**
     * This test ensures the correct setter behaviour for a collectional property:
     * <ul>
     * <li>meta-property has correct validators (determination of validators is done by {@link IMetaPropertyFactory})
     * <li>changes done through mutators are observed
     * <li>validators are provided with correct values (this is actually done by {@link ValidationMutatorInterceptor})), which is checked indirectly
     * </ul>
     */
    @Test
    public void testThatCollectionalPropertySetterIsObservedAndValidated() {
        final MetaProperty<List<Double>> doublesProperty = entity.getProperty("doubles");
        assertEquals("Incorrect number of validators.", 2, doublesProperty.getValidators().size());
        assertTrue("Should have domain validation.", doublesProperty.getValidators().containsKey(ValidationAnnotation.DOMAIN));
        assertTrue("Should have not-null validation.", doublesProperty.getValidators().containsKey(ValidationAnnotation.REQUIRED));
        assertNull("There should be no domain validation result at this stage.", doublesProperty.getValidationResult(ValidationAnnotation.DOMAIN));
        assertNull("There should be no rquiredness validation result at this stage.", doublesProperty.getValidationResult(ValidationAnnotation.REQUIRED));
        entity.setDoubles(Arrays.asList(new Double[] { 2.0, 3.0 }));
        entity.addPropertyChangeListener("doubles", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                assertEquals("Incorrect old value.", 0, event.getOldValue()); // should always be 0
                assertEquals("Incorrect new value.", 1, event.getNewValue()); // should always be 1
                observed = true;
            }
        });
        entity.setDoubles(Arrays.asList(new Double[] { 2.0 }));

        assertEquals("Property should have been observed.", true, observed);
        
        assertNull("Incorrect original value", doublesProperty.getOriginalValue());
        assertTrue("Incorrect isChangedFrom original.", doublesProperty.isChangedFromOriginal());
        assertEquals("Incorrect previous value", Arrays.asList(new Double[] { 2.0, 3.0 }), doublesProperty.getPrevValue());
        assertTrue("Incorrect isChangedFrom previous.", doublesProperty.isChangedFromPrevious());
        assertTrue("Incorrect isDirty.", doublesProperty.isDirty());
        assertTrue("Incorrect isDirty for whole entity.", entity.isDirty());
        
        assertNotNull("There should be domain validation result at this stage.", doublesProperty.getValidationResult(ValidationAnnotation.DOMAIN));
        assertTrue("Domain validation result should be successful.", doublesProperty.getValidationResult(ValidationAnnotation.DOMAIN).isSuccessful());
        assertNotNull("There should be a requiredness validation result at this stage.", doublesProperty.getValidationResult(ValidationAnnotation.REQUIRED));
        assertTrue("Requirendess validation result should be successful.", doublesProperty.getValidationResult(ValidationAnnotation.REQUIRED).isSuccessful());

        entity.setDoubles(null);
        assertFalse("Requiredness validation result should not be successful.", doublesProperty.getValidationResult(ValidationAnnotation.REQUIRED).isSuccessful());
        assertEquals("Incorrect size for doubles", 1, entity.getDoubles().size());
    }

    /**
     * This test ensures correct incrementor behaviour for a collectional property:
     * <ul>
     * <li>meta-property has correct validators (determination of validators is done by {@link IMetaPropertyFactory})
     * <li>changes done through mutators are observed
     * <li>validators are provided with correct values (this is actually done by {@link ValidationMutatorInterceptor})), which is checked indirectly
     * </ul>
     */
    @Test
    public void testThatCollectionalPropertyIncrementorIsObservedAndValidated() {
        final MetaProperty<List<Double>> doublesProperty = entity.getProperty("doubles");
        entity.setDoubles(Arrays.asList(new Double[] { -2.0, -3.0 }));

        entity.addPropertyChangeListener("doubles", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                assertEquals("Incorrect old value.", 2, event.getOldValue()); // oldValue == oldSize
                assertEquals("Incorrect new value.", 2.0, event.getNewValue()); // newValue == newAddedValue!!!
                observed = true;
            }
        });
        entity.addToDoubles(2.0);

        assertEquals("Property should have been observed.", true, observed);
        assertEquals("Incorrect size for doubles", 3, entity.getDoubles().size());

        assertNull("Incorrect original value", doublesProperty.getOriginalValue());
        assertTrue("Incorrect isChangedFrom original.", doublesProperty.isChangedFromOriginal());
        assertEquals("Incorrect previous value", Arrays.asList(new Double[] { -2.0, -3.0 }), doublesProperty.getPrevValue());
        assertTrue("Incorrect isChangedFrom previous.", doublesProperty.isChangedFromPrevious());
        assertTrue("Incorrect isDirty.", doublesProperty.isDirty());
        assertTrue("Incorrect isDirty for whole entity.", entity.isDirty());
        
        assertNotNull("There should be a domain validation result.", doublesProperty.getValidationResult(ValidationAnnotation.DOMAIN));
        assertNotNull("There should be requiredness validation result at this stage.", doublesProperty.getValidationResult(ValidationAnnotation.REQUIRED));
        assertTrue("Requiredness validation result should be successful.", doublesProperty.getValidationResult(ValidationAnnotation.REQUIRED).isSuccessful());

        entity.addToDoubles(null);
        assertFalse("Requiredness validation result should not be successful.", doublesProperty.getValidationResult(ValidationAnnotation.REQUIRED).isSuccessful());
        assertEquals("Incorrect size for doubles", 3, entity.getDoubles().size());
        assertNull("Null value is expected for the last invalid value.", entity.getProperty("doubles").getLastInvalidValue());
    }

    /**
     * This test ensures correct decrementor behaviour for a collectional property:
     * <ul>
     * <li>meta-property has correct validators (determination of validators is done by {@link IMetaPropertyFactory})
     * <li>changes done through mutators are observed
     * <li>validators are provided with correct values (this is actually done by {@link ValidationMutatorInterceptor})), which is checked indirectly
     * </ul>
     */
    @Test
    public void testThatCollectionalPropertyDecrementorIsObservedAndValidated() {
        final MetaProperty<List<Double>> doublesProperty = entity.getProperty("doubles");
        entity.setDoubles(Arrays.asList(new Double[] { -2.0, -3.0 }));

        entity.addPropertyChangeListener("doubles", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                assertEquals("Incorrect old value.", -2.0, event.getOldValue()); // oldValue = oldRemovedValue!!!
                assertEquals("Incorrect new value.", 1, event.getNewValue()); // newValue = newSize
                observed = true;
            }
        });
        entity.removeFromDoubles(-2.0);

        assertTrue("Property should have been observed.", observed);
        assertEquals("Incorrect size for doubles", 1, entity.getDoubles().size());
        
        assertNull("Incorrect original value", doublesProperty.getOriginalValue());
        assertTrue("Incorrect isChangedFrom original.", doublesProperty.isChangedFromOriginal());
        assertEquals("Incorrect previous value", Arrays.asList(new Double[] { -2.0, -3.0 }), doublesProperty.getPrevValue());
        assertTrue("Incorrect isChangedFrom previous.", doublesProperty.isChangedFromPrevious());
        assertTrue("Incorrect isDirty.", doublesProperty.isDirty());
        assertTrue("Incorrect isDirty for whole entity.", entity.isDirty());
        
        assertNotNull("There should be domain validation result at this stage.", doublesProperty.getValidationResult(ValidationAnnotation.DOMAIN));
        assertTrue("Domain validation result should be successful.", doublesProperty.getValidationResult(ValidationAnnotation.DOMAIN).isSuccessful());
        assertNotNull("There should be requiredness validation result.", doublesProperty.getValidationResult(ValidationAnnotation.REQUIRED));
        assertEquals("Incorrect size for doubles", 1, entity.getDoubles().size());
    }

    @Test
    public void testMetaPropertyComparisonLogic() {
        final Integer key1 = Integer.valueOf(1);
        final String key2 = "StringCompositeKeyMember";
        final Entity key3 = entity;

        final EntityWithDynamicEntityKey entityWithDynamicEntityKey = factory.newByKey(EntityWithDynamicEntityKey.class, key1, key2, key3);

        final Set<MetaProperty<?>> metaProperties = Finder.getMetaProperties(entityWithDynamicEntityKey);
        assertEquals("Incorrect number of meta properties.", 5, metaProperties.size());

        final List<MetaProperty<?>> list = new ArrayList<>();
        list.addAll(metaProperties);
        assertEquals("Incorrect sorting order for meta-properties.", "key1", list.get(0).getName());
        assertEquals("Incorrect sorting order for meta-properties.", "key2", list.get(1).getName());
        assertEquals("Incorrect sorting order for meta-properties.", "key3", list.get(2).getName());
        assertEquals("Incorrect sorting order for meta-properties.", "desc", list.get(3).getName());
        assertEquals("Incorrect sorting order for meta-properties.", "key", list.get(4).getName());
    }

    @Test
    public void testValidationAndSettingRestrictionInObservableMutator() {
        // preparing
        entity.addPropertyChangeListener("observableProperty", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                observed = true;
            }
        });
        final MetaProperty<Double> metaProperty = entity.getProperty("observableProperty");
        // 1. test the error recovery by the same value
        entity.setObservableProperty(100.0);
        entity.setObservableProperty(null);
        observed = false;
        entity.setObservableProperty(100.0);
        assertTrue("the property after the error recovery have to be valid", metaProperty.isValid());
        assertTrue("observing logic have to be invoked after the error recovery", observed);

        // 2. test the error recovery by the different value
        entity.setObservableProperty(100.0);
        entity.setObservableProperty(null);
        observed = false;
        entity.setObservableProperty(101.0);
        assertTrue("the property after the error recovery have to be valid", metaProperty.isValid());
        assertTrue("observing logic have to be invoked after the error recovery", observed);

        // 3. collectional property validation/observation tested in previous tests

        // 4. test simple property validation/observation invoking for different new/old values :
        entity.setObservableProperty(100.0);
        observed = false;
        entity.setObservableProperty(101.0);
        assertTrue("the property after different value setted have to be valid", metaProperty.isValid());
        assertTrue("observing logic have to be invoked after different value setted ", observed);

        // 5. test simple property validation/observation not invoking for the same new/old values :
        entity.setObservableProperty(100.0);
        observed = false;
        entity.setObservableProperty(100.0);
        assertTrue("the property after the same value setted have to be valid", metaProperty.isValid());
        assertFalse("observing logic have not be invoked after the same value setted ", observed);

        // 6. test simple property validation/observation in case where it is initialised with NULL at construction time
        // previous value, which is also original, is null
        // property is valid since no mutator was yet invoked
        // property has NotNull validation
        entity.setObservablePropertyInitialisedAsNull(null);
        assertTrue("ObservablePropertyInitialisedAsNull should be still be recognised as valid since setting null does not change its previous value, and  thus no validation should occur.", entity.getProperty("observablePropertyInitialisedAsNull").isValid());
    }

    @Test
    public void testValidationWarnings() {
        // preparing
        entity.addPropertyChangeListener("number", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                observed = true;
            }
        });
        final MetaProperty<Integer> metaProperty = entity.getProperty("number");
        entity.setNumber(40);
        // 1. test warning generation on Domain Validation level:
        observed = false;
        entity.setNumber(77);
        assertTrue("DOMAIN : The property should be valid with warning.", metaProperty.isValid());
        assertTrue("DOMAIN : The property should have warnings.", metaProperty.hasWarnings());
        assertTrue("DOMAIN : Observing logic should be invoked after setting warning value.", observed);
        assertEquals("DOMAIN : Warning value should be set.", Integer.valueOf(77), entity.get("number"));

        // 2. recovery :
        observed = false;
        entity.setNumber(40);
        assertTrue("DOMAIN : The property should remain valid after recovery.", metaProperty.isValid());
        assertFalse("DOMAIN : The property should not have warnings after recovery.", metaProperty.hasWarnings());
        assertTrue("DOMAIN : Observing logic should be invoked during recovery.", observed);
        assertEquals("DOMAIN : Recovery value should be set.", Integer.valueOf(40), entity.get("number"));

        // 3. test warning generation on Dynamic Validation level (validation inside setter):
        observed = false;
        entity.setNumber(777);
        assertTrue("DYNAMIC : The property should be valid with warning.", metaProperty.isValid());
        assertTrue("DYNAMIC : The property should have warnings.", metaProperty.hasWarnings());
        assertTrue("DYNAMIC : Observing logic should be invoked after setting warning value.", observed);
        assertEquals("DYNAMIC : Warning value should be set.", Integer.valueOf(777), entity.get("number"));

        // 4. recovery :
        observed = false;
        entity.setNumber(40);
        assertTrue("The property should remain valid after recovery.", metaProperty.isValid());
        assertFalse("The property should not have warnings after recovery.", metaProperty.hasWarnings());
        assertTrue("Observing logic should be invoked during recovery.", observed);
        assertEquals("Recovery value should be set.", Integer.valueOf(40), entity.get("number"));

        // 5. test warning generation on Dynamic Validation level (validation inside setter):
        observed = false;
        entity.setNumber(777);
        assertTrue("DYNAMIC : The property should be valid with warning.", metaProperty.isValid());
        assertTrue("DYNAMIC : The property should have warnings.", metaProperty.hasWarnings());
        assertTrue("DYNAMIC : Observing logic should be invoked after setting warning value.", observed);
        assertEquals("DYNAMIC : Warning value should be set.", Integer.valueOf(777), entity.get("number"));

        // 6. recovery by setting another warning value! :
        observed = false;
        entity.setNumber(77);
        assertTrue("The property should remain valid after recovery (by setting another warning value).", metaProperty.isValid());
        assertTrue("The property should have warnings after recovery (by setting another warning value).", metaProperty.hasWarnings());
        assertTrue("Observing logic should be invoked during recovery.", observed);
        assertEquals("Recovery warning value should be set.", Integer.valueOf(77), entity.get("number"));
    }

    @Test
    public void testIncorrectAttemptFiringInObservableMutator() {
        // simple property change listener
        entity.addPropertyChangeListener("observableProperty", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                observed = true;
            }
        });
        // incorrect attempt or property change listener
        entity.addPropertyChangeListener("observableProperty", new PropertyChangeOrIncorrectAttemptListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                observedForIncorrectAttempt = true;
            }
        });
        // 1. test that incorrect setting attempt fires PropertyChangeOrIncorrectAttemptListener and not fires simple PropertyChangeListener
        observed = false;
        observedForIncorrectAttempt = false;
        entity.setObservableProperty(null);
        assertTrue("incorrect attempt firing have to be invoked", observedForIncorrectAttempt);
        assertFalse("observing logic for simple PropertyChangeListeners haven't to be invoked", observed);

        // 2. test that correct setting attempt fires ALL PropertyChangeListeners
        observed = false;
        observedForIncorrectAttempt = false;
        entity.setObservableProperty(100.0);
        assertTrue("all types of Listeners have to be invoked (even PropertyChangeOrIncorrectAttemptListener!)", observedForIncorrectAttempt);
        assertTrue("observing logic for simple PropertyChangeListeners have to be invoked", observed);
    }

    @Test
    public void testCorrectAssignmentOfMetaPropertyPropertiesBaseOnAnnotations() {
        final MetaProperty<Integer> firstPropertyMetaProp = entity.getProperty("firstProperty");
        assertTrue("Should be requried", firstPropertyMetaProp.isRequired());
        assertFalse("Should not be editable", firstPropertyMetaProp.isEditable());
        assertEquals("Incorrect title", "First Property", firstPropertyMetaProp.getTitle());
        assertEquals("Incorrect desc", "used for testing", firstPropertyMetaProp.getDesc());

        final MetaProperty<Double> observablePropertyMetaProp = entity.getProperty("observableProperty");
        assertTrue("Should be requried", observablePropertyMetaProp.isRequired());
        assertTrue("Should be editable", observablePropertyMetaProp.isEditable());
        assertEquals("Incorrect title", "Observable Property", observablePropertyMetaProp.getTitle());
        assertEquals("Incorrect desc", "Observable Property", observablePropertyMetaProp.getDesc());

        final MetaProperty<Double> finalPropertyMetaProp = entity.getProperty("finalProperty");
        assertFalse("Should not bevisible", finalPropertyMetaProp.isVisible());

        final MetaProperty<String> keyMetaProp = entity.getProperty("key");
        assertTrue("Should be requried", keyMetaProp.isRequired());
        assertTrue("Should be editable", keyMetaProp.isEditable());
        assertEquals("Incorrect title", "Entity No", keyMetaProp.getTitle());
        assertEquals("Incorrect desc", "Key Property", keyMetaProp.getDesc());

        final MetaProperty<String> descMetaProp = entity.getProperty("desc");
        assertTrue("Should be requried", descMetaProp.isRequired());
        assertTrue("Should be editable", descMetaProp.isEditable());
        assertEquals("Incorrect title", "Description", descMetaProp.getTitle());
        assertEquals("Incorrect desc", "Description Property", descMetaProp.getDesc());
    }

    @Test
    public void testMetaPropertyRequirementValidation() {
        final MetaProperty<Integer> firstPropertyMetaProp = entity.getProperty("firstProperty");
        assertTrue("Should be required", firstPropertyMetaProp.isRequired());
        assertTrue("REQUIREDValidator should be present for 'required' property.", firstPropertyMetaProp.getValidators().containsKey(ValidationAnnotation.REQUIRED));
        entity.setFirstProperty(null);
        assertFalse("Required property is not yet populated and thus entity should be invalid.", entity.isValid().isSuccessful());

        entity.setFirstProperty(56);
        assertTrue("Required property with not null value have to be valid.", firstPropertyMetaProp.isValid());
        assertTrue("Should remain required", firstPropertyMetaProp.isRequired());

        firstPropertyMetaProp.setRequired(false);
        assertFalse("Should not be required", firstPropertyMetaProp.isRequired());
        assertTrue("REQUIREDValidator should be present for 'required in the past' property.", firstPropertyMetaProp.getValidators().containsKey(ValidationAnnotation.REQUIRED));
        entity.setFirstProperty(null);
        assertTrue("Not 'required' property with null value has to be valid.", firstPropertyMetaProp.isValid());

        final MetaProperty<Money> secondMetaProperty = entity.getProperty("money");
        secondMetaProperty.setRequired(true);
        assertTrue("Should be required", secondMetaProperty.isRequired());
        assertTrue("REQUIREDValidator should be present for 'required' property.", secondMetaProperty.getValidators().containsKey(ValidationAnnotation.REQUIRED));

        secondMetaProperty.setRequired(false);
        assertFalse("Should not be required", secondMetaProperty.isRequired());
        assertTrue("REQUIREDValidator should be present for 'required in the past' property.", secondMetaProperty.getValidators().containsKey(ValidationAnnotation.REQUIRED));
        entity.setMoney(null);
        assertTrue("Not 'required' property (with no NotNullValidator) with null value have to be valid.", secondMetaProperty.isValid());
    }

    /**
     * The requirement checks inside MetaProperty's isValid() method, rather than inside AbstractEntity's isValid(). So this logic this tests here.
     */
    @Test
    public void testAbstractEntityRequirementValidation() {
        final MetaProperty<Integer> firstPropertyMetaProp = entity.getProperty("firstProperty");

        firstPropertyMetaProp.setRequired(false);
        assertFalse("Should not be required", firstPropertyMetaProp.isRequired());
        assertTrue("REQUIREDValidator should be present for 'required in the past' property.", firstPropertyMetaProp.getValidators().containsKey(ValidationAnnotation.REQUIRED));

        // check MetProperty's isValid() requiredness forcing.
        assertNull("Not required property first failure with null value should be null (before isValid() invoked).", firstPropertyMetaProp.getFirstFailure());
        assertTrue("Not required property with null value should be valid.", firstPropertyMetaProp.isValid());
        assertNull("Not required property first failure with null value should be null (after isValid() invoked).", firstPropertyMetaProp.getFirstFailure());
        assertTrue("Entity with not-required property with null value should be valid.", entity.isValid().isSuccessful());

        firstPropertyMetaProp.setRequired(true);

        assertNull("Required property (with null value) first failure should be null (before isValid() invoked).", firstPropertyMetaProp.getFirstFailure());
        assertFalse("Required property (with null value) should be invalid.", firstPropertyMetaProp.isValidWithRequiredCheck());
        assertNotNull("Required property (with null value) first failure should be null (after isValid() invoked).", firstPropertyMetaProp.getFirstFailure());
        assertFalse("Entity with required property with null value should be invalid.", entity.isValid().isSuccessful());

        // at these cases no MetaProperty isValid() performs manually, just AE isValid() :

        firstPropertyMetaProp.setRequired(false);

        // check MetProperty's isValid() requiredness forcing.
        assertNull("Not required property first failure with null value should be null (before AbstractEntity's isValid() invoked).", firstPropertyMetaProp.getFirstFailure());
        assertTrue("Entity with not-required property with null value should be valid.", entity.isValid().isSuccessful());
        assertNull("Not required property first failure with null value should be null (after AbstractEntity's isValid() invoked).", firstPropertyMetaProp.getFirstFailure());

        firstPropertyMetaProp.setRequired(true);

        assertNull("Required property (with null value) first failure should be null (before AbstractEntity's isValid() invoked).", firstPropertyMetaProp.getFirstFailure());
        assertFalse("Entity with required property with null value should be invalid.", entity.isValid().isSuccessful());
        assertNotNull("Required property (with null value) first failure should be null (after AbstractEntity's isValid() invoked).", firstPropertyMetaProp.getFirstFailure());
    }

    @Test
    public void testSetterExceptionsAndResultsHandling() {
        final MetaProperty<Integer> property = entity.getProperty("number");
        // No exception have to be thrown from setter.
        try {
            entity.setNumber(10);
        } catch (final Throwable e) {
            fail("Have not to throw NullPointerException!");
        } finally {
            assertTrue("The 'number' property have to be valid.", property.isValid());
            assertFalse("DynamicValidator have to be null before any Result thrown.", property.containsDynamicValidator());
            assertEquals("The value in the 'number' property is not correct.", (Object) 10, entity.getNumber());
        }

        // NullPointerException have to be thrown on the higher level.
        try {
            entity.setNumber(null);
            fail("Have to throw NullPointerException!");
        } catch (final Throwable e) {
            assertTrue("The throwed exception have to be instanceof NullPointerException", e instanceof NullPointerException);
        } finally {
            assertTrue("The 'number' property have to be valid. Because any validation succeeded -> just unhandled exception is thrown in setter!", property.isValid());
            assertFalse("DynamicValidator have to be null before any Result thrown.", property.containsDynamicValidator());
            assertEquals("The value in the 'number' property is not correct.", (Object) 10, entity.getNumber());
        }

        // IllegalArgumentException have to be thrown on the higher level.
        try {
            entity.setNumber(50);
            fail("Have to throw IllegalArgumentException!");
        } catch (final Throwable e) {
            assertTrue("The throwed exception have to be instanceof IllegalArgumentException", e instanceof IllegalArgumentException);
        } finally {
            assertTrue("The 'number' property have to be valid. Because any validation succeeded -> just unhandled exception is thrown in setter!", property.isValid());
            assertFalse("DynamicValidator have to be null before any Result thrown.", property.containsDynamicValidator());
            assertEquals("The value in the 'number' property is not correct.", (Object) 10, entity.getNumber());
        }

        // DomainValidation have to be not successful.
        try {
            entity.setNumber(35);
        } catch (final Throwable e) {
            fail("Have not to throw any Exception!");
        } finally {
            assertFalse("The 'number' property have to be invalid(Domain Validation).", property.isValid());
            assertFalse("DynamicValidator have to be null before any Result thrown.", property.containsDynamicValidator());
            assertEquals("The value in the 'number' property is not correct.", (Object) 10, entity.getNumber());
        }

        // Result exception (DYNAMIC validator) have to be throwed but catched in ObservableMutator (and also handled)!
        try {
            entity.setNumber(100);
        } catch (final Throwable e) {
            fail("Have not to throw Result! it have to be handled inside ObservableMutatorInterceptor!");
        } finally {
            assertFalse("The 'number' property have to be invalid.", property.isValid());
            assertTrue("DynamicValidator have to be not null after any Result has been thrown.", property.containsDynamicValidator());
            assertNotNull("The result for DYNAMIC validator have to be not null.", property.getValidationResult(ValidationAnnotation.DYNAMIC));
            assertFalse("The result for DYNAMIC validator have to be not successful.", property.getValidationResult(ValidationAnnotation.DYNAMIC).isSuccessful());
            assertEquals("The value in the 'number' property is not correct.", (Object) 10, entity.getNumber());
        }
    }

    @Test
    public void testPropertyDependencies() {
        try {
            entity.addPropertyChangeListener("dependent", new PropertyChangeListener() {
                @Override
                public void propertyChange(final PropertyChangeEvent event) {
                    observed = true;
                }
            });

            entity.setDependent(15);
            assertTrue("Dependent property have to be valid.", entity.getProperty("dependent").isValid());
            assertEquals("Incorrect value.", (Integer) 15, entity.getDependent());

            entity.setDependent(25);
            assertFalse("Dependent property has to be invalid.", entity.getProperty("dependent").isValid());
            assertEquals("Incorrect value.", (Integer) 15, entity.getDependent());
            assertEquals("Incorrect lastInvalidValue.", 25, entity.getProperty("dependent").getLastInvalidValue());

            observed = false;
            // Update incorrect value of the dependent property by setting the main property.
            entity.setMain(30);
            assertTrue("Dependent property setter need to be invoked! Because dependent property is invalid and need to be error recovered.", observed);
            assertTrue("Dependent property have to be valid.", entity.getProperty("dependent").isValid());
            assertEquals("Incorrect value.", (Integer) 25, entity.getDependent());
            assertEquals("Incorrect lastInvalidValue.", null, entity.getProperty("dependent").getLastInvalidValue());

            assertTrue("Main property have to be valid.", entity.getProperty("main").isValid());
            assertEquals("Incorrect value.", (Integer) 30, entity.getMain());
            assertEquals("Incorrect lastInvalidValue.", null, entity.getProperty("main").getLastInvalidValue());

            // Do not update anything in the case of correct dependent property.
            entity.setDependent(15);
            assertTrue("Dependent property have to be valid.", entity.getProperty("dependent").isValid());
            assertEquals("Incorrect value.", (Integer) 15, entity.getDependent());

            observed = false;
            entity.setMain(35);
            assertFalse("Dependent property setter should not have been invoked.", observed);
            assertTrue("Dependent property have to be valid.", entity.getProperty("dependent").isValid());
            assertEquals("Incorrect value.", (Integer) 15, entity.getDependent());
            assertEquals("Incorrect lastInvalidValue.", null, entity.getProperty("dependent").getLastInvalidValue());

            assertTrue("Main property have to be valid.", entity.getProperty("main").isValid());
            assertEquals("Incorrect value.", (Integer) 35, entity.getMain());
            assertEquals("Incorrect lastInvalidValue.", null, entity.getProperty("main").getLastInvalidValue());
        } catch (final Result e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_get_last_attempt_value() {
        final Entity entity = factory.newEntity(Entity.class, "key", "description");
        entity.setNumber(5);
        assertEquals(5, entity.getProperty("number").getLastAttemptedValue());

        // setting invalid value
        entity.setNumber(35);
        assertEquals(35, entity.getProperty("number").getLastAttemptedValue());

        // setting once again incorrect value
        entity.setNumber(100);
        assertEquals(100, entity.getProperty("number").getLastAttemptedValue());

        // setting correct value
        entity.setNumber(25);
        assertEquals(25, entity.getProperty("number").getLastAttemptedValue());
    }

    @Test
    public void test_original_state_can_be_restored() {
        final Entity entity = factory.newEntity(Entity.class, "key", "description");
        final Money originalMoney = entity.getMoney();
        entity.setMoney(new Money("23.25"));
        final Date originalDate = entity.getDate();
        entity.setDate(new Date());

        entity.restoreToOriginal();
        assertEquals("Could not restore to original.", originalMoney, entity.getMoney());
        assertEquals("Could not restore to original.", originalDate, entity.getDate());
    }

    @Test
    public void test_that_composite_key_is_not_affected_by_restore_to_original_operation() {
        final CorrectEntityWithDynamicEntityKey compositeEntity = factory.newEntity(CorrectEntityWithDynamicEntityKey.class, 12L);
        assertNotNull("Composite should not be null.", compositeEntity.getKey());

        final Long orig1 = compositeEntity.getProperty1();
        final Long orig2 = compositeEntity.getProperty2();

        compositeEntity.setProperty1(1L);
        compositeEntity.setProperty2(2L);

        compositeEntity.restoreToOriginal();
        assertEquals("Could not restore to original.", orig1, compositeEntity.getProperty1());
        assertEquals("Could not restore to original.", orig2, compositeEntity.getProperty2());
        assertNotNull("Composite should not be null after restoration to original.", compositeEntity.getKey());
    }

    @Test
    public void test_copy_for_same_type() {
        final Entity entity = factory.newEntity(Entity.class, 1L);
        entity.setKey("key");
        entity.setDesc("description");
        entity.setMoney(new Money("23.25"));

        final Entity copy = entity.copy(Entity.class);

        assertEquals("Copy does not equal to the original instance", entity, copy);
        assertFalse("Should have not been dirty", copy.isDirty());
        assertEquals("Property id does not match", entity.getId(), copy.getId());
        assertEquals("Property desc does not match", entity.getDesc(), copy.getDesc());
        assertEquals("Property money does not match", entity.getMoney(), copy.getMoney());
    }

    @Test
    public void test_copy_for_parent_to_descendant() {
        final Entity entity = factory.newEntity(Entity.class, 1L);
        entity.setKey("key");
        entity.setDesc("description");
        entity.setMoney(new Money("23.25"));

        final EntityExt copy = entity.copy(EntityExt.class);

        assertTrue("Unexpected type of copy", copy instanceof EntityExt);
        assertFalse("Copy of descendant should not be equal to the original instance because of type difference", entity.equals(copy));
        assertFalse("Should have not been dirty", copy.isDirty());
        assertEquals("Property id does not match", entity.getId(), copy.getId());
        assertEquals("Property desc does not match", entity.getDesc(), copy.getDesc());
        assertEquals("Property money does not match", entity.getMoney(), copy.getMoney());
        assertNull("Additional property should be null", copy.getAdditionalProperty());
    }

    @Test
    public void test_copy_for_descendant_back_to_parennt() {
        final EntityExt entity = factory.newEntity(EntityExt.class, 1L);
        entity.setKey("key");
        entity.setDesc("description");
        entity.setMoney(new Money("23.25"));
        entity.setAdditionalProperty(23.0);

        final Entity copy = entity.copy(Entity.class);

        assertTrue("Unexpected type of copy", copy instanceof Entity);
        assertFalse("Copy of descendant should not be equal to the original instance because of type difference", entity.equals(copy));
        assertFalse("Should have not been dirty", copy.isDirty());
        assertEquals("Property id does not match", entity.getId(), copy.getId());
        assertEquals("Property desc does not match", entity.getDesc(), copy.getDesc());
        assertEquals("Property money does not match", entity.getMoney(), copy.getMoney());
    }

    @Test
    public void test_copy_for_entities_with_dynamic_key() {
        final CorrectEntityWithDynamicEntityKey one = factory.newEntity(CorrectEntityWithDynamicEntityKey.class, 1L);
        one.property1 = 38L;
        one.property2 = 98L;
        final DynamicEntityKey keyOne = new DynamicEntityKey(one);
        one.setKey(keyOne);

        Object[] values = one.getKey().getKeyValues();
        assertEquals("Incorrect number of values.", 2, values.length);
        assertEquals("Incorrect value for the first key property.", 38L, values[0]);
        assertEquals("Incorrect value for the second key property.", 98L, values[1]);
        assertEquals("Incorrect value for the first key property.", (Long) 38L, one.getProperty1());
        assertEquals("Incorrect value for the second key property.", (Long) 98L, one.getProperty2());

        final CorrectEntityWithDynamicEntityKey copy = one.copy(CorrectEntityWithDynamicEntityKey.class);

        values = copy.getKey().getKeyValues();
        assertEquals("Incorrect number of values.", 2, values.length);
        assertEquals("Incorrect value for the first key property.", 38L, values[0]);
        assertEquals("Incorrect value for the second key property.", 98L, values[1]);
        assertEquals("Incorrect value for the first key property.", (Long) 38L, copy.getProperty1());
        assertEquals("Incorrect value for the second key property.", (Long) 98L, copy.getProperty2());

        copy.setProperty1(40L);
        copy.setProperty2(100L);

        values = copy.getKey().getKeyValues();
        assertEquals("Incorrect number of values.", 2, values.length);
        assertEquals("Incorrect value for the first key property.", 40L, values[0]);
        assertEquals("Incorrect value for the second key property.", 100L, values[1]);
        assertEquals("Incorrect value for the first key property.", (Long) 40L, copy.getProperty1());
        assertEquals("Incorrect value for the second key property.", (Long) 100L, copy.getProperty2());
    }

    @Test
    public void copy_from_non_instrumented_instance_is_also_non_instrumented() {
        final Entity entity = new Entity();
        entity.setVersion(42L);
        entity.setId(1L);
        entity.setKey("key");
        entity.setDesc("description");
        entity.setMoney(new Money("23.25"));

        final Entity copy = entity.copy(Entity.class);

        assertEquals("Copy does not equal to the original instance", entity, copy);
        assertFalse("Copy is instrumented", copy.isInstrumented());
        assertEquals("IDs do not match", entity.getId(), copy.getId());
        assertEquals("Versions do not match.", Long.valueOf(42L), copy.getVersion());
        assertEquals("Property desc does not match", entity.getDesc(), copy.getDesc());
        assertEquals("Property money does not match", entity.getMoney(), copy.getMoney());
    }
    
    @Test
    public void copy_from_instrumented_instance_is_also_instrumented() {
        final Entity entity = factory.newEntity(Entity.class);
        entity.setVersion(42L);
        entity.setId(1L);
        entity.setKey("key");
        entity.setDesc("description");
        entity.setMoney(new Money("23.25"));

        final Entity copy = entity.copy(Entity.class);

        assertEquals("Copy does not equal to the original instance", entity, copy);
        assertTrue("Copy is not instrumented", copy.isInstrumented());
        assertFalse("Copy is dirty", copy.isDirty());
        assertEquals("IDs do not match", entity.getId(), copy.getId());
        assertEquals("Versions do not match.", Long.valueOf(42L), copy.getVersion());
        assertEquals("Property desc does not match", entity.getDesc(), copy.getDesc());
        assertEquals("Property money does not match", entity.getMoney(), copy.getMoney());
    }
    
    @Test
    public void copy_from_uninstrumented_proxied_instance_is_also_uninstrumented_and_proxied() {
        final Class<? extends Entity> type = EntityProxyContainer.proxy(Entity.class, "firstProperty", "monitoring", "observableProperty");
        
        final Entity entity = EntityFactory.newPlainEntity(type, 12L);
        entity.setVersion(42L);
        entity.setKey("key");
        entity.setDesc("description");

        final Entity copy = entity.copy(type);
        assertEquals("Copy does not equal to the original instance", entity, copy);
        assertFalse("Copy is instrumented", copy.isInstrumented());
        assertEquals("Property id does not match", entity.getId(), copy.getId());
        assertEquals("Versions should match.", Long.valueOf(42L), copy.getVersion());
        assertEquals("Property desc does not match", entity.getDesc(), copy.getDesc());
        assertEquals("Proxied properties do not match", entity.proxiedPropertyNames(), copy.proxiedPropertyNames());
    }

    @Test
    public void copy_from_instrumented_proxied_instance_is_also_instrumented_and_proxied() {
        final Class<? extends Entity> type = EntityProxyContainer.proxy(Entity.class, "firstProperty", "monitoring", "observableProperty");
        
        final Entity entity = factory.newEntity(type, 12L);
        entity.setVersion(42L);
        entity.setKey("key");
        entity.setDesc("description");

        final Entity copy = entity.copy(type);
        assertEquals("Copy does not equal to the original instance", entity, copy);
        assertTrue("Copy is not instrumented", copy.isInstrumented());
        assertEquals("Property id does not match", entity.getId(), copy.getId());
        assertEquals("Versions should match.", Long.valueOf(42L), copy.getVersion());
        assertEquals("Property desc does not match", entity.getDesc(), copy.getDesc());
        assertEquals("Proxied properties do not match", entity.proxiedPropertyNames(), copy.proxiedPropertyNames());
    }

    @Test
    public void two_instances_of_the_same_type_with_the_same_key_values_are_equal() {
        final Entity thisEntity = factory.newEntity(Entity.class, 1L);
        thisEntity.setKey("key");
        final Entity thatEntity = factory.newEntity(Entity.class, 2L);
        thatEntity.setKey("key");

        assertTrue("Should be equal", thisEntity.equals(thatEntity));
        assertTrue("Should be equal", thatEntity.equals(thisEntity));
    }

    @Test
    public void test_equals_for_instances_of_the_same_type_with_the_different_key_values() {
        final Entity thisEntity = factory.newEntity(Entity.class, 1L);
        thisEntity.setKey("key1");
        final Entity thatEntity = factory.newEntity(Entity.class, 2L);
        thatEntity.setKey("key2");

        assertFalse("Should not be equal", thisEntity.equals(thatEntity));
        assertFalse("Should not be equal", thatEntity.equals(thisEntity));
    }

    @Test
    public void test_equals_for_instances_of_the_different_types_with_the_same_key_values() {
        final Entity thisEntity = factory.newEntity(EntityExt.class, 1L);
        thisEntity.setKey("key");
        final Entity thatEntity = factory.newEntity(Entity.class, 2L);
        thatEntity.setKey("key");

        assertFalse("Should not be equal", thisEntity.equals(thatEntity));
        assertFalse("Should not be equal", thatEntity.equals(thisEntity));
    }

    @Test
    public void test_that_set_method_works() {
        final SecondLevelEntity inst = new SecondLevelEntity();
        inst.setPropertyOfSelfType(inst);

        final SimplePartEntity simpleProperty = factory.newEntity(SimplePartEntity.class, 1L, "KEY");
        simpleProperty.setDesc("DESC");
        simpleProperty.setLevelEntity(inst);
        simpleProperty.setUncommonProperty("uncommon value");

        final UnionEntityForReflector unionEntity = factory.newEntity(UnionEntityForReflector.class);

        inst.set("property", "value");
        assertEquals("The property value of the SecondLevelInstance must be \"value\"", "value", inst.getProperty());
        unionEntity.set("simplePartEntity", simpleProperty);
        assertEquals("The simplePartEntity value must be equla to simpleProperty", simpleProperty, unionEntity.activeEntity());
        unionEntity.set("commonProperty", "another common value");
        assertEquals("The commonProperty value of the UnionEntity must be equla to \"another common value\"", "another common value", unionEntity.get("commonProperty"));
        try {
            unionEntity.set("uncommonProperty", "uncommon value");
            fail("There is no uncommonProperty in the UnionEntity");
        } catch (final Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void test_illegal_propertyDescriptor_property_definition_without_type_definition() {
        try {
            factory.newEntity(MasterEntity1.class, "key", "description");
            fail("Should be failed.");
        } catch (final IllegalStateException e) {
        }
    }

    @Test
    public void test_illegal_one2many_collectional_property_definition_without_type_definition() {
        try {
            factory.newEntity(MasterEntity2.class, "key", "description");
            fail("Should be failed.");
        } catch (final IllegalStateException e) {
        }
    }

    @Test
    public void test_illegal_one2many_collectional_property_definition_without_linkProperty_definition() {
        try {
            factory.newEntity(MasterEntity3.class, "key", "description");
            fail("Should be failed.");
        } catch (final IllegalStateException e) {
        }
    }

    @Test
    public void test_legal_single_property_definition_without_linkProperty_definition_which_makes_many2one_association() {
        factory.newEntity(MasterEntity4.class, "key", "description"); // should be considered legal
        assertFalse("Should be many2one association.", Finder.isOne2Many_or_One2One_association(MasterEntity4.class, "one2manyAssociationSpecialCase"));
    }

    @Test
    public void test_illegal_one2one_property_definition_with_KEY_of_non_parent_type() {
        try {
            factory.newEntity(MasterEntity6.class, "key", "description");
            fail("Should be failed.");
        } catch (final IllegalStateException e) {
        }
    }

    @Test
    public void sameAs_for_two_entities_with_same_id_should_return_true() {
        final Entity thisEntity = factory.newEntity(Entity.class, 1L, "key1", "description");
        final Entity thatEntity = factory.newEntity(Entity.class, 1L, "key2", "description");

        assertTrue(thisEntity.sameAs(thatEntity));
    }

    @Test
    public void sameAs_for_non_persisted_entities_with_the_same_keys_should_return_true() {
        final Entity thisEntity = factory.newEntity(Entity.class, "key", "description");
        final Entity thatEntity = factory.newEntity(Entity.class, "key", "description");

        assertTrue(thisEntity.sameAs(thatEntity));
    }

    @Test
    public void sameAs_for_non_persisted_entities_with_the_different_keys_should_return_false() {
        final Entity thisEntity = factory.newEntity(Entity.class, "key1", "description");
        final Entity thatEntity = factory.newEntity(Entity.class, "key2", "description");

        assertFalse(thisEntity.sameAs(thatEntity));
    }

    @Test
    public void sameAs_when_that_entity_is_null_should_return_false() {
        final Entity thisEntity = factory.newEntity(Entity.class, 1L, "key1", "description");

        assertFalse(thisEntity.sameAs(null));
    }

    @Test
    public void sameAs_when_that_entity_is_not_persisted_should_return_false() {
        final Entity thisEntity = factory.newEntity(Entity.class, 1L, "key", "description");
        final Entity thatEntity = factory.newEntity(Entity.class, "key", "description");

        assertFalse(thisEntity.sameAs(thatEntity));
    }

    @Test
    public void sameAs_for_non_persisted_instance_with_different_keys_should_return_false() {
        final Entity thisEntity = factory.newEntity(Entity.class, "key1", "description");
        final Entity thatEntity = factory.newEntity(Entity.class, "key2", "description");

        assertFalse(thisEntity.sameAs(thatEntity));
    }

    @Test
    public void required_desc_should_not_permit_empty_values() {
        final Entity entity = factory.newEntity(Entity.class, "key1", "description");
        assertTrue(entity.getProperty("desc").isValid());

        entity.setDesc("");
        assertFalse(entity.getProperty("desc").isValid());

    }

    @Test
    public void required_desc_should_not_permit_blank_values() {
        final Entity entity = factory.newEntity(Entity.class, "key1", "description");
        assertTrue(entity.getProperty("desc").isValid());

        entity.setDesc("    ");
        assertFalse(entity.getProperty("desc").isValid());
    }

    @Test
    public void required_desc_should_not_permit_null_values() {
        final Entity entity = factory.newEntity(Entity.class, "key1", "description");
        assertTrue(entity.getProperty("desc").isValid());

        entity.setDesc(null);
        assertFalse(entity.getProperty("desc").isValid());
    }

    @Test
    public void required_desc_should_not_be_valid_after_instantiation() {
        final Entity entity = factory.newByKey(Entity.class, "key1");

        final MetaProperty<String> descProperty = entity.getProperty("desc");
        assertFalse(descProperty.isValid());

        // let's also check that blank value is not permitted even when the original value was blank
        entity.setDesc("    ");
        assertFalse(descProperty.isValid());
    }

    @Test
    public void required_desc_should_support_custo_error_messages_with_templating() {
        final Entity entity = factory.newByKey(Entity.class, "key1");
        final MetaProperty<String> descProperty = entity.getProperty("desc");
        assertFalse(descProperty.isValid());
        assertEquals(
                format("Property \"%s\" in entity \"%s\" does not permit blank values.",
                        TitlesDescsGetter.getTitleAndDesc("desc", Entity.class).getKey(),
                        TitlesDescsGetter.getEntityTitleAndDesc(Entity.class).getKey()),
                descProperty.getFirstFailure().getMessage());
    }
    
    @Test
    public void warnings_are_empty_for_entity_without_any_properties_with_warnings() {
        final EntityWithWarnings entity = factory.newByKey(EntityWithWarnings.class, "some key");
        entity.setIntProp(20);
        
        assertFalse(entity.hasWarnings());
        assertTrue(entity.warnings().isEmpty());
    }
    
    @Test
    public void number_of_warnings_is_equal_to_number_of_entity_properties_with_warnings() {
        final EntityWithWarnings entity = factory.newByKey(EntityWithWarnings.class, "some key");
        entity.setSelfRefProp(entity);
        entity.setIntProp(120);
        
        assertTrue(entity.hasWarnings());
        assertEquals(2, entity.warnings().size());
    }
    
    @Test
    public void one_warning_is_identified_for_entity_with_one_property_in_error_and_one_property_with_warning() {
        final EntityWithWarnings entity = factory.newByKey(EntityWithWarnings.class, "some key");
        entity.setSelfRefProp(null);
        entity.setIntProp(120);
        
        assertFalse(entity.isValid().isSuccessful());
        assertTrue(entity.hasWarnings());
        assertEquals(1, entity.warnings().size());
    }
    
    @Test
    public void warnings_are_identified_correctly_after_reassigning_the_property_value_to_valid_one() {
        final EntityWithWarnings entity = factory.newByKey(EntityWithWarnings.class, "some key");

        // assign warning triggering value
        entity.setIntProp(120);
        assertTrue(entity.hasWarnings());
        assertEquals(1, entity.warnings().size());

        // assign warning clearing value
        entity.setIntProp(20);
        assertFalse(entity.hasWarnings());
        assertEquals(0, entity.warnings().size());
    }

}
