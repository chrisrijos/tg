package ua.com.fielden.platform.web.menu.module.impl;

import ua.com.fielden.platform.menu.Module;
import ua.com.fielden.platform.web.interfaces.IExecutable;
import ua.com.fielden.platform.web.minijs.JsCode;

public class WebMenuModule implements IExecutable {

    private final String title;
    private String description;
    private String bgColor;
    private String captionBgColor;
    private String icon;
    private String detailIcon;
    private WebMenu menu;
    private WebView view;

    public WebMenuModule(final String title) {
        this.title = title;
    }

    public WebMenuModule description(final String description) {
        this.description = description;
        return this;
    }

    public WebMenuModule bgColor(final String bgColor) {
        this.bgColor = bgColor;
        return this;
    }

    public WebMenuModule captionBgColor(final String captionBgColor) {
        this.captionBgColor = captionBgColor;
        return this;
    }

    public WebMenuModule icon(final String icon) {
        this.icon = icon;
        return this;
    }

    public WebMenuModule detailIcon(final String detailIcon) {
        this.detailIcon = detailIcon;
        return this;
    }

    public WebMenu menu() {
        this.menu = new WebMenu();
        return this.menu;
    }

    public WebMenuModule view(final WebView view) {
        this.view = view;
        return this;
    }

    @Override
    public JsCode code() {
        final String code = "{ title: \"" + this.title + "\", " +
                "description: \"" + this.description + "\", " +
                "bgColor: \"" + this.bgColor + "\", " +
                "captionBgColor: \"" + this.captionBgColor + "\", " +
                "icon: \"" + this.icon + "\", " +
                "detailIcon: \"" + this.detailIcon + "\"" +
                (this.menu != null ? ", menu: " + menu.code() : "") +
                (this.view != null ? ", view: " + view.code() : "") +
                "}";
        return new JsCode(code);
    }

    @Override
    public String toString() {
        return code().toString();
    }

    public Module getModule() {
        final Module module = new Module().
                setBgColor(bgColor).
                setCaptionBgColor(captionBgColor).
                setIcon(icon).
                setDetailIcon(detailIcon).
                setKey(title).
                setDesc(description);
        //TODO module menu can not be null. Right now platform supports modules with view. This case should be covered with separate issue.
        if (this.menu != null) {
            module.setMenu(menu.getMenu());
        }
        return module;
    }
}
