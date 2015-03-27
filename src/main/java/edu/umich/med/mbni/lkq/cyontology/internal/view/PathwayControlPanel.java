package edu.umich.med.mbni.lkq.cyontology.internal.view;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

import org.jdom.Element;

/**
 * This customized JPanel, which implements CytoPanelComponent, is used as a
 * control panel for Reactome pathways. The pathway hierarchy and an overview of
 * displayed diagram will be displayed here.
 * 
 * @author gwu
 *
 */
@SuppressWarnings("serial")
public class PathwayControlPanel extends JPanel implements CytoPanelComponent {

	private TreePane treePane;

	private static PathwayControlPanel instance = new PathwayControlPanel();

	/**
	 * Default private constrcutor so that this class should be used as a
	 * singleton only.
	 */
	private PathwayControlPanel() {
		init();
	}

	public static PathwayControlPanel getInstance() {
		return instance;
	}

	private void init() {
		setLayout(new BorderLayout());
		treePane = new TreePane(true);
		add(treePane, BorderLayout.CENTER);
	}

	/**
	 * Call to load the actual event tree from a RESTful API. This method should
	 * be called in order to see the tree.
	 */
	public void loadFrontPageItems() throws Exception {
		treePane.loadFrontPageItems();
	}

	public void setAllPathwaysInElement(Element root) throws Exception {
		treePane.setAllPathwaysInElement(root);
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
		return "Reactome";
	}

	@Override
	public Icon getIcon() {
		return null;
	}
}
