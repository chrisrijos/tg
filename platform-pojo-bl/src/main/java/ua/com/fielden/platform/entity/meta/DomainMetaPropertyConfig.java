package ua.com.fielden.platform.entity.meta;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a configuration point for instances of {@link IMetaPropertyDefiner} to be associated with entity types and their properties.
 * 
 * @author 01es
 * 
 */
public class DomainMetaPropertyConfig {
    private final Map<Class<?>, Map<String, IMetaPropertyDefiner>> domainMetaDefiners = new HashMap<Class<?>, Map<String, IMetaPropertyDefiner>>();

    /**
     * Return domain validator associated with an entity of the specified type and its property. The returned value is null if no association was found.
     * 
     * @param entityType
     * @param propertyName
     * @return
     */
    public IMetaPropertyDefiner getDefiner(final Class<?> entityType, final String propertyName) {
	final Map<String, IMetaPropertyDefiner> map = domainMetaDefiners.get(entityType);
	if (map != null) { // some of the entity properties are mapped to some domain validators
	    return map.get(propertyName); // may return null if propertyName is not associated with any domain validator
	}
	return null; // entity's properties are not mapped to any domain validator
    }

    /**
     * Associates an instance of domain validator with an entity type and its property.
     * 
     * @param entityType
     * @param propertyName
     * @param domainMetaDefiner
     * @return
     */
    public DomainMetaPropertyConfig setDefiner(final Class<?> entityType, final String propertyName, final IMetaPropertyDefiner domainMetaDefiner) {
	final Map<String, IMetaPropertyDefiner> map = domainMetaDefiners.get(entityType) == null ? new HashMap<String, IMetaPropertyDefiner>() : domainMetaDefiners.get(entityType);
	map.put(propertyName, domainMetaDefiner); // this put replaces a validator if there was already one associated with the specified property
	domainMetaDefiners.put(entityType, map);
	return this;
    }

}
