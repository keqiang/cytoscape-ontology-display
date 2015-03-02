package edu.umich.med.mbni.lkq.cyontology.internal.view;

import java.awt.Choice;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;

import edu.umich.med.mbni.lkq.cyontology.internal.event.OntologyAggregationColumnChangeEvent;
import edu.umich.med.mbni.lkq.cyontology.internal.event.OntologyAggregationMethodChangeEvent;
import edu.umich.med.mbni.lkq.cyontology.internal.event.OntologyHideDanglingNodesEvent;
import edu.umich.med.mbni.lkq.cyontology.internal.event.OntologyInteractionChangeEvent;
import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyAggregationChoiceRefreshListener;
import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyAggregationColumnChangeListener;
import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyAggregationMethodChangeListener;
import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyHideDanglingNodesListener;
import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyInteractionChangeListener;
import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyTreeSelectionChangeListener;
import edu.umich.med.mbni.lkq.cyontology.internal.util.AggregationMethodUtil;
import edu.umich.med.mbni.lkq.cyontology.internal.util.ResourceUtil;

public class OntologyPluginPanel extends JPanel implements CytoPanelComponent2 {

	private static final long serialVersionUID = -5561297105387148003L;

	public static final String ONTOLOGY_PANEL_TITLE = "Ontology Panel";

	private Choice aggregationColumnChoice;
	private Choice interactionTypeChoice;
	private Choice aggregationMethodChoice;
	private JCheckBox hideDanglingNodesCheckBox;

	private JButton refreshAggregationChoicesButton;

	private OntologyInteractionChangeListener ontologyInteractionChangeListener;
	private OntologyAggregationColumnChangeListener ontologyAggregationColumnChangeListener;
	private OntologyAggregationMethodChangeListener ontologyAggregationMethodChangeListener;
	private OntologyAggregationChoiceRefreshListener ontologyAggregationChoiceRefreshListener;
	private OntologyHideDanglingNodesListener ontologyHideDanglingNodesListener;
	private OntologyTreeSelectionChangeListener ontologyTreeSelectionChangeListener;

	private OntologyTree ontologyTree;

	/**
	 * Create the panel.
	 */
	public OntologyPluginPanel() {
		setUpUI();
	}

	public OntologyTree getOntologyTree() {
		return ontologyTree;
	}

	/*
	 * methods for setting up all the event listeners for ontology panel
	 */

	public void setOntologyTreeSelectionChangeListener(
			OntologyTreeSelectionChangeListener ontologyTreeSelectionChangeListener) {
		this.ontologyTreeSelectionChangeListener = ontologyTreeSelectionChangeListener;
	}

	public void fireOntologyTreeSelectionChangeEvent(TreeSelectionEvent event) {
		if (ontologyTreeSelectionChangeListener != null) {
			ontologyTreeSelectionChangeListener
					.ontologyTreeSelectionChangePerformed(event);
		}
	}

	public void setOntologyHideDanglingNodesListener(
			OntologyHideDanglingNodesListener ontologyHideDanglingNodesListener) {
		this.ontologyHideDanglingNodesListener = ontologyHideDanglingNodesListener;
	}

	private void fireOntologyHideDanglingNodesEvent(
			OntologyHideDanglingNodesEvent event) {
		if (ontologyHideDanglingNodesListener != null) {
			ontologyHideDanglingNodesListener
					.ontologyHideDanglingNodesPerformed(event);
		}
	}

	public void setOntologyRefreshAggregationChoiceChangeListener(
			OntologyAggregationChoiceRefreshListener ontologyAggregationChoiceRefreshListener) {
		this.ontologyAggregationChoiceRefreshListener = ontologyAggregationChoiceRefreshListener;
	}

	private void fireOntologyRefreshAggregationChoiceEvent(EventObject event) {
		if (ontologyAggregationChoiceRefreshListener != null) {
			ontologyAggregationChoiceRefreshListener
					.ontologyAggregationChoiceRefreshPerformed(event);
		}
	}

	public void setOntologyInteractionChangeListener(
			OntologyInteractionChangeListener ontologyInteractionChangeListener) {
		this.ontologyInteractionChangeListener = ontologyInteractionChangeListener;
	}

	private void fireOntologyInteractionChangeEvent(
			OntologyInteractionChangeEvent event) {
		if (ontologyInteractionChangeListener != null) {
			ontologyInteractionChangeListener.interactionChangePerformed(event);
		}
	}

	public void setOntologyAggregationColumnChangeListener(
			OntologyAggregationColumnChangeListener ontologyAggregationColumnChangeListener) {
		this.ontologyAggregationColumnChangeListener = ontologyAggregationColumnChangeListener;
	}

	private void fireOntologyAggregationColumnChangeEvent(
			OntologyAggregationColumnChangeEvent event) {
		if (ontologyAggregationColumnChangeListener != null) {
			ontologyAggregationColumnChangeListener
					.ontologyAggregationColumnChangePerformed(event);
		}
	}

	public void setOntologyAggregationMethodChangeListener(
			OntologyAggregationMethodChangeListener ontologyAggregationMethodChangeListener) {
		this.ontologyAggregationMethodChangeListener = ontologyAggregationMethodChangeListener;
	}

	private void fireOntologyAggregationMethodChangeEvent(
			OntologyAggregationMethodChangeEvent event) {
		if (ontologyAggregationMethodChangeListener != null) {
			ontologyAggregationMethodChangeListener
					.ontologyAggreagationMethodChangePerformed(event);
		}
	}

	private void setUpUI() {

		setLayout(null);
		setPreferredSize(new Dimension(500, 600));

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
		
		aggregationColumnChoice.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {

					String aggregationMethod = (String) aggregationMethodChoice
							.getSelectedItem();

					String aggregationColumn = (String) aggregationColumnChoice
							.getSelectedItem();

					fireOntologyAggregationColumnChangeEvent(new OntologyAggregationColumnChangeEvent(
							aggregationColumnChoice, aggregationMethod,
							aggregationColumn));
				}
			}
		});

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
				"Select to Hide Dangling Nodes");
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

					String aggregationColumn = (String) aggregationColumnChoice
							.getSelectedItem();

					fireOntologyAggregationMethodChangeEvent(new OntologyAggregationMethodChangeEvent(
							aggregationMethodChoice, aggregationMethod,
							aggregationColumn));
				}
			}
		});

		refreshAggregationChoicesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireOntologyRefreshAggregationChoiceEvent(e);
			}
		});

		interactionTypeChoice.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {
					String selectedItem = (String) interactionTypeChoice
							.getSelectedItem();

					OntologyInteractionChangeEvent event = new OntologyInteractionChangeEvent(
							interactionTypeChoice, selectedItem);
					fireOntologyInteractionChangeEvent(event);
				}

			}
		});

		hideDanglingNodesCheckBox.setSelected(true);

		hideDanglingNodesCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				fireOntologyHideDanglingNodesEvent(new OntologyHideDanglingNodesEvent(
						hideDanglingNodesCheckBox, hideDanglingNodesCheckBox
								.isSelected()));
			}
		});

		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(
				"Ontology Tree");
		ontologyTree = new OntologyTree(root, null);

		// TODO Doesn't work for now
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

		renderer.setLeafIcon(ResourceUtil.leafIcon);
		renderer.setOpenIcon(ResourceUtil.openIcon);
		renderer.setClosedIcon(ResourceUtil.closedIcon);

		ontologyTree.setCellRenderer(renderer);

		JScrollPane scrollPane = new JScrollPane(ontologyTree);
		scrollPane.setBounds(17, 149, 397, 700);
		add(scrollPane);

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
		return ONTOLOGY_PANEL_TITLE;
	}

	@Override
	public String getIdentifier() {
		return ONTOLOGY_PANEL_TITLE;
	}

	public void cleanUpView() {
		aggregationColumnChoice.removeAll();
		interactionTypeChoice.removeAll();

		DefaultTreeModel model = (DefaultTreeModel) ontologyTree.getModel();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(
				"Ontology Tree");
		model.setRoot(root);

		ontologyTree.setOntologyNetwork(null);
	}

}
