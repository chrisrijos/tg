package ua.com.fielden.platform.web.factories.webui;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;

import ua.com.fielden.platform.utils.IDates;
import ua.com.fielden.platform.web.interfaces.IDeviceProvider;
import ua.com.fielden.platform.web.resources.webui.ErrorLoggerResource;

public class ErrorLoggerResourceFactory extends Restlet {


    private final IDeviceProvider deviceProvider;
    private final IDates dates;

    public ErrorLoggerResourceFactory(final IDeviceProvider deviceProvider, final IDates dates) {
        this.deviceProvider = deviceProvider;
        this.dates = dates;
    }

    /**
     * Invokes on POST request from client.
     */
    @Override
    public void handle(final Request request, final Response response) {
        super.handle(request, response);

        if (Method.POST.equals(request.getMethod())) {
            new ErrorLoggerResource(getContext(),
                    request,
                    response,
                    deviceProvider,
                    dates).handle();
        }
    }
}
