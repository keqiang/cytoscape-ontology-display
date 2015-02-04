package edu.umich.med.mbni.lkq.cyontology.internal.edit;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.undo.AbstractCyEdit;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.ViewOperationUtils;

public class ExpandNodeEdit extends AbstractCyEdit {

	private CyNetworkView networkView;
	private View<CyNode> nodeView;

	public ExpandNodeEdit(String presentationName, CyNetworkView networkView,
			View<CyNode> nodeView) {
		super(presentationName);
		this.networkView = networkView;
		this.nodeView = nodeView;
	}


	@Override
	public void redo() {

		CyNetwork underlyingNetwork = networkView.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationCenter.getInstance()
				.getEncapsulatingOntologyNetwork(underlyingNetwork);
		ExpandableNode expandableNode = ontologyNetwork
				.getNode(nodeView.getModel().getSUID());

		expandableNode.expand();

		ViewOperationUtils.showSubTree(expandableNode, networkView);

	}

	@Override
	public void undo() {
		CyNetwork underlyingNetwork = networkView.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationCenter.getInstance()
				.getEncapsulatingOntologyNetwork(underlyingNetwork);
		ExpandableNode expandableNode = ontologyNetwork
				.getNode(nodeView.getModel().getSUID());

		expandableNode.collapse();

		ViewOperationUtils.hideSubTree(expandableNode, networkView);

	}

}
