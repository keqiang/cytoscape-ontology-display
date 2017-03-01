package edu.umich.med.mbni.lkq.cyontology.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.app.CytoscapeServiceManager;
import edu.umich.med.mbni.lkq.cyontology.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.util.ViewOperationUtils;

public class HideOrShowDanglingNodesTask extends AbstractNetworkViewTask {

	private boolean isHiding;
	private CytoscapeServiceManager cytoscapeServiceManager;

	public HideOrShowDanglingNodesTask(CyNetworkView view, boolean isHiding) {
		super(view);
		this.isHiding = isHiding;
		cytoscapeServiceManager = MyApplicationManager.getInstance()
				.getCytoscapeServiceManager();
	}

	@Override
	public void run(TaskMonitor taskMonitor) {
		CyNetwork underlyingNetwork = view.getModel();

		if (!MyApplicationManager.getInstance()
				.hasOntologyNetworkFromUnderlyingCyNetwork(underlyingNetwork))
			return;

		OntologyNetwork ontologyNetwork = MyApplicationManager.getInstance()
				.getOntologyNetworkFromUnderlyingCyNetwork(underlyingNetwork);

		for (ExpandableNode node : ontologyNetwork.getAllRootNodes()) {
			if (node.getDirectChildNodes().isEmpty()) {
				view.getNodeView(node.getCyNode()).setVisualProperty(
						BasicVisualLexicon.NODE_VISIBLE, !isHiding);
			}
		}

		ViewOperationUtils.reLayoutNetwork(
				cytoscapeServiceManager.getCyLayoutAlgorithmManager(), view,
				MyApplicationManager.getInstance().getLayoutAlgorithmName(),
				CyLayoutAlgorithm.ALL_NODE_VIEWS);
		view.updateView();
	}

}
