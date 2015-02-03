package edu.umich.med.mbni.lkq.cyontology.internal.view;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;

public class OntologyViewerControlPanel extends JPanel implements
		CytoPanelComponent2, NetworkAddedListener {
	
	private static final long serialVersionUID = -5561297105387148003L;
	
	public static final String CONTROL_PANEL_TITLE = "Ontology Control Panel";

	Choice aggregateColumnChoice;
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

		//this.updateUI();
	}

}
