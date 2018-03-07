package ua.com.fielden.platform.web.factories.webui;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;

import ua.com.fielden.platform.web.app.ISourceController;
import ua.com.fielden.platform.web.resources.webui.DesktopApplicationStartupResourcesComponentResource;

/**
 * Resource factory for 'desktop-application-startup-resources' component.
 *
 * @author TG Team
 *
 */
public class DesktopApplicationStartupResourcesComponentResourceFactory extends Restlet {
    private final ISourceController sourceController;
    
    public DesktopApplicationStartupResourcesComponentResourceFactory(final ISourceController sourceController) {
        this.sourceController = sourceController;
    }
    
    @Override
    public void handle(final Request request, final Response response) {
        super.handle(request, response);
        
        if (Method.GET == request.getMethod()) {
            final DesktopApplicationStartupResourcesComponentResource resource = new DesktopApplicationStartupResourcesComponentResource(sourceController, getContext(), request, response);
            resource.handle();
        }
    }
    
}