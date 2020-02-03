package ua.com.fielden.platform.web_api;

import static graphql.ExecutionInput.newExecutionInput;
import static graphql.GraphQL.newGraphQL;
import static graphql.schema.FieldCoordinates.coordinates;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLCodeRegistry.newCodeRegistry;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang.StringUtils.uncapitalize;
import static ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeRepresentation.constructKeysAndProperties;
import static ua.com.fielden.platform.reflection.TitlesDescsGetter.getEntityTitleAndDesc;
import static ua.com.fielden.platform.web_api.FieldSchema.createField;
import static ua.com.fielden.platform.web_api.WebApiUtils.operationName;
import static ua.com.fielden.platform.web_api.WebApiUtils.query;
import static ua.com.fielden.platform.web_api.WebApiUtils.variables;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.Scalars;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;
import ua.com.fielden.platform.basic.config.IApplicationDomainProvider;
import ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeRepresentation;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.DynamicEntityKey;
import ua.com.fielden.platform.entity.IContinuationData;
import ua.com.fielden.platform.entity.NoKey;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.factory.ICompanionObjectFinder;
import ua.com.fielden.platform.entity.meta.PropertyDescriptor;
import ua.com.fielden.platform.menu.AbstractView;
import ua.com.fielden.platform.reflection.PropertyTypeDeterminator;
import ua.com.fielden.platform.reflection.TitlesDescsGetter;
import ua.com.fielden.platform.types.Colour;
import ua.com.fielden.platform.types.Hyperlink;
import ua.com.fielden.platform.utils.EntityUtils;
import ua.com.fielden.platform.web.centre.CentreContext;

/**
 * Represents GraphQL implementation of TG Web API using graphql-java library.
 * 
 * @author TG Team
 *
 */
public class GraphQLService implements IWebApi {
    private final Logger logger = Logger.getLogger(getClass());
    private final GraphQL graphQL;
    
    /**
     * Creates GraphQLService instance based on <code>applicationDomainProvider</code> which contains all entity types.
     * 
     * @param applicationDomainProvider
     * @param coFinder
     */
    @Inject
    public GraphQLService(final IApplicationDomainProvider applicationDomainProvider, final ICompanionObjectFinder coFinder, final EntityFactory entityFactory) {
        this(applicationDomainProvider.entityTypes(), coFinder, entityFactory);
    }
    
    /**
     * Creates GraphQLService instance based on passed entity types.
     * 
     * @param entityTypes
     * @param coFinder
     */
    private GraphQLService(final List<Class<? extends AbstractEntity<?>>> entityTypes, final ICompanionObjectFinder coFinder, final EntityFactory entityFactory) {
        logger.info("GraphQL Web API...");
        final GraphQLCodeRegistry.Builder codeRegistryBuilder = newCodeRegistry();
        logger.info("\tBuilding dictionary...");
        final Map<Class<? extends AbstractEntity<?>>, GraphQLType> dictionary = createDictionary(entityTypes);
        logger.info("\tBuilding query schema...");
        final GraphQLObjectType queryType = createQueryType(dictionary.keySet(), coFinder, codeRegistryBuilder);
        logger.info("\tBuilding mutation schema...");
        final GraphQLObjectType mutationType = createMutationType(dictionary.keySet(), coFinder, entityFactory, codeRegistryBuilder);
        final GraphQLSchema schema = newSchema()
                .codeRegistry(codeRegistryBuilder.build())
                .query(queryType)
                .mutation(mutationType)
                .additionalTypes(new LinkedHashSet<>(dictionary.values()))
                .build();
        graphQL = newGraphQL(schema).build();
        logger.info("GraphQL Web API...done");
    }
    
    @Override
    public Map<String, Object> execute(final Map<String, Object> input) {
        System.err.println("======================================= INPUT =======================================\n" + input + "\n=====================================================================================");
        final ExecutionResult execResult = graphQL.execute(
            newExecutionInput()
            .query(query(input))
            .operationName(operationName(input).orElse(null))
            .variables(variables(input))
        );
        final Map<String, Object> result = execResult.toSpecification();
        System.err.println("======================================= RESULT =======================================\n" + result + "\n=====================================================================================");
        return result;
    }
    
    /**
     * Creates GraphQL dictionary reflecting existing entity types.
     * 
     * @param entityTypes
     * @return
     */
    private static Map<Class<? extends AbstractEntity<?>>, GraphQLType> createDictionary(final List<Class<? extends AbstractEntity<?>>> entityTypes) {
        final Map<Class<? extends AbstractEntity<?>>, GraphQLType> types = new LinkedHashMap<>();
        for (final Class<? extends AbstractEntity<?>> entityType: entityTypes) {
            createType(entityType).map(type -> types.put(entityType, type));
        }
        return types;
    }
    
    /**
     * Creates query type for GraphQL quering of TG <code>entityTypes</code> entities.
     * <p>
     * All query field names are represented as uncapitalised entity type simple names. All sub-field names are represented as entity property names.
     * 
     * @param dictionary
     * @param coFinder
     * @param codeRegistryBuilder
     * @return
     */
    private static GraphQLObjectType createQueryType(final Set<Class<? extends AbstractEntity<?>>> dictionary, final ICompanionObjectFinder coFinder, final GraphQLCodeRegistry.Builder codeRegistryBuilder) {
        final String queryTypeName = "Query";
        final Builder queryTypeBuilder = newObject().name(queryTypeName);
        for (final Class<? extends AbstractEntity<?>> entityType: dictionary) {
            final String simpleTypeName = entityType.getSimpleName();
            final String fieldName = uncapitalize(simpleTypeName);
            queryTypeBuilder.field(newFieldDefinition()
                .name(fieldName)
                .description(format("Query [%s] entity.", getEntityTitleAndDesc(entityType).getValue()))
                .type(new GraphQLList(new GraphQLTypeReference(simpleTypeName)))
            );
            codeRegistryBuilder.dataFetcher(coordinates(queryTypeName, fieldName), new RootEntityFetcher<>((Class<AbstractEntity<?>>) entityType, coFinder));
        }
        return queryTypeBuilder.build();
    }
    
    /**
     * Creates mutation type for GraphQL mutating of TG <code>entityTypes</code> entities.
     * <p>
     * All query field names are represented as uncapitalised entity type simple names. All sub-field names are represented as entity property names.
     * <p>
     * There are two arguments for entity field: <code>input</code> (required) and <code>keys</code> (optional).
     * Update query requires <code>keys</code> in order to define which entity needs mutation.
     * Create query does not require <code>keys</code>, only <code>input</code> that contains mutated properties.
     * 
     * @param dictionary
     * @param coFinder
     * @param entityFactory
     * @param codeRegistryBuilder
     * @return
     */
    private static GraphQLObjectType createMutationType(final Set<Class<? extends AbstractEntity<?>>> dictionary, final ICompanionObjectFinder coFinder, final EntityFactory entityFactory, final GraphQLCodeRegistry.Builder codeRegistryBuilder) {
        final String mutationTypeName = "Mutation";
        final Builder mutationTypeBuilder = newObject().name(mutationTypeName);
        for (final Class<? extends AbstractEntity<?>> entityType: dictionary) {
            final String simpleTypeName = entityType.getSimpleName();
            final String inputArgumentDescription = format("Input values for mutating / creating [%s] entity.", getEntityTitleAndDesc(entityType).getValue());
            final String rootMutationFieldDescription = format("Mutate [%s] entity.", getEntityTitleAndDesc(entityType).getValue());
            final String fieldName = uncapitalize(simpleTypeName);
            createMutationInputArgumentType(entityType, inputArgumentDescription).map(mutationInputArgumentType -> mutationTypeBuilder.field(newFieldDefinition()
                .name(fieldName)
                .description(rootMutationFieldDescription)
                .type(new GraphQLTypeReference(simpleTypeName))
                .argument(newArgument()
                    .name("input")
                    .description(inputArgumentDescription)
                    .type(new GraphQLNonNull(mutationInputArgumentType))
                    .defaultValue(null /*default value of argument */)
                    .build()))
                // TODO .argument(new GraphQLArgument("keys", String.format("Key criteria for mutating some concrete [%s] entity", typeName), createKeysType(entityType), null /*default value of argument */))
            );
            codeRegistryBuilder.dataFetcher(coordinates(mutationTypeName, fieldName), new RootEntityMutator<>((Class<AbstractEntity<?>>) entityType, coFinder, entityFactory));
        }
        return mutationTypeBuilder.build();
    }
    
    /**
     * Creates GraphQL object type for <code>entityType</code>d entities quering.
     * 
     * @param entityType
     * @return
     */
    private static Optional<GraphQLObjectType> createType(final Class<? extends AbstractEntity<?>> entityType) {
        final Builder typeBuilder = newObject();
        
        // the name of object should correspond to simple entity type name
        // TODO naming conflicts?
        typeBuilder.name(entityType.getSimpleName());
        
        typeBuilder.description(getEntityTitleAndDesc(entityType).getValue());
        
        constructKeysAndProperties(entityType).stream().forEach(prop ->
            createField(entityType, prop.getName())
            .map(field -> typeBuilder.field(field))
        );
        
        final GraphQLObjectType type = typeBuilder.build();
        if (type.getFieldDefinitions().isEmpty()) { // ignore types that have no GraphQL field equivalents; we can not use such types for any purpose including quering
            return empty();
        }
        return of(type);
    }
    
    /**
     * Creates the type for required <code>input</code> argument of mutation query for <code>entityType</code>d entities.
     * 
     * @param entityType
     * @param inputArgumentDescription
     * 
     * @return
     */
    private static Optional<GraphQLInputObjectType> createMutationInputArgumentType(final Class<? extends AbstractEntity<?>> entityType, final String inputArgumentDescription) {
        final String typeName = entityType.getSimpleName();
        final graphql.schema.GraphQLInputObjectType.Builder builder = newInputObject().name(typeName + "Input").description(inputArgumentDescription);
        
        final List<Field> keysAndProperties = AbstractDomainTreeRepresentation.constructKeysAndProperties(entityType);
        for (final Field propertyField : keysAndProperties) {
            createMutationInputArgumentField(entityType, propertyField).map(b -> builder.field(b));
        }
        final GraphQLInputObjectType type = builder.build();
        if (type.getFieldDefinitions().isEmpty()) { // ignore input types that have no GraphQL field equivalents; we can not use entity types without proper input types for mutation
            return empty();
        }
        return of(type);
    }
    
    /**
     * Creates GraphQL field definition on entity type's <b>input</b> object from entity property defined by <code>propertyField</code>.
     * 
     * @param entityType
     * @param propertyField
     * @return
     */
    private static Optional<graphql.schema.GraphQLInputObjectField.Builder> createMutationInputArgumentField(final Class<? extends AbstractEntity<?>> entityType, final Field propertyField) {
        final String name = propertyField.getName();
        final Optional<GraphQLInputType> fieldType = determineMutationInputArgumentFieldType(entityType, name);
        return fieldType.map(type -> {
            final graphql.schema.GraphQLInputObjectField.Builder builder = newInputObjectField();
            // if (Scalars.GraphQLBoolean.equals(type)) {
            //     builder.argument(new GraphQLArgument("value", null /* description of argument */, Scalars.GraphQLBoolean, null /*default value of argument */));
            // }
            builder.name(name);
            builder.description(TitlesDescsGetter.getTitleAndDesc(name, entityType).getValue());
            return builder.type(type);
        });
    }
    
    private static Optional<GraphQLInputType> determineMutationInputArgumentFieldType(final Class<? extends AbstractEntity<?>> entityType, final String name) {
        final Class<?> realType = PropertyTypeDeterminator.determineClass(entityType, name, true, false);
        final Class<?> parameterType = PropertyTypeDeterminator.determineClass(entityType, name, true, true);
        if (EntityUtils.isCollectional(realType)) {
            return Optional.empty(); // TODO at this stage there will be no support for collectional input object fields
            // TODO return determineFieldTypeNonCollectional(parameterType, entityType, name).map(t -> new GraphQLList(t));
        } else {
            return determineMutationInputArgumentFieldTypeNonCollectional(parameterType, entityType, name);
        }
    }
    
    private static Optional<GraphQLInputType> determineMutationInputArgumentFieldTypeNonCollectional(final Class<?> type, final Class<? extends AbstractEntity<?>> entityType, final String name) {
        if (EntityUtils.isString(type)) {
            return Optional.of(Scalars.GraphQLString);
        } else if (EntityUtils.isBoolean(type)) {
            return Optional.of(Scalars.GraphQLBoolean);
        } else if (EntityUtils.isDecimal(type) || Double.class.isAssignableFrom(type)) {
            return Optional.of(Scalars.GraphQLBigDecimal);
            // TODO remove return TgScalars.GraphQLBigDecimal;
        } else if (Long.class.isAssignableFrom(type)) {
            return Optional.of(Scalars.GraphQLLong);
        } else if (Integer.class.isAssignableFrom(type)) {
            return Optional.of(Scalars.GraphQLInt);
        } else if (
                IContinuationData.class.isAssignableFrom(type) ||
                Map.class.isAssignableFrom(type) ||
                byte[].class.isAssignableFrom(type) ||
                Class.class.isAssignableFrom(type) || 
                CentreContext.class.isAssignableFrom(type) || 
                Optional.class.isAssignableFrom(type) || 
                Boolean.class.isAssignableFrom(type) || 
                int.class.isAssignableFrom(type) || 
                AbstractEntity.class == type || // CentreContextHolder.selectedEntities => List<AbstractEntity>, masterEntity => AbstractEntity
                AbstractView.class == type || 
                PropertyDescriptor.class == type ||
                Modifier.isAbstract(type.getModifiers())
        ) {
            return Optional.empty();
        } else if (EntityUtils.isDate(type)) {
            return Optional.empty();
            // TODO return TgScalars.GraphQLDate;
        } else if (Hyperlink.class.isAssignableFrom(type)) {
            return Optional.empty();
            // TODO return TgScalars.GraphQLHyperlink;
        } else if (Colour.class.isAssignableFrom(type)) {
            return Optional.empty();
            // TODO return TgScalars.GraphQLColour;
        } else if (EntityUtils.isEntityType(type)) {
            return Optional.of(Scalars.GraphQLString);
        } else if (NoKey.class.isAssignableFrom(type)) {
            return Optional.empty();
        } else if (DynamicEntityKey.class.isAssignableFrom(type)) { // this is for the weird cases where DynamicEntityKey is used but no @CompositeKeyMember exists
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException(String.format("Mutation input argument field: type [%s] is unknown (type = %s, name = %s).", type.getSimpleName(), entityType.getSimpleName(), name));
        }
    }
}
