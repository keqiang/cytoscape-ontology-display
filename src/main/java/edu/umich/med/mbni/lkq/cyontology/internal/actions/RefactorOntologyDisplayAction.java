package edu.umich.med.mbni.lkq.cyontology.internal.actions;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.swing.DialogTaskManager;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.task.PopulateOntologyNetworkTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.OntologyNetworkUtils;

public class RefactorOntologyDisplayAction extends AbstractCyAction {

	private static final long serialVersionUID = 7170161875226705765L;
	private MyApplicationManager appManager;

	public RefactorOntologyDisplayAction() {
		super("Create collapsable and expandable ontology network");
		appManager = MyApplicationCenter.getInstance().getApplicationManager();
		setPreferredMenu("Apps.Ontology Viewer");
	}

	public void actionPerformed(ActionEvent e) {
		
		DialogTaskManager taskManager = appManager.getTaskManager();
		
		CyNetwork underlyingNetwork = appManager.getCyApplicationManager()
				.getCurrentNetwork();
		PopulateOntologyNetworkTaskFactory populateOntologyNetworkTaskFactory = new PopulateOntologyNetworkTaskFactory(OntologyNetworkUtils.INTERACTION_IS_A);
		
		taskManager.execute(populateOntologyNetworkTaskFactory.createTaskIterator(underlyingNetwork));
	}
}
