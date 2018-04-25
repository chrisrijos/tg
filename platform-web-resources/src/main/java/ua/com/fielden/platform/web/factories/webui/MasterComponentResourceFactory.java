package ua.com.fielden.platform.web.factories.webui;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;

import ua.com.fielden.platform.web.app.ISourceController;
import ua.com.fielden.platform.web.interfaces.IDeviceProvider;
import ua.com.fielden.platform.web.resources.RestServerUtil;
import ua.com.fielden.platform.web.resources.webui.MasterComponentResource;

/**
 * The server resource factory for entity masters.
 *
 * The master identification information is a part of the URI: "/master_ui/{entityType}".
 *
 * @author TG Team
 *
 */
public class MasterComponentResourceFactory extends Restlet {
    private final ISourceController sourceController;
    private final RestServerUtil restUtil;
    private final IDeviceProvider deviceProvider;

    /**
     * Creates the {@link MasterComponentResourceFactory} instance.
     *
     * @param centres
     */
    public MasterComponentResourceFactory(final ISourceController sourceController, final RestServerUtil restUtil, final IDeviceProvider deviceProvider) {
        this.sourceController = sourceController;
        this.restUtil = restUtil;
        this.deviceProvider = deviceProvider;
    }

    @Override
    public void handle(final Request request, final Response response) {
        super.handle(request, response);

        if (Method.GET.equals(request.getMethod())) {
            new MasterComponentResource(
                    sourceController,
                    restUtil,
                    deviceProvider,
                    getContext(),
                    request,
                    response //
            ).handle();
        }
    }
}
