package edu.umich.med.mbni.lkq.cyontology.view;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import edu.umich.med.mbni.lkq.cyontology.model.OntologyTerm;

public class OntologyTreeCellRenderer extends DefaultTreeCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -302657874679347050L;
	
	private Icon ontologyIcon;
    //private Icon reactionIcon;
    
    public OntologyTreeCellRenderer() {
        super();
        ontologyIcon = new ImageIcon(getClass().getResource("Ontology.gif"));
        //reactionIcon = new ImageIcon(getClass().getResource("Reaction.gif"));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, 
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf, int row,
                                                  boolean hasFocus) {
        Component comp = super.getTreeCellRendererComponent(tree, 
                                                            value, 
                                                            sel,
                                                            expanded,
                                                            leaf,
                                                            row, 
                                                            hasFocus);
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
        OntologyTerm term = (OntologyTerm) treeNode.getUserObject();
        if (term == null)
            return comp; // For the default root?

        setText(term.getOntologyId());
        setIcon(ontologyIcon);
        setBackgroundNonSelectionColor(tree.getBackground());
 
        return comp;
    }
}
