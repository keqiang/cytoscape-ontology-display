package edu.umich.med.mbni.lkq.cyontology.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.osgi.framework.BundleContext;;

public class CyActivator extends AbstractCyActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		
		CySwingApplication cytoscapeDesktopService = getService(context, CySwingApplication.class);
        
        CyApplicationManager applicationManager = getService(context, CyApplicationManager.class);
        
        CyGroupFactory groupFactory = getService(context, CyGroupFactory.class);
        
        CyGroupManager groupManager = getService(context, CyGroupManager.class);
        
        CyNetworkViewManager networkViewManager = getService(context, CyNetworkViewManager.class);
        
        VisualMappingManager vmMgr = getService(context, VisualMappingManager.class);
        
        CyNetworkFactory networkFactory = getService(context, CyNetworkFactory.class);
        
        CyNetworkManager networkManager = getService(context, CyNetworkManager.class);
        
        CyNetworkViewFactory networkViewFactory = getService(context, CyNetworkViewFactory.class);
        
        CyEventHelper eventHelper = getService(context, CyEventHelper.class);
        
        RefactorOntologyDisplayAction action = new RefactorOntologyDisplayAction(cytoscapeDesktopService, applicationManager, groupFactory, groupManager, networkViewManager, vmMgr, networkFactory, networkManager, networkViewFactory, eventHelper);
        // Register it as a service:
        registerService(context, action, CyAction.class, new Properties());
	}
	
	

}
