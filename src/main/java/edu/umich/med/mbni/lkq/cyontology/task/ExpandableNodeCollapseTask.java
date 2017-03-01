package edu.umich.med.mbni.lkq.cyontology.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.util.ViewOperationUtils;

public class ExpandableNodeCollapseTask extends AbstractNodeViewTask {
	
	public ExpandableNodeCollapseTask(View<CyNode> nodeView,
			CyNetworkView netView) {
		super(nodeView, netView);
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		if (!MyApplicationManager.getInstance().hasOntologyNetworkFromUnderlyingCyNetwork(
				netView.getModel()))
			return;
		
		taskMonitor.setTitle("Collapsing node");
		
		taskMonitor.setProgress(0.0);
		
		CyNetwork underlyingNetwork = netView.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationManager.getInstance()
				.getOntologyNetworkFromUnderlyingCyNetwork(underlyingNetwork);
		ExpandableNode expandableNode = ontologyNetwork.getNodeFromUnderlyingNode(nodeView.getModel());
		
		// the node is already in collapsed state
		if (expandableNode.isCollapsed()) {
			taskMonitor.setProgress(1.0);
			return;
		}

		expandableNode.collapse();
		taskMonitor.setProgress(0.3);

		ViewOperationUtils.hideSubTree(expandableNode, netView);
		taskMonitor.setProgress(0.8);
		
		netView.updateView();
		taskMonitor.setProgress(1.0);
		
		ontologyNetwork.fireNodeExpansionEvent(expandableNode);
	}

}
