package edu.umich.med.mbni.lkq.cyontology.internal.view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import org.gk.model.ReactomeJavaConstants;
import org.jdom.Element;

import edu.umich.med.mbni.lkq.cyontology.internal.model.PathwayEvent;
import edu.umich.med.mbni.lkq.cyontology.internal.service.ReactomeRESTfulService;

public class TreePane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 861161976706967012L;
	
	// a jtree which holds an ontology or pathway hierarchy 
	JTree contentTree;
	boolean isOntologyTree = false;
	
	public TreePane(boolean isOntolgoyTree) {
		this.isOntologyTree = isOntolgoyTree;
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		
		contentTree = new JTree();
		contentTree.setRootVisible(false);
		contentTree.setShowsRootHandles(true);
        contentTree.setExpandsSelectedPaths(true);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultTreeModel model = new DefaultTreeModel(root);
        contentTree.setModel(model);
        
        TreeCellRenderer renderer;
        if (isOntologyTree) {
        	renderer = new OntologyTreeCellRenderer();
        } else {
        	renderer = new PathwayTreeCellRenderer();
        }
        
        contentTree.setCellRenderer(renderer);
        
        add(new JScrollPane(contentTree), BorderLayout.CENTER);
	}
	
    /**
     * Set all pathways encoded in an JDOM Element.
     * @param root
     * @throws Exception
     */
    public void setAllPathwaysInElement(Element root) throws Exception {
        DefaultTreeModel model = (DefaultTreeModel) contentTree.getModel();
        DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) model.getRoot();
        treeRoot.removeAllChildren(); // Just in case there is anything there.
        List<?> children = root.getChildren();
        for (Object obj : children) {
            Element elm = (Element) obj;
            // To avoid disease for the time being
            String name = elm.getAttributeValue("displayName");
            if (name.equals("Disease"))
                continue;
            addEvent(treeRoot, elm);
        }
        model.nodeStructureChanged(treeRoot);
    }
    
    
    private void addEvent(DefaultMutableTreeNode parentNode,
                          Element eventElm) {
        PathwayEvent event = parseEvent(eventElm);
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
        treeNode.setUserObject(event);
        parentNode.add(treeNode);
        List<?> children = eventElm.getChildren();
        if (children == null || children.size() == 0)
            return;
        for (Object obj : children) {
            Element childElm = (Element) obj;
            addEvent(treeNode, childElm);
        }
    }
    
    private PathwayEvent parseEvent(Element elm) {
        String dbId = elm.getAttributeValue("dbId");
        String name = elm.getAttributeValue("displayName");
        PathwayEvent event = new PathwayEvent();
        event.setDbId(new Long(dbId));
        event.setName(name);
        String clsName = elm.getName();
        if (clsName.equals(ReactomeJavaConstants.Pathway))
            event.setPathway(true);
        else
            event.setPathway(false);
        String hasDiagram = elm.getAttributeValue("hasDiagram");
        if (hasDiagram != null)
            event.setHasDiagram(hasDiagram.equals("true") ? true : false);
        return event;
    }
    
    
    /**
     * Load the top-level pathways from the Reactome RESTful API.
     */
    public void loadFrontPageItems() throws Exception {
        Element root = ReactomeRESTfulService.getService().frontPageItems();
        List<?> children = root.getChildren();
        DefaultTreeModel model = (DefaultTreeModel) contentTree.getModel();
        DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) model.getRoot();
        treeRoot.removeAllChildren(); // Just in case there is anything there.
        for (Object obj : children) {
            Element elm = (Element) obj;
            PathwayEvent event = parseFrontPageEvent(elm);
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
            treeNode.setUserObject(event);
            treeRoot.add(treeNode);
        }
        // Needs an update
        model.nodeStructureChanged(treeRoot);
    }
    
    /**
     * Parse an XML element for an EventObject object.
     * @param elm
     * @return
     */
    private PathwayEvent parseFrontPageEvent(Element elm) {
        String dbId = elm.getChildText("dbId");
        String name = elm.getChildText("displayName");
        PathwayEvent event = new PathwayEvent();
        event.setDbId(new Long(dbId));
        event.setName(name);
        String clsName = elm.getChildText("schemaClass");
        if (clsName.equals(ReactomeJavaConstants.Pathway))
            event.setPathway(true);
        else
            event.setPathway(false);
        String hasDiagram = elm.getChildText("hasDiagram");
        if (hasDiagram != null)
            event.setHasDiagram(hasDiagram.equals("true") ? true : false);
        return event;
    }
    
}
