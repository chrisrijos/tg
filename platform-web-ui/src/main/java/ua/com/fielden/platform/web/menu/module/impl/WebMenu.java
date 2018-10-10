package ua.com.fielden.platform.web.menu.module.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import ua.com.fielden.platform.menu.ModuleMenuItem;
import ua.com.fielden.platform.web.interfaces.IExecutable;
import ua.com.fielden.platform.web.minijs.JsCode;

public class WebMenu implements IExecutable {

    List<WebMenuItem> menuItems = new ArrayList<>();

    public WebMenuItem addMenuItem(final String title) {
        final WebMenuItem menuItem = new WebMenuItem(title);
        menuItems.add(menuItem);
        return menuItem;
    }

    @Override
    public JsCode code() {
        return new JsCode("[" + StringUtils.join(menuItems, ",") + "]");
    }

    public List<ModuleMenuItem> getMenu() {
        return menuItems.stream().map(menuItem -> menuItem.getModuleMenuItem()).collect(Collectors.toList());
    }
}
