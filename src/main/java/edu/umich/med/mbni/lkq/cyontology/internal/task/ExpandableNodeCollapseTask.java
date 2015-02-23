package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.ViewOperationUtils;

public class ExpandableNodeCollapseTask extends AbstractNodeViewTask {
	
	public ExpandableNodeCollapseTask(View<CyNode> nodeView,
			CyNetworkView netView) {
		super(nodeView, netView);
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		if (!MyApplicationCenter.getInstance().hasEncapsulatingOntologyNetwork(
				netView.getModel()))
			return;
		
		taskMonitor.setProgress(0.0);
		
		CyNetwork underlyingNetwork = netView.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationCenter.getInstance()
				.getEncapsulatingOntologyNetwork(underlyingNetwork);
		ExpandableNode expandableNode = ontologyNetwork.getNode(nodeView.getModel());

		expandableNode.collapse();
		taskMonitor.setProgress(0.3);

		ViewOperationUtils.hideSubTree(expandableNode, netView);
		taskMonitor.setProgress(0.8);
		
		netView.updateView();
		taskMonitor.setProgress(1.0);
		
		ontologyNetwork.fireNodeExpansionEvent(expandableNode);
	}

}
