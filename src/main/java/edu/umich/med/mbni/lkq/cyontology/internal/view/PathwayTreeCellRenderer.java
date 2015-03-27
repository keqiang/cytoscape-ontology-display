package edu.umich.med.mbni.lkq.cyontology.internal.view;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import edu.umich.med.mbni.lkq.cyontology.internal.model.PathwayEvent;

class PathwayTreeCellRenderer extends DefaultTreeCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1412601401253879863L;
	
	private Icon pathwayIcon;
    private Icon reactionIcon;
    
    public PathwayTreeCellRenderer() {
        super();
        pathwayIcon = new ImageIcon(getClass().getResource("Pathway.gif"));
        reactionIcon = new ImageIcon(getClass().getResource("Reaction.gif"));
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
        PathwayEvent event = (PathwayEvent) treeNode.getUserObject();
        if (event == null)
            return comp; // For the default root?

        setText(event.getName());
        setBackgroundNonSelectionColor(tree.getBackground());
 
        if (event.isPathway())
            setIcon(pathwayIcon);
        else
            setIcon(reactionIcon);
        return comp;
    }
    
    /*
    private Color getFDRColor(GeneSetAnnotation annotation) {
        if (annotation.getFdr().startsWith("<"))
            return fdrColors.get(3);
        else {
            Double value = new Double(annotation.getFdr());
            if (value >= 0.1d)
                return fdrColors.get(0);
            if (value >= 0.01d)
                return fdrColors.get(1);
            return fdrColors.get(2);
        }
    }*/
}