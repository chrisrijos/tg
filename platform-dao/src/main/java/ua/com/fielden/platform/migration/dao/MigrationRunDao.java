package ua.com.fielden.platform.migration.dao;

import ua.com.fielden.platform.dao.CommonEntityDao;
import ua.com.fielden.platform.entity.annotation.EntityType;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.migration.MigrationRun;
import ua.com.fielden.platform.migration.controller.IMigrationRun;

import com.google.inject.Inject;

/**
 * DAO for {@link MigrationRun}.
 * 
 * @author TG Team
 */
@EntityType(MigrationRun.class)
public class MigrationRunDao extends CommonEntityDao<MigrationRun> implements IMigrationRun {

    @Inject
    protected MigrationRunDao(final IFilter filter) {
        super(filter);
    }
}
