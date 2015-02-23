package edu.umich.med.mbni.lkq.cyontology.internal.view;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.swing.DialogTaskManager;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyTree;
import edu.umich.med.mbni.lkq.cyontology.internal.task.ExpandableNodeCollapseTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.ExpandableNodeExpandOneLevelTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.HideOrShowDanglingNodesTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.PopulateOntologyNetworkTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateAggregationTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateOntologyControlPanelTask;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateOntologyControlPanelTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.AggregationMethodUtil;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.ResourceUtil;

public class OntologyControlPanel extends JPanel implements
		CytoPanelComponent2, TreeWillExpandListener, TreeSelectionListener {

	private static final long serialVersionUID = -5561297105387148003L;

	public static final String CONTROL_PANEL_TITLE = "Ontology Control Panel";

	private final MyApplicationManager appManager;
	private final DialogTaskManager taskManager;

	Choice aggregationColumnChoice;
	Choice interactionTypeChoice;
	Choice aggregationMethodChoice;
	JCheckBox hideDanglingNodesCheckBox;

	JButton refreshAggregationChoicesButton;

	private OntologyTree ontologyTree;

	/**
	 * Create the panel.
	 */
	public OntologyControlPanel() {
		appManager = MyApplicationCenter.getInstance().getApplicationManager();
		taskManager = appManager.getTaskManager();

		setUpUI();
	}

	private void setUpUI() {

		setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(255, 200, 0),
				new Color(255, 200, 0), Color.ORANGE, Color.ORANGE));
		setLayout(null);
		setPreferredSize(new Dimension(433, 600));

		JLabel lblNewLabel = new JLabel("Aggregation Method");
		lblNewLabel.setBounds(17, 6, 130, 29);
		add(lblNewLabel);

		aggregationMethodChoice = new Choice();
		aggregationMethodChoice.setBounds(176, 10, 130, 23);
		add(aggregationMethodChoice);

		JLabel lblNewLabel_1 = new JLabel("Aggregation Column");
		lblNewLabel_1.setBounds(17, 40, 130, 29);
		add(lblNewLabel_1);

		aggregationColumnChoice = new Choice();
		aggregationColumnChoice.setBounds(176, 44, 130, 23);
		add(aggregationColumnChoice);

		refreshAggregationChoicesButton = new JButton("Refresh");
		refreshAggregationChoicesButton.setBounds(326, 41, 88, 29);
		add(refreshAggregationChoicesButton);

		JLabel lblNewLabel_2 = new JLabel("Interaction Type");
		lblNewLabel_2.setBounds(17, 73, 119, 29);
		add(lblNewLabel_2);

		interactionTypeChoice = new Choice();
		interactionTypeChoice.setBounds(176, 77, 130, 23);
		add(interactionTypeChoice);

		hideDanglingNodesCheckBox = new JCheckBox(
				"Selecte to Hide Dangling Nodes");
		hideDanglingNodesCheckBox.setBounds(6, 114, 242, 23);
		add(hideDanglingNodesCheckBox);

		aggregationMethodChoice
				.addItem(AggregationMethodUtil.AGGREGATION_METHOD_MEAN);
		aggregationMethodChoice
				.addItem(AggregationMethodUtil.AGGREGATION_METHOD_MEDIAN);
		aggregationMethodChoice
				.addItem(AggregationMethodUtil.AGGREGATION_METHOD_MAX);
		aggregationMethodChoice
				.addItem(AggregationMethodUtil.AGGREGATION_METHOD_MIN);

		aggregationMethodChoice
				.select(AggregationMethodUtil.AGGREGATION_METHOD_MEAN);

		aggregationMethodChoice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {

					String aggregationMethod = (String) aggregationMethodChoice
							.getSelectedItem();

					CyNetworkView networkView = appManager
							.getCyApplicationManager().getCurrentNetworkView();

					String aggregationColumn = (String) aggregationColumnChoice
							.getSelectedItem();

					if (aggregationColumn == null
							|| aggregationColumn.isEmpty())
						return;

					UpdateAggregationTaskFactory updateAggregationTaskFactory = new UpdateAggregationTaskFactory(
							aggregationMethod, aggregationColumn);

					taskManager.execute(updateAggregationTaskFactory
							.createTaskIterator(networkView));

				}
			}
		});

		refreshAggregationChoicesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				UpdateOntologyControlPanelTask.UpdateOntologyControlOptions options = new UpdateOntologyControlPanelTask.UpdateOntologyControlOptions(
						false, true, false, null);
				CyNetwork currentNetwork = appManager.getCyApplicationManager()
						.getCurrentNetwork();
				UpdateOntologyControlPanelTaskFactory updateOntologyControlPanelTaskFactory = new UpdateOntologyControlPanelTaskFactory(
						OntologyControlPanel.this, options);

				taskManager.execute(updateOntologyControlPanelTaskFactory
						.createTaskIterator(currentNetwork));
			}
		});

		interactionTypeChoice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {

					CyNetwork underlyingNetwork = appManager
							.getCyApplicationManager().getCurrentNetwork();

					String selectedItem = (String) interactionTypeChoice
							.getSelectedItem();
					PopulateOntologyNetworkTaskFactory populateOntologyNetworkTaskFactory = new PopulateOntologyNetworkTaskFactory(
							selectedItem);

					taskManager.execute(populateOntologyNetworkTaskFactory
							.createTaskIterator(underlyingNetwork));

				}

			}
		});

		hideDanglingNodesCheckBox.setSelected(true);

		hideDanglingNodesCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				CyNetwork underlyingNetwork = appManager
						.getCyApplicationManager().getCurrentNetwork();

				Collection<CyNetworkView> networkViews = appManager
						.getCyNetworkViewManager().getNetworkViews(
								underlyingNetwork);
				if (networkViews.isEmpty())
					return;

				CyNetworkView networkView = networkViews.iterator().next();

				HideOrShowDanglingNodesTaskFactory hideOrShowDanglingNodesTaskFactory = new HideOrShowDanglingNodesTaskFactory(
						!hideDanglingNodesCheckBox.isSelected());

				taskManager.execute(hideOrShowDanglingNodesTaskFactory
						.createTaskIterator(networkView));
			}
		});

	}

	public void setOntologyTree(DefaultMutableTreeNode root,
			OntologyNetwork ontologyNetwork) {

		if (ontologyTree == null) {
			ontologyTree = new OntologyTree(root, ontologyNetwork);

			// TODO Doesn't work for now
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

			renderer.setLeafIcon(ResourceUtil.leafIcon);
			renderer.setOpenIcon(ResourceUtil.openIcon);
			renderer.setClosedIcon(ResourceUtil.closedIcon);

			ontologyTree.setCellRenderer(renderer);
			ontologyTree.addTreeSelectionListener(this);

			JScrollPane scrollPane = new JScrollPane(ontologyTree);
			scrollPane.setBounds(17, 149, 397, 450);
			add(scrollPane);
		}

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

	public void setAggregationColumnChoice(Collection<String> columns) {
		aggregationColumnChoice.removeAll();
		for (String item : columns) {
			aggregationColumnChoice.addItem(item);
		}
	}

	public void setInteractionTypeChoice(Collection<String> interactions,
			String selectedItem) {
		interactionTypeChoice.removeAll();
		for (String item : interactions) {
			interactionTypeChoice.addItem(item);
		}
		interactionTypeChoice.select(selectedItem);
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public String getTitle() {
		return CONTROL_PANEL_TITLE;
	}

	@Override
	public String getIdentifier() {
		return CONTROL_PANEL_TITLE;
	}

	@Override
	public void valueChanged(TreeSelectionEvent event) {

		CyNetworkView underlyingNetworkView = appManager
				.getCyApplicationManager().getCurrentNetworkView();
		OntologyNetwork encapsulatingOntologyNetwork = ontologyTree
				.getOntologyNetwork();

		if (encapsulatingOntologyNetwork.getUnderlyingNetwork() != underlyingNetworkView
				.getModel())
			return;

		for (TreePath path : event.getPaths()) {

			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path
					.getLastPathComponent();

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
		if (encapsulatingOntologyNetwork.getUnderlyingNetwork() != underlyingNetworkView
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

		// remove the listener to avoid triggering of the event on child nodes
		// again
		ontologyTree.removeTreeWillExpandListener(this);

		TreePath path = event.getPath();
		DefaultMutableTreeNode collpasingNode = (DefaultMutableTreeNode) path
				.getLastPathComponent();

		collpaseNodeCompletely(collpasingNode);

		ontologyTree.addTreeWillExpandListener(this);

		ExpandableNode correspondingNode = (ExpandableNode) collpasingNode
				.getUserObject();

		CyNetworkView underlyingNetworkView = appManager
				.getCyApplicationManager().getCurrentNetworkView();
		OntologyNetwork encapsulatingOntologyNetwork = ontologyTree
				.getOntologyNetwork();
		if (encapsulatingOntologyNetwork.getUnderlyingNetwork() != underlyingNetworkView
				.getModel())
			return;

		ExpandableNodeCollapseTaskFactory expandableNodeCollapseTaskFactory = new ExpandableNodeCollapseTaskFactory();

		taskManager.execute(expandableNodeCollapseTaskFactory
				.createTaskIterator(underlyingNetworkView
						.getNodeView(correspondingNode.getCyNode()),
						underlyingNetworkView));

		// veto the default collapsing of the tree
		throw new ExpandVetoException(event);
	}

	private void collpaseNodeCompletely(DefaultMutableTreeNode node) {
		for (int i = 0; i < node.getChildCount(); ++i) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
					.getChildAt(i);
			collpaseNodeCompletely(child);
		}
		ontologyTree.collapsePath(new TreePath(node.getPath()));
	}
}
