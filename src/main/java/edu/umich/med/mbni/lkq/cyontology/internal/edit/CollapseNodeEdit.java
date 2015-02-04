package edu.umich.med.mbni.lkq.cyontology.internal.edit;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.undo.AbstractCyEdit;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.ViewOperationUtils;

public class CollapseNodeEdit extends AbstractCyEdit {

	private CyNetworkView networkView;
	private CyNode nodeToCollapse;

	public CollapseNodeEdit(String presentationName, CyNetworkView networkView,
			CyNode nodeToCollapse) {
		super(presentationName);
		this.networkView = networkView;
		this.nodeToCollapse = nodeToCollapse;
	}

	@Override
	public void redo() {
		CyNetwork underlyingNetwork = networkView.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationCenter.getInstance()
				.getEncapsulatingOntologyNetwork(underlyingNetwork);
		ExpandableNode expandableNode = ontologyNetwork.getNode(nodeToCollapse.getSUID());

		expandableNode.collapse();

		ViewOperationUtils.hideSubTree(expandableNode, networkView);

	}

	@Override
	public void undo() {
		CyNetwork underlyingNetwork = networkView.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationCenter.getInstance()
				.getEncapsulatingOntologyNetwork(underlyingNetwork);
		ExpandableNode expandableNode = ontologyNetwork
				.getNode(nodeToCollapse.getSUID());

		expandableNode.expand();

		ViewOperationUtils.showSubTree(expandableNode, networkView);

	}

}
