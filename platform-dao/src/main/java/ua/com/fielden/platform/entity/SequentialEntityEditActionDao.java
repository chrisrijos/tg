package ua.com.fielden.platform.entity;

import com.google.inject.Inject;

import ua.com.fielden.platform.dao.CommonEntityDao;
import ua.com.fielden.platform.entity.query.IFilter;

/**
 * DAO implementation for companion object {@link ISequentialEntityEditAction}.
 *
 * @author Developers
 *
 */
public class SequentialEntityEditActionDao extends CommonEntityDao<SequentialEntityEditAction> implements ISequentialEntityEditAction {

    @Inject
    protected SequentialEntityEditActionDao(final IFilter filter) {
        super(filter);
    }

}
