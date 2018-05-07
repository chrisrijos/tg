package ua.com.fielden.platform.web.security;

import java.util.LinkedHashSet;
import java.util.Optional;

import ua.com.fielden.platform.basic.IValueMatcherWithContext;
import ua.com.fielden.platform.dom.DomElement;
import ua.com.fielden.platform.dom.InnerTextElement;
import ua.com.fielden.platform.entity.SecurityMatrixInsertionPoint;
import ua.com.fielden.platform.utils.ResourceLoader;
import ua.com.fielden.platform.web.centre.api.actions.EntityActionConfig;
import ua.com.fielden.platform.web.centre.api.resultset.impl.FunctionalActionKind;
import ua.com.fielden.platform.web.interfaces.IRenderable;
import ua.com.fielden.platform.web.view.master.api.IMaster;
import ua.com.fielden.platform.web.view.master.api.impl.SimpleMasterBuilder;

/**
 * An entity master that represents a chart for {@link VehiclePmCostSavingsChart}.
 *
 * @author TG Team
 *
 * @param <T>
 */
public class SecurityMatrixInsertionPointMaster implements IMaster<SecurityMatrixInsertionPoint> {

    private final IRenderable renderable;

    public SecurityMatrixInsertionPointMaster() {

        final LinkedHashSet<String> importPaths = new LinkedHashSet<>();
        importPaths.add("components/tg-security-matrix");
        importPaths.add("editors/tg-singleline-text-editor");

        final DomElement tokenFilter = new DomElement("tg-singleline-text-editor")
                .attr("id", "tokenFilter")
                .attr("class", "filter-element")
                .attr("entity", "{{_currBindingEntity}}")
                .attr("original-entity", "{{_originalBindingEntity}}")
                .attr("previous-modified-properties-holder", "[[_previousModifiedPropertiesHolder]]")
                .attr("property-name", "tokenFilter")
                .attr("validation-callback", "[[doNotValidate]]")
                .attr("prop-title", "Type to filter security tokens")
                .attr("prop-desc", "Displays only tokens those matched entered text")
                .attr("current-state", "[[currentState]]");

        final DomElement securityMatrix = new DomElement("tg-security-matrix")
                .attr("id", "securityMatrix")
                .attr("entity", "{{_currBindingEntity}}")
                .attr("original-entity", "{{_originalBindingEntity}}")
                .attr("parent-modified-properties-holder", "[[_previousModifiedPropertiesHolder]]")
                .attr("centre-selection", "[[centreSelection]]")
                .attr("custom-event-target", "[[customEventTarget]]")
                .attr("retrieved-entities", "{{retrievedEntities}}")
                .attr("is-centre-running", "[[isCentreRunning]]")
                .attr("uuid", "[[centreUuid]]")
                .attr("lock", "[[lock]]")
                .add(tokenFilter);

        final String entityMasterStr = ResourceLoader.getText("ua/com/fielden/platform/web/master/tg-entity-master-template.html")
                .replace("<!--@imports-->", SimpleMasterBuilder.createImports(importPaths))
                .replace("@entity_type", SecurityMatrixInsertionPoint.class.getSimpleName())
                .replace("<!--@tg-entity-master-content-->", securityMatrix.toString())
                .replace("//generatedPrimaryActions", "")
                .replace("//@ready-callback", readyCallback())
                .replace("@prefDim", "null")
                .replace("@noUiValue", "false")
                .replace("@saveOnActivationValue", "true");

        renderable = new IRenderable() {
            @Override
            public DomElement render() {
                return new InnerTextElement(entityMasterStr);
            }
        };
    }

    private String readyCallback() {
        return "self.classList.remove('canLeave');\n"
                +"//Need for security marix editors binding.\n"
                + "self._isNecessaryForConversion = function (propertyName) { \n"
                + "    return ['tokenFilter'].indexOf(propertyName) >= 0; \n"
                + "}; \n"
                + "self.$.tokenFilter._onInput = function () {\n"
                + "    // clear token filter timer if it is in progress.\n"
                + "    this._cancelTokenFilterTimer();\n"
                + "    this._filterTokenTimer = this.async(this._filterToken, 500);\n"
                + "}.bind(self);\n"
                + "self._cancelTokenFilterTimer = function () {\n"
                + "    if (this._filterTokenTimer) {\n"
                + "        this.cancelAsync(this._filterTokenTimer);\n"
                + "        this._filterTokenTimer = null;\n"
                + "    }\n"
                + "}.bind(self);\n"
                + "self._filterToken = function () {\n"
                + "    this.$.securityMatrix.filterTokens(this.$.tokenFilter._editingValue);\n"
                + "}.bind(self);\n"
                + "//Locks/Unlocks tg-security-matrix lock layer during insertion point activation.\n"
                + "self.disableViewForDescendants = function () {\n"
                + "    Polymer.TgBehaviors.TgEntityBinderBehavior.disableViewForDescendants.call(this);\n"
                + "    self.lock = true;\n"
                + "    self.showDataLoadingPromt();\n"
                + "};\n"
                + "self.enableViewForDescendants = function () {\n"
                + "    Polymer.TgBehaviors.TgEntityBinderBehavior.enableViewForDescendants.call(this);\n"
                + "    self.lock = false;"
                + "    self.showDataLoadedPromt();\n"
                + "};\n"
                + "self.showDataLoadingPromt = function () {\n"
                + "    this._toastGreeting().text = 'Loading scheduling data...';\n"
                + "    this._toastGreeting().hasMore = false;\n"
                + "    this._toastGreeting().showProgress = true;\n"
                + "    this._toastGreeting().msgHeading = 'Info';\n"
                + "    this._toastGreeting().isCritical = false;\n"
                + "    this._toastGreeting().show();\n"
                + "};\n"
                + "self.showDataLoadedPromt = function () {\n"
                + "    this._toastGreeting().text = 'Loading completed successfully';\n"
                + "    this._toastGreeting().hasMore = false;\n"
                + "    this._toastGreeting().showProgress = false;\n"
                + "    this._toastGreeting().msgHeading = 'Info';\n"
                + "    this._toastGreeting().isCritical = false;\n"
                + "    this._toastGreeting().show();\n"
                + "};\n";
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
    public Optional<Class<? extends IValueMatcherWithContext<SecurityMatrixInsertionPoint, ?>>> matcherTypeFor(final String propName) {
        return Optional.empty();
    }

}
