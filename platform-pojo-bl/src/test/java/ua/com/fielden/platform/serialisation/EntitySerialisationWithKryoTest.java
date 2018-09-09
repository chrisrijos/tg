package ua.com.fielden.platform.serialisation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import ua.com.fielden.platform.domaintree.impl.DomainTreeEnhancerCache;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.ClassWithMap;
import ua.com.fielden.platform.entity.Entity;
import ua.com.fielden.platform.entity.EntityWithByteArray;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.meta.PropertyDescriptor;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.error.Warning;
import ua.com.fielden.platform.ioc.ApplicationInjectorFactory;
import ua.com.fielden.platform.serialisation.api.ISerialiserEngine;
import ua.com.fielden.platform.serialisation.api.SerialiserEngines;
import ua.com.fielden.platform.serialisation.api.impl.ProvidedSerialisationClassProvider;
import ua.com.fielden.platform.serialisation.api.impl.Serialiser;
import ua.com.fielden.platform.serialisation.entity.BaseEntity;
import ua.com.fielden.platform.serialisation.entity.EntityWithPolymorphicProperty;
import ua.com.fielden.platform.serialisation.entity.EntityWithQueryProperty;
import ua.com.fielden.platform.serialisation.entity.SubBaseEntity1;
import ua.com.fielden.platform.serialisation.entity.SubBaseEntity2;
import ua.com.fielden.platform.test.CommonTestEntityModuleWithPropertyFactory;
import ua.com.fielden.platform.types.Money;

import com.esotericsoftware.kryo.Kryo;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Unit tests to ensure correct serialised/deserialised of {@link AbstractEntity} descendants.
 *
 * @author TG Team
 *
 */
public class EntitySerialisationWithKryoTest {
    private final Module module = new CommonTestEntityModuleWithPropertyFactory();
    private final Injector injector = new ApplicationInjectorFactory().add(module).getInjector();
    private final EntityFactory factory = injector.getInstance(EntityFactory.class);
    private final ISerialiserEngine kryoWriter = new Serialiser(factory, new ProvidedSerialisationClassProvider(Entity.class, ClassWithMap.class, EntityWithPolymorphicProperty.class, BaseEntity.class, SubBaseEntity1.class, SubBaseEntity2.class, EntityWithByteArray.class, EntityWithQueryProperty.class), new DomainTreeEnhancerCache()).getEngine(SerialiserEngines.KRYO);
    private final ISerialiserEngine kryoReader = new Serialiser(factory, new ProvidedSerialisationClassProvider(Entity.class, ClassWithMap.class, EntityWithPolymorphicProperty.class, BaseEntity.class, SubBaseEntity1.class, SubBaseEntity2.class, EntityWithByteArray.class, EntityWithQueryProperty.class), new DomainTreeEnhancerCache()).getEngine(SerialiserEngines.KRYO);
    private Entity entity;
    private Entity entityForResult;
    private Entity entForProp;

    @Before
    public void setUp() {
        entityForResult = factory.newEntity(Entity.class, 1L, "key", "description");
        assertFalse("Property should not be dirty.", entityForResult.getProperty("dependent").isDirty()); // has default value
        entityForResult.setDirty(false);
        assertFalse("Entity should not be dirty.", entityForResult.isDirty());
        // now let's change some properties to make entity dirty
        assertFalse("Property should not be dirty.", entityForResult.getProperty("date").isDirty()); // not yet set
        entityForResult.setDate(new Date());
        assertTrue("Property should be dirty.", entityForResult.getProperty("date").isDirty()); // value has changed
        // assign entity property
        entForProp = factory.newEntity(Entity.class, "key-1", "description");
        entForProp.setEntity(factory.newEntity(Entity.class, "key-1-1", "description"));
        entityForResult.setEntity(entForProp);
        // assign collectional entity property
        entityForResult.setEntities(new ArrayList<Entity>() {
            {
                add(factory.newEntity(Entity.class, "key-2", "description"));
                add(factory.newEntity(Entity.class, "key-3", "description"));
                add(factory.newEntity(Entity.class, "key-4", "description"));
            }
        });
        // assign collectional property
        entityForResult.addToDoubles(23.).addToDoubles(45.);
        entityForResult.setMoney(new Money("23.00", Currency.getInstance("AUD")));
    }

    @Test
    public void test_marshaling_unmarshalling_of_entities() throws Exception {
        //////////////////////////////////////////////////
        ///////////// set up the data ////////////////////
        //////////////////////////////////////////////////
        entity = factory.newEntity(Entity.class, 1L, "key", "description");
        // assign entity property
        final Entity ent = factory.newEntity(Entity.class, "key-1", "description");
        ent.setEntity(factory.newEntity(Entity.class, "key-1-1", "description"));
        entity.setEntity(ent);
        // assign collectional entity property
        entity.setEntities(new ArrayList<Entity>() {
            {
                add(factory.newEntity(Entity.class, "key-2", "description"));
                add(factory.newEntity(Entity.class, "key-3", "description"));
                add(factory.newEntity(Entity.class, "key-4", "description"));
            }
        });
        // assign collectional property
        entity.addToDoubles(23.).addToDoubles(45.);
        entity.setMoney(new Money("23.00", 20, Currency.getInstance("AUD")));
        // assign property, which is not collectional, instance of AE or key
        final Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("one", 1);
        map.put("two", 2);
        entity.setClassWithMapProp(new ClassWithMap(new HashMap<String, Integer>(map)));

        //////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////// serialise entity /////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////
        final ByteBuffer writeBuffer = ByteBuffer.allocate(10485760);
        ((Kryo) kryoWriter).writeObject(writeBuffer, entity);
        writeBuffer.flip();
        final byte[] data = new byte[writeBuffer.limit()];
        writeBuffer.get(data);
        writeBuffer.clear();

        //////////////////////////////////////////////////////////////////////////////////////////
        /////////////// deserialise entity from the byte array and test it ///////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////
        final ByteBuffer readBuffer = ByteBuffer.wrap(data);
        final Entity restoredEntity = ((Kryo) kryoReader).readObject(readBuffer, Entity.class);

        assertEquals("'key' should be equal.", entity.getKey(), restoredEntity.getKey());
        assertEquals("'observableProperty' has incorrect value", new Double(0.0), restoredEntity.getObservableProperty());
        restoredEntity.setObservableProperty(22.0);

        // test property of entity type
        assertEquals("'entity' has incorrect value", entity.getEntity(), restoredEntity.getEntity());
        assertFalse("'entity' has incorrect value", entity.getEntity() == restoredEntity.getEntity());
        assertEquals("'entity' has incorrect type", "ua.com.fielden.platform.entity.Entity", restoredEntity.getEntity().getType().getName());
        assertTrue("'entity' has incorrect type", "ua.com.fielden.platform.entity.Entity".equals(restoredEntity.getEntity().getClass().getName()));
        // test sub-property of entity type
        assertEquals("'entity' has incorrect value", entity.getEntity().getEntity(), restoredEntity.getEntity().getEntity());
        assertFalse("'entity' has incorrect value", entity.getEntity().getEntity() == restoredEntity.getEntity().getEntity());
        assertEquals("'entity' has incorrect type", "ua.com.fielden.platform.entity.Entity", restoredEntity.getEntity().getEntity().getType().getName());

        // test collectional property of entity type
        assertFalse("'entities' should have different addresses.", entity.getEntities() == restoredEntity.getEntities());
        assertEquals("'entities' should have the same size.", entity.getEntities().size(), restoredEntity.getEntities().size());
        final Iterator<Entity> cleanEntityIter = entity.getEntities().iterator();
        final Iterator<Entity> enhancedEntityIter = restoredEntity.getEntities().iterator();
        while (cleanEntityIter.hasNext()) {
            final Entity cleanMember = cleanEntityIter.next();
            final Entity enhancedMember = enhancedEntityIter.next();
            assertEquals("'entities' members should be equal", cleanMember, enhancedMember);
            assertFalse("'entities' members should not be == (equal)", cleanMember == enhancedMember);
        }

        // test collectional property of non-entity type
        assertFalse("'doubles' should have different addresses.", entity.getDoubles() == restoredEntity.getDoubles());
        assertEquals("'doubles' should have the same size.", entity.getDoubles().size(), restoredEntity.getDoubles().size());
        final Iterator<Double> cleanEntityDoubleIter = entity.getDoubles().iterator();
        final Iterator<Double> enhancedEntityDoubleIter = restoredEntity.getDoubles().iterator();
        while (cleanEntityDoubleIter.hasNext()) {
            final Double cleanMember = cleanEntityDoubleIter.next();
            final Double enhancedMember = enhancedEntityDoubleIter.next();
            assertEquals("'doubles' members should be equal", cleanMember, enhancedMember);
        }

        // test non-collectional and non-AE-descendant property serialisation
        assertEquals("Property of type " + ClassWithMap.class.getName() + " was not serialised correctly.", entity.getClassWithMapProp().getMapProp(), restoredEntity.getClassWithMapProp().getMapProp());
    }

    @Test
    public void serialisation_of_entity_with_query_property() {
        final EntityWithQueryProperty entity = factory.newEntity(EntityWithQueryProperty.class, 1L, "key", "description");
        final EntityResultQueryModel<BaseEntity> model = select(BaseEntity.class).model();
        entity.setQuery(model);

        final ByteBuffer writeBuffer = ByteBuffer.allocate(10485760);
        ((Kryo) kryoWriter).writeObject(writeBuffer, entity);
        writeBuffer.flip();
        final byte[] data = new byte[writeBuffer.limit()];
        writeBuffer.get(data);
        writeBuffer.clear();

        final ByteBuffer readBuffer = ByteBuffer.wrap(data);
        final EntityWithQueryProperty restoredEntity = ((Kryo) kryoReader).readObject(readBuffer, EntityWithQueryProperty.class);
        assertNotNull(restoredEntity.getQuery());
    }

    @Test
    public void test_marshaling_of_result_without_exception() throws Exception {
        final Result result = new Result(entityForResult, "All cool.");

        //////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////// serialise result /////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////
        final ByteBuffer writeBuffer = ByteBuffer.allocate(10485760);
        ((Kryo) kryoWriter).writeObject(writeBuffer, result);
        writeBuffer.flip();
        final byte[] data = new byte[writeBuffer.limit()];
        writeBuffer.get(data);
        writeBuffer.clear();

        //////////////////////////////////////////////////////////////////////////////////////////
        /////////////// deserialise result from the byte array and test it ///////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////
        final ByteBuffer readBuffer = ByteBuffer.wrap(data);
        final Result restoredResult = ((Kryo) kryoReader).readObject(readBuffer, Result.class);
        // testing successful result serialisation
        assertNotNull("Restored result could not be null", restoredResult);
        assertNull("Restored result should not have exception", restoredResult.getEx());
        assertNotNull("Restored result should have message", restoredResult.getMessage());
        assertNotNull("Restored result should have instance", restoredResult.getInstance());
        assertEquals("Incorrect value for property entity.", entForProp, ((Entity) restoredResult.getInstance()).getEntity());
    }

    @Test
    public void test_marshaling_of_result_with_exception() throws Exception {
        final Result resultWithEx = new Result(entityForResult, new Exception("exception message"));
        final Result restoredResultWithEx = kryoReader.deserialise(kryoWriter.serialise(resultWithEx), Result.class);

        assertNotNull("Restored result could not be null", restoredResultWithEx);
        assertNotNull("Restored result should have exception", restoredResultWithEx.getEx());
        assertNotNull("Restored result should have message", restoredResultWithEx.getMessage());
        assertNotNull("Restored result should have instance", restoredResultWithEx.getInstance());
    }

    @Test
    public void test_marshaling_of_warning_result() throws Exception {
        final Warning restoredWarning = kryoReader.deserialise(kryoWriter.serialise(new Warning(entityForResult, "Warning message.")), Warning.class);

        assertNotNull("Restored warning could not be null", restoredWarning);
        assertNotNull("Restored warning should have message", restoredWarning.getMessage());
        assertNotNull("Restored warning should have instance", restoredWarning.getInstance());
        assertEquals("Incorrect value for property entity.", entForProp, ((Entity) restoredWarning.getInstance()).getEntity());
    }

    @Test
    public void test_marshaling_of_property_descriptor() throws Exception {
        final Entity entity = factory.newEntity(Entity.class, 1L, "key", "description");
        final PropertyDescriptor<Entity> pd = new PropertyDescriptor<Entity>(Entity.class, "key");
        entity.setPropertyDescriptor(pd);
        final Result result = new Result(entity, "All cool.");

        final Result restoredResult = kryoReader.deserialise(kryoWriter.serialise(result), Result.class);

        assertNotNull("Restored result could not be null", restoredResult);
        assertNull("Restored result should not have exception", restoredResult.getEx());
        assertNotNull("Restored result should have message", restoredResult.getMessage());
        assertNotNull("Restored result should have instance", restoredResult.getInstance());
        assertEquals("Incorrectly unmarshaled property descriptor.", pd, ((Entity) restoredResult.getInstance()).getPropertyDescriptor());
    }

    @Test
    public void test_joda_interval_serialisation() throws Exception {
        final Interval interval = kryoReader.deserialise(kryoWriter.serialise(new Interval(0, 100)), Interval.class);

        assertNotNull("Interval has not been deserialised successfully.", interval);
        assertEquals("Incorrect start.", 0L, interval.getStartMillis());
        assertEquals("Incorrect end.", 100L, interval.getEndMillis());
    }

    @Test
    public void test_serialisation_of_entity_with_polymorphyc_property() throws Exception {
        final EntityWithPolymorphicProperty entity = factory.newEntity(EntityWithPolymorphicProperty.class, 1L, "key", "description");
        entity.setPolyProperty(factory.newEntity(SubBaseEntity1.class, 1L, "key", "description"));

        try {
            kryoReader.deserialise(kryoWriter.serialise(entity), EntityWithPolymorphicProperty.class);
        } catch (final Exception ex) {
            ex.printStackTrace();
            fail("Failed to serialise entity with polymorphyc property.");
        }

    }

    @Test
    public void test_deserialisation_when_specifying_ancestor_as_the_type() throws Exception {
        final Entity entity = factory.newEntity(Entity.class, 1L, "key", "description");

        final AbstractEntity<?> restoredEntity = kryoReader.deserialise(kryoWriter.serialise(entity), AbstractEntity.class);

        assertNotNull("Restored entity could not be null", restoredEntity);
        assertEquals("Incorrectly restored key.", "key", restoredEntity.getKey());
        assertEquals("Incorrectly restored description.", "description", restoredEntity.getDesc());
    }

    @Test
    public void test_serialisation_for_entity_with_byte_array_property() throws Exception {
        final EntityWithByteArray entity = factory.newEntity(EntityWithByteArray.class, "key", "description");
        entity.setByteArray(new byte[] { 1, 2, 3 });

        final AbstractEntity<?> restoredEntity = kryoReader.deserialise(kryoWriter.serialise(entity), AbstractEntity.class);

        assertNotNull("Restored entity could not be null", restoredEntity);
        assertEquals("Incorrectly restored key.", "key", restoredEntity.getKey());
        assertEquals("Incorrectly restored description.", "description", restoredEntity.getDesc());
        assertTrue("Byte array property was not serialised or deserialised properly.", Arrays.equals(new byte[] { 1, 2, 3 }, entity.getByteArray()));
    }

}
