package ua.com.fielden.platform.serialisation.jackson.serialisers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity_centre.review.criteria.EntityQueryCriteria;
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.reflection.PropertyTypeDeterminator;
import ua.com.fielden.platform.reflection.asm.impl.DynamicEntityClassLoader;
import ua.com.fielden.platform.serialisation.api.impl.TgJackson;
import ua.com.fielden.platform.serialisation.jackson.EntityType;
import ua.com.fielden.platform.utils.EntityUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Serialiser for {@link Result} type.
 * <p>
 * Serialises information about concrete {@link Result}'s subtype, message, exception and instance with its type.
 *
 * @author TG Team
 *
 */
public class ResultJsonSerialiser extends StdSerializer<Result> {
    private final TgJackson tgJackson;

    public ResultJsonSerialiser(final TgJackson tgJackson) {
        super(Result.class);
        this.tgJackson = tgJackson;
    }

    @Override
    public void serialize(final Result result, final JsonGenerator generator, final SerializerProvider provider) throws IOException, JsonProcessingException {
        generator.writeStartObject();

        generator.writeFieldName("@resultType");
        generator.writeObject(result.getClass().getName());
        generator.writeFieldName("message");
        generator.writeObject(result.getMessage());

        if (result.getInstance() != null) {
            generator.writeFieldName("@instanceType");
            final Class<?> type = PropertyTypeDeterminator.stripIfNeeded(result.getInstance().getClass());

            if (List.class.isAssignableFrom(type)) {
                generator.writeObject(type.getName());

                generator.writeFieldName("@instanceTypes");
                final List<Object> list = (List<Object>) result.getInstance();
                // TODO there could be the hierarchy of generatedTypes!
                // TODO there could be the hierarchy of generatedTypes!
                // TODO there could be the hierarchy of generatedTypes!
                // TODO there could be the hierarchy of generatedTypes!
                // TODO there could be the hierarchy of generatedTypes!
                // TODO there could be the hierarchy of generatedTypes!
                // TODO there could be the hierarchy of generatedTypes!
                final Set<Class<?>> generatedTypes = new LinkedHashSet<>();
                list.forEach(item -> {
                    if (item != null) {
                        final Class<?> itemClass = PropertyTypeDeterminator.stripIfNeeded(item.getClass());
                        if (DynamicEntityClassLoader.isGenerated(itemClass)) {
                            generatedTypes.add(itemClass);
                        }
                    }
                });
                final ArrayList<EntityType> genList = new ArrayList<>();
                generatedTypes.forEach(t -> {
                    genList.add(tgJackson.registerNewEntityType((Class<AbstractEntity<?>>) t));
                });
                generator.writeObject(genList);
            } else if (EntityUtils.isEntityType(type) && EntityQueryCriteria.class.isAssignableFrom(type)) {
                final Class<AbstractEntity<?>> newType = (Class<AbstractEntity<?>>) type;
                generator.writeObject(tgJackson.registerNewEntityType(newType));
            } else {
                generator.writeObject(type.getName());
            }

            generator.writeFieldName("instance");
            generator.writeObject(result.getInstance());
        }

        if (result.getEx() != null) {
            generator.writeFieldName("ex");
            generator.writeObject(result.getEx());
        }

        generator.writeEndObject();
    }
}
