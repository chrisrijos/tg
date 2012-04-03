package ua.com.fielden.platform.web;

import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ua.com.fielden.platform.dao2.IUserRoleDao2;
import ua.com.fielden.platform.security.provider.IUserController2;
import ua.com.fielden.platform.serialisation.api.ISerialiser;
import ua.com.fielden.platform.web.resources.RestServerUtil;
import ua.com.fielden.platform.web.resources.UserRoleAssociationResource;

import com.google.inject.Injector;

/**
 * This is {@link Restlet} implementation that provides logic for correct instantiation of {@link UserRoleAssociationResource}.
 *
 * @author TG Team
 *
 */
public class UserRoleAssociationResourceFactory extends Restlet {
    private final Injector injector;
    private final RestServerUtil restUtil;

    /**
     * Principle constructor.
     */
    public UserRoleAssociationResourceFactory(final Injector injector) {
	this.injector = injector;
	this.restUtil = new RestServerUtil(injector.getInstance(ISerialiser.class));
    }

    @Override
    public void handle(final Request request, final Response response) {
	super.handle(request, response);

	if (Method.POST.equals(request.getMethod())) {
	    final IUserController2 controller = injector.getInstance(IUserController2.class);
	    final IUserRoleDao2 userRoleDao = injector.getInstance(IUserRoleDao2.class);

	    new UserRoleAssociationResource(controller, userRoleDao, restUtil, getContext(), request, response).handlePost();
	}
    }
}