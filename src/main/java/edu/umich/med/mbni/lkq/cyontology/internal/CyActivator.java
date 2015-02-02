package edu.umich.med.mbni.lkq.cyontology.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

import edu.umich.med.mbni.lkq.cyontology.internal.actions.RefactorOntologyDisplayAction;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.task.ExpandableNodeCollapseTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.ExpandableNodeExpandTaskFactory;

public class CyActivator extends AbstractCyActivator {

	@Override
	public void start(BundleContext context) throws Exception {

		CySwingApplication cytoscapeDesktopService = getService(context,
				CySwingApplication.class);

		CyApplicationManager applicationManager = getService(context,
				CyApplicationManager.class);

		CyNetworkViewManager networkViewManager = getService(context,
				CyNetworkViewManager.class);

		VisualMappingManager vmMgr = getService(context,
				VisualMappingManager.class);

		CyNetworkFactory networkFactory = getService(context,
				CyNetworkFactory.class);

		CyNetworkManager networkManager = getService(context,
				CyNetworkManager.class);

		CyNetworkViewFactory networkViewFactory = getService(context,
				CyNetworkViewFactory.class);

		CyEventHelper eventHelper = getService(context, CyEventHelper.class);

		CyLayoutAlgorithmManager algorithmManager = getService(context,
				CyLayoutAlgorithmManager.class);
		
		UndoSupport undoSupport = getService(context, UndoSupport.class);

		MyApplicationManager myApplicationManager = new MyApplicationManager(
				cytoscapeDesktopService, applicationManager, networkFactory,
				networkManager, networkViewFactory, networkViewManager, vmMgr,
				algorithmManager, eventHelper, undoSupport);
		
		MyApplicationCenter appCenter = MyApplicationCenter.getInstance();
		MyApplicationCenter.registerApplicationManager(myApplicationManager);

		RefactorOntologyDisplayAction action = new RefactorOntologyDisplayAction(
				"force-directed");
		// Register it as a service:
		registerService(context, action, CyAction.class, new Properties());
		
		registerService(context, appCenter, NetworkAboutToBeDestroyedListener.class, new Properties());
		
		// Register myNodeViewTaskFactory as a service in CyActivator
		Properties myNodeViewTaskFactoryProps = new Properties();
		myNodeViewTaskFactoryProps.setProperty("title","Collpase this ontology term");
		ExpandableNodeCollapseTaskFactory expandableNodeCollapseTask = new ExpandableNodeCollapseTaskFactory();
		registerService(context, expandableNodeCollapseTask, NodeViewTaskFactory.class, myNodeViewTaskFactoryProps);
		
		myNodeViewTaskFactoryProps = new Properties();
		myNodeViewTaskFactoryProps.setProperty("title","Expand this ontology term");
		ExpandableNodeExpandTaskFactory expandableNodeExpandTask = new ExpandableNodeExpandTaskFactory();
		registerService(context, expandableNodeExpandTask, NodeViewTaskFactory.class, myNodeViewTaskFactoryProps);

	}

}
