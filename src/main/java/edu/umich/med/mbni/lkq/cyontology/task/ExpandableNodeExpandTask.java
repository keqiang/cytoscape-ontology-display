package edu.umich.med.mbni.lkq.cyontology.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.app.CytoscapeServiceManager;
import edu.umich.med.mbni.lkq.cyontology.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.util.ViewOperationUtils;

public class ExpandableNodeExpandTask extends AbstractNodeViewTask {

	public ExpandableNodeExpandTask(View<CyNode> nodeView,
			CyNetworkView netView) {
		super(nodeView, netView);
	}
	@Override
	public void run(TaskMonitor taskMonitor) {
		if (!MyApplicationManager.getInstance().hasOntologyNetworkFromUnderlyingCyNetwork(netView.getModel())) return;
		
		taskMonitor.setProgress(0.0);
		
		CyNetwork underlyingNetwork = netView.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationManager.getInstance()
				.getOntologyNetworkFromUnderlyingCyNetwork(underlyingNetwork);
		ExpandableNode expandableNode = ontologyNetwork
				.getNodeFromUnderlyingNode(nodeView.getModel());

		expandableNode.expand();
		taskMonitor.setProgress(0.3);

		ViewOperationUtils.showSubTree(expandableNode, netView);
		taskMonitor.setProgress(0.5);
		
		CytoscapeServiceManager cytoscapeServiceManager = MyApplicationManager.getInstance().getCytoscapeServiceManager();
		ViewOperationUtils.reLayoutNetwork(
				cytoscapeServiceManager.getCyLayoutAlgorithmManager(), netView,
				MyApplicationManager.getInstance().getLayoutAlgorithmName(), CyLayoutAlgorithm.ALL_NODE_VIEWS);
		
		netView.updateView();
		taskMonitor.setProgress(1.0);
	}

}

