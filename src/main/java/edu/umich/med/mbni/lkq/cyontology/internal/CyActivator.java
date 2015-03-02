package edu.umich.med.mbni.lkq.cyontology.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

import edu.umich.med.mbni.lkq.cyontology.internal.actions.OntologyControlPanelAction;
import edu.umich.med.mbni.lkq.cyontology.internal.actions.RefactorOntologyDisplayAction;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.task.ExpandableNodeCollapseTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.ExpandableNodeExpandOneLevelTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.FindCommonChildNodesTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.SelectChildNodeTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.SelectDirectChildNodeTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyControlPanel;

public class CyActivator extends AbstractCyActivator {

	@Override
	public void start(BundleContext context) throws Exception {

		/*
		 * get all the services needed by this App
		 */
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
		
		DialogTaskManager taskManager = getService(context, DialogTaskManager.class);
		
		/*
		 * register these services to this App
		 */
		MyApplicationManager myApplicationManager = new MyApplicationManager(
				cytoscapeDesktopService, applicationManager, networkFactory,
				networkManager, networkViewFactory, networkViewManager, vmMgr,
				algorithmManager, eventHelper, undoSupport, taskManager);
		
		MyApplicationCenter appCenter = MyApplicationCenter.getInstance();
		MyApplicationCenter.registerApplicationManager(myApplicationManager);

		appCenter.setLayoutAlgorithmName("hierarchical");
		/*
		 *  register all the services this App provides
		 */
		RefactorOntologyDisplayAction action = new RefactorOntologyDisplayAction();
		registerService(context, action, CyAction.class, new Properties());
		
		registerService(context, appCenter, NetworkAboutToBeDestroyedListener.class, new Properties());
		
		Properties myNodeViewTaskFactoryProps = new Properties();
		myNodeViewTaskFactoryProps.setProperty("title","Collpase this ontology term");
		ExpandableNodeCollapseTaskFactory expandableNodeCollapseTaskFactory = new ExpandableNodeCollapseTaskFactory();
		registerService(context, expandableNodeCollapseTaskFactory, NodeViewTaskFactory.class, myNodeViewTaskFactoryProps);
		
		
		myNodeViewTaskFactoryProps = new Properties();
		myNodeViewTaskFactoryProps.setProperty("title","Select child nodes in common");
		FindCommonChildNodesTaskFactory findCommonChildNodesTaskFactory = new FindCommonChildNodesTaskFactory();
		registerService(context, findCommonChildNodesTaskFactory, NodeViewTaskFactory.class, myNodeViewTaskFactoryProps);
		
		myNodeViewTaskFactoryProps = new Properties();
		myNodeViewTaskFactoryProps.setProperty("title","Expand this ontology term");
		ExpandableNodeExpandOneLevelTaskFactory expandableNodeExpandOneLevelTaskFactory = new ExpandableNodeExpandOneLevelTaskFactory();
		registerService(context, expandableNodeExpandOneLevelTaskFactory, NodeViewTaskFactory.class, myNodeViewTaskFactoryProps);
		
		myNodeViewTaskFactoryProps = new Properties();
		myNodeViewTaskFactoryProps.setProperty("title","Select all children ontology items");
		SelectChildNodeTaskFactory selectChildNodeTaskFactory = new SelectChildNodeTaskFactory();
		registerService(context, selectChildNodeTaskFactory, NodeViewTaskFactory.class, myNodeViewTaskFactoryProps);
		
		myNodeViewTaskFactoryProps = new Properties();
		myNodeViewTaskFactoryProps.setProperty("title","Select direct children ontology items");
		SelectDirectChildNodeTaskFactory selectDirectChildNodeTaskFactory = new SelectDirectChildNodeTaskFactory();
		registerService(context, selectDirectChildNodeTaskFactory, NodeViewTaskFactory.class, myNodeViewTaskFactoryProps);
		
		OntologyControlPanel controlPanel = new OntologyControlPanel();
		registerService(context, controlPanel, CytoPanelComponent.class, new Properties());
		
		registerService(context, controlPanel, RowsSetListener.class, new Properties());
		
		registerService(context, controlPanel, NetworkAboutToBeDestroyedListener.class, new Properties());
		registerService(context, controlPanel, NetworkViewAboutToBeDestroyedListener.class, new Properties());
		
		OntologyControlPanelAction controlPanelAction = new OntologyControlPanelAction(cytoscapeDesktopService, controlPanel);
		registerService(context, controlPanelAction, CyAction.class, new Properties());
		
		

	}

}
