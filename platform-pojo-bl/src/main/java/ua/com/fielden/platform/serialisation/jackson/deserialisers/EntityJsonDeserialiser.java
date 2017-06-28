package ua.com.fielden.platform.serialisation.jackson.deserialisers;

import static java.lang.String.format;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.entity.meta.PropertyDescriptor;
import ua.com.fielden.platform.entity.proxy.EntityProxyContainer;
import ua.com.fielden.platform.entity.proxy.IIdOnlyProxiedEntityTypeCache;
import ua.com.fielden.platform.reflection.AnnotationReflector;
import ua.com.fielden.platform.reflection.Finder;
import ua.com.fielden.platform.reflection.PropertyTypeDeterminator;
import ua.com.fielden.platform.serialisation.api.ISerialisationTypeEncoder;
import ua.com.fielden.platform.serialisation.api.impl.TgJackson;
import ua.com.fielden.platform.serialisation.jackson.EntitySerialiser;
import ua.com.fielden.platform.serialisation.jackson.EntitySerialiser.CachedProperty;
import ua.com.fielden.platform.serialisation.jackson.JacksonContext;
import ua.com.fielden.platform.serialisation.jackson.References;
import ua.com.fielden.platform.serialisation.jackson.exceptions.EntityDeserialisationException;
import ua.com.fielden.platform.utils.EntityUtils;
import static ua.com.fielden.platform.serialisation.jackson.EntitySerialiser.ID_ONLY_PROXY_PREFIX;

public class EntityJsonDeserialiser<T extends AbstractEntity<?>> extends StdDeserializer<T> {
    private static final long serialVersionUID = 1L;
    private final EntityFactory factory;
    private final ObjectMapper mapper;
    private final Field versionField;
    private final Class<T> type;
    private final Logger logger = Logger.getLogger(getClass());
    private final List<CachedProperty> properties;
    private final ISerialisationTypeEncoder serialisationTypeEncoder;
    private final boolean propertyDescriptorType;
    private final IIdOnlyProxiedEntityTypeCache idOnlyProxiedEntityTypeCache;

    public EntityJsonDeserialiser(final ObjectMapper mapper, final EntityFactory entityFactory, final Class<T> type, final List<CachedProperty> properties, final ISerialisationTypeEncoder serialisationTypeEncoder, final IIdOnlyProxiedEntityTypeCache idOnlyProxiedEntityTypeCache, final boolean propertyDescriptorType) {
        super(type);
        this.factory = entityFactory;
        this.mapper = mapper;
        this.properties = properties;
        this.serialisationTypeEncoder = serialisationTypeEncoder;
        this.idOnlyProxiedEntityTypeCache = idOnlyProxiedEntityTypeCache;

        this.type = type;
        versionField = Finder.findFieldByName(type, AbstractEntity.VERSION);
        versionField.setAccessible(true);
        
        this.propertyDescriptorType = propertyDescriptorType;
    }

    @Override
    public T deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final JacksonContext context = EntitySerialiser.getContext();
        References references = (References) context.getTemp(EntitySerialiser.ENTITY_JACKSON_REFERENCES);
        if (references == null) {
            // Use non-temporary storage to avoid repeated allocation.
            references = (References) context.get(EntitySerialiser.ENTITY_JACKSON_REFERENCES);
            if (references == null) {
                context.put(EntitySerialiser.ENTITY_JACKSON_REFERENCES, references = new References());
            } else {
                references.reset();
            }
            context.putTemp(EntitySerialiser.ENTITY_JACKSON_REFERENCES, references);
        }

        final JsonNode node = jp.readValueAsTree();
        // final int reference = IntSerializer.get(buffer, true);

        if (node.isObject() && node.get("@id_ref") != null) {
            final String reference = node.get("@id_ref").asText();
            return (T) references.getEntity(reference);
        } else {
            //            // deserialise type
            //            final Class<T> type = (Class<T>) findClass(node.get("@entityType").asText());

            // deserialise id
            final JsonNode idJsonNode = node.get(AbstractEntity.ID); // the node should not be null itself
            final Long id = idJsonNode.isNull() ? null : idJsonNode.asLong();
            
            final JsonNode uninstrumentedJsonNode = node.get("@uninstrumented");
            final boolean uninstrumented = uninstrumentedJsonNode != null;

            final String[] proxiedProps = properties.stream()
                    .map(cachedProp -> cachedProp.field().getName())
                    .filter(prop -> node.get(prop) == null)
                    .collect(Collectors.toList())
                    .toArray(new String[] {});
            final T entity;
            if (uninstrumented) {
                if (propertyDescriptorType) {
                    entity = (T) PropertyDescriptor.fromString(node.get("@pdString").asText());
                } else {
                    entity = EntityFactory.newPlainEntity(EntityProxyContainer.proxy(type, proxiedProps), id);
                }
            } else {
                if (propertyDescriptorType) {
                    entity = (T) PropertyDescriptor.fromString(node.get("@pdString").asText(), Optional.of(factory));
                } else {
                    entity = factory.newEntity(EntityProxyContainer.proxy(type, proxiedProps), id);
                }
            }
            entity.beginInitialising();

            final JsonNode atIdNode = node.get("@id");
            // At this stage 'clientSideReference' has been already decoded using ISerialisationTypeEncoder, that is why concrete EntityJsonDeserialiser has been chosen for deserialisation
            // Method determineValue is doing the necessary type determination with the usage of TgJackson.extractConcreteType method.
            final String clientSideReference = atIdNode == null ? null : atIdNode.asText();
            references.putEntity(clientSideReference, entity);

            // deserialise version -- should never be null
            final JsonNode versionJsonNode = node.get(AbstractEntity.VERSION); // the node should not be null itself
            //            if (versionJsonNode.isNull()) {
            //                throw new IllegalStateException("EntitySerialiser has got null 'version' property when reading entity of type [" + type.getName() + "].");
            //            }
            if (!versionJsonNode.isNull()) {
                final Long version = versionJsonNode.asLong();

                try {
                    // at this stage the field should be already accessible
                    versionField.set(entity, version);
                } catch (final IllegalAccessException e) {
                    // developer error -- please ensure that all fields are accessible
                    e.printStackTrace();
                    logger.error("The field [" + versionField + "] is not accessible. Fatal error during deserialisation process for entity [" + entity + "].", e);
                    throw new RuntimeException(e);
                } catch (final IllegalArgumentException e) {
                    e.printStackTrace();
                    logger.error("The field [" + versionField + "] is not declared in entity with type [" + type.getName() + "]. Fatal error during deserialisation process for entity [" + entity + "].", e);
                    throw e;
                }
            }

            final List<CachedProperty> nonProxiedProps = properties.stream().filter(prop -> node.get(prop.field().getName()) != null).collect(Collectors.toList()); 
            for (final CachedProperty prop : nonProxiedProps) {
                final String propertyName = prop.field().getName();
                final JsonNode propNode = node.get(propertyName);
                final Object value = determineValue(propNode, prop.field());
                try {
                    // at this stage the field should be already accessible
                    prop.field().set(entity, value);
                } catch (final IllegalAccessException e) {
                    // developer error -- please ensure that all fields are accessible
                    e.printStackTrace();
                    logger.error("The field [" + prop.field() + "] is not accessible. Fatal error during deserialisation process for entity [" + entity + "].", e);
                    throw new RuntimeException(e);
                } catch (final IllegalArgumentException e) {
                    e.printStackTrace();
                    logger.error("The field [" + prop.field() + "] is not declared in entity with type [" + type.getName() + "]. Fatal error during deserialisation process for entity [" + entity + "].", e);
                    throw e;
                }
                final JsonNode metaPropNode = node.get("@" + propertyName);
                if (metaPropNode != null) {
                    if (metaPropNode.isNull()) {
                        throw new IllegalStateException("EntitySerialiser has got null meta property '@" + propertyName + "' when reading entity of type [" + type.getName() + "].");
                    }
                    final Optional<MetaProperty<?>> metaProperty = entity.getPropertyOptionally(propertyName);
                    provideChangedFromOriginal(metaProperty, propertyName, metaPropNode);
                    provideEditable(metaProperty, propertyName, metaPropNode);
                    provideRequired(metaProperty, propertyName, metaPropNode);
                    provideVisible(metaProperty, propertyName, metaPropNode);
                }
            }

            return entity;
        }
    }
    
    private Object determineValue(final JsonNode propNode, final Field propertyField) throws IOException, JsonMappingException, JsonParseException {
        final Object value;
        if (propNode.isNull()) {
            value = null;
        } else {
            final JavaType concreteType = concreteTypeOf(constructType(mapper.getTypeFactory(), propertyField), () -> {
                return propNode.get("@id") == null ? propNode.get("@id_ref") : propNode.get("@id");
            });
            if (propNode.isTextual() && EntityUtils.isEntityType(concreteType.getRawClass()) && propNode.asText().startsWith(ID_ONLY_PROXY_PREFIX)) { // id-only proxy instance is represented as id-only proxy prefix concatenated with id number
                final Long determinedId = Long.valueOf(propNode.asText().replaceFirst(Pattern.quote(ID_ONLY_PROXY_PREFIX), ""));
                value = EntityFactory.newPlainEntity(idOnlyProxiedEntityTypeCache.getIdOnlyProxiedTypeFor((Class) concreteType.getRawClass()), determinedId);
            } else {
                value = mapper.readValue(propNode.traverse(mapper), concreteType);
            }
        }
        return value;
    }

    /**
     * Extracts concrete type for property based on constructed type (perhaps abstract).
     *
     * @param constructedType
     * @param idNodeSupplier
     * @return
     */
    private JavaType concreteTypeOf(final ResolvedType constructedType, final Supplier<JsonNode> idNodeSupplier) {
        return TgJackson.extractConcreteType(constructedType, idNodeSupplier, mapper.getTypeFactory(), serialisationTypeEncoder);
    }

    /**
     * Retrieves 'dirty' value from entity JSON tree.
     *
     * @param metaProperty
     * @param metaPropNode
     * @return
     */
    private void provideChangedFromOriginal(final Optional<MetaProperty<?>> metaProperty, final String propName, final JsonNode metaPropNode) {
        final JsonNode changedPropNode = metaPropNode.get("_cfo");
        if (changedPropNode == null) {
            // do nothing -- there is no node and that means that there is default value
        } else {
            if (changedPropNode.isNull()) {
                throw new EntityDeserialisationException(format("EntitySerialiser has got null 'changedFromOriginal' inside meta property '@%s' when reading entity of type [%s].", propName, type.getName()));
            }
            if (metaProperty.isPresent()) {
                metaProperty.get().setDirty(changedPropNode.asBoolean());
            }
        }
    }

    /**
     * Retrieves 'editable' value from entity JSON tree.
     *
     * @param metaProperty
     * @param metaPropNode
     * @return
     */
    private void provideEditable(final Optional<MetaProperty<?>> metaProperty, final String propName, final JsonNode metaPropNode) {
        final JsonNode editablePropNode = metaPropNode.get("_" + MetaProperty.EDITABLE_PROPERTY_NAME);
        if (editablePropNode == null) {
            // do nothing -- there is no node and that means that there is default value
        } else {
            if (editablePropNode.isNull()) {
                throw new EntityDeserialisationException(format("EntitySerialiser has got null 'editable' inside meta property '@%s' when reading entity of type [%s].", propName, type.getName()));
            }
            if (metaProperty.isPresent()) {
                metaProperty.get().setEditable(editablePropNode.asBoolean());
            }
        }
    }

    /**
     * Retrieves 'required' value from entity JSON tree.
     *
     * @param metaProperty
     * @param metaPropNode
     * @return
     */
    private void provideRequired(final Optional<MetaProperty<?>> metaProperty, final String propName, final JsonNode metaPropNode) {
        final JsonNode requiredPropNode = metaPropNode.get("_" + MetaProperty.REQUIRED_PROPERTY_NAME);
        if (requiredPropNode == null) {
            // do nothing -- there is no node and that means that there is default value
        } else {
            if (requiredPropNode.isNull()) {
                throw new EntityDeserialisationException(format("EntitySerialiser has got null 'required' inside meta property '@%s' when reading entity of type [%s].", propName, type.getName()));
            }
            if (metaProperty.isPresent()) {
                metaProperty.get().setRequired(requiredPropNode.asBoolean());
            }
        }
    }

    /**
     * Retrieves 'visible' value from entity JSON tree.
     *
     * @param metaProperty
     * @param metaPropNode
     * @return
     */
    private void provideVisible(final Optional<MetaProperty<?>> metaProperty, final String propName, final JsonNode metaPropNode) {
        final JsonNode visiblePropNode = metaPropNode.get("_visible");
        if (visiblePropNode == null) {
            // do nothing -- there is no node and that means that there is default value
        } else {
            if (visiblePropNode.isNull()) {
                throw new EntityDeserialisationException(format("EntitySerialiser has got null 'visible' inside meta property '@%s' when reading entity of type [%s].", propName, type.getName()));
            }
            if (metaProperty.isPresent()) {
                metaProperty.get().setVisible(visiblePropNode.asBoolean());
            }
        }
    }

    //    /**
    //     * Retrieves 'ValidationResult' value from entity JSON tree.
    //     *
    //     * @param metaProperty
    //     * @param metaPropNode
    //     * @return
    //     */
    //    private void provideValidationResult(final MetaProperty metaProperty, final JsonNode metaPropNode) throws IOException, JsonProcessingException {
    //        final JsonNode validationResultPropNode = metaPropNode.get("_validationResult");
    //        if (validationResultPropNode == null) {
    //            // do nothing -- there is no node and that means that there is default value
    //        } else {
    //            if (validationResultPropNode.isNull()) {
    //                throw new IllegalStateException("EntitySerialiser has got null 'ValidationResult' inside meta property '@" + metaProperty.getName() + "' when reading entity of type [" + type.getName() + "].");
    //            }
    //            if (metaProperty != null) {
    //                final JsonParser jsonParser = validationResultPropNode.traverse(mapper);
    //                metaProperty.setRequiredValidationResult(jsonParser.readValueAs(Result.class)); // TODO how can it be done for Warning.class??
    //            }
    //        }
    //    }

    private ResolvedType constructType(final TypeFactory typeFactory, final Field propertyField) {
        final Class<?> fieldType = AbstractEntity.KEY.equals(propertyField.getName()) ? AnnotationReflector.getKeyType(type) : PropertyTypeDeterminator.stripIfNeeded(propertyField.getType());
        if (Set.class.isAssignableFrom(fieldType) || List.class.isAssignableFrom(fieldType)) {
            final ParameterizedType paramType = (ParameterizedType) propertyField.getGenericType();
            final Class<?> elementClass = PropertyTypeDeterminator.classFrom(paramType.getActualTypeArguments()[0]);

            return typeFactory.constructCollectionType((Class<? extends Collection>) fieldType, elementClass);
        } else if (Map.class.isAssignableFrom(fieldType)) {
            final ParameterizedType paramType = (ParameterizedType) propertyField.getGenericType();
            // IMPORTANT: only simple Java types are supported for map keys (see http://stackoverflow.com/questions/6371092/can-not-find-a-map-key-deserializer-for-type-simple-type-class-com-comcast-i)
            final Class<?> keyClass = PropertyTypeDeterminator.classFrom(paramType.getActualTypeArguments()[0]);
            final Class<?> valueClass = PropertyTypeDeterminator.classFrom(paramType.getActualTypeArguments()[1]);

            return typeFactory.constructMapType((Class<? extends Map>) fieldType, keyClass, valueClass);
        } else {
            // TODO no other collectional types are supported at this stage -- should be added one by one
            return typeFactory.constructType(fieldType);
        }
    }
}
