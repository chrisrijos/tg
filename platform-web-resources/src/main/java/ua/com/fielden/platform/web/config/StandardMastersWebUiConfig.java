package ua.com.fielden.platform.web.config;

import static ua.com.fielden.platform.entity.EntityExportAction.PROP_EXPORT_ALL;
import static ua.com.fielden.platform.entity.EntityExportAction.PROP_EXPORT_SELECTED;
import static ua.com.fielden.platform.entity.EntityExportAction.PROP_EXPORT_TOP;
import static ua.com.fielden.platform.entity.EntityExportAction.PROP_NUMBER;
import static ua.com.fielden.platform.web.layout.api.impl.LayoutBuilder.cell;
import static ua.com.fielden.platform.web.layout.api.impl.LayoutCellBuilder.layout;
import ua.com.fielden.platform.web.layout.api.impl.FlexLayoutConfig;

import static java.lang.String.format;

import java.util.Optional;

import com.google.inject.Injector;

import ua.com.fielden.platform.entity.EntityEditAction;
import ua.com.fielden.platform.entity.EntityEditActionProducer;
import ua.com.fielden.platform.entity.EntityExportAction;
import ua.com.fielden.platform.entity.EntityNewAction;
import ua.com.fielden.platform.entity.EntityNewActionProducer;
import ua.com.fielden.platform.web.interfaces.ILayout.Device;
import ua.com.fielden.platform.web.view.master.EntityMaster;
import ua.com.fielden.platform.web.view.master.api.IMaster;
import ua.com.fielden.platform.web.view.master.api.actions.MasterActions;
import ua.com.fielden.platform.web.view.master.api.impl.SimpleMasterBuilder;
import ua.com.fielden.platform.web.view.master.api.with_master.impl.EntityManipulationMasterBuilder;

/**
 * A set of factory methods for various standard platform-level entity masters such as Export to Excel. 
 * 
 * @author TG Team
 *
 */
public class StandardMastersWebUiConfig {

    private StandardMastersWebUiConfig() {}
    
    public static EntityMaster<EntityNewAction> createEntityNewMaster(final Injector injector) {
        return new EntityMaster<>(EntityNewAction.class,
                EntityNewActionProducer.class,
                new EntityManipulationMasterBuilder<EntityNewAction>()
                /*  */.forEntityWithSaveOnActivate(EntityNewAction.class)
                /*  */.withMaster(null) // the master instance is not passing here, this is generic implementation, and master type is calculated from selection criteria context
                /*  */.done(),
                injector);
    }

    public static EntityMaster<EntityEditAction> createEntityEditMaster(final Injector injector) {
        return new EntityMaster<>(EntityEditAction.class,
                EntityEditActionProducer.class,
                new EntityManipulationMasterBuilder<EntityEditAction>()
                /*  */.forEntityWithSaveOnActivate(EntityEditAction.class)
                /*  */.withMaster(null) // the master instance is not passing here, this is generic implementation, and master type is calculated from currentEntity context
                /*  */.done(),
                injector);
    }

    public static EntityMaster<EntityExportAction> createExportMaster(final Injector injector) {
        final FlexLayoutConfig CELL_LAYOUT = layout().flex().end();
        
        final String layout = cell(
                cell(cell(CELL_LAYOUT))
               .cell(cell(CELL_LAYOUT))
               .cell(cell(CELL_LAYOUT))
               .cell(cell(CELL_LAYOUT), layout().withStyle("padding-left", "32px").end()),
               layout().withStyle("padding", "20px").end()).toString();
        
        final String MASTER_ACTION_SPECIFICATION = "'margin: 10px', 'width: 110px'";
        final String MASTER_ACTION_LAYOUT_SPECIFICATION = "'horizontal', 'padding: 20px', 'wrap', 'justify-content: center'";
        final String buttonPanelLayout = format("[%s, [%s], [%s]]", MASTER_ACTION_LAYOUT_SPECIFICATION, MASTER_ACTION_SPECIFICATION, MASTER_ACTION_SPECIFICATION);
        final IMaster<EntityExportAction> masterConfig = new SimpleMasterBuilder<EntityExportAction>()
                .forEntity(EntityExportAction.class)
                .addProp(PROP_EXPORT_ALL).asCheckbox().also()
                .addProp(PROP_EXPORT_SELECTED).asCheckbox().also()
                .addProp(PROP_EXPORT_TOP).asCheckbox().also()
                .addProp(PROP_NUMBER).asSpinner()
                .also()
                .addAction(MasterActions.REFRESH)
                /*      */.shortDesc("CANCEL")
                /*      */.longDesc("Cancel action")
                .addAction(MasterActions.SAVE)
                /*      */.shortDesc("EXPORT")
                /*      */.longDesc("Export action")
                .setActionBarLayoutFor(Device.DESKTOP, Optional.empty(), buttonPanelLayout)
                .setLayoutFor(Device.DESKTOP, Optional.empty(), layout)
                .done();

        return new EntityMaster<>(EntityExportAction.class, masterConfig, injector);
    }
    
    // TODO once it will be necessary, uncomment this code to implement generic EDIT / NEW actions with 'no parent centre refresh' capability:
//    public static EntityMaster<EntityNewActionWithNoParentCentreRefresh> createEntityNewMasterWithNoParentCentreRefresh(final Injector injector) {
//        return new EntityMaster<EntityNewAction>(EntityNewActionWithNoParentCentreRefresh.class,
//                EntityNewActionWithNoParentCentreRefreshProducer.class,
//                new EntityManipulationMasterBuilder<EntityNewActionWithNoParentCentreRefresh>()
//                /*  */.forEntityWithSaveOnActivate(EntityNewActionWithNoParentCentreRefresh.class)
//                /*  */.withMasterAndWithNoParentCentreRefresh(null) // the master instance is not passing here, this is generic implementation, and master type is calculated from selection criteria context
//                /*  */.done(),
//                injector);
//    }
//
//    public static EntityMaster<EntityEditActionWithNoParentCentreRefresh> createEntityEditMasterWithNoParentCentreRefresh(final Injector injector) {
//        return new EntityMaster<EntityEditActionWithNoParentCentreRefresh>(EntityEditActionWithNoParentCentreRefresh.class,
//                EntityEditActionWithNoParentCentreRefreshProducer.class,
//                new EntityManipulationMasterBuilder<EntityEditActionWithNoParentCentreRefresh>()
//                /*  */.forEntityWithSaveOnActivate(EntityEditActionWithNoParentCentreRefresh.class)
//                /*  */.withMasterAndWithNoParentCentreRefresh(null) // the master instance is not passing here, this is generic implementation, and master type is calculated from currentEntity context
//                /*  */.done(),
//                injector);
//    }
}
