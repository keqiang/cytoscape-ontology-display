package edu.umich.med.mbni.lkq.cyontology.internal.view;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.swing.DialogTaskManager;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.task.HideOrShowDanglingNodesTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.PopulateOntologyNetworkTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.NumberConvertUtil;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.OntologyNetworkUtils;

public class OntologyViewerControlPanel extends JPanel implements
		CytoPanelComponent2, NetworkAddedListener {

	private static final long serialVersionUID = -5561297105387148003L;

	public static final String CONTROL_PANEL_TITLE = "Ontology Control Panel";

	Choice aggregateColumnChoice;
	Choice interactionTypeChoice;
	Choice aggregationType;
	Checkbox hideDanglingNodes;

	Button refreshAggregationChoicesButton;
	Button triggerAggregationButton;

	public OntologyViewerControlPanel() {
		
		//this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		this.setLayout(layout);
		
		JPanel methodPanel = new JPanel();
		
		Label label = new Label("aggretation methods");
		methodPanel.add(label);
		
		aggregationType = new Choice();
		aggregationType.addItem("mean");
		aggregationType.addItem("median");
		aggregationType.addItem("max");
		aggregationType.addItem("min");
		aggregationType.addItem("sum");
		methodPanel.add(aggregationType);
		
		this.add(methodPanel);

		JPanel collumnPanel = new JPanel();
		
		label = new Label("collumn used to aggregate");
		collumnPanel.add(label);

		aggregateColumnChoice = new Choice();
		collumnPanel.add(aggregateColumnChoice);

		refreshAggregationChoicesButton = new Button("Refresh");
		collumnPanel.add(refreshAggregationChoicesButton);

		refreshAggregationChoicesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				rePopTheAggregationValues();
			}
		});

		rePopTheAggregationValues();

		triggerAggregationButton = new Button("update view");
		collumnPanel.add(triggerAggregationButton);
		
		this.add(collumnPanel);

		triggerAggregationButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String selectedColumn = aggregateColumnChoice.getSelectedItem();

				MyApplicationManager appManager = MyApplicationCenter
						.getInstance().getApplicationManager();

				CyNetwork underlyingNetwork = appManager
						.getCyApplicationManager().getCurrentNetwork();

				double min = Double.MAX_VALUE;
				double max = Double.MIN_VALUE;

				for (CyNode node : underlyingNetwork.getNodeList()) {
					if (underlyingNetwork.getRow(node).isSet(selectedColumn)) {

						Double value = underlyingNetwork.getRow(node).get(
								selectedColumn, Double.class);
						if (value == null)
							continue;
						if (value < min) {
							min = value;
						}

						if (value > max) {
							max = value;
						}
					}
				}

				NumberConvertUtil convertUtil = new NumberConvertUtil(min, max);

				CyNetworkView networkView = appManager
						.getCyApplicationManager().getCurrentNetworkView();

				for (CyNode node : underlyingNetwork.getNodeList()) {

					if (underlyingNetwork.getRow(node).isSet(selectedColumn)) {

						Double value = underlyingNetwork.getRow(node).get(
								selectedColumn, Double.class);
						Color setColor = convertUtil.convertToColor(value);

						networkView.getNodeView(node).setLockedValue(
								BasicVisualLexicon.NODE_FILL_COLOR, setColor);
					}
				}

				networkView.updateView();

			}
		});

		JPanel interactionPanel = new JPanel();
		
		label = new Label("interaction type");
		interactionPanel.add(label);

		interactionTypeChoice = new Choice();
		interactionPanel.add(interactionTypeChoice);
		
		this.add(interactionPanel);

		interactionTypeChoice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {

					MyApplicationManager appManager = MyApplicationCenter
							.getInstance().getApplicationManager();

					CyNetwork underlyingNetwork = appManager
							.getCyApplicationManager().getCurrentNetwork();

					String selectedItem = interactionTypeChoice
							.getSelectedItem();
					PopulateOntologyNetworkTaskFactory populateOntologyNetworkTaskFactory = new PopulateOntologyNetworkTaskFactory(
							selectedItem);

					DialogTaskManager taskManager = appManager.getTaskManager();
					taskManager.execute(populateOntologyNetworkTaskFactory
							.createTaskIterator(underlyingNetwork));

				}

			}
		});

		rePopTheInteractionType();

		hideDanglingNodes = new Checkbox();
		hideDanglingNodes.setLabel("select to hide dangling nodes");
		hideDanglingNodes.setState(true);
		this.add(hideDanglingNodes);

		hideDanglingNodes.addItemListener(new ItemListener() {

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
						!hideDanglingNodes.getState());

				taskManager.execute(hideOrShowDanglingNodesTaskFactory
						.createTaskIterator(networkView));
			}
		});
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
		// TODO Auto-generated method stub
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
	public void handleEvent(NetworkAddedEvent e) {
		rePopTheAggregationValues();
		rePopTheInteractionType();
	}

	public void rePopTheInteractionType() {
		interactionTypeChoice.removeAll();

		HashSet<String> allTypes = new HashSet<String>();
		allTypes.add(OntologyNetworkUtils.INTERACTION_IS_A);

		CyNetwork curNetwork = MyApplicationCenter.getInstance()
				.getApplicationManager().getCyApplicationManager()
				.getCurrentNetwork();

		Collection<CyRow> allRows = curNetwork.getDefaultEdgeTable()
				.getAllRows();

		for (CyRow row : allRows) {
			String interactionType = row.get(CyEdge.INTERACTION, String.class);
			allTypes.add(interactionType);
		}

		for (String interactionType : allTypes) {
			interactionTypeChoice.add(interactionType);
		}
		interactionTypeChoice.select(OntologyNetworkUtils.INTERACTION_IS_A);

	}

	public void rePopTheAggregationValues() {

		aggregateColumnChoice.removeAll();

		CyNetwork curNetwork = MyApplicationCenter.getInstance()
				.getApplicationManager().getCyApplicationManager()
				.getCurrentNetwork();

		if (!MyApplicationCenter.getInstance().hasEncapsulatingOntologyNetwork(
				curNetwork))
			return;

		Collection<CyColumn> allColumns = curNetwork.getDefaultNodeTable()
				.getColumns();

		for (CyColumn column : allColumns) {

			if (column.getType() == Double.class
					|| column.getType() == Long.class) {
				if (column.getName() == "SUID")
					continue;
				aggregateColumnChoice.add(column.getName());
			}
		}
	}

}
