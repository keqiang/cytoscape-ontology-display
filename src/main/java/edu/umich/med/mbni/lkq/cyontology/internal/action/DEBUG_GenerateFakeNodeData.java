package edu.umich.med.mbni.lkq.cyontology.internal.action;

import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Random;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.swing.DialogTaskManager;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.task.DEBUG_MapDataToColorTaskFactory;

public class DEBUG_GenerateFakeNodeData extends AbstractCyAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2750295913071192044L;

	public DEBUG_GenerateFakeNodeData(String name) {
		super(name);
		setPreferredMenu("Apps.Ontology Viewer");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		MyApplicationManager appManager = MyApplicationManager.getInstance();
		
		CyNetwork underlyingNetwork = appManager.getCytoscapeServiceManager().getCyApplicationManager().getCurrentNetwork();
		
		OntologyNetwork ontologyNetwork = appManager.getOntologyNetworkFromUnderlyingCyNetwork(underlyingNetwork);
		
		if (underlyingNetwork.getDefaultNodeTable().getColumn("test_data") == null) {
			underlyingNetwork.getDefaultNodeTable().createColumn("test_data", Double.class, false);
		}
		
		Random random = new Random(Calendar.getInstance().getTimeInMillis());
		for (CyNode node : underlyingNetwork.getNodeList()) {
			
			ExpandableNode expandableNode = ontologyNetwork.getNodeFromUnderlyingNode(node);
			
			// set value only for leaf nodes
			if (expandableNode.getDirectChildNodes().isEmpty())
				underlyingNetwork.getRow(node).set("test_data", random.nextDouble());
			else {
				underlyingNetwork.getRow(node).set("test_data", null);
			}
		}
		
		DEBUG_MapDataToColorTaskFactory taskFactory = new DEBUG_MapDataToColorTaskFactory();
		
		DialogTaskManager taskManager = appManager.getCytoscapeServiceManager().getTaskManager();
		
		CyNetworkView networkView = appManager.getCytoscapeServiceManager().getCyApplicationManager().getCurrentNetworkView();
		taskManager.execute(taskFactory.createTaskIterator(networkView));
		
	}

}
