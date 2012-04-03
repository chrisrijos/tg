package ua.com.fielden.platform.security;

import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.orderBy;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;

import java.util.List;

import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ua.com.fielden.platform.dao2.IUserRoleDao2;
import ua.com.fielden.platform.entity.query.fetch;
import ua.com.fielden.platform.entity.query.fetchAll;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.entity.query.model.OrderingModel;
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.rao.CommonEntityRao;
import ua.com.fielden.platform.rao.RestClientUtil;
import ua.com.fielden.platform.security.provider.IUserController2;
import ua.com.fielden.platform.security.user.User;
import ua.com.fielden.platform.security.user.UserAndRoleAssociation;
import ua.com.fielden.platform.security.user.UserRole;
import ua.com.fielden.platform.swing.review.annotations.EntityType;
import ua.com.fielden.platform.utils.CollectionUtil;
import ua.com.fielden.platform.utils.Pair;

import com.google.inject.Inject;

/**
 * RAO implementation of the {@link IUserController2}.
 *
 * @author TG Team
 *
 */

@EntityType(User.class)
public class UserControllerRao extends CommonEntityRao<User> implements IUserController2 {
    private final fetch<User> fetchModel = new fetch<User>(User.class).with("basedOnUser").with("roles", new fetch<UserAndRoleAssociation>(UserAndRoleAssociation.class).with("userRole"));

    private final IUserRoleDao2 userRoleDao;

    @Inject
    public UserControllerRao(final IUserRoleDao2 userRoleDao, final RestClientUtil restUtil) {
	super(restUtil);
	this.userRoleDao = userRoleDao;
    }

    @Override
    public List<? extends UserRole> findAllUserRoles() {
	return userRoleDao.findAll();
    }

    @Override
    public List<User> findAllUsers() {
	return findAllUsersWithRoles();
    }

    @Override
    public void updateUser(final User user, final List<UserRole> roles) {
	final String ids = CollectionUtil.toString(roles, "id", ",");
	final Request request = restUtil.newRequest(Method.POST, restUtil.getBaseUri(getDefaultWebResourceType()) + "/useroles?userId=" + user.getId() + "&roles=" + ids);
	final Pair<Response, Result> result = restUtil.process(request);
	if (!result.getValue().isSuccessful()) {
	    throw result.getValue();
	}
    }

    @Override
    public User findUserByIdWithRoles(final Long id) {
	return findById(id, fetchModel);
    }

    @Override
    public User findUserByKeyWithRoles(final String key) {
	final EntityResultQueryModel<User> model = select(User.class).where().prop("key").eq().val(key).model();
	return getEntity(from(model).with(fetchModel).build());
    }

    @Override
    public List<User> findAllUsersWithRoles() {
	final EntityResultQueryModel<User> model = select(User.class).where().prop("key").isNotNull().model();
	final OrderingModel orderBy = orderBy().prop("key").asc().model();
	return getAllEntities(from(model).with(fetchModel).with(orderBy).build());
    }

    @Override
    public User findUser(final String username) {
	return findByKeyAndFetch(new fetchAll<User>(User.class), username);
    }
}