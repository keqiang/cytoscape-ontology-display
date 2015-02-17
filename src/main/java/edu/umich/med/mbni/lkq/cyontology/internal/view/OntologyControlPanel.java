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
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.swing.DialogTaskManager;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.task.HideOrShowDanglingNodesTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.PopulateOntologyNetworkTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateAggregationTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateOntologyControlPanelTask;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateOntologyControlPanelTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.AggregationMethodUtil;

public class OntologyControlPanel extends JPanel implements CytoPanelComponent2 {

	private static final long serialVersionUID = -5561297105387148003L;

	public static final String CONTROL_PANEL_TITLE = "Ontology Control Panel";

	Choice aggregationColumnChoice;
	Choice interactionTypeChoice;
	Choice aggregationMethodChoice;
	JCheckBox hideDanglingNodesCheckBox;

	JButton refreshAggregationChoicesButton;

	private JTree ontologyTree;

	/**
	 * Create the panel.
	 */
	public OntologyControlPanel() {
		setUpUI();
		// rePopTheAggregationValues();
		// rePopTheInteractionType();
	}

	private void createsOntologyTree(DefaultMutableTreeNode root) {
		root.add(new DefaultMutableTreeNode("test 1"));
		root.add(new DefaultMutableTreeNode("test 2"));

	}

	private void setUpUI() {

		setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(255, 200, 0),
				new Color(255, 200, 0), Color.ORANGE, Color.ORANGE));
		setLayout(null);
		setPreferredSize(new Dimension(433, 545));

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

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(
				"Ontology Root");
		createsOntologyTree(root);
		ontologyTree = new JTree(root);

		JScrollPane scrollPane = new JScrollPane(ontologyTree);
		scrollPane.setBounds(17, 149, 397, 377);
		add(scrollPane);

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

					MyApplicationManager appManager = MyApplicationCenter
							.getInstance().getApplicationManager();

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

					DialogTaskManager taskManager = appManager.getTaskManager();
					taskManager.execute(updateAggregationTaskFactory
							.createTaskIterator(networkView));

				}
			}
		});

		refreshAggregationChoicesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MyApplicationManager appManager = MyApplicationCenter
						.getInstance().getApplicationManager();
				UpdateOntologyControlPanelTask.UpdateOntologyControlOptions options = new UpdateOntologyControlPanelTask.UpdateOntologyControlOptions(false, true, false, null);
				CyNetwork currentNetwork = appManager
						.getCyApplicationManager().getCurrentNetwork();
				UpdateOntologyControlPanelTaskFactory updateOntologyControlPanelTaskFactory = new UpdateOntologyControlPanelTaskFactory(OntologyControlPanel.this, options);
				
				DialogTaskManager taskManager = appManager.getTaskManager();
				taskManager.execute(updateOntologyControlPanelTaskFactory
						.createTaskIterator(currentNetwork));
			}
		});

		interactionTypeChoice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {

					MyApplicationManager appManager = MyApplicationCenter
							.getInstance().getApplicationManager();

					CyNetwork underlyingNetwork = appManager
							.getCyApplicationManager().getCurrentNetwork();

					String selectedItem = (String) interactionTypeChoice
							.getSelectedItem();
					PopulateOntologyNetworkTaskFactory populateOntologyNetworkTaskFactory = new PopulateOntologyNetworkTaskFactory(
							selectedItem);

					DialogTaskManager taskManager = appManager.getTaskManager();
					taskManager.execute(populateOntologyNetworkTaskFactory
							.createTaskIterator(underlyingNetwork));

				}

			}
		});

		hideDanglingNodesCheckBox.setSelected(true);

		hideDanglingNodesCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				MyApplicationManager appManager = MyApplicationCenter
						.getInstance().getApplicationManager();

				CyNetwork underlyingNetwork = appManager
						.getCyApplicationManager().getCurrentNetwork();

				Collection<CyNetworkView> networkViews = appManager
						.getCyNetworkViewManager().getNetworkViews(
								underlyingNetwork);
				if (networkViews.isEmpty())
					return;

				CyNetworkView networkView = networkViews.iterator().next();

				DialogTaskManager taskManager = appManager.getTaskManager();
				HideOrShowDanglingNodesTaskFactory hideOrShowDanglingNodesTaskFactory = new HideOrShowDanglingNodesTaskFactory(
						!hideDanglingNodesCheckBox.isSelected());

				taskManager.execute(hideOrShowDanglingNodesTaskFactory
						.createTaskIterator(networkView));
			}
		});

	}
	
	public void setAggregationColumnChoice(Collection<String> columns) {
		aggregationColumnChoice.removeAll();
		for (String item : columns) {
			aggregationColumnChoice.addItem(item);
		}
	}
	
	public void setInteractionTypeChoice(Collection<String> interactions, String selectedItem) {
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
}
