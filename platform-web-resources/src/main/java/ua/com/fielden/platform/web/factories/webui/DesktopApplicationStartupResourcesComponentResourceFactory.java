package ua.com.fielden.platform.web.factories.webui;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;

import ua.com.fielden.platform.web.app.ISourceController;
import ua.com.fielden.platform.web.interfaces.IDeviceProvider;
import ua.com.fielden.platform.web.resources.webui.DesktopApplicationStartupResourcesComponentResource;

/**
 * Resource factory for 'desktop-application-startup-resources' component.
 *
 * @author TG Team
 *
 */
public class DesktopApplicationStartupResourcesComponentResourceFactory extends Restlet {
    private final ISourceController sourceController;
    private final IDeviceProvider deviceProvider;

    public DesktopApplicationStartupResourcesComponentResourceFactory(final ISourceController sourceController, final IDeviceProvider deviceProvider) {
        this.sourceController = sourceController;
        this.deviceProvider = deviceProvider;
    }

    @Override
    public void handle(final Request request, final Response response) {
        super.handle(request, response);

        if (Method.GET == request.getMethod()) {
            final DesktopApplicationStartupResourcesComponentResource resource = new DesktopApplicationStartupResourcesComponentResource(sourceController, deviceProvider, getContext(), request, response);
            resource.handle();
        }
    }

}