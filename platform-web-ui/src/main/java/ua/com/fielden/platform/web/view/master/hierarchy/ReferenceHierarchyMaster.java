package ua.com.fielden.platform.web.view.master.hierarchy;

import static java.util.Optional.empty;
import static ua.com.fielden.platform.web.centre.EntityCentre.IMPORTS;
import static ua.com.fielden.platform.web.view.master.EntityMaster.ENTITY_TYPE;
import static ua.com.fielden.platform.web.view.master.EntityMaster.flattenedNameOf;
import static ua.com.fielden.platform.web.view.master.api.impl.SimpleMasterBuilder.createImports;

import java.util.LinkedHashSet;
import java.util.Optional;

import ua.com.fielden.platform.basic.IValueMatcherWithContext;
import ua.com.fielden.platform.dom.DomElement;
import ua.com.fielden.platform.dom.InnerTextElement;
import ua.com.fielden.platform.entity.ReferenceHierarchy;
import ua.com.fielden.platform.utils.ResourceLoader;
import ua.com.fielden.platform.web.centre.api.actions.EntityActionConfig;
import ua.com.fielden.platform.web.centre.api.resultset.impl.FunctionalActionKind;
import ua.com.fielden.platform.web.interfaces.IRenderable;
import ua.com.fielden.platform.web.view.master.api.IMaster;

public class ReferenceHierarchyMaster implements IMaster<ReferenceHierarchy> {

//    private final List<EntityActionConfig> actions = new ArrayList<>();
    private final IRenderable renderable;

    public ReferenceHierarchyMaster (/*final List<EntityActionConfig> actions*/) {

        final LinkedHashSet<String> importPaths = new LinkedHashSet<>();
        importPaths.add("components/tg-reference-hierarchy");
        importPaths.add("editors/tg-singleline-text-editor");

//        this.actions.clear();
//        this.actions.addAll(actions);

        final DomElement hierarchyFilter = new DomElement("tg-singleline-text-editor")
                .attr("id", "referenceHierarchyFilter")
                .attr("class", "filter-element")
                .attr("slot", "filter-element")
                .attr("entity", "{{_currBindingEntity}}")
                .attr("original-entity", "{{_originalBindingEntity}}")
                .attr("previous-modified-properties-holder", "[[_previousModifiedPropertiesHolder]]")
                .attr("property-name", "referenceHierarchyFilter")
                .attr("validation-callback", "[[doNotValidate]]")
                .attr("prop-title", "Type to filter reference hierarchy")
                .attr("prop-desc", "Display types or instances those matched entered text")
                .attr("current-state", "[[currentState]]");

        final DomElement referenceHierarchyDom = new DomElement("tg-refernce-hierarchy")
                .attr("id", "refrenceHierarchy")
                .attr("entity", "{{_currBindingEntity}}")
                .attr("on-tg-load-refrence-hierarchy", "_loadSubReferenceHierarchy")
                .add(hierarchyFilter);

        //Generating action's DOM and JS functions
//        final StringBuilder customActionObjects = new StringBuilder();
//        final String prefix = ",\n";
//        final int prefixLength = prefix.length();
//        for (int actionIdx = 0; actionIdx < this.actions.size(); actionIdx++) {
//            final EntityActionConfig action = this.actions.get(actionIdx);
//            if (!action.isNoAction()) {
//                final FunctionalActionElement el = FunctionalActionElement.newEntityActionForMaster(action, actionIdx);
//                importPaths.add(el.importPath());
//                hierarchyDom.add(el.render().attr("hidden", null).clazz("primary-action").attr("slot", "primary-action"));
//                customActionObjects.append(prefix + el.createActionObject());
//            }
//        }
//        final String customActionObjectsString = customActionObjects.toString();

        final StringBuilder prefDimBuilder = new StringBuilder();
        prefDimBuilder.append("{'width': function() {return '50%'}, 'height': function() {return '70%'}, 'widthUnit': '', 'heightUnit': ''}");

        final String entityMasterStr = ResourceLoader.getText("ua/com/fielden/platform/web/master/tg-entity-master-template.js")
                .replace(IMPORTS, createImports(importPaths))
                .replace(ENTITY_TYPE, flattenedNameOf(ReferenceHierarchy.class))
                .replace("<!--@tg-entity-master-content-->", referenceHierarchyDom.toString())
//                .replace("//generatedPrimaryActions", customActionObjectsString.length() > prefixLength ? customActionObjectsString.substring(prefixLength)
//                        : customActionObjectsString)
                .replace("//@ready-callback", readyCallback())
                .replace("@prefDim", prefDimBuilder.toString())
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
        return "self.classList.add('layout');\n"
                + "self.classList.add('vertical');\n"
                + "self.canLeave = function () {\n"
                + "    return true;\n"
                + "}.bind(self);\n"
                + "//Need for security marix editors binding.\n"
                + "self._isNecessaryForConversion = function (propertyName) { \n"
                + "    return ['referenceHierarchyFilter','refEntityId', 'refEntityType', 'entityType'].indexOf(propertyName) >= 0; \n"
                + "}; \n"
                + "self.$.referenceHierarchyFilter._onInput = function () {\n"
                + "    // clear hierarchy filter timer if it is in progress.\n"
                + "    this._cancelHierarchyFilterTimer();\n"
                + "    this._hierarchyFilterTimer = this.async(this._filterHierarchy, 500);\n"
                + "}.bind(self);\n"
                + "self._cancelHierarchyFilterTimer = function () {\n"
                + "    if (this._hierarchyFilterTimer) {\n"
                + "        this.cancelAsync(this._hierarchyFilterTimer);\n"
                + "        this._hierarchyFilterTimer = null;\n"
                + "    }\n"
                + "}.bind(self);\n"
                + "self._filterHierarchy = function () {\n"
                + "    this.$.refrenceHierarchy.filterHierarchy(this.$.referenceHierarchyFilter._editingValue);\n"
                + "}.bind(self);\n"
                + "self._loadSubReferenceHierarchy = function (e) {\n"
                + "    this.save();\n"
                + "}.bind(self);\n";
    }

    @Override
    public Optional<Class<? extends IValueMatcherWithContext<ReferenceHierarchy, ?>>> matcherTypeFor(final String propName) {
        return empty();
    }

    @Override
    public IRenderable render() {
        return renderable;
    }

    @Override
    public EntityActionConfig actionConfig(final FunctionalActionKind actionKind, final int actionNumber) {
//        if (FunctionalActionKind.PRIMARY_RESULT_SET == actionKind) {
//            return this.actions.get(actionNumber);
//        }
        throw new UnsupportedOperationException("Getting of action configuration is not supported.");
    }

}
