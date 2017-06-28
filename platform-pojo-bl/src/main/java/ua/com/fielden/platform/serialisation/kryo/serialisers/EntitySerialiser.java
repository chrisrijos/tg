package ua.com.fielden.platform.serialisation.kryo.serialisers;

import static ua.com.fielden.platform.serialisation.kryo.IoHelper.ENTITY_REFERENCES;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.reflection.AnnotationReflector;
import ua.com.fielden.platform.reflection.Finder;
import ua.com.fielden.platform.reflection.PropertyTypeDeterminator;
import ua.com.fielden.platform.reflection.asm.impl.DynamicEntityClassLoader;
import ua.com.fielden.platform.utils.EntityUtils;

import com.esotericsoftware.kryo.Context;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.RegisteredClass;
import com.esotericsoftware.kryo.SerializationException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serialize.IntSerializer;
import com.esotericsoftware.kryo.serialize.LongSerializer;

/**
 * Serialises descendants of {@link AbstractEntity}.
 *
 * @author TG Team
 *
 */
public final class EntitySerialiser extends Serializer {

    private static final Logger logger = Logger.getLogger(EntitySerialiser.class);

    private static final byte NULL_NOT_DIRTY = 1;
    private static final byte NULL_DIRTY = 2;
    private static final byte NOT_NULL_NOT_DIRTY = 3;
    private static final byte NOT_NULL_DIRTY = 4;

    private final Class<AbstractEntity> type;
    private final Field versionField;
    private final List<CachedProperty> properties;
    private final EntityFactory factory; // is used during read (i.e. deserialisation)

    private final Kryo kryo;

    public EntitySerialiser(final Kryo kryo, final Class<AbstractEntity> type, final EntityFactory factory) {
        this.kryo = kryo;
        this.type = type;
        this.factory = factory;

        versionField = Finder.findFieldByName(type, AbstractEntity.VERSION);
        versionField.setAccessible(true);

        // cache all properties annotated with @IsProperty
        properties = createCachedProperties(type);
    }

    private List<CachedProperty> createCachedProperties(final Class<AbstractEntity> type) {
        final boolean hasCompositeKey = EntityUtils.isCompositeEntity(type);
        final List<CachedProperty> properties = new ArrayList<CachedProperty>();
        for (final Field propertyField : Finder.findRealProperties(type)) {
            // take into account only persistent properties
            //if (!propertyField.isAnnotationPresent(Calculated.class)) {
            propertyField.setAccessible(true);
            // need to handle property key in a special way -- composite key does not have to be serialised
            if (AbstractEntity.KEY.equals(propertyField.getName())) {
                if (!hasCompositeKey) {
                    final CachedProperty prop = new CachedProperty(propertyField);
                    properties.add(prop);
                    final Class<?> fieldType = AnnotationReflector.getKeyType(type);
                    final int modifiers = fieldType.getModifiers();
                    if (!Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers)) {
                        prop.setPropertyType(fieldType);
                    }
                }
            } else {
                final CachedProperty prop = new CachedProperty(propertyField);
                properties.add(prop);
                final Class<?> fieldType = PropertyTypeDeterminator.stripIfNeeded(propertyField.getType());
                final int modifiers = fieldType.getModifiers();
                if (!Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers)) {
                    prop.setPropertyType(fieldType);
                }
            }
            //}
        }
        return properties;
    }

    @Override
    public void writeObjectData(final ByteBuffer buffer, final Object instance) {
        ////////////////////////////////////////////////////
        ///////////////// handle references ////////////////
        ////////////////////////////////////////////////////
        final Context context = Kryo.getContext();
        References references = (References) context.getTemp(ENTITY_REFERENCES);
        if (references == null) {
            // Use non-temporary storage to avoid repeated allocation.
            references = (References) context.get(ENTITY_REFERENCES);
            if (references == null) {
                context.put(ENTITY_REFERENCES, references = new References());
            } else {
                references.reset();
            }
            context.putTemp(ENTITY_REFERENCES, references);
        }
        final Integer reference = references.objectToReference.get(instance);
        if (reference != null) {
            IntSerializer.put(buffer, reference, true);
            return;
        }
        
        IntSerializer.put(buffer, 0, true);
        references.referenceCount++;
        references.objectToReference.put(instance, references.referenceCount);

        //////////////////////////////////////////////////////
        //////////////// perform serialisation ///////////////
        //////////////////////////////////////////////////////

        // save all property value with dirty attribute
        final AbstractEntity<?> entity = type.cast(instance);
        // serialise id
        if (entity.isPersisted()) {
            buffer.put(NOT_NULL_NOT_DIRTY);
            LongSerializer.put(buffer, entity.getId(), false);
        } else {
            buffer.put(NULL_NOT_DIRTY);
        }
        // serialise version -- should never be null
        buffer.put(NOT_NULL_DIRTY); // the actual put value to indicate nullness or dirtiness for version is irrelevant
        LongSerializer.put(buffer, entity.getVersion(), false);

        // serialise all the properties relying on the fact that property sequence is consistent with order of fields in the class declaration
        String lastProperty = null;
        try {
            for (final CachedProperty prop : properties) {
                // non-composite keys should be persisted by identifying their actual type
                final String name = prop.field.getName();
                lastProperty = name;
                
                Object protoValue = prop.field.get(entity);
                if (protoValue instanceof AbstractEntity) {
                    protoValue = ((AbstractEntity<?>) protoValue).isIdOnlyProxy() ? null : protoValue; 
                }
                
                final Object value =  protoValue;
                final Optional<MetaProperty<?>> metaProp = entity.getPropertyOptionally(name);
                final boolean dirty = !metaProp.isPresent() || metaProp.get().isProxy() ? false : metaProp.get().isDirty();

                if (prop.propertyType != null) {
                    if (prop.serialiser == null) {
                        prop.serialiser = kryo.getRegisteredClass(prop.propertyType).getSerializer();
                    }
                }

                if (dirty && value != null) {
                    buffer.put(NOT_NULL_DIRTY);

                    Serializer serializer = prop.serialiser;
                    if (serializer == null) {
                        final RegisteredClass registeredClass = kryo.writeClass(buffer, value.getClass());
                        serializer = registeredClass.getSerializer();
                    }
                    serializer.writeObjectData(buffer, value);
                } else if (dirty && value == null) {
                    buffer.put(NULL_DIRTY);
                } else {
                    // TODO Theoretically the following two conditions can be removed when serialising from the client side due to the fact that server can retrieve the data from db if required
                    if (!dirty && value != null) {
                        buffer.put(NOT_NULL_NOT_DIRTY);
                        Serializer serializer = prop.serialiser;
                        if (serializer == null) {
                            final RegisteredClass registeredClass = kryo.writeClass(buffer, value.getClass());
                            serializer = registeredClass.getSerializer();
                        }
                        serializer.writeObjectData(buffer, value);
                    } else if (!dirty && value == null) {
                        buffer.put(NULL_NOT_DIRTY);
                    }
                }
            }
        } catch (final Exception ex) {
            logger.error("Could not serialise property " + lastProperty);
            throw new SerializationException(ex);
        }

    }

    @Override
    public <T> T readObjectData(final ByteBuffer buffer, final Class<T> clazz) {
        try {
            final Context context = Kryo.getContext();
            References references = (References) context.getTemp(ENTITY_REFERENCES);
            if (references == null) {
                // Use non-temporary storage to avoid repeated allocation.
                references = (References) context.get(ENTITY_REFERENCES);
                if (references == null) {
                    context.put(ENTITY_REFERENCES, references = new References());
                } else {
                    references.reset();
                }
                context.putTemp(ENTITY_REFERENCES, references);
            }

            final int reference = IntSerializer.get(buffer, true);
            if (reference != 0) {
                final T object = (T) references.referenceToObject.get(reference);
                if (object == null) {
                    throw new SerializationException("Invalid object reference: " + reference);
                }
                return object;
            }

            // 1. read Id
            final Long id = NOT_NULL_NOT_DIRTY == buffer.get() ? LongSerializer.get(buffer, false) : null;
            final AbstractEntity<?> entity;

            if (DynamicEntityClassLoader.isGenerated(type)) {
                entity = EntityFactory.newPlainEntity(type, id);
                entity.setEntityFactory(factory);
            } else {
                entity = factory.newEntity(type, id);
            }

            references.referenceCount++;
            references.referenceToObject.put(references.referenceCount, entity);

            // 2. read and set version
            buffer.get(); // read to just move to the next value
            versionField.set(entity, LongSerializer.get(buffer, false));

            // 3. read the rest of properties

            for (final CachedProperty prop : properties) {
                final byte attr = buffer.get();

                switch (attr) {
                case NOT_NULL_DIRTY:
                    final MetaProperty mp = readProperty(buffer, entity, prop);
                    if (mp != null) {
                        mp.setDirty(true);
                    }
                    break;
                case NULL_DIRTY:
                    if (!DynamicEntityClassLoader.isGenerated(type)) {
                        entity.getProperty(prop.field.getName()).setOriginalValue(null).setDirty(true);
                    }
                    break;
                case NOT_NULL_NOT_DIRTY:
                    readProperty(buffer, entity, prop);
                    break;
                case NULL_NOT_DIRTY:
                    if (!DynamicEntityClassLoader.isGenerated(type)) {
                        entity.getProperty(prop.field.getName()).setOriginalValue(null);
                    }
                    break;

                default:
                    throw new SerializationException("EntitySerialiser could not correctly identify state attribute for property " + prop.field.getName()
                            + " when reading entity of type " + type.getName());
                }

            }
            return (T) entity;
        } catch (final Exception ex) {
            ex.printStackTrace();
            throw new SerializationException("Could not deserialise instance of type " + clazz.getName() + ": " + ex.getMessage());
        }
    }

    /**
     * Read a non-null value from the buffer and assigns it to the property. Also, handles assignment of the restored value as the original for meta-property.
     *
     * @param buffer
     * @param entity
     * @param prop
     * @return
     * @throws Exception
     */
    private final MetaProperty readProperty(final ByteBuffer buffer, final AbstractEntity<?> entity, final CachedProperty prop) throws Exception {
        if (prop.propertyType != null) {
            if (prop.serialiser == null) {
                prop.serialiser = kryo.getRegisteredClass(prop.propertyType).getSerializer();
            }
        }

        Serializer serializer = prop.serialiser;
        Class<?> concreteType = prop.propertyType;

        if (serializer == null) {
            final RegisteredClass registeredClass = kryo.readClass(buffer);
            concreteType = registeredClass.getType();
            serializer = registeredClass.getSerializer();
        }
        final Object value = serializer.readObjectData(buffer, concreteType);
        prop.field.set(entity, value);

        return !DynamicEntityClassLoader.isGenerated(type) ? entity.getProperty(prop.field.getName()).setOriginalValue(value) : null;
    }

    /**
     * A convenient class to store property related information.
     *
     * Serialiser is initialised lazily, which is required to avoid issues with class registration order.
     *
     * @author TG Team
     *
     */
    private final class CachedProperty {
        final Field field;
        private Serializer serialiser;
        private Class<?> propertyType;

        CachedProperty(final Field field) {
            this.field = field;
        }

        public Class<?> getPropertyType() {
            return propertyType;
        }

        public void setPropertyType(final Class<?> type) {
            this.propertyType = type;
        }

        public Serializer getSerialiser() {
            return serialiser;
        }

        public void setSerialiser(final Serializer serialiser) {
            this.serialiser = serialiser;
        }
    }

}
