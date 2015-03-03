package ua.com.fielden.platform.web.app.config;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.swing.menu.MiWithConfigurationSupport;
import ua.com.fielden.platform.web.app.IWebApp;
import ua.com.fielden.platform.web.centre.EntityCentre;
import ua.com.fielden.platform.web.view.master.EntityMaster;

/**
 * Web application's global configuration object.
 *
 * @author TG Team
 *
 */
public interface IWebAppConfig {

    /**
     * Set the minimal desktop width.
     *
     * @param width
     * @return
     */
    IWebAppConfig setMinDesktopWidth(int width);

    /**
     * Set the minimal tablet width
     *
     * @param width
     * @return
     */
    IWebAppConfig setMinTabletWidth(int width);

    /**
     * Set the locale for the web application.
     *
     * @param locale
     * @return
     */
    IWebAppConfig setLocale(String locale);

    /**
     * Adds the entity master to web application configuration object.
     *
     * @param entityType
     * @param master
     * @return
     */
    <T extends AbstractEntity<?>> IWebAppConfig addMaster(Class<T> entityType, EntityMaster<T> master);

    /**
     * Adds the entity centre to web application configuration object.
     *
     * @param menuType
     * @param centre
     * @return
     */
    <M extends MiWithConfigurationSupport<?>> IWebAppConfig addCentre(Class<M> menuType, EntityCentre centre);

    /**
     * Finish to configure the web application.
     *
     * @return
     */
    IWebApp done();

}
