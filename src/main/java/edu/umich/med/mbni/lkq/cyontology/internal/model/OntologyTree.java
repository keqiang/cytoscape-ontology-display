package edu.umich.med.mbni.lkq.cyontology.internal.model;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.cytoscape.model.CyNetwork;

public class OntologyTree extends JTree {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3795237605090672068L;
	private final OntologyNetwork bindedOntologyNetwork;
	
	public OntologyTree(DefaultMutableTreeNode root, final OntologyNetwork bindedOntologyNetwork) {
		super(root);
		this.bindedOntologyNetwork = bindedOntologyNetwork;
	}

	@Override
	public String convertValueToText(Object value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {

		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)value;
		
		if (treeNode.isRoot()) {
			return treeNode.toString();
		}
		ExpandableNode expandableNode = (ExpandableNode) treeNode.getUserObject();
		
		if (bindedOntologyNetwork == null) return expandableNode.getSUID().toString();
		
		CyNetwork underlyingNetwork = bindedOntologyNetwork.getUnderlyingNetwork();
		String nodeName = underlyingNetwork.getDefaultNodeTable().getRow(expandableNode.getSUID()).get("name", String.class);
		return nodeName;
	}
	
	public OntologyNetwork getOntologyNetwork() {
		return bindedOntologyNetwork;
	}

}
