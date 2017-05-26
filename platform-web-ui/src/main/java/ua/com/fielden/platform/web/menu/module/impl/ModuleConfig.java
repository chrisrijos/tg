package ua.com.fielden.platform.web.menu.module.impl;

import ua.com.fielden.platform.web.centre.EntityCentre;
import ua.com.fielden.platform.web.centre.api.actions.EntityActionConfig;
import ua.com.fielden.platform.web.custom_view.AbstractCustomView;
import ua.com.fielden.platform.web.menu.IMainMenuBuilderWithLayout;
import ua.com.fielden.platform.web.menu.IModuleConfig;
import ua.com.fielden.platform.web.menu.IModuleMenuConfig;
import ua.com.fielden.platform.web.menu.impl.MainMenuBuilder;
import ua.com.fielden.platform.web.menu.module.IModuleConfig0;
import ua.com.fielden.platform.web.menu.module.IModuleConfig1;
import ua.com.fielden.platform.web.menu.module.IModuleConfig2;
import ua.com.fielden.platform.web.menu.module.IModuleConfig3;
import ua.com.fielden.platform.web.menu.module.IModuleConfig4;
import ua.com.fielden.platform.web.menu.module.IModuleConfigDone;
import ua.com.fielden.platform.web.menu.module.IModuleConfigWithAction;
import ua.com.fielden.platform.web.view.master.EntityMaster;

public class ModuleConfig implements IModuleConfig, IModuleConfigWithAction, IModuleConfig0, IModuleConfig1, IModuleConfig2, IModuleConfig3, IModuleConfig4, IModuleConfigDone {

    private final WebMenuModule module;
    private final MainMenuBuilder menuConfig;

    public ModuleConfig(final MainMenuBuilder menuConfig, final WebMenuModule module) {
        this.module = module;
        this.menuConfig = menuConfig;
    }

    @Override
    public IModuleConfigWithAction description(final String description) {
        module.description(description);
        return this;
    }

    @Override
    public IModuleConfigWithAction withAction(final EntityActionConfig actionConfig) {
        module.addAction(actionConfig);
        return this;
    }

    @Override
    public IModuleConfig1 icon(final String icon) {
        module.icon(icon);
        return this;
    }

    @Override
    public IModuleConfig2 detailIcon(final String icon) {
        module.detailIcon(icon);
        return this;
    }

    @Override
    public IModuleConfig3 bgColor(final String htmlColor) {
        module.bgColor(htmlColor);
        return this;
    }

    @Override
    public IModuleConfig4 captionBgColor(final String htmlColor) {
        module.captionBgColor(htmlColor);
        return this;
    }

    @Override
    public IModuleConfigDone centre(final EntityCentre<?> centre) {
        module.view(new WebView(centre));
        return this;
    }

    @Override
    public IModuleConfigDone master(final EntityMaster<?> master) {
        module.view(new WebView(master));
        return this;
    }

    @Override
    public IModuleConfigDone view(final AbstractCustomView view) {
        module.view(new WebView(view));
        return this;
    }

    @Override
    public IModuleMenuConfig menu() {
        return new ModuleMenuConfig(module.menu(), this);
    }

    @Override
    public IMainMenuBuilderWithLayout done() {
        return menuConfig;
    }
}
