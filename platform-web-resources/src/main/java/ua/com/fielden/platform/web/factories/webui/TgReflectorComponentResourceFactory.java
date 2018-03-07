package ua.com.fielden.platform.web.factories.webui;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;

import ua.com.fielden.platform.security.user.IUserProvider;
import ua.com.fielden.platform.web.app.ISourceController;
import ua.com.fielden.platform.web.resources.webui.TgReflectorComponentResource;

/**
 * Resource factory for tg-reflector component.
 *
 * @author TG Team
 *
 */
public class TgReflectorComponentResourceFactory extends Restlet {
    private final ISourceController sourceController;
    private final IUserProvider userProvider;
    
    public TgReflectorComponentResourceFactory(final ISourceController sourceController, final IUserProvider userProvider) {
        this.sourceController = sourceController;
        this.userProvider = userProvider;
    }
    
    @Override
    public void handle(final Request request, final Response response) {
        super.handle(request, response);
        
        if (Method.GET == request.getMethod()) {
            final TgReflectorComponentResource resource = new TgReflectorComponentResource(sourceController, userProvider, getContext(), request, response);
            resource.handle();
        }
    }
    
}