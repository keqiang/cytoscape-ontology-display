package edu.umich.med.mbni.lkq.cyontology.internal.view;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

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
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.DelayedVizProp;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.OntologyNetworkUtils;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.ViewOperationUtils;

public class OntologyViewerControlPanel extends JPanel implements
		CytoPanelComponent2, NetworkAddedListener {
	
	private static final long serialVersionUID = -5561297105387148003L;
	
	public static final String CONTROL_PANEL_TITLE = "Ontology Control Panel";

	Choice aggregateColumnChoice;
	Choice interactionTypeChoice;
	
	Button refreshAggregationChoicesButton;

	public OntologyViewerControlPanel() {
		
		Label label = new Label("value used to aggregate");
		label.setSize(50, 20);
		this.add(label);

		aggregateColumnChoice = new Choice();
		aggregateColumnChoice.setSize(50, 20);
		this.add(aggregateColumnChoice);
		
		refreshAggregationChoicesButton = new Button("Refresh");
		this.add(refreshAggregationChoicesButton);
		
		refreshAggregationChoicesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rePopTheAggregationValues();
			}
		});

		rePopTheAggregationValues();
		
		label = new Label("interaction type");
		label.setSize(50, 20);
		this.add(label);
		
		interactionTypeChoice = new Choice();
		interactionTypeChoice.setSize(50, 50);
		this.add(interactionTypeChoice);
		
		interactionTypeChoice.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if (e.getStateChange() == ItemEvent.SELECTED) {
					
					String selectedItem = interactionTypeChoice.getSelectedItem();
					CyNetwork underlyingNetwork = MyApplicationCenter.getInstance().getApplicationManager().getCyApplicationManager().getCurrentNetwork();

					MyApplicationManager appManager = MyApplicationCenter.getInstance().getApplicationManager();
					Collection<CyNetworkView> networkViews = appManager.getCyNetworkViewManager().getNetworkViews(underlyingNetwork);
					CyNetworkView networkView;
					
					if (networkViews.isEmpty()) {
						networkView = appManager.getCyNetworkViewFactory().createNetworkView(underlyingNetwork);
						appManager.getCyNetworkViewManager().addNetworkView(networkView);
					} else {
						networkView = networkViews.iterator().next();
					}
					
					LinkedList<DelayedVizProp> vizProps = new LinkedList<>();
					
					for (CyEdge edge : underlyingNetwork.getEdgeList()) {
						DelayedVizProp vizProp = new DelayedVizProp(edge,
								BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE,
								ArrowShapeVisualProperty.NONE, true);
						vizProps.add(vizProp);
						vizProp = new DelayedVizProp(edge,
								BasicVisualLexicon.EDGE_WIDTH,
								1.0, true);
						vizProps.add(vizProp);
						vizProp = new DelayedVizProp(edge,
								BasicVisualLexicon.EDGE_LINE_TYPE,
								LineTypeVisualProperty.LONG_DASH, true);
						vizProps.add(vizProp);
						
						vizProp = new DelayedVizProp(edge,
								BasicVisualLexicon.EDGE_TRANSPARENCY,
								120, true);
						vizProps.add(vizProp);
						
					}
					
					appManager.getCyEventHelper().flushPayloadEvents();
					DelayedVizProp.applyAll(networkView, vizProps);
							
					vizProps.clear();
					
					MyApplicationCenter.getInstance().addOntologyNetwork(OntologyNetworkUtils.convertNetworkToOntology(underlyingNetwork, vizProps, selectedItem));
					
					appManager.getCyEventHelper().flushPayloadEvents();
					DelayedVizProp.applyAll(networkView, vizProps);
					
					ViewOperationUtils.reLayoutNetwork(
							appManager.getCyLayoutAlgorithmManager(), networkView,
							"force-directed");				
				}
				
			}
		});
		
		rePopTheInteractionType();
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
		
		Collection<CyRow> allRows = curNetwork.getDefaultEdgeTable().getAllRows();
		
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
		
		if (!MyApplicationCenter.getInstance().hasEncapsulatingOntologyNetwork(curNetwork)) return;

		Collection<CyColumn> allColumns = curNetwork.getDefaultNodeTable()
				.getColumns();

		for (CyColumn column : allColumns) {

			if (column.getType() == Double.class || column.getType() == Long.class) {
				if (column.getName() == "SUID") continue;
				aggregateColumnChoice.add(column.getName());
			}
		}
	}

}
