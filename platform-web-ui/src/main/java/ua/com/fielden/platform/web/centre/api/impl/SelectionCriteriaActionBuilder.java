package ua.com.fielden.platform.web.centre.api.impl;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.utils.EntityUtils;
import ua.com.fielden.platform.web.centre.api.actions.EntityActionConfig;
import ua.com.fielden.platform.web.centre.api.crit.ISelectionCritKindSelector;
import ua.com.fielden.platform.web.centre.api.selection_crit_actions.IAlsoSelectionCriteriaActions;
import ua.com.fielden.platform.web.centre.api.selection_crit_actions.ISelectionCriteriaActions;

public class SelectionCriteriaActionBuilder<T extends AbstractEntity<?>> extends ResultSetBuilder<T> implements ISelectionCriteriaActions<T>, IAlsoSelectionCriteriaActions<T> {

    private final EntityCentreBuilder<T> builder;

    public SelectionCriteriaActionBuilder(final EntityCentreBuilder<T> builder) {
        super(builder);
        this.builder = builder;
    }

    @Override
    public ISelectionCritKindSelector<T> addCrit(final String propName) {
        if (StringUtils.isEmpty(propName)) {
            throw new IllegalArgumentException("Property name should not be empty.");
        }

        if (!"this".equals(propName) && !EntityUtils.isProperty(this.builder.getEntityType(), propName)) {
            throw new IllegalArgumentException(String.format("Provided value '%s' is not a valid property expression for entity '%s'", propName, builder.getEntityType().getSimpleName()));
        }

        if (builder.selectionCriteria.contains(propName)) {
            throw new IllegalArgumentException(String.format("Provided value '%s' has been already added as a selection criterion for entity '%s'", propName, builder.getEntityType().getSimpleName()));
        }

        builder.currSelectionCrit = Optional.of(propName);
        builder.selectionCriteria.add(propName);
        return new SelectionCriteriaBuilder<>(builder, this);
    }

    @Override
    public IAlsoSelectionCriteriaActions<T> addSelectionCriteriaAction(final EntityActionConfig actionConfig) {
        builder.selectionCriteriaActions.add(actionConfig);
        return this;
    }

    @Override
    public ISelectionCriteriaActions<T> also() {
        // this could be a genuine also call to add more top level actions
        // or a polymorphic call inherited from ResultSetBuilder to add more properties into the result set
        // they need to be differentiated
        if (propName.isPresent() || propDef.isPresent()) {
            super.also();
        }

        return this;
    };
}
