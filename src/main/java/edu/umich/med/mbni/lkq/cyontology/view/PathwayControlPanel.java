package edu.umich.med.mbni.lkq.cyontology.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.gk.model.ReactomeJavaConstants;
import org.jdom2.Element;

import edu.umich.med.mbni.lkq.cyontology.model.PathwayEvent;
import edu.umich.med.mbni.lkq.cyontology.service.ReactomeRESTfulService;

public class PathwayControlPanel extends JPanel implements CytoPanelComponent2 {
	
	private static final long serialVersionUID = 7453552014395203614L;
	
	
	private JTree contentTree;
	
	private static PathwayControlPanel instance = new PathwayControlPanel();
	
	private PathwayControlPanel() {
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		initTree();
		add(contentTree);
	}
	
	private void initTree() {
		contentTree = new JTree();
		contentTree.setRootVisible(false);
		contentTree.setShowsRootHandles(true);
        contentTree.setExpandsSelectedPaths(true);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultTreeModel model = new DefaultTreeModel(root);
        contentTree.setModel(model);
        
        TreeCellRenderer renderer = new PathwayTreeCellRenderer();
        contentTree.setCellRenderer(renderer);
	}
	
	public static PathwayControlPanel getInstance() {
		return instance;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	@Override
	public String getTitle() {
		return "Pathways";
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIdentifier() {
		return "edu.umich.med.mbni.lkq.pathway";
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
