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

public class MyApplicationManager {

	CySwingApplication cyDesktopService;

	CyApplicationManager cyApplicationManager;

	CyNetworkFactory cyNetworkFactory;
	CyNetworkManager cyNetworkManager;

	CyNetworkViewFactory cyNetworkViewFactory;
	CyNetworkViewManager cyNetworkViewManager;

	VisualMappingManager cyVisualMappingManager;
	CyLayoutAlgorithmManager cyLayoutAlgorithmManager;

	CyEventHelper cyEventHelper;

	public MyApplicationManager(CySwingApplication desktopService,
			CyApplicationManager applicationManager,
			CyNetworkFactory networkFactory, CyNetworkManager networkManager,
			CyNetworkViewFactory networkViewFactory,
			CyNetworkViewManager networkViewManager,
			VisualMappingManager visualMappingManager,
			CyLayoutAlgorithmManager layoutAlgorithmManager,
			CyEventHelper eventHelper) {
		cyDesktopService = desktopService;

		cyApplicationManager = applicationManager;

		cyNetworkFactory = networkFactory;
		cyNetworkManager = networkManager;

		cyNetworkViewFactory = networkViewFactory;
		cyNetworkViewManager = networkViewManager;

		cyVisualMappingManager = visualMappingManager;
		cyLayoutAlgorithmManager = layoutAlgorithmManager;

		cyEventHelper = eventHelper;
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

}