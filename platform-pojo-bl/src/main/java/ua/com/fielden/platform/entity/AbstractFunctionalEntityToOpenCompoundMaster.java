package ua.com.fielden.platform.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.Observable;
import ua.com.fielden.platform.entity.annotation.SkipEntityExistsValidation;
import ua.com.fielden.platform.entity.annotation.Title;
import ua.com.fielden.platform.entity.annotation.mutator.AfterChange;
import ua.com.fielden.platform.entity.meta.impl.DefaultOpenCompoundMasterActionKeyDefiner;

/**
 * A base class for functional entities that are intended to be used as actions to open some compound master.
 * <p>
 * Please, note that this implementation contains handling of changes to <code>sectionTitle</code> through the default key definer.
 * This logic can be overridden in concrete descendants by redefining property <code>key</code> to provide an alternative definer.
 * In most cases the default behaviour should suffice. Please refer {@link DefaultOpenCompoundMasterActionKeyDefiner} for more details.
 *
 * @author TG Team
 *
 * @param <K> -- primary entity type for compound master
 */
public abstract class AbstractFunctionalEntityToOpenCompoundMaster<K extends AbstractEntity<?>> extends AbstractFunctionalEntityWithCentreContext<K> {

    @IsProperty
    @SkipEntityExistsValidation // this is needed to be able to use new (not persisted) instances of keys
    @AfterChange(DefaultOpenCompoundMasterActionKeyDefiner.class)
    private K key;

    @IsProperty
    @Title("Section Title")
    private String sectionTitle;

    @IsProperty
    @Title("Menu To Open")
    private String menuToOpen;

    @IsProperty(Object.class)
    @Title("Entity Presence")
    private Map<String, Integer> entityPresence = new HashMap<>();

    @IsProperty
    @Title("Is Calculated?")
    private boolean calculated = false;

    @Observable
    public AbstractFunctionalEntityToOpenCompoundMaster<K> setCalculated(final boolean calculated) {
        this.calculated = calculated;
        return this;
    }

    public boolean isCalculated() {
        return calculated;
    }

    @Observable
    public AbstractFunctionalEntityToOpenCompoundMaster<K> setEntityPresence(final Map<String, Integer> entityPresence) {
        this.entityPresence.clear();
        this.entityPresence.putAll(entityPresence);
        return this;
    }

    public Map<String, Integer> getEntityPresence() {
        return Collections.unmodifiableMap(entityPresence);
    }

    @Observable
    public AbstractFunctionalEntityToOpenCompoundMaster<K> setMenuToOpen(final String menuToOpen) {
        this.menuToOpen = menuToOpen;
        return this;
    }

    public String getMenuToOpen() {
       return menuToOpen;
    }

    @Observable
    public AbstractFunctionalEntityToOpenCompoundMaster<K> setSectionTitle(final String sectionTitle) {
        this.sectionTitle = sectionTitle;
        return this;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    @Override
    @Observable
    public AbstractFunctionalEntityToOpenCompoundMaster<K> setKey(final K key) {
        this.key = key;
        return this;
    }

    @Override
    public K getKey() {
        return key;
    }

}
