package edu.umich.med.mbni.lkq.cyontology.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.JOptionPane;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyRow;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import edu.umich.med.mbni.lkq.cyontology.controller.PathwayControlPanelController;
import edu.umich.med.mbni.lkq.cyontology.service.FINetworkGenerator;
import edu.umich.med.mbni.lkq.cyontology.service.FIVisualStyle;
import edu.umich.med.mbni.lkq.cyontology.service.RESTFulFIService;
import edu.umich.med.mbni.lkq.cyontology.task.PathwayHierarchyLoadTask;
import edu.umich.med.mbni.lkq.cyontology.util.PlugInObjectManager;
import edu.umich.med.mbni.lkq.cyontology.util.PlugInUtilities;
import edu.umich.med.mbni.lkq.cyontology.view.PathwayControlPanel;

/**
 * @author keqiangli an action that loads the overall FI network of genes, which
 *         is, loading the reactome control panel on the left side and meanwhile
 *         generating a FI network contains all the proteins in the database on
 *         the right side
 *
 */

public class OverallFINetworkLoadAction extends AbstractCyAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4476322031414232438L;

	public OverallFINetworkLoadAction() {
		super("Load Overall FI Network");
		setPreferredMenu("Apps.Ontology Viewer");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// load the pathway panel first by running a task
		loadReactomePathway();

		Task task = new AbstractTask() {
			@Override
			public void run(TaskMonitor taskMonitor) throws Exception {
				loadOverallFINetwork(taskMonitor);
			}
		};

		@SuppressWarnings("rawtypes")
		TaskManager taskManager = PlugInObjectManager.getManager()
				.getTaskManager();
		taskManager.execute(new TaskIterator(task));
	}

	@SuppressWarnings("rawtypes")
	private void loadReactomePathway() {
		// Check if Reactome pathways have been loaded
		if (PlugInObjectManager.getManager().isPathwaysLoaded()) {
			CySwingApplication application = PlugInObjectManager.getManager()
					.getCySwingApplication();
			CytoPanel panel = application.getCytoPanel(CytoPanelName.WEST);
			if (panel.getState() == CytoPanelState.HIDE)
				panel.setState(CytoPanelState.DOCK);

			// Check if controlPane has been displayed
			for (int i = 0; i < panel.getCytoPanelComponentCount(); i++) {
				Component comp = panel.getComponentAt(i);
				if (comp instanceof PathwayControlPanel) {
					panel.setSelectedIndex(i);
					break;
				}
			}
			return;
		}

		// Need a new session
		if (!PlugInUtilities.createNewSession())
			return;
		// Make sure the latest version of RESTful API is used since the latest
		// version of pathways are configured in the Reactome RESTful API
		// that is used for pathway loading
		PlugInObjectManager manager = PlugInObjectManager.getManager();
		String fiVersion = manager.getLatestFINetworkVersion();
		manager.setFiNetworkVersion(fiVersion);
		// Actual loading
		TaskManager tm = manager.getTaskManager();
		if (tm == null)
			return;
		
		PathwayHierarchyLoadTask task = new PathwayHierarchyLoadTask();
		tm.execute(new TaskIterator(task));
	}

	private void loadOverallFINetwork(TaskMonitor taskMonitor) throws Exception {
		
		taskMonitor.setTitle("Load Overall FI Network");
		taskMonitor.setStatusMessage("Retrieving FI From Remote Server");
		
		taskMonitor.setProgress(0.1d);

		// get the interactions from the web server
		RESTFulFIService fiService = new RESTFulFIService();
		Set<String> fis = fiService.queryAllFIs();

		if (fis == null || fis.size() == 0) {
			JOptionPane.showMessageDialog(
					PlugInUtilities.getCytoscapeDesktop(),
					"There is no FI existing", "No FI",
					JOptionPane.INFORMATION_MESSAGE);
			taskMonitor.setProgress(1.0d);
			return;
		}

		taskMonitor.setProgress(0.50d);

		taskMonitor.setStatusMessage("Generating FI Network and View");

		// Need to create a new CyNetwork
		FINetworkGenerator generator = new FINetworkGenerator();
		CyNetwork network = generator.constructFINetwork(fis);
		// Add some meta information
		CyRow row = network.getDefaultNetworkTable().getRow(network.getSUID());
		row.set(CyNetwork.NAME, "Overall FI Network");
		
		// TODO can be used when selected genes
		// PathwayEnrichmentHighlighter.getHighlighter().highlightNework(network,
		// hitGenes);
		// Store Instance ids information

		BundleContext context = PlugInObjectManager.getManager()
				.getBundleContext();

		ServiceReference reference = context
				.getServiceReference(CyNetworkManager.class.getName());
		CyNetworkManager networkManager = (CyNetworkManager) context
				.getService(reference);
		networkManager.addNetwork(network);
		networkManager = null;
		context.ungetService(reference);

		reference = context.getServiceReference(CyNetworkViewFactory.class
				.getName());
		CyNetworkViewFactory viewFactory = (CyNetworkViewFactory) context
				.getService(reference);
		CyNetworkView view = viewFactory.createNetworkView(network);
		viewFactory = null;
		context.ungetService(reference);

		reference = context.getServiceReference(CyNetworkViewManager.class
				.getName());
		CyNetworkViewManager viewManager = (CyNetworkViewManager) context
				.getService(reference);
		viewManager.addNetworkView(view);
		viewManager = null;
		context.ungetService(reference);

		ServiceReference servRef = context
				.getServiceReference(FIVisualStyle.class.getName());
		FIVisualStyle visStyler = (FIVisualStyle) context.getService(servRef);
		visStyler.setVisualStyle(view);
		visStyler.setLayout(view);
		visStyler = null;
		context.ungetService(servRef);
		
		PathwayControlPanelController.getInstance().setOverallCyNetworkView(view);

		taskMonitor.setProgress(1.0d);
	}

}

