package edu.umich.med.mbni.lkq.cyontology.internal.view;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
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
import org.cytoscape.model.CyRow;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.swing.DialogTaskManager;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.task.HideOrShowDanglingNodesTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.PopulateOntologyNetworkTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateAggregationTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.AggregationMethodUtil;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.OntologyNetworkUtils;

public class OntologyViewerControlPanel extends JPanel implements
		CytoPanelComponent2, NetworkAddedListener {

	private static final long serialVersionUID = -5561297105387148003L;

	public static final String CONTROL_PANEL_TITLE = "Ontology Control Panel";

	Choice aggregationColumnChoice;
	Choice interactionTypeChoice;
	Choice aggregationMethodChoice;
	Checkbox hideDanglingNodesCheckBox;

	Button refreshAggregationChoicesButton;
	Button triggerAggregationButton;

	public OntologyViewerControlPanel() {

		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		this.setLayout(layout);

		JPanel AggregationMethodPanel = new JPanel();

		Label label = new Label("Choose Aggretation Methods");
		AggregationMethodPanel.add(label);

		aggregationMethodChoice = new Choice();
		aggregationMethodChoice
				.addItem(AggregationMethodUtil.AGGREGATION_METHOD_MEAN);
		aggregationMethodChoice
				.addItem(AggregationMethodUtil.AGGREGATION_METHOD_MEDIAN);
		aggregationMethodChoice
				.addItem(AggregationMethodUtil.AGGREGATION_METHOD_MAX);
		aggregationMethodChoice
				.addItem(AggregationMethodUtil.AGGREGATION_METHOD_MIN);
//		aggregationMethodChoice
//				.addItem(AggregationMethodUtil.AGGREGATION_METHOD_SUM);

		aggregationMethodChoice
				.select(AggregationMethodUtil.AGGREGATION_METHOD_MEAN);

		AggregationMethodPanel.add(aggregationMethodChoice);

		this.add(AggregationMethodPanel);

		aggregationMethodChoice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {

					MyApplicationManager appManager = MyApplicationCenter
							.getInstance().getApplicationManager();

					String aggregationMethod = aggregationMethodChoice
							.getSelectedItem();

					CyNetworkView networkView = appManager
							.getCyApplicationManager().getCurrentNetworkView();

					String aggregationColumn = aggregationColumnChoice
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

		JPanel collumnPanel = new JPanel();

		label = new Label("Choose Column to Aggregate");
		collumnPanel.add(label);

		aggregationColumnChoice = new Choice();
		collumnPanel.add(aggregationColumnChoice);

		refreshAggregationChoicesButton = new Button(
				"Populate All Aggregatable Columns");
		collumnPanel.add(refreshAggregationChoicesButton);

		refreshAggregationChoicesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				rePopTheAggregationValues();
			}
		});

		rePopTheAggregationValues();
		this.add(collumnPanel);

		JPanel interactionPanel = new JPanel();

		label = new Label("Choose Interaction Type");
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

		hideDanglingNodesCheckBox = new Checkbox();
		hideDanglingNodesCheckBox.setLabel("Select to Hide Dangling Nodes");
		hideDanglingNodesCheckBox.setState(true);
		this.add(hideDanglingNodesCheckBox);

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
						!hideDanglingNodesCheckBox.getState());

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

	// TODO : should rewrite to task
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

	// TODO: make it a task
	public void rePopTheAggregationValues() {

		aggregationColumnChoice.removeAll();

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
				aggregationColumnChoice.add(column.getName());
			}
		}
	}

}
