package edu.umich.med.mbni.lkq.cyontology.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		
		CySwingApplication cytoscapeDesktopService = getService(context, CySwingApplication.class);
        
        CyApplicationManager applicationManager = getService(context, CyApplicationManager.class);
        
        CyGroupFactory groupFactory = getService(context, CyGroupFactory.class);
        
        CyGroupManager groupManager = getService(context, CyGroupManager.class);
        
        RefactorOntologyDisplayAction action = new RefactorOntologyDisplayAction(cytoscapeDesktopService, applicationManager, groupFactory, groupManager);
        // Register it as a service:
        registerService(context, action, CyAction.class, new Properties());
	}

}
