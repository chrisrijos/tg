package ua.com.fielden.platform.eql.meta;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static ua.com.fielden.platform.domaintree.ICalculatedProperty.CalculatedPropertyCategory.AGGREGATED_EXPRESSION;
import static ua.com.fielden.platform.entity.AbstractEntity.ID;
import static ua.com.fielden.platform.entity.AbstractEntity.KEY;
import static ua.com.fielden.platform.entity.AbstractEntity.VERSION;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.expr;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;
import static ua.com.fielden.platform.entity.query.metadata.CompositeKeyEqlExpressionGenerator.generateCompositeKeyEqlExpression;
import static ua.com.fielden.platform.entity.query.metadata.DomainMetadataUtils.extractExpressionModelFromCalculatedProperty;
import static ua.com.fielden.platform.entity.query.metadata.DomainMetadataUtils.generateUnionEntityPropertyExpression;
import static ua.com.fielden.platform.entity.query.metadata.EntityCategory.PERSISTED;
import static ua.com.fielden.platform.entity.query.metadata.EntityCategory.QUERY_BASED;
import static ua.com.fielden.platform.entity.query.metadata.PropertyCategory.COLLECTIONAL;
import static ua.com.fielden.platform.entity.query.metadata.PropertyCategory.COMPONENT_HEADER;
import static ua.com.fielden.platform.entity.query.metadata.PropertyCategory.EXPRESSION;
import static ua.com.fielden.platform.entity.query.metadata.PropertyCategory.SYNTHETIC;
import static ua.com.fielden.platform.entity.query.metadata.PropertyCategory.SYNTHETIC_COMPONENT_HEADER;
import static ua.com.fielden.platform.entity.query.metadata.PropertyCategory.VIRTUAL_OVERRIDE;
import static ua.com.fielden.platform.reflection.AnnotationReflector.getAnnotation;
import static ua.com.fielden.platform.reflection.AnnotationReflector.getKeyType;
import static ua.com.fielden.platform.reflection.AnnotationReflector.getPropertyAnnotation;
import static ua.com.fielden.platform.reflection.AnnotationReflector.isAnnotationPresent;
import static ua.com.fielden.platform.reflection.Finder.hasLinkProperty;
import static ua.com.fielden.platform.reflection.Finder.isOne2One_association;
import static ua.com.fielden.platform.reflection.PropertyTypeDeterminator.determinePropertyType;
import static ua.com.fielden.platform.types.tuples.T3.t3;
import static ua.com.fielden.platform.utils.CollectionUtil.unmodifiableListOf;
import static ua.com.fielden.platform.utils.EntityUtils.getRealProperties;
import static ua.com.fielden.platform.utils.EntityUtils.isEntityType;
import static ua.com.fielden.platform.utils.EntityUtils.isOneToOne;
import static ua.com.fielden.platform.utils.EntityUtils.isPersistedEntityType;
import static ua.com.fielden.platform.utils.EntityUtils.isSyntheticBasedOnPersistentEntityType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.hibernate.type.BasicType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

import com.google.inject.Injector;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.AbstractUnionEntity;
import ua.com.fielden.platform.entity.DynamicEntityKey;
import ua.com.fielden.platform.entity.annotation.Calculated;
import ua.com.fielden.platform.entity.annotation.CritOnly;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.MapTo;
import ua.com.fielden.platform.entity.annotation.PersistentType;
import ua.com.fielden.platform.entity.exceptions.EntityDefinitionException;
import ua.com.fielden.platform.entity.query.DbVersion;
import ua.com.fielden.platform.entity.query.ICompositeUserTypeInstantiate;
import ua.com.fielden.platform.entity.query.IUserTypeInstantiate;
import ua.com.fielden.platform.entity.query.exceptions.EqlException;
import ua.com.fielden.platform.entity.query.metadata.EntityCategory;
import ua.com.fielden.platform.entity.query.metadata.EntityTypeInfo;
import ua.com.fielden.platform.entity.query.metadata.PropertyCategory;
import ua.com.fielden.platform.entity.query.metadata.PropertyMetadata;
import ua.com.fielden.platform.entity.query.model.ExpressionModel;
import ua.com.fielden.platform.eql.meta.LongPropertyMetadata.Builder;
import ua.com.fielden.platform.eql.meta.model.PropColumn;
import ua.com.fielden.platform.reflection.PropertyTypeDeterminator;
import ua.com.fielden.platform.types.tuples.T3;
import ua.com.fielden.platform.utils.EntityUtils;

public class LongMetadata {
    
    private static final TypeResolver typeResolver = new TypeResolver();
    private static final Type H_LONG = typeResolver.basic("long");
    private static final Type H_STRING = typeResolver.basic("string");
    private static final Type H_BOOLEAN = typeResolver.basic("yes_no");
    private static final Type H_BIG_DECIMAL = typeResolver.basic("big_decimal");
    private static final Type H_BIG_INTEGER = typeResolver.basic("big_integer");

    public static final List<String> specialProps = unmodifiableListOf(ID, KEY, VERSION);

    private final PropColumn id;
    private final PropColumn version;
    private final PropColumn key = new PropColumn("KEY_");

    public final DbVersion dbVersion;
    /**
     * Map between java type and hibernate persistence type (implementers of Type, IUserTypeInstantiate, ICompositeUserTypeInstantiate).
     */
    private final ConcurrentMap<Class<?>, Object> hibTypesDefaults;


    private final List<Class<? extends AbstractEntity<?>>> entityTypes;
    
    private Injector hibTypesInjector;

    public LongMetadata(//
            final Map<Class<?>, Class<?>> hibTypesDefaults, //
            final Injector hibTypesInjector, //
            final List<Class<? extends AbstractEntity<?>>> entityTypes, //
            final DbVersion dbVersion) {
        this.dbVersion = dbVersion;

        this.hibTypesDefaults = new ConcurrentHashMap<>(entityTypes.size());
        
        this.entityTypes = new ArrayList<>(entityTypes);
        
        // initialise meta-data for basic entity properties, which is RDBMS dependent
        if (dbVersion != DbVersion.ORACLE) {
            id = new PropColumn("_ID");
            version = new PropColumn("_VERSION");
        } else {
            id = new PropColumn("TG_ID");
            version = new PropColumn("TG_VERSION");
        }

        // carry on with other stuff
        if (hibTypesDefaults != null) {
            for (final Entry<Class<?>, Class<?>> entry : hibTypesDefaults.entrySet()) {
                try {
                    this.hibTypesDefaults.put(entry.getKey(), entry.getValue().newInstance());
                } catch (final Exception e) {
                    throw new IllegalStateException("Couldn't generate instantiate hibernate type [" + entry.getValue() + "] due to: " + e);
                }
            }
        }
        this.hibTypesDefaults.put(Boolean.class, H_BOOLEAN);
        this.hibTypesDefaults.put(boolean.class, H_BOOLEAN);

        this.hibTypesInjector = hibTypesInjector;
    }
    
    /**
     * Determines hibernate type instance for entity property based on provided property's meta information.
     *
     * @param entityType
     * @param field
     * @return
     * @throws Exception
     * @throws
     */
    private Object getHibernateType(final Field propField) {
        final String propName = propField.getName();
        final Class<?> propType = propField.getType();
        
        if (isPersistedEntityType(propType)) {
            return H_LONG;
        }

        final PersistentType persistentType = getAnnotation(propField, PersistentType.class);
        
        if (persistentType == null) {
            final Object defaultHibType = hibTypesDefaults.get(propType);
            if (defaultHibType != null) { // default is provided for given property java type
                return defaultHibType;
            } else { // trying to mimic hibernate logic when no type has been specified - use hibernate's map of defaults
                final BasicType result = typeResolver.basic(propType.getName());
                if (result == null) {
                    throw new EqlException(propField.getName() + " has no hibType (1)");
                }
                return result;
            }
        } else {
            final String hibernateTypeName = persistentType.value();
            final Class<?> hibernateUserTypeImplementor = persistentType.userType();
            if (isNotEmpty(hibernateTypeName)) {
                final BasicType result = typeResolver.basic(hibernateTypeName);
                if (result == null) {
                    throw new EqlException(propField.getName() + " has no hibType (2)");
                }
                return result;
            } else if (hibTypesInjector != null && !Void.class.equals(hibernateUserTypeImplementor)) { // Hibernate type is definitely either IUserTypeInstantiate or ICompositeUserTypeInstantiate
                return hibTypesInjector.getInstance(hibernateUserTypeImplementor);
            } else {
                throw new EqlException("Persistent annotation doen't provide intended information.");
            }
        }
    }
    
    private T3<Type, IUserTypeInstantiate, ICompositeUserTypeInstantiate> getHibernateConverter(final Object instance) {
        if (instance instanceof ICompositeUserTypeInstantiate) {
            return t3(null, null, (ICompositeUserTypeInstantiate) instance);
        } else if (instance instanceof IUserTypeInstantiate) {
            return t3(null, (IUserTypeInstantiate) instance, null);
        } else if (instance instanceof Type) {
            return t3((Type) instance, null, null);
        } else {
            throw new EqlException("Can't determine propert hibernate converter"); 
        }
    }
    
    private void safeMapAdd(final Map<String, LongPropertyMetadata> map, final LongPropertyMetadata addedItem) {
        if (addedItem != null) {
            map.put(addedItem.name, addedItem);
//
//            for (final PropertyMetadata propMetadata : addedItem.getCompositeTypeSubprops()) {
//                map.put(propMetadata.getName(), propMetadata);
//            }
//
//            for (final PropertyMetadata propMetadata : addedItem.getComponentTypeSubprops()) {
//                map.put(propMetadata.getName(), propMetadata);
//            }
        }
    }
    
    private LongPropertyMetadata generateIdPropertyMetadata(final EntityTypeInfo <? extends AbstractEntity<?>> parentInfo) {
        final LongPropertyMetadata idProperty = new LongPropertyMetadata.Builder(ID, Long.class, false).column(id).hibType(H_LONG).build();
        final LongPropertyMetadata idPropertyInOne2One = new LongPropertyMetadata.Builder(ID, Long.class, false).column(id).hibType(H_LONG).build();
        switch (parentInfo.category) {
        case PERSISTED:
            return isOneToOne(parentInfo.entityType) ? idPropertyInOne2One : idProperty/*(entityType)*/;
        case QUERY_BASED:
            if (isSyntheticBasedOnPersistentEntityType(parentInfo.entityType)) {
                if (isEntityType(getKeyType(parentInfo.entityType))) {
                    throw new EntityDefinitionException(format("Entity [%s] is recognised as synthetic that is based on a persystent type with an entity-typed key. This is not supported.", parentInfo.entityType.getName()));
                }
                return idProperty;
            } else if (isEntityType(getKeyType(parentInfo.entityType))) {
                return new LongPropertyMetadata.Builder(ID, Long.class, false).hibType(H_LONG).expression(expr().prop(KEY).model()).build();
            } else {
                return null;
            }
        case UNION:
            return new LongPropertyMetadata.Builder(ID, Long.class, false).hibType(H_LONG).expression(generateUnionEntityPropertyExpression((Class<? extends AbstractUnionEntity>) parentInfo.entityType, ID)).build();
        default:
            return null;
        }
    }
    
    private LongPropertyMetadata generateVersionPropertyMetadata(final EntityTypeInfo <? extends AbstractEntity<?>> parentInfo) {
        return PERSISTED == parentInfo.category ? new LongPropertyMetadata.Builder(VERSION, Long.class, false).column(version).hibType(H_LONG).build() : null;
    }
    
    private LongPropertyMetadata generateKeyPropertyMetadata(final EntityTypeInfo <? extends AbstractEntity<?>> parentInfo) throws Exception {
        final Class<? extends Comparable> keyType = getKeyType(parentInfo.entityType);
        if (isOneToOne(parentInfo.entityType)) {
            switch (parentInfo.category) {
            case PERSISTED:
                return new LongPropertyMetadata.Builder(KEY, keyType, false).column(id).hibType(H_LONG).build();
            case QUERY_BASED:
                return new LongPropertyMetadata.Builder(KEY, keyType, false).hibType(H_LONG).build();
            default:
                return null;
            }
        } else if (DynamicEntityKey.class.equals(keyType)) {
            return new LongPropertyMetadata.Builder(KEY, String.class, true).expression(generateCompositeKeyEqlExpression((Class<? extends AbstractEntity<DynamicEntityKey>>) parentInfo.entityType)).hibType(H_STRING).build();
        } else {
            switch (parentInfo.category) {
            case PERSISTED:
                return new LongPropertyMetadata.Builder(KEY, keyType, false).column(key).hibType(typeResolver.basic(keyType.getName())).build();
            case QUERY_BASED:
                if (isSyntheticBasedOnPersistentEntityType(parentInfo.entityType)) {
                    return new LongPropertyMetadata.Builder(KEY, keyType, false).column(key).hibType(typeResolver.basic(keyType.getName())).build();
                }
                return null; //FIXME
            case UNION:
                return new LongPropertyMetadata.Builder(KEY, String.class, false).hibType(H_STRING).expression(generateUnionEntityPropertyExpression((Class<? extends AbstractUnionEntity>) parentInfo.entityType, KEY)).build();
            default:
                return null;
            }
        }
    }

    /**
     * Generates persistence info for common properties of provided entity type.
     *
     * @param entityType
     * @return
     * @throws Exception
     */
    public SortedMap<String, LongPropertyMetadata> generatePropertyMetadatasForEntity(final EntityTypeInfo <? extends AbstractEntity<?>> parentInfo) throws Exception {
        final SortedMap<String, LongPropertyMetadata> result = new TreeMap<>();

        safeMapAdd(result, generateIdPropertyMetadata(parentInfo));
        safeMapAdd(result, generateVersionPropertyMetadata(parentInfo));
        safeMapAdd(result, generateKeyPropertyMetadata(parentInfo));

        for (final Field field : getRealProperties(parentInfo.entityType)) {
            if (!result.containsKey(field.getName())) {
                if (Collection.class.isAssignableFrom(field.getType()) && hasLinkProperty(parentInfo.entityType, field.getName())) {
                    //safeMapAdd(result, getCollectionalPropInfo(field, parentInfo));
                } else if ((isAnnotationPresent(field, Calculated.class) || isAnnotationPresent(field, MapTo.class) || (parentInfo.category == QUERY_BASED && !isAnnotationPresent(field, CritOnly.class)))) {
                    safeMapAdd(result, getCommonPropInfo(field, parentInfo.entityType));
                } else if (isOne2One_association(parentInfo.entityType, field.getName())) {
                    safeMapAdd(result, getOneToOnePropInfo(field, parentInfo));
                } else {
                    //System.out.println(" --------------------------------------------------------- " + entityType.getSimpleName() + ": " + field.getName());
                }
            }
        }

        return result;
    }
    
    private List<PropColumn> getPropColumns(final Field field, final IsProperty isProperty, final MapTo mapTo, final Object hibernateType) throws Exception {
        final String columnName = isNotEmpty(mapTo.value()) ? mapTo.value() : field.getName().toUpperCase() + "_";

        final List<PropColumn> result = new ArrayList<>();
        if (hibernateType instanceof ICompositeUserTypeInstantiate) {
            final ICompositeUserTypeInstantiate hibCompositeUSerType = (ICompositeUserTypeInstantiate) hibernateType;
            for (final PropColumn column : getCompositeUserTypeColumns(hibCompositeUSerType, columnName)) {
                result.add(column);
            }
        } else {
            final Integer length = isProperty.length() > 0 ? isProperty.length() : null;
            final Integer precision = isProperty.precision() >= 0 ? isProperty.precision() : null;
            final Integer scale = isProperty.scale() >= 0 ? isProperty.scale() : null;
            result.add(new PropColumn(columnName, length, precision, scale));
        }
        return result;
    }
    
    
    /**
     * Generates list of column names for mapping of CompositeUserType implementors.
     *
     * @param hibType
     * @param parentColumnPrefix
     * @return
     * @throws Exception
     */
    private List<PropColumn> getCompositeUserTypeColumns(final ICompositeUserTypeInstantiate hibType, final String parentColumnPrefix) throws Exception {
        final String[] propNames = hibType.getPropertyNames();
        final List<PropColumn> result = new ArrayList<>();
        for (final String propName : propNames) {
            final MapTo mapTo = getPropertyAnnotation(MapTo.class, hibType.returnedClass(), propName);
            final IsProperty isProperty = getPropertyAnnotation(IsProperty.class, hibType.returnedClass(), propName);
            final String mapToColumn = mapTo.value();
            final Integer length = isProperty.length() > 0 ? isProperty.length() : null;
            final Integer precision = isProperty.precision() >= 0 ? isProperty.precision() : null;
            final Integer scale = isProperty.scale() >= 0 ? isProperty.scale() : null;
            final String columnName = propNames.length == 1 ? parentColumnPrefix
                    : (parentColumnPrefix + (parentColumnPrefix.endsWith("_") ? "" : "_") + (isEmpty(mapToColumn) ? propName.toUpperCase() : mapToColumn));
            result.add(new PropColumn(columnName, length, precision, scale));
        }
        return result;
    }
    
    private LongPropertyMetadata getCommonPropInfo(final Field propField, final Class<? extends AbstractEntity<?>> entityType) throws Exception {
        final String propName = propField.getName();
        final Class<?> propType = propField.getType();

        if (EntityUtils.isUnionEntityType(propType)) {
            return null;
        }
            
        final boolean nullable = !PropertyTypeDeterminator.isRequiredByDefinition(propField, entityType);

        final Object ht = getHibernateType(propField);

        final T3<Type, IUserTypeInstantiate, ICompositeUserTypeInstantiate> hibernateType = ht == null ? null : getHibernateConverter(ht);
        final Object hibType = hibernateType == null ? null : (hibernateType._1 != null ? hibernateType._1 : (hibernateType._2 != null ? hibernateType._2 : (hibernateType._3 != null ? hibernateType._3 : null)));

        final MapTo mapTo = getAnnotation(propField, MapTo.class);
        final IsProperty isProperty = getAnnotation(propField, IsProperty.class);
        final Calculated calculated = getAnnotation(propField, Calculated.class);
        
        final Builder resultInProgress = new LongPropertyMetadata.
                Builder(propName, propType, nullable).
                hibType(hibType);
        

        
        if (mapTo != null) {
            final List<PropColumn> columns = getPropColumns(propField, isProperty, mapTo, hibernateType);
            if (columns.size() == 1) {
                return resultInProgress.column(columns.get(0)).build(); 
            } else {
                return null;
            }
        } else if (calculated != null) {
            return resultInProgress.expression(extractExpressionModelFromCalculatedProperty(entityType, propField)).build();
        } else {
            return resultInProgress.build();
        }
    }
    
    private PropertyMetadata getVirtualPropInfoForDynamicEntityKey(final EntityTypeInfo <? extends AbstractEntity<DynamicEntityKey>> parentInfo) throws Exception {
        return new PropertyMetadata.Builder(KEY, String.class, true, parentInfo).expression(generateCompositeKeyEqlExpression(parentInfo.entityType)).hibType(H_STRING).category(VIRTUAL_OVERRIDE).build();
    }

    private LongPropertyMetadata getOneToOnePropInfo(final Field propField, final EntityTypeInfo <? extends AbstractEntity<?>> parentInfo) throws Exception {
        final String propName = propField.getName();
        final Class<?> javaType = propField.getType();
        final Object ht = getHibernateType(propField);
        final T3<Type, IUserTypeInstantiate, ICompositeUserTypeInstantiate> hibernateType = ht == null ? null : getHibernateConverter(ht);
        final Object hibType = hibernateType == null ? null : (hibernateType._1 != null ? hibernateType._1 : (hibernateType._2 != null ? hibernateType._2 : (hibernateType._3 != null ? hibernateType._3 : null)));

        
        // 1-2-1 is not required to exist -- that's why need longer formula -- that's why 1-2-1 is in fact implicitly calculated nullable prop
        final ExpressionModel expressionModel = expr().model(select((Class<? extends AbstractEntity<?>>) propField.getType()).where().prop(KEY).eq().extProp(ID).model()).model();
        return new LongPropertyMetadata.
                Builder(propName, javaType, true).
                expression(expressionModel).
                hibType(hibType).
                build();
    }

    private PropertyMetadata getCollectionalPropInfo(final Field propField, final EntityTypeInfo <? extends AbstractEntity<?>> parentInfo) throws Exception {
        return new PropertyMetadata.Builder(propField.getName(), determinePropertyType(parentInfo.entityType, propField.getName()), true, parentInfo).category(COLLECTIONAL).build();
    }
}