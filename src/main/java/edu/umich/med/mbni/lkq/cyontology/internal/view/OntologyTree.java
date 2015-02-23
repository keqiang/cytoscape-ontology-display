package edu.umich.med.mbni.lkq.cyontology.internal.view;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;

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
	
	public OntologyNetwork getOntologyNetwork() {
		return bindedOntologyNetwork;
	}
	
	public void collpaseNodeCompletely(DefaultMutableTreeNode node) {
		for (int i = 0; i < node.getChildCount(); ++i) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
					.getChildAt(i);
			if (this.isExpanded(new TreePath(child.getPath()))) {
				collpaseNodeCompletely(child);
			}
		}
		this.collapsePath(new TreePath(node.getPath()));
	}

	public void expandNode(DefaultMutableTreeNode node) {
		this.expandPath(new TreePath(node.getPath()));
	}

}
