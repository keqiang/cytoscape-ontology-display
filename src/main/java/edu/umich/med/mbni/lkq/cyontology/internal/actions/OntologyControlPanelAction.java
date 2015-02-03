package edu.umich.med.mbni.lkq.cyontology.internal.actions;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyViewerControlPanel;

public class OntologyControlPanelAction extends AbstractCyAction {

	private static final long serialVersionUID = 1L;
	private CySwingApplication desktopApp;
	private final CytoPanel cytoPanelWest;
	private OntologyViewerControlPanel ontologyViewerControlPanel;

	public OntologyControlPanelAction(CySwingApplication swingApplication,
			OntologyViewerControlPanel ontologyViewerControlPanel) {
		super("Ontology Control Panel");

		setPreferredMenu("Apps.Ontology Viewer");

		this.desktopApp = swingApplication;
		this.cytoPanelWest = this.desktopApp.getCytoPanel(CytoPanelName.WEST);
		this.ontologyViewerControlPanel = ontologyViewerControlPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (cytoPanelWest.getState() == CytoPanelState.HIDE) {
			cytoPanelWest.setState(CytoPanelState.DOCK);
		}

		int index = cytoPanelWest.indexOfComponent(ontologyViewerControlPanel);

		if (index == -1)
			return;

		cytoPanelWest.setSelectedIndex(index);
		
		ontologyViewerControlPanel.rePopTheAggregationValues();

	}

}
