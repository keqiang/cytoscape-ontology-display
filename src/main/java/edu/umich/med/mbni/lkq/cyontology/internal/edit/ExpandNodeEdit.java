package edu.umich.med.mbni.lkq.cyontology.internal.edit;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.undo.AbstractCyEdit;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.ViewOperationUtils;

public class ExpandNodeEdit extends AbstractCyEdit {

	private CyNetworkView networkView;
	private CyNode nodeToExpand;

	public ExpandNodeEdit(String presentationName, CyNetworkView networkView,
			CyNode nodeToExpand) {
		super(presentationName);
		this.networkView = networkView;
		this.nodeToExpand = nodeToExpand;
	}

	@Override
	public void redo() {

		CyNetwork underlyingNetwork = networkView.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationCenter.getInstance()
				.getCorrespondingOntologyNetwork(underlyingNetwork);
		ExpandableNode expandableNode = ontologyNetwork
				.getCorrespondingNode(nodeToExpand);

		expandableNode.expand();

		ViewOperationUtils.showSubTree(expandableNode, networkView);

	}

	@Override
	public void undo() {
		CyNetwork underlyingNetwork = networkView.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationCenter.getInstance()
				.getCorrespondingOntologyNetwork(underlyingNetwork);
		ExpandableNode expandableNode = ontologyNetwork
				.getCorrespondingNode(nodeToExpand);

		expandableNode.collapse();

		ViewOperationUtils.hideSubTree(expandableNode, networkView);

	}

}
