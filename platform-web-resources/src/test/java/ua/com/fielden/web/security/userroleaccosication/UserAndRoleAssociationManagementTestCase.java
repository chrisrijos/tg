package ua.com.fielden.web.security.userroleaccosication;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.restlet.Restlet;
import org.restlet.Router;

import ua.com.fielden.platform.dao2.IUserRoleDao2;
import ua.com.fielden.platform.security.UserControllerRao;
import ua.com.fielden.platform.security.UserRoleRao;
import ua.com.fielden.platform.security.provider.IUserController2;
import ua.com.fielden.platform.security.user.IUserDao2;
import ua.com.fielden.platform.security.user.User;
import ua.com.fielden.platform.security.user.UserRole;
import ua.com.fielden.platform.test.DbDrivenTestCase2;
import ua.com.fielden.platform.web.UserRoleAssociationResourceFactory;
import ua.com.fielden.platform.web.resources.RouterHelper;
import ua.com.fielden.platform.web.test.WebBasedTestCase;

/**
 * Provides a unit test for user/role management.
 *
 * @author TG Team
 *
 */
public class UserAndRoleAssociationManagementTestCase extends WebBasedTestCase {

    private final IUserRoleDao2 userRoleRao = new UserRoleRao(config.restClientUtil());
    private final IUserController2 userControllerRao = new UserControllerRao(userRoleRao, config.restClientUtil());

    @Test
    public void test_that_all_user_roles_can_be_found() {
	final List<? extends UserRole> roles = userControllerRao.findAllUserRoles();
	assertEquals("Incorrect number of roles.", 8, roles.size());
    }

    @Test
    public void test_that_all_users_can_be_found() {
	final List<? extends User> users = userControllerRao.findAllUsers();
	assertEquals("Incorrect number of user.", 4, users.size());
    }

    @Test
    public void test_that_all_users_have_roles() {
	final List<? extends User> users = userControllerRao.findAllUsers();
	for (final User user: users) {
	    assertEquals("Incorrect number of roles for user " +  user, 2, user.getRoles().size());
	}
    }

    @Test
    public void test_that_user_role_associations_can_be_updated_by_substituting_existing_associaitons() {
	final User user = userControllerRao.findAllUsers().get(0);
	assertEquals("Unexpected first user.", "USER-1", user.getKey());
	final UserRole role = userControllerRao.findAllUserRoles().get(2);
	assertEquals("Unexpected third role.", "role3", role.getKey());

	userControllerRao.updateUser(user, new ArrayList<UserRole>(){{add(role);}});
	assertEquals("Incorrect number of roles.", 1, userControllerRao.findAllUsers().get(0).getRoles().size());
    }

    @Test
    public void test_that_user_role_associations_can_be_updated_by_adding_new_association() {
	final User user = userControllerRao.findAllUsers().get(0);
	assertEquals("Unexpected first user.", "USER-1", user.getKey());
	final UserRole role = userControllerRao.findAllUserRoles().get(2);
	assertEquals("Unexpected third role.", "role3", role.getKey());

	userControllerRao.updateUser(user, new ArrayList<UserRole>(user.roles()){{add(role);}});
	assertEquals("Incorrect number of roles.", 3, userControllerRao.findAllUsers().get(0).getRoles().size());
    }

    @Test
    public void test_that_user_role_associations_can_be_updated_by_removing_associations() {
	final User user = userControllerRao.findAllUsers().get(0);
	assertEquals("Unexpected first user.", "USER-1", user.getKey());
	final UserRole role = userControllerRao.findAllUserRoles().get(2);
	assertEquals("Unexpected third role.", "role3", role.getKey());

	userControllerRao.updateUser(user, new ArrayList<UserRole>());
	assertEquals("Incorrect number of roles.", 0, userControllerRao.findAllUsers().get(0).getRoles().size());
    }


    @Override
    public synchronized Restlet getRoot() {
	final Router router = new Router(getContext());

	final RouterHelper helper = new RouterHelper(DbDrivenTestCase2.injector, DbDrivenTestCase2.entityFactory);
	helper.register(router, IUserRoleDao2.class);
	helper.register(router, IUserDao2.class);

	final Restlet userRoleAssociationRestlet = new UserRoleAssociationResourceFactory(DbDrivenTestCase2.injector);
	router.attach("/users/{username}/useroles", userRoleAssociationRestlet);

	return router;
    }

    @Override
    protected String[] getDataSetPaths() {
	return new String[] { "src/test/resources/data-files/user-role-test-case.flat.xml" };
    }

}
