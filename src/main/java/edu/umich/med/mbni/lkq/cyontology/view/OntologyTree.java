package edu.umich.med.mbni.lkq.cyontology.view;

import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import edu.umich.med.mbni.lkq.cyontology.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.model.OntologyNetwork;

public class OntologyTree extends JTree {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3795237605090672068L;
	private OntologyNetwork bindedOntologyNetwork;
	
	public OntologyTree(DefaultMutableTreeNode root, OntologyNetwork bindedOntologyNetwork) {
		super(root);
		this.bindedOntologyNetwork = bindedOntologyNetwork;
	}
	
	public OntologyNetwork getOntologyNetwork() {
		return bindedOntologyNetwork;
	}
	
	public void setOntologyNetwork(OntologyNetwork ontologyNetwork) {
		this.bindedOntologyNetwork = ontologyNetwork;
	}
	
	public synchronized void collpaseNodeCompletely(DefaultMutableTreeNode node) {
		for (int i = 0; i < node.getChildCount(); ++i) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
					.getChildAt(i);
			if (this.isExpanded(new TreePath(child.getPath()))) {
				collpaseNodeCompletely(child);
			}
		}
		this.collapsePath(new TreePath(node.getPath()));
	}

	public synchronized void expandNode(DefaultMutableTreeNode node) {
		this.expandPath(new TreePath(node.getPath()));
	}
	
	@Override
	public String getToolTipText(MouseEvent event) {
		if (getRowForLocation(event.getX(), event.getY()) == -1)
	          return null;
	    TreePath curPath = getPathForLocation(event.getX(), event.getY());
	    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) curPath.getLastPathComponent();
	    
	    Object userObject = treeNode.getUserObject();
	    if (userObject instanceof String) {
	    	return (String)userObject;
	    } else {
	    	ExpandableNode expandableNode = (ExpandableNode)userObject;
	    	return expandableNode.getToolTip();
	    }
	}
}
