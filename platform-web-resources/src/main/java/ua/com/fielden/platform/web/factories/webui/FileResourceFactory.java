package ua.com.fielden.platform.web.factories.webui;

import java.util.Collections;
import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;

import ua.com.fielden.platform.web.app.ISourceController;
import ua.com.fielden.platform.web.interfaces.IDeviceProvider;
import ua.com.fielden.platform.web.resources.webui.FileResource;

/**
 * The server resource factory for {@link FileResource} that returns file to the client.
 *
 * @author TG Team
 *
 */
public class FileResourceFactory extends Restlet {
    private final ISourceController sourceController;
    private final List<String> resourcePaths;
    private final IDeviceProvider deviceProvider;

    /**
     * Creates new {@link FileResourceFactory} instance with specified paths of file resources.
     *
     * @param resourcePaths
     */
    public FileResourceFactory(final ISourceController sourceController, final List<String> resourcePaths, final IDeviceProvider deviceProvider) {
        this.sourceController = sourceController;
        this.resourcePaths = resourcePaths;
        this.deviceProvider = deviceProvider;
    }
    
    /**
     * Invoked on GET request from client.
     */
    @Override
    public void handle(final Request request, final Response response) {
        super.handle(request, response);
        
        if (Method.GET.equals(request.getMethod())) {
            new FileResource(sourceController, Collections.unmodifiableList(resourcePaths), deviceProvider, getContext(), request, response).handle();
        }
    }
    
}