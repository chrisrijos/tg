package ua.com.fielden.platform.example.dynamiccriteria;

import java.util.Map;
import java.util.Properties;

import ua.com.fielden.platform.basic.config.IApplicationDomainProvider;
import ua.com.fielden.platform.criteria.generator.ICriteriaGenerator;
import ua.com.fielden.platform.criteria.generator.impl.CriteriaGenerator;
import ua.com.fielden.platform.dao.IGeneratedEntityController;
import ua.com.fielden.platform.domaintree.IGlobalDomainTreeManager;
import ua.com.fielden.platform.domaintree.impl.GlobalDomainTreeManager;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.example.dynamiccriteria.ao.NestedEntityDao;
import ua.com.fielden.platform.example.dynamiccriteria.ao.SimpleCompositeEntityDao;
import ua.com.fielden.platform.example.dynamiccriteria.ao.SimpleECEEntityDao;
import ua.com.fielden.platform.example.dynamiccriteria.iao.INestedEntityDao;
import ua.com.fielden.platform.example.dynamiccriteria.iao.ISimpleCompositeEntityDao;
import ua.com.fielden.platform.example.dynamiccriteria.iao.ISimpleECEEntityDao;
import ua.com.fielden.platform.ioc.BasicWebServerModule;
import ua.com.fielden.platform.security.user.IUserProvider;
import ua.com.fielden.platform.serialisation.api.ISerialisationClassProvider;
import ua.com.fielden.platform.swing.review.EntityMasterManager;
import ua.com.fielden.platform.swing.review.IEntityMasterManager;

import com.google.inject.Scopes;

public class EntityCentreExampleModule extends BasicWebServerModule {

    @SuppressWarnings("rawtypes")
    public EntityCentreExampleModule(//
    final Map<Class, Class> defaultHibernateTypes, //
            final IApplicationDomainProvider applicationDomainProvider,//
            final Class<? extends ISerialisationClassProvider> serialisationClassProviderType, //
            final Class<? extends IFilter> automaticDataFilterType, //
            final Properties props) throws Exception {
        super(defaultHibernateTypes, applicationDomainProvider, serialisationClassProviderType, automaticDataFilterType, null, props);

    }

    @Override
    protected void configure() {
        super.configure();
        bind(IUserProvider.class).to(BaseUserProvider.class).in(Scopes.SINGLETON);
        bind(ICriteriaGenerator.class).to(CriteriaGenerator.class).in(Scopes.SINGLETON);
        bind(IGlobalDomainTreeManager.class).to(GlobalDomainTreeManager.class).in(Scopes.SINGLETON);
        bind(IEntityMasterManager.class).to(EntityMasterManager.class).in(Scopes.SINGLETON);
        bind(IGeneratedEntityController.class).to(GeneratedEntityControllerStub.class).in(Scopes.SINGLETON);
        bind(ISimpleECEEntityDao.class).to(SimpleECEEntityDao.class);
        bind(INestedEntityDao.class).to(NestedEntityDao.class);
        //bind(IMasterConfigurationController.class).to(RemoteMasterConfigurationController.class).in(Scopes.SINGLETON);
        bind(ISimpleCompositeEntityDao.class).to(SimpleCompositeEntityDao.class);
    }

}
