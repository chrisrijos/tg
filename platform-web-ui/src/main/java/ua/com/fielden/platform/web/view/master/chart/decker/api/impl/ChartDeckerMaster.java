package ua.com.fielden.platform.web.view.master.chart.decker.api.impl;

import java.util.Optional;

import ua.com.fielden.platform.basic.IValueMatcherWithContext;
import ua.com.fielden.platform.dom.DomElement;
import ua.com.fielden.platform.dom.InnerTextElement;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.utils.ResourceLoader;
import ua.com.fielden.platform.web.centre.api.actions.EntityActionConfig;
import ua.com.fielden.platform.web.centre.api.resultset.impl.FunctionalActionKind;
import ua.com.fielden.platform.web.interfaces.IRenderable;
import ua.com.fielden.platform.web.view.master.api.IMaster;
import ua.com.fielden.platform.web.view.master.chart.decker.api.IChartDeckerConfig;

public class ChartDeckerMaster<T extends AbstractEntity<?>> implements IMaster<T>{

    private final IRenderable renderable;

    public ChartDeckerMaster(final IChartDeckerConfig<T> deckerConfig) {

        final String entityMasterStr = ResourceLoader.getText("ua/com/fielden/platform/web/master/tg-entity-master-template.html")
                .replace("<!--@imports-->", ""/*SimpleMasterBuilder.createImports(importPaths)*/)
                .replace("@entity_type", deckerConfig.getEntityType().getSimpleName())
                .replace("<!--@tg-entity-master-content-->", "")
                .replace("//generatedPrimaryActions", "")
                .replace("//@ready-callback", readyCallback())
                .replace("@prefDim", "null")
                .replace("@noUiValue", "false")
                .replace("@saveOnActivationValue", String.valueOf(deckerConfig.shouldSaveOnActivation()));

        renderable = new IRenderable() {
            @Override
            public DomElement render() {
                return new InnerTextElement(entityMasterStr);
            }
        };
    }

    private String readyCallback() {
        return "";
    }

    @Override
    public IRenderable render() {
        return renderable;
    }


    @Override
    public EntityActionConfig actionConfig(final FunctionalActionKind actionKind, final int actionNumber) {
        throw new UnsupportedOperationException("Getting of action configuration is not supported.");
    }

    @Override
    public Optional<Class<? extends IValueMatcherWithContext<T, ?>>> matcherTypeFor(final String propName) {
        return Optional.empty();
    }

}
