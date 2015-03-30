package edu.umich.med.mbni.lkq.cyontology.task;

import java.awt.Component;
import java.io.StringReader;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umich.med.mbni.lkq.cyontology.service.ReactomeRESTfulService;
import edu.umich.med.mbni.lkq.cyontology.util.PlugInObjectManager;
import edu.umich.med.mbni.lkq.cyontology.view.PathwayControlPanel;

/**
 * This class is used to load Reactome pathway hierarchy and then displayed them
 * in Cytoscape.
 * 
 * @author gwu
 *
 */
public class PathwayHierarchyLoadTask extends AbstractTask {

	private static final Logger logger = LoggerFactory
			.getLogger(PathwayHierarchyLoadTask.class);

	/**
	 * Default constructor.
	 */
	public PathwayHierarchyLoadTask() {
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		displayReactomePathways(taskMonitor);
	}

	private void displayReactomePathways(TaskMonitor monitor) {
		CySwingApplication application = PlugInObjectManager.getManager()
				.getCySwingApplication();
		CytoPanel panel = application.getCytoPanel(CytoPanelName.WEST);
		if (panel.getState() == CytoPanelState.HIDE)
			panel.setState(CytoPanelState.DOCK);
		// Check if controlPane has been displayed
		int index = getIndexOfPathwayControlPane(panel);
		if (index >= 0) {
			// Make sure it is selected
			panel.setSelectedIndex(index);
			return;
		}
		BundleContext context = PlugInObjectManager.getManager()
				.getBundleContext();
		PathwayControlPanel controlPane = PathwayControlPanel.getInstance();

		if (panel.getCytoPanelComponentCount() > 0
				&& panel.getComponentAt(0) != null) {
			Component comp = panel.getComponentAt(0);
			controlPane.setPreferredSize(comp.getPreferredSize()); // Control
																	// its size
																	// to avoid
																	// weird
																	// behavior
		}
		try {
			monitor.setStatusMessage("Loading pathways...");
			monitor.setTitle("Pathway Loading");
			monitor.setProgress(0.2d);
			String text = ReactomeRESTfulService.getService()
					.pathwayHierarchy();
			monitor.setStatusMessage("parsing results...");
			StringReader reader = new StringReader(text);
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(reader);
			Element root = document.getRootElement();
			controlPane.setAllPathwaysInElement(root);
			monitor.setProgress(1.0d);
		} catch (Exception e) {
			logger.error("Error in loading tree: " + e.getMessage(), e);
			e.printStackTrace();
		}
		context.registerService(CytoPanelComponent.class.getName(),
				controlPane, null);
		// Make sure if a new Reactome control panel can be selected
		// Note: This may not work if the above call is asynchronous!
		index = getIndexOfPathwayControlPane(panel);
		if (index >= 0)
			panel.setSelectedIndex(index);
	}

	/**
	 * A helper method to get the PathwayControlPanel panel.
	 * 
	 * @param cytoPanel
	 * @return
	 */
	private int getIndexOfPathwayControlPane(CytoPanel cytoPanel) {
		for (int i = 0; i < cytoPanel.getCytoPanelComponentCount(); i++) {
			Component comp = cytoPanel.getComponentAt(i);
			if (comp instanceof PathwayControlPanel)
				return i;
		}
		return -1;
	}

}
