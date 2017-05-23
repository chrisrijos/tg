package ua.com.fielden.platform.entity.validation;

import java.util.Set;

import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.error.Result;

/**
 * Stub validator, which should be used strictly for association of validation results in cases where there is no real validator. For example, property requiredness does not use
 * any validator and validation login in setter does not use any validator.
 *
 * @author TG Team
 *
 */
public class StubValidator implements IBeforeChangeEventHandler {

    public static final StubValidator singleton = new StubValidator();

    private StubValidator() {
    }

    @Override
    public Result handle(final MetaProperty property, final Object oldValue, final Set mutatorAnnotations) {
        throw new IllegalStateException("Handling BCE with stub validator is illegal. It should only be used for referencing purposes.");
    }

}
