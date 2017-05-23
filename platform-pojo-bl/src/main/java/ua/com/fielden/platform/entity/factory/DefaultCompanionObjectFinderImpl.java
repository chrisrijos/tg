package ua.com.fielden.platform.entity.factory;

import static java.lang.String.*;
import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;

import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.CompanionObject;

/**
 * Default implementation for {@link ICompanionObjectFinder}, which utilises injector for creating controller instances.
 * 
 * @author TG Team
 * 
 */
public class DefaultCompanionObjectFinderImpl implements ICompanionObjectFinder {

    private static final Logger LOGGER = Logger.getLogger(DefaultCompanionObjectFinderImpl.class);
    
    @Inject
    private Injector injector;

    @Override
    public <T extends IEntityDao<E>, E extends AbstractEntity<?>> T find(final Class<E> type) {
        if (type.isAnnotationPresent(CompanionObject.class)) {
            try {
                final Class<T> coType = (Class<T>) type.getAnnotation(CompanionObject.class).value();
                return injector.getInstance(coType);
            } catch (final Exception e) {
                LOGGER.warn(format("Could not locate companion for type [%s].", type.getName()), e);
                // if controller could not be instantiated for whatever reason it can be considered non-existent
                // thus, returning null
                return null;
            }
        }
        return null;
    }

    public Injector getInjector() {
        return injector;
    }

    public void setInjector(final Injector injector) {
        this.injector = injector;
    }
}