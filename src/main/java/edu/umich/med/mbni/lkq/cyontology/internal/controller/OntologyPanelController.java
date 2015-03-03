package edu.umich.med.mbni.lkq.cyontology.internal.controller;

import java.util.Collection;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.RowSetRecord;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.swing.DialogTaskManager;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.event.OntologyAggregationColumnChangeEvent;
import edu.umich.med.mbni.lkq.cyontology.internal.event.OntologyAggregationMethodChangeEvent;
import edu.umich.med.mbni.lkq.cyontology.internal.event.OntologyHideDanglingNodesEvent;
import edu.umich.med.mbni.lkq.cyontology.internal.event.OntologyInteractionChangeEvent;
import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyAggregationChoiceRefreshListener;
import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyAggregationColumnChangeListener;
import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyAggregationMethodChangeListener;
import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyHideDanglingNodesListener;
import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyInteractionChangeListener;
import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyNodeExpansionListener;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.task.ExpandableNodeCollapseTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.ExpandableNodeExpandOneLevelTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.HideOrShowDanglingNodesTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.PopulateNewOntologyNetworkTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateAggregationTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateOntologyControlPanelTask;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateOntologyControlPanelTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyPluginPanel;
import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyTree;

public class OntologyPanelController implements
		OntologyInteractionChangeListener,
		OntologyAggregationColumnChangeListener,
		OntologyAggregationMethodChangeListener,
		OntologyAggregationChoiceRefreshListener,
		OntologyHideDanglingNodesListener, TreeSelectionListener,
		RowsSetListener, NetworkAboutToBeDestroyedListener,
		NetworkViewAboutToBeDestroyedListener, TreeWillExpandListener,
		OntologyNodeExpansionListener {
	private OntologyPluginPanel ontologyPluginPanel;
	private MyApplicationManager appManager;
	private DialogTaskManager taskManager;
	private OntologyTree ontologyTree;

	public OntologyPanelController(OntologyPluginPanel ontologyPluginPanel) {
		ontologyPluginPanel.setOntologyInteractionChangeListener(this);
		ontologyPluginPanel.setOntologyAggregationColumnChangeListener(this);
		ontologyPluginPanel.setOntologyAggregationMethodChangeListener(this);
		ontologyPluginPanel
				.setOntologyRefreshAggregationChoiceChangeListener(this);
		ontologyPluginPanel.setOntologyHideDanglingNodesListener(this);

		ontologyPluginPanel.getOntologyTree().addTreeSelectionListener(this);
		ontologyPluginPanel.getOntologyTree().addTreeWillExpandListener(this);

		this.ontologyPluginPanel = ontologyPluginPanel;
		this.ontologyTree = ontologyPluginPanel.getOntologyTree();
		appManager = MyApplicationCenter.getInstance().getApplicationManager();
		taskManager = appManager.getTaskManager();
	}

	@Override
	public void interactionChangePerformed(OntologyInteractionChangeEvent event) {

		String interactionType = event.getInteractionType();
		CyNetwork underlyingNetwork = appManager.getCyApplicationManager()
				.getCurrentNetwork();

		PopulateNewOntologyNetworkTaskFactory populateNewOntologyNetworkTaskFactory = new PopulateNewOntologyNetworkTaskFactory(
				interactionType);
		
		CyNetwork originalNetwork;
		if(MyApplicationCenter.getInstance().hasOntologyNetworkFromUnderlyingCyNetwork(underlyingNetwork)) {
			originalNetwork = MyApplicationCenter.getInstance().getOntologyNetworkFromUnderlyingCyNetwork(underlyingNetwork).getOriginalCyNetwork();
		} else {
			originalNetwork = underlyingNetwork;
		}

		taskManager.execute(populateNewOntologyNetworkTaskFactory
				.createTaskIterator(originalNetwork));

	}

	@Override
	public void ontologyAggregationColumnChangePerformed(
			OntologyAggregationColumnChangeEvent event) {

		CyNetworkView networkView = appManager.getCyApplicationManager()
				.getCurrentNetworkView();

		String aggregationColumn = event.getAggregationColumn();
		String aggregationMethod = event.getAggregationMethod();
		if (aggregationColumn == null || aggregationColumn.isEmpty())
			return;

		UpdateAggregationTaskFactory updateAggregationTaskFactory = new UpdateAggregationTaskFactory(
				aggregationMethod, aggregationColumn);

		taskManager.execute(updateAggregationTaskFactory
				.createTaskIterator(networkView));

	}

	@Override
	public void ontologyAggreagationMethodChangePerformed(
			OntologyAggregationMethodChangeEvent event) {
		CyNetworkView networkView = appManager.getCyApplicationManager()
				.getCurrentNetworkView();

		String aggregationColumn = event.getAggregationColumn();
		String aggregationMethod = event.getAggregationMethod();
		if (aggregationColumn == null || aggregationColumn.isEmpty())
			return;

		UpdateAggregationTaskFactory updateAggregationTaskFactory = new UpdateAggregationTaskFactory(
				aggregationMethod, aggregationColumn);

		taskManager.execute(updateAggregationTaskFactory
				.createTaskIterator(networkView));

	}

	@Override
	public void ontologyAggregationChoiceRefreshPerformed(EventObject event) {
		UpdateOntologyControlPanelTask.UpdateOntologyControlOptions options = new UpdateOntologyControlPanelTask.UpdateOntologyControlOptions(
				false, true, false, null);
		CyNetwork currentNetwork = appManager.getCyApplicationManager()
				.getCurrentNetwork();
		
		if (currentNetwork == null)
			return;
		UpdateOntologyControlPanelTaskFactory updateOntologyControlPanelTaskFactory = new UpdateOntologyControlPanelTaskFactory(
				ontologyPluginPanel, options);

		taskManager.execute(updateOntologyControlPanelTaskFactory
				.createTaskIterator(currentNetwork));

	}

	@Override
	public void ontologyHideDanglingNodesPerformed(
			OntologyHideDanglingNodesEvent event) {
		CyNetworkView currentNetworkView = appManager.getCyApplicationManager()
				.getCurrentNetworkView();

		HideOrShowDanglingNodesTaskFactory hideOrShowDanglingNodesTaskFactory = new HideOrShowDanglingNodesTaskFactory(
				event.isHiding());

		taskManager.execute(hideOrShowDanglingNodesTaskFactory
				.createTaskIterator(currentNetworkView));
	}

	@Override
	public void valueChanged(TreeSelectionEvent event) {
		CyNetworkView underlyingNetworkView = appManager
				.getCyApplicationManager().getCurrentNetworkView();
		OntologyNetwork encapsulatingOntologyNetwork = ontologyTree
				.getOntologyNetwork();
		if (underlyingNetworkView == null
				|| encapsulatingOntologyNetwork == null)
			return;

		if (encapsulatingOntologyNetwork.getUnderlyingCyNetwork() != underlyingNetworkView
				.getModel())
			return;

		for (TreePath path : event.getPaths()) {

			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path
					.getLastPathComponent();

			if (selectedNode.equals(ontologyTree.getModel().getRoot()))
				continue;

			ExpandableNode correspondingNode = (ExpandableNode) selectedNode
					.getUserObject();

			if (underlyingNetworkView
					.getNodeView(correspondingNode.getCyNode())
					.getVisualProperty(BasicVisualLexicon.NODE_VISIBLE)) {
				if (event.isAddedPath(path)) {
					underlyingNetworkView.getModel()
							.getRow(correspondingNode.getCyNode())
							.set("selected", true);
				} else {
					underlyingNetworkView.getModel()
							.getRow(correspondingNode.getCyNode())
							.set("selected", false);
				}
			}
		}

	}

	@Override
	public void handleEvent(RowsSetEvent e) {
		if (ontologyTree == null)
			return;

		// get all the selected items in the network, check if having selected
		// nodes
		Collection<RowSetRecord> rowSetRecords = e.getColumnRecords("selected");
		if (rowSetRecords.isEmpty())
			return;

		LinkedList<TreePath> treePathsShouldBeSelected = new LinkedList<>();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) ontologyTree
				.getModel().getRoot();
		if (root == null)
			return;

		List<CyNode> nodes = CyTableUtil.getNodesInState(ontologyTree
				.getOntologyNetwork().getUnderlyingCyNetwork(), "selected", true);

		for (CyNode node : nodes) {

			ExpandableNode userNode = ontologyTree.getOntologyNetwork()
					.getNode(node);

			if (userNode == null)
				continue;

			List<DefaultMutableTreeNode> nodesFound = searchNodeInTree(
					userNode, ontologyTree);
			if (!nodesFound.isEmpty()) {
				for (DefaultMutableTreeNode nodeFound : nodesFound) {
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode) nodeFound
							.getParent();

					// to prevent selection of an invisible node, which causes a
					// expansion
					if (ontologyTree.isExpanded(new TreePath(parent.getPath()))) {
						treePathsShouldBeSelected.add(new TreePath(nodeFound
								.getPath()));
					}
				}
			}

		}

		ontologyTree.removeTreeSelectionListener(this);

		try {
			ontologyTree.setSelectionPaths(treePathsShouldBeSelected
					.toArray(new TreePath[treePathsShouldBeSelected.size()]));
			if (treePathsShouldBeSelected.size() == 1) {
				ontologyTree.scrollPathToVisible(treePathsShouldBeSelected
						.getFirst());
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			ontologyTree.addTreeSelectionListener(this);
		}
	}

	@Override
	public void handleEvent(NetworkViewAboutToBeDestroyedEvent e) {
		ontologyPluginPanel.cleanUpView();
	}

	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		ontologyPluginPanel.cleanUpView();
	}

	@Override
	public void expansionPerformed(EventObject event) {
		ExpandableNode expandableNode = (ExpandableNode) event.getSource();
		List<DefaultMutableTreeNode> nodesFound = searchNodeInTree(
				expandableNode, ontologyTree);

		if (nodesFound.isEmpty())
			return;

		for (DefaultMutableTreeNode nodeFound : nodesFound) {
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) nodeFound
					.getParent();
			// if the node is invisible in the tree now, just skip it.
			if (ontologyTree.isCollapsed(new TreePath(parent.getPath())))
				continue;
			if (expandableNode.isCollapsed()) {
				setOntologyTreeNodeCollpased(nodeFound, true);
			} else {
				setOntologyTreeNodeCollpased(nodeFound, false);
			}
		}
	}

	@Override
	public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		TreePath path = event.getPath();
		DefaultMutableTreeNode expandingNode = (DefaultMutableTreeNode) path
				.getLastPathComponent();

		ExpandableNode correspondingNode = (ExpandableNode) expandingNode
				.getUserObject();

		CyNetworkView underlyingNetworkView = appManager
				.getCyApplicationManager().getCurrentNetworkView();
		OntologyNetwork encapsulatingOntologyNetwork = ontologyTree
				.getOntologyNetwork();
		if (encapsulatingOntologyNetwork.getUnderlyingCyNetwork() != underlyingNetworkView
				.getModel())
			return;

		ExpandableNodeExpandOneLevelTaskFactory expandableNodeExpandOneLevelTaskFactory = new ExpandableNodeExpandOneLevelTaskFactory();

		taskManager.execute(expandableNodeExpandOneLevelTaskFactory
				.createTaskIterator(underlyingNetworkView
						.getNodeView(correspondingNode.getCyNode()),
						underlyingNetworkView));
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {
		TreePath path = event.getPath();
		DefaultMutableTreeNode collpasingNode = (DefaultMutableTreeNode) path
				.getLastPathComponent();

		setOntologyTreeNodeCollpased(collpasingNode, true);

		ExpandableNode correspondingNode = (ExpandableNode) collpasingNode
				.getUserObject();

		CyNetworkView underlyingNetworkView = appManager
				.getCyApplicationManager().getCurrentNetworkView();
		OntologyNetwork encapsulatingOntologyNetwork = ontologyTree
				.getOntologyNetwork();
		if (encapsulatingOntologyNetwork.getUnderlyingCyNetwork() != underlyingNetworkView
				.getModel())
			return;

		ExpandableNodeCollapseTaskFactory expandableNodeCollapseTaskFactory = new ExpandableNodeCollapseTaskFactory();

		taskManager.execute(expandableNodeCollapseTaskFactory
				.createTaskIterator(underlyingNetworkView
						.getNodeView(correspondingNode.getCyNode()),
						underlyingNetworkView));

		// veto the default collapsing of the tree which won't collapse the
		// child nodes of the collapsing nodes
		throw new ExpandVetoException(event);
	}

	public void setOntologyTree(DefaultMutableTreeNode root,
			OntologyNetwork ontologyNetwork) {

		ontologyTree.setOntologyNetwork(ontologyNetwork);

		ontologyTree.getOntologyNetwork().addNodeExpansionListener(this);

		DefaultTreeModel model = (DefaultTreeModel) ontologyTree.getModel();
		model.setRoot(root);

		ontologyTree.removeTreeWillExpandListener(this);

		// expand the tree to level 1 to match the Ontology Network displayed on
		// the canvas
		for (int i = 0; i < root.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) root
					.getChildAt(i);
			ontologyTree.expandPath(new TreePath(child.getPath()));
		}

		ontologyTree.addTreeWillExpandListener(this);
	}

	private List<DefaultMutableTreeNode> searchNodeInTree(Object userNode,
			JTree tree) {
		LinkedList<DefaultMutableTreeNode> nodesFound = new LinkedList<DefaultMutableTreeNode>();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel()
				.getRoot();

		for (Enumeration<?> enumeration = root.breadthFirstEnumeration(); enumeration
				.hasMoreElements();) {
			DefaultMutableTreeNode current = (DefaultMutableTreeNode) enumeration
					.nextElement();

			if (userNode.equals(current.getUserObject())) {
				nodesFound.add(current);
			}
		}

		return nodesFound;
	}

	public void setOntologyTreeNodeCollpased(DefaultMutableTreeNode node,
			boolean isCollpasing) {
		// remove the listener to avoid triggering of the event on child nodes
		// again
		ontologyTree.removeTreeWillExpandListener(this);

		if (!isCollpasing
				&& !ontologyTree.isExpanded(new TreePath(node.getPath()))) {
			ontologyTree.expandNode(node);
		}

		if (isCollpasing
				&& ontologyTree.isExpanded(new TreePath(node.getPath()))) {
			ontologyTree.collpaseNodeCompletely(node);
		}

		ontologyTree.addTreeWillExpandListener(this);
	}
}
