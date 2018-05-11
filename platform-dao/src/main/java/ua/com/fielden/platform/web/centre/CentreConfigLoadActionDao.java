package ua.com.fielden.platform.web.centre;

import com.google.inject.Inject;

import ua.com.fielden.platform.dao.CommonEntityDao;
import ua.com.fielden.platform.dao.annotations.SessionRequired;
import ua.com.fielden.platform.entity.annotation.EntityType;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.error.Result;

/** 
 * DAO implementation for companion object {@link ICentreConfigLoadAction}.
 * 
 * @author TG Team
 *
 */
@EntityType(CentreConfigLoadAction.class)
public class CentreConfigLoadActionDao extends CommonEntityDao<CentreConfigLoadAction> implements ICentreConfigLoadAction {
    
    @Inject
    public CentreConfigLoadActionDao(final IFilter filter) {
        super(filter);
    }
    
    @Override
    @SessionRequired
    public CentreConfigLoadAction save(final CentreConfigLoadAction entity) {
        entity.isValid().ifFailure(Result::throwRuntime);
        return super.save(entity);
    }
    
}
