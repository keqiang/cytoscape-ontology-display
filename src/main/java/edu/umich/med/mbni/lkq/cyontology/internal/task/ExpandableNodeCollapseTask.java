package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.ViewOperationUtils;

public class ExpandableNodeCollapseTask implements Task {

	private CyNetworkView networkView;
	private CyNode nodeToCollapse;
	
	public ExpandableNodeCollapseTask(CyNode node, CyNetworkView networkView) {
		this.networkView = networkView;
		this.nodeToCollapse = node;
	}
	
	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		
		CyNetwork underlyingNetwork = networkView.getModel();
		
		OntologyNetwork ontologyNetwork = MyApplicationCenter.getCorrespondingOntologyNetwork(underlyingNetwork);
		ExpandableNode expandableNode = ontologyNetwork.getCorrespondingNode(nodeToCollapse);
		
		expandableNode.collapse();
		
		ViewOperationUtils.hideSubTree(expandableNode, networkView);
		
	}

}
