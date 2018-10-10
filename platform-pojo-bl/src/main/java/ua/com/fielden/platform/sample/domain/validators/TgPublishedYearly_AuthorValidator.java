package ua.com.fielden.platform.sample.domain.validators;

import java.lang.annotation.Annotation;
import java.util.Set;

import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.entity.validation.IBeforeChangeEventHandler;
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.sample.domain.TgAuthor;

public class TgPublishedYearly_AuthorValidator implements IBeforeChangeEventHandler<TgAuthor> {

    @Override
    public Result handle(final MetaProperty<TgAuthor> property, final TgAuthor newValue, final Set<Annotation> mutatorAnnotations) {
        return Result.successful(newValue);
    }
}