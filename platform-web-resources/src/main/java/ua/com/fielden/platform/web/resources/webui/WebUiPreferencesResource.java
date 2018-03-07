package ua.com.fielden.platform.web.resources.webui;

import java.io.ByteArrayInputStream;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Encoding;
import org.restlet.engine.application.EncodeRepresentation;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.google.common.base.Charsets;

import ua.com.fielden.platform.security.user.IUserProvider;
import ua.com.fielden.platform.web.app.ISourceController;

/**
 * Responds to GET requests with generated application specific Web UI preferences, which include location, widths settings for responsive layout etc.
 *
 * @author TG Team
 *
 */
public class WebUiPreferencesResource extends DeviceProfileDifferentiatorResource {
    private final ISourceController sourceController;
    
    public WebUiPreferencesResource(final ISourceController sourceController, final IUserProvider userProvider, final Context context, final Request request, final Response response) {
        super(context, request, response, userProvider);
        this.sourceController = sourceController;
    }
    
    @Override
    protected Representation get() throws ResourceException {
        final String source = sourceController.loadSource("/app/tg-app-config.html", device());
        return new EncodeRepresentation(Encoding.GZIP, new InputRepresentation(new ByteArrayInputStream(source.getBytes(Charsets.UTF_8))));
    }
    
}