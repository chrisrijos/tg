package ua.com.fielden.platform.web.factories.webui;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;

import ua.com.fielden.platform.web.app.ISourceController;
import ua.com.fielden.platform.web.resources.webui.TgElementLoaderComponentResource;

/**
 * Resource factory for tg-element-loader component.
 *
 * @author TG Team
 *
 */
public class TgElementLoaderComponentResourceFactory extends Restlet {
    private final ISourceController sourceController;
    
    public TgElementLoaderComponentResourceFactory(final ISourceController sourceController) {
        this.sourceController = sourceController;
    }
    
    @Override
    public void handle(final Request request, final Response response) {
        super.handle(request, response);
        
        if (Method.GET == request.getMethod()) {
            final TgElementLoaderComponentResource resource = new TgElementLoaderComponentResource(sourceController, getContext(), request, response);
            resource.handle();
        }
    }
    
}