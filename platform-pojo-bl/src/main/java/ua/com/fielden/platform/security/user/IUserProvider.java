package ua.com.fielden.platform.security.user;

/**
 * An abstraction for accessing a logged in application user.
 * 
 * @author TG Team
 * 
 */
public interface IUserProvider {
    
    User getUser();
    
    void setUsername(final String username, final IUser coUser);
    
    void setUser(final User user);
    
}
