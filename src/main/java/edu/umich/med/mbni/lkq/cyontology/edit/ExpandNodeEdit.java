package edu.umich.med.mbni.lkq.cyontology.edit;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.undo.AbstractCyEdit;

import edu.umich.med.mbni.lkq.cyontology.app.CytoscapeServiceManager;
import edu.umich.med.mbni.lkq.cyontology.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.util.ViewOperationUtils;

public class ExpandNodeEdit extends AbstractCyEdit {

	private CyNetworkView networkView;
	private View<CyNode> nodeView;
	private CytoscapeServiceManager cytoscapeServiceManager;

	public ExpandNodeEdit(String presentationName, CyNetworkView networkView,
			View<CyNode> nodeView) {
		super(presentationName);
		this.networkView = networkView;
		this.nodeView = nodeView;
		cytoscapeServiceManager = MyApplicationManager.getInstance().getCytoscapeServiceManager();
	}


	@Override
	public void redo() {

		CyNetwork underlyingNetwork = networkView.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationManager.getInstance()
				.getOntologyNetworkFromUnderlyingCyNetwork(underlyingNetwork);
		ExpandableNode expandableNode = ontologyNetwork
				.getNodeFromUnderlyingNode(nodeView.getModel());

		expandableNode.expand();

		ViewOperationUtils.showSubTree(expandableNode, networkView);
		ViewOperationUtils.reLayoutNetwork(
				cytoscapeServiceManager.getCyLayoutAlgorithmManager(), networkView,
				"hierarchical", CyLayoutAlgorithm.ALL_NODE_VIEWS);
		networkView.updateView();
		cytoscapeServiceManager.getCyEventHelper().flushPayloadEvents();
	}

	@Override
	public void undo() {
		CyNetwork underlyingNetwork = networkView.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationManager.getInstance()
				.getOntologyNetworkFromUnderlyingCyNetwork(underlyingNetwork);
		ExpandableNode expandableNode = ontologyNetwork
				.getNodeFromUnderlyingNode(nodeView.getModel());

		expandableNode.collapse();

		ViewOperationUtils.hideSubTree(expandableNode, networkView);
		networkView.updateView();
		cytoscapeServiceManager.getCyEventHelper().flushPayloadEvents();
	}

}
