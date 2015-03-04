package edu.umich.med.mbni.lkq.cyontology.internal.action;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.swing.DialogTaskManager;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.app.CytoscapeServiceManager;
import edu.umich.med.mbni.lkq.cyontology.internal.task.PopulateOntologyNetworkTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.util.OntologyNetworkUtils;

public class GenerateOntologyNetworkAction extends AbstractCyAction {

	private static final long serialVersionUID = 7170161875226705765L;
	private CytoscapeServiceManager cytoscapeServiceManager;

	public GenerateOntologyNetworkAction(String name) {
		super(name);
		cytoscapeServiceManager = MyApplicationManager.getInstance().getCytoscapeServiceManager();
		setPreferredMenu("Apps.Ontology Viewer");
	}

	public void actionPerformed(ActionEvent e) {
		
		DialogTaskManager taskManager = cytoscapeServiceManager.getTaskManager();
		
		CyNetwork underlyingNetwork = cytoscapeServiceManager.getCyApplicationManager()
				.getCurrentNetwork();
		PopulateOntologyNetworkTaskFactory populateOntologyNetworkTaskFactory = new PopulateOntologyNetworkTaskFactory(OntologyNetworkUtils.INTERACTION_IS_A);
		
		taskManager.execute(populateOntologyNetworkTaskFactory.createTaskIterator(underlyingNetwork));
	}
}
