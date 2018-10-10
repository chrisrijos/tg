package ua.com.fielden.platform.web.menu.module;

import ua.com.fielden.platform.web.centre.EntityCentre;
import ua.com.fielden.platform.web.custom_view.AbstractCustomView;
import ua.com.fielden.platform.web.menu.IModuleMenuConfig;
import ua.com.fielden.platform.web.view.master.EntityMaster;

/**
 * Used for specifying view (entity centre, master or custom view) for this module.
 *
 * @author TG Team
 *
 */
public interface IModuleConfig4 {

    IModuleConfigDone centre(final EntityCentre<?> centre);
    IModuleConfigDone master(final EntityMaster<?> master);

    /**
     * Specifies custom view for this module config.
     *
     * @param view
     * @return
     */
    IModuleConfigDone view(final AbstractCustomView view);

    //TODO must provide other custom views.

    IModuleMenuConfig menu();
}
