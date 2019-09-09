package ua.com.fielden.platform.web.factories.webui;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;

import ua.com.fielden.platform.web.app.IWebResourceLoader;
import ua.com.fielden.platform.web.interfaces.IDeviceProvider;
import ua.com.fielden.platform.web.resources.webui.MainWebUiComponentResource;

public class MainWebUiComponentResourceFactory extends Restlet {
    private final IWebResourceLoader sourceController;
    private final IDeviceProvider deviceProvider;

    public MainWebUiComponentResourceFactory(final IWebResourceLoader sourceController, final IDeviceProvider deviceProvider) {
        this.sourceController = sourceController;
        this.deviceProvider = deviceProvider;
    }

    @Override
    public void handle(final Request request, final Response response) {
        super.handle(request, response);

        if (Method.GET.equals(request.getMethod())) {
            new MainWebUiComponentResource(sourceController, deviceProvider, getContext(), request, response).handle();
        }
    }

}