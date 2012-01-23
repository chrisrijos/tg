package ua.com.fielden.platform.swing.treewitheditors.development;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.DefaultTreeCheckingModel;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel.CheckingMode;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import ua.com.fielden.platform.domaintree.IDomainTreeManager.ChangedAction;
import ua.com.fielden.platform.domaintree.IDomainTreeManager.IDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.domaintree.IDomainTreeManager.IPropertyStructureChangedListener;
import ua.com.fielden.platform.domaintree.IDomainTreeManager.ITickManager;
import ua.com.fielden.platform.domaintree.impl.AbstractDomainTree;
import ua.com.fielden.platform.domaintree.impl.EnhancementPropertiesMap;
import ua.com.fielden.platform.reflection.PropertyTypeDeterminator;
import ua.com.fielden.platform.reflection.TitlesDescsGetter;
import ua.com.fielden.platform.swing.dynamicreportstree.EntitiesTreeColumn;
import ua.com.fielden.platform.swing.menu.filter.FilterableTreeModel;
import ua.com.fielden.platform.swing.menu.filter.WordFilter;
import ua.com.fielden.platform.swing.treewitheditors.domaintree.development.EntitiesTreeCellRenderer;
import ua.com.fielden.platform.utils.EntityUtils;
import ua.com.fielden.platform.utils.Pair;

/**
 * A new tree model implementation for the 'entities tree' relying on {@link IDomainTreeManagerAndEnhancer}.
 *
 * @author TG Team
 *
 */
public class EntitiesTreeModel2 extends MultipleCheckboxTreeModel2 {
    private static final long serialVersionUID = -5156365765004770688L;
    public static final String ROOT_PROPERTY = "entities-root";

    private final IDomainTreeManagerAndEnhancer manager;
    private final EntitiesTreeNode2 rootNode;
    /** A cached map of nodes by its names (includes "dummy" and "common"). */
    private final EnhancementPropertiesMap<EntitiesTreeNode2> nodesCache;
    /** A cached map of nodes by its names (includes only real properties without "dummy" and "common" stuff). */
    private final EnhancementPropertiesMap<EntitiesTreeNode2> nodesForSimplePropertiesCache;
    private final TreeCheckingListener [] listeners;
    private final EntitiesTreeCellRenderer cellRenderer1, cellRenderer2;
    private final FilterableTreeModel filterableModel;
    private final Logger logger = Logger.getLogger(getClass());

    /**
     * Creates a new tree model for the 'entities tree' relying on {@link IDomainTreeManagerAndEnhancer}.
     *
     * @param manager
     * @param firstTickCaption
     *            - the name of area corresponding to 0-check-box to which properties should be added/removed.
     * @param secondTickCaption
     * 		  - the name of area corresponding to 1-check-box to which properties should be added/removed.
     */
    public EntitiesTreeModel2(final IDomainTreeManagerAndEnhancer manager, final String firstTickCaption, final String secondTickCaption) {
	super(2);

	this.getCheckingModel(EntitiesTreeColumn.CRITERIA_COLUMN.getColumnIndex()).setCheckingMode(CheckingMode.SIMPLE);
	this.getCheckingModel(EntitiesTreeColumn.TABLE_HEADER_COLUMN.getColumnIndex()).setCheckingMode(CheckingMode.SIMPLE);

	this.manager = manager;
	this.listeners = new TreeCheckingListener [] { createTreeCheckingListener(this.manager.getFirstTick()), createTreeCheckingListener(this.manager.getSecondTick()) };
	this.setRoot(this.rootNode = new EntitiesTreeNode2(createUserObject(EntitiesTreeModel2.class, ROOT_PROPERTY)));
	this.nodesCache = AbstractDomainTree.createPropertiesMap();
	this.nodesForSimplePropertiesCache = AbstractDomainTree.createPropertiesMap();
	this.cellRenderer1 = createCellRenderer(this, firstTickCaption, secondTickCaption);
	this.cellRenderer2 = createCellRenderer(this, firstTickCaption, secondTickCaption);

	// initialise nodes according to included properties of the manager (these include "dummy" and "common properties" stuff)
	for (final Class<?> root : manager.getRepresentation().rootTypes()) {
	    final List<String> properties = manager.getRepresentation().includedProperties(root);
	    for (final String property : properties) {
		createAndAddNode(root, property);
		updateNodeState(manager, root, property, ChangedAction.ADDED);
	    }
	}

	// add the listener into manager to correctly reflect structural changes (property added / removed / checked / disabled / etc.) in this EntitiesTreeModel
	final IPropertyStructureChangedListener managerListener = new IPropertyStructureChangedListener() {
	    @Override
	    public void propertyStructureChanged(final Class<?> root, final String property, final ChangedAction changedAction) {
		if (ChangedAction.REMOVED.equals(changedAction)) {
		    removeNode(root, property);
		    updateNodeState(manager, root, property, changedAction);
		} else if (ChangedAction.ADDED.equals(changedAction)) {
		    createAndAddNode(root, property);
		    updateNodeState(manager, root, property, changedAction);
		} else if (ChangedAction.ENABLEMENT_OR_CHECKING_CHANGED.equals(changedAction)) {
		    updateNodeState(manager, root, AbstractDomainTree.reflectionProperty(property), changedAction);
		}
	    }
	};
	this.manager.addPropertyStructureChangedListener(managerListener);

	// add the listener into EntitiesTreeModel to correctly reflect changes (node checked) in its manager
	this.addTreeCheckingListener(listeners[0], 0);
	this.addTreeCheckingListener(listeners[1], 1);

	this.filterableModel = createFilteringModel(this);
    }

    protected FilterableTreeModel createFilteringModel(final EntitiesTreeModel2 entitiesTreeModel2) {
	// wrap the model
	final FilterableTreeModel model = new FilterableTreeModel(entitiesTreeModel2);
	// filter by "containing words".
	model.addFilter(new WordFilter());
	return model;
    }

    /**
     * Creates a tree cell renderer with some ticks invisible (e.g. "common" property and )
     *
     * @param entitiesTree
     * @param firstTickCaption
     * @param secondTickCaption
     * @return
     */
    protected EntitiesTreeCellRenderer createCellRenderer(final EntitiesTreeModel2 entitiesTreeModel, final String firstTickCaption, final String secondTickCaption) {
	return new EntitiesTreeCellRenderer(entitiesTreeModel, firstTickCaption, secondTickCaption) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
		setCheckingComponentVisible(true);

		final EntitiesTreeNode2 node = (EntitiesTreeNode2) value;
		final Class<?> root = node.getUserObject().getKey();
		final String property = node.getUserObject().getValue();

		if (!isNotDummyAndNotCommonProperty(property)) {
		    setCheckingComponentVisible(false);
		}

		if (PropertyTypeDeterminator.isDotNotation(property)) {
		    final String parentProperty = PropertyTypeDeterminator.penultAndLast(property).getKey();
		    if (!AbstractDomainTree.isCommonBranch(parentProperty) && EntityUtils.isUnionEntityType(PropertyTypeDeterminator.determinePropertyType(root, AbstractDomainTree.reflectionProperty(parentProperty)))) {
			setCheckingComponentVisible(1, false);
		    }
		}
		return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	    }
	};
    }

    /**
     * Creates a node for a property (can be "dummy" or "common") and adds to appropriate place of entities tree.
     *
     * @param root
     * @param property
     */
    private void createAndAddNode(final Class<?> root, final String property) {
	final EntitiesTreeNode2 parentNode = StringUtils.isEmpty(property) ? rootNode //
		: !PropertyTypeDeterminator.isDotNotation(property) ? node(root, "", true) //
			: node(root, PropertyTypeDeterminator.penultAndLast(property).getKey(), true);
	final EntitiesTreeNode2 node = new EntitiesTreeNode2(createUserObject(root, property));
	nodesCache.put(AbstractDomainTree.key(root, property), node);
	if (isNotDummyAndNotCommonProperty(property)) {
	    nodesForSimplePropertiesCache.put(AbstractDomainTree.key(root, AbstractDomainTree.reflectionProperty(property)), node);
	}
	parentNode.add(node);
    }

    protected boolean isNotDummyAndNotCommonProperty(final String property) {
	return !AbstractDomainTree.isDummyMarker(property) && !AbstractDomainTree.isCommonBranch(property);
    }

    /**
     * Removes a node for a property (can be "dummy" or "common") from its place in entities tree.
     *
     * @param root
     * @param property
     */
    private void removeNode(final Class<?> root, final String property) {
	node(root, property, true).removeFromParent();
	nodesCache.remove(AbstractDomainTree.key(root, property));
	if (isNotDummyAndNotCommonProperty(property)) {
	    nodesForSimplePropertiesCache.remove(AbstractDomainTree.key(root, AbstractDomainTree.reflectionProperty(property)));
	}
    }

    /**
     * Provides a checking path in a model.
     *
     * @param model
     * @param path
     * @param checked
     */
    private void provideCheckingPath(final TreeCheckingModel model, final TreePath path, final boolean checked) {
	final List<TreePath> currentPaths = Arrays.asList(model.getCheckingPaths());
	if (checked) {
	    if (!currentPaths.contains(path)) {
		model.addCheckingPath(path);
	    } else {
		logger.warn("Currently checked path [" + path + "] is trying to be checked again.");
	    }
	} else {
	    if (!currentPaths.contains(path)) {
		logger.warn("Currently unchecked path [" + path + "] is trying to be unchecked again.");
	    } else {
		model.removeCheckingPath(path);
	    }
	}
    }

    /**
     * Updates a state of node for a property. Note that for {@link ChangedAction#REMOVED} or {@link ChangedAction#ADDED} actions -- a "dummy" / "common" property can be used,
     * but for other actions -- "real" properties should be used.
     *
     * @param manager
     * @param root
     * @param property
     * @param changedAction
     */
    protected void updateNodeState(final IDomainTreeManagerAndEnhancer manager, final Class<?> root, final String property, final ChangedAction changedAction) {
	if (ChangedAction.REMOVED.equals(changedAction)) { // do nothing with an useless item
	    return;
	} else if (ChangedAction.ADDED.equals(changedAction)) { // in this case property can be "dummy" or under "common-properties" umbrella
	    if (isNotDummyAndNotCommonProperty(property)) { // Update the state of newly created node according to a property state in manager (ignore "dummy" due to its temporal nature)
		provideNodeState(manager, root, AbstractDomainTree.reflectionProperty(property));
	    }
	} else if (ChangedAction.ENABLEMENT_OR_CHECKING_CHANGED.equals(changedAction)) {
	    provideNodeState(manager, root, property);
	}
    }

    /**
     * Provides a state for a node corresponding to a property.
     *
     * @param manager
     * @param root
     * @param property
     */
    protected void provideNodeState(final IDomainTreeManagerAndEnhancer manager, final Class<?> root, final String property) {
	final TreePath path = new TreePath(getPathToRoot(node(root, property, false)));
	final int firstIndex = EntitiesTreeColumn.CRITERIA_COLUMN.getColumnIndex();
	final int secondIndex = EntitiesTreeColumn.TABLE_HEADER_COLUMN.getColumnIndex();

	provideCheckingPath(getCheckingModel(firstIndex), path, manager.getFirstTick().isChecked(root, property));
	provideCheckingPath(getCheckingModel(secondIndex), path, manager.getSecondTick().isChecked(root, property));
	getCheckingModel(firstIndex).setPathEnabled(path, !manager.getRepresentation().getFirstTick().isDisabledImmutably(root, property));
	getCheckingModel(secondIndex).setPathEnabled(path, !manager.getRepresentation().getSecondTick().isDisabledImmutably(root, property));
    }

    /**
     * A {@link DefaultTreeCheckingModelWithoutLosingChecking} with listeners removal (to correctly perform check/uncheck) when {@link #setTreeModel(TreeModel)} invokes.
     *
     * @author TG Team
     *
     */
    protected class DefaultTreeCheckingModelWithoutLosingCheckingWithListenersRemoval extends DefaultTreeCheckingModelWithoutLosingChecking {
	private final int index;

	public DefaultTreeCheckingModelWithoutLosingCheckingWithListenersRemoval(final TreeModel model, final int index) {
	    super(model);
	    this.index = index;
	}

	@Override
	public void setTreeModel(final TreeModel newModel) {
	    if (listeners != null) {
		EntitiesTreeModel2.this.removeTreeCheckingListener(listeners[index], index);
	    }
	    super.setTreeModel(newModel);
	    if (listeners != null) {
		EntitiesTreeModel2.this.addTreeCheckingListener(listeners[index], index);
	    }
	}
    }

    /**
     * Creates a {@link DefaultTreeCheckingModelWithoutLosingChecking} with listeners removal (to correctly perform check/uncheck) when {@link #setTreeModel(TreeModel)} invokes.
     */
    @Override
    protected DefaultTreeCheckingModel createCheckingModel(final int index) {
	return new DefaultTreeCheckingModelWithoutLosingCheckingWithListenersRemoval(this, index);
    }

    /**
     * Creates a listener to perform connection Entities Tree Model => Domain Tree Manager (in sense of "checking").
     *
     * @param tickManager
     * @return
     */
    private TreeCheckingListener createTreeCheckingListener(final ITickManager tickManager) {
	return new TreeCheckingListener() {
	    @Override
	    public void valueChanged(final TreeCheckingEvent checkingEvent) {
		final EntitiesTreeNode2 node = (EntitiesTreeNode2) checkingEvent.getPath().getLastPathComponent();
		final Pair<Class<?>, String> userObject = node.getUserObject();
		final Class<?> root = userObject.getKey();
		final String property = userObject.getValue();
		if (!isNotDummyAndNotCommonProperty(property)) {
		    throw new IllegalArgumentException("The dummy / common property [" + property + "] for type [" + root.getSimpleName() + "] can not be [un]checked.");
		}
		tickManager.check(root, AbstractDomainTree.reflectionProperty(property), checkingEvent.isCheckedPath());
	    }
	};
    }

    /**
     * Finds a node corresponding to a property.
     *
     * @param root
     * @param property
     * @param withDummyNaming -- indicates whether a property can contain "dummy" or "common" properties
     * @return
     */
    private EntitiesTreeNode2 node(final Class<?> root, final String property, final boolean withDummyNaming) {
	final EnhancementPropertiesMap<EntitiesTreeNode2> cache = withDummyNaming ? nodesCache : nodesForSimplePropertiesCache;
	return cache.get(AbstractDomainTree.key(root, property));
    }

    /**
     * Creates a {@link TreeWillExpandListener} that "warms up" the manager's property (loads children), which node is trying to be expanded.
     *
     * @return
     */
    public TreeWillExpandListener createTreeWillExpandListener() {
	return new TreeWillExpandListener() {
	    @Override
	    public void treeWillExpand(final TreeExpansionEvent event) throws ExpandVetoException {
		final EntitiesTreeNode2 node = (EntitiesTreeNode2) event.getPath().getLastPathComponent();
		final Pair<Class<?>, String> rootAndProp = node.getUserObject();
		manager.getRepresentation().warmUp(rootAndProp.getKey(), rootAndProp.getValue());
	    }

	    @Override
	    public void treeWillCollapse(final TreeExpansionEvent event) throws ExpandVetoException {
	    }
	};
    }

    /**
     * Extracts title and description from a property (with a "dummy" contract).
     *
     * @param root
     * @param property
     * @return
     */
    public static Pair<String, String> extractTitleAndDesc(final Class<?> root, final String property) {
	final String title, desc;
	if (EntitiesTreeModel2.ROOT_PROPERTY.equals(property)) { // root node
	    title = "Entities";
	    desc = "<b>Available entities</b>";
	} else if (AbstractDomainTree.isCommonBranch(property)) { // common group
	    title = "Common";
	    desc = TitlesDescsGetter.italic("<b>Common properties</b>");
	} else { // entity node
	    final Pair<String, String> tad = "".equals(property) ? TitlesDescsGetter.getEntityTitleAndDesc(root) : TitlesDescsGetter.getTitleAndDesc(AbstractDomainTree.reflectionProperty(property), root);
	    title = tad.getKey();
	    desc = TitlesDescsGetter.italic("<b>" + tad.getValue() + "</b>");
	}
	return new Pair<String, String>(title, desc);
    }

    /**
     * Returns the {@link IDomainTreeManagerAndEnhancer} instance associated with this {@link EntitiesTreeModel2}.
     *
     * @return
     */
    public IDomainTreeManagerAndEnhancer getManager() {
	return manager;
    }

    /**
     * Creates a user object of the {@link EntitiesTreeNode2} with correct {@link #toString()} implementation to correctly reflect title of the node in the tree.
     *
     * @param root
     * @param property
     * @return
     */
    protected Pair<Class<?>, String> createUserObject(final Class<?> root, final String property) {
	return new Pair<Class<?>, String>(root, property) {
	    private static final long serialVersionUID = -7106027050288695731L;

	    @Override
	    public String toString() {
		return extractTitleAndDesc(getManager().getEnhancer().getManagedType(root), property).getKey();
	    }
	};
    }

    public EntitiesTreeCellRenderer getCellRenderer1() {
        return cellRenderer1;
    }

    public EntitiesTreeCellRenderer getCellRenderer2() {
        return cellRenderer2;
    }

    public FilterableTreeModel getFilterableModel() {
	return filterableModel;
    }
}
