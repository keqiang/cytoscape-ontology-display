package edu.umich.med.mbni.lkq.cyontology.internal.app;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.undo.UndoSupport;

/**
 * @author keqiangli This class give the access to all the cytoscape services needed in this plugin
 *
 */
public class CytoscapeServiceManager {

	CySwingApplication cyDesktopService;

	CyApplicationManager cyApplicationManager;

	CyNetworkFactory cyNetworkFactory;
	CyNetworkManager cyNetworkManager;

	CyNetworkViewFactory cyNetworkViewFactory;
	CyNetworkViewManager cyNetworkViewManager;

	VisualMappingManager cyVisualMappingManager;
	CyLayoutAlgorithmManager cyLayoutAlgorithmManager;

	CyEventHelper cyEventHelper;
	
	UndoSupport cyUndoSupport;
	
	DialogTaskManager cyTaskManager;

	public CytoscapeServiceManager(CySwingApplication desktopService,
			CyApplicationManager applicationManager,
			CyNetworkFactory networkFactory, CyNetworkManager networkManager,
			CyNetworkViewFactory networkViewFactory,
			CyNetworkViewManager networkViewManager,
			VisualMappingManager visualMappingManager,
			CyLayoutAlgorithmManager layoutAlgorithmManager,
			CyEventHelper eventHelper, UndoSupport undoSupport, DialogTaskManager taskManager) {
		cyDesktopService = desktopService;

		cyApplicationManager = applicationManager;

		cyNetworkFactory = networkFactory;
		cyNetworkManager = networkManager;

		cyNetworkViewFactory = networkViewFactory;
		cyNetworkViewManager = networkViewManager;

		cyVisualMappingManager = visualMappingManager;
		cyLayoutAlgorithmManager = layoutAlgorithmManager;

		cyEventHelper = eventHelper;
		
		cyUndoSupport = undoSupport;
		cyTaskManager = taskManager;
	}

	public CySwingApplication getCyDesktopService() {
		return cyDesktopService;
	}

	public CyApplicationManager getCyApplicationManager() {
		return cyApplicationManager;
	}

	public CyNetworkFactory getCyNetworkFactory() {
		return cyNetworkFactory;
	}

	public CyNetworkManager getCyNetworkManager() {
		return cyNetworkManager;
	}

	public CyNetworkViewFactory getCyNetworkViewFactory() {
		return cyNetworkViewFactory;
	}

	public CyNetworkViewManager getCyNetworkViewManager() {
		return cyNetworkViewManager;
	}

	public VisualMappingManager getCyVisualMappingManager() {
		return cyVisualMappingManager;
	}

	public CyLayoutAlgorithmManager getCyLayoutAlgorithmManager() {
		return cyLayoutAlgorithmManager;
	}

	public CyEventHelper getCyEventHelper() {
		return cyEventHelper;
	}
	
	public UndoSupport getCyUndoSupport() {
		return cyUndoSupport;
	}
	
	public DialogTaskManager getTaskManager() {
		return cyTaskManager;
	}

}