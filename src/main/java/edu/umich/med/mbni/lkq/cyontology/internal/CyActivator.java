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
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

import edu.umich.med.mbni.lkq.cyontology.internal.action.DEBUG_GenerateFakeNodeData;
import edu.umich.med.mbni.lkq.cyontology.internal.action.GenerateOntologyNetworkAction;
import edu.umich.med.mbni.lkq.cyontology.internal.action.OntologyControlPanelAction;
import edu.umich.med.mbni.lkq.cyontology.internal.action.ReactomePathwayAction;
import edu.umich.med.mbni.lkq.cyontology.internal.app.CytoscapeServiceManager;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.controller.OntologyPanelController;
import edu.umich.med.mbni.lkq.cyontology.internal.task.ExpandableNodeCollapseTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.ExpandableNodeExpandOneLevelTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.FindCommonChildNodesTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.SearchGeneIdBelongToSelectedGoItemInOtherNetworkTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.SelectChildNodeTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.SelectDirectChildNodeTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.util.PlugInObjectManager;
import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyPluginPanel;

public class CyActivator extends AbstractCyActivator {

	@Override
	public void start(BundleContext context) throws Exception {

		PlugInObjectManager.getManager().setBundleContext(context);

		ReactomePathwayAction pathwayLoadAction = new ReactomePathwayAction();

		registerService(context, pathwayLoadAction, CyAction.class,
				new Properties());
		
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

		DialogTaskManager taskManager = getService(context,
				DialogTaskManager.class);

		/*
		 * register these services to this App, my application manager is the
		 * central point to get the cytoscape services
		 */
		CytoscapeServiceManager myApplicationManager = new CytoscapeServiceManager(
				cytoscapeDesktopService, applicationManager, networkFactory,
				networkManager, networkViewFactory, networkViewManager, vmMgr,
				algorithmManager, eventHelper, undoSupport, taskManager);

		MyApplicationManager appCenter = MyApplicationManager.getInstance();
		MyApplicationManager.registerApplicationManager(myApplicationManager);

		appCenter.setLayoutAlgorithmName("hierarchical");
		/*
		 * register all the services this App provides
		 */

		GenerateOntologyNetworkAction generateOntologyNetworkWithOneInteractionAction = new GenerateOntologyNetworkAction(
				"Create ontology network with one interaction", false);
		registerService(context,
				generateOntologyNetworkWithOneInteractionAction,
				CyAction.class, new Properties());

		GenerateOntologyNetworkAction generateOntologyNetworkRetainOtherInteraction = new GenerateOntologyNetworkAction(
				"Create ontology network retain other interaction", true);
		registerService(context, generateOntologyNetworkRetainOtherInteraction,
				CyAction.class, new Properties());

		registerService(context, appCenter,
				NetworkAboutToBeDestroyedListener.class, new Properties());

		Properties myNodeViewTaskFactoryProps = new Properties();

		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.TITLE,
				"Collpase this ontology term");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.PREFERRED_MENU, "Ontology Viewer");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.ENABLE_FOR, "selectedNodesOrEdges");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.MENU_GRAVITY, "1.0");
		ExpandableNodeCollapseTaskFactory expandableNodeCollapseTaskFactory = new ExpandableNodeCollapseTaskFactory();
		registerService(context, expandableNodeCollapseTaskFactory,
				NodeViewTaskFactory.class, myNodeViewTaskFactoryProps);
		
		myNodeViewTaskFactoryProps = new Properties();
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.TITLE,
				"Expand this ontology term");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.PREFERRED_MENU, "Ontology Viewer");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.ENABLE_FOR, "selectedNodesOrEdges");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.MENU_GRAVITY, "2.0");
		ExpandableNodeExpandOneLevelTaskFactory expandableNodeExpandOneLevelTaskFactory = new ExpandableNodeExpandOneLevelTaskFactory();
		registerService(context, expandableNodeExpandOneLevelTaskFactory,
				NodeViewTaskFactory.class, myNodeViewTaskFactoryProps);

		myNodeViewTaskFactoryProps = new Properties();
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.TITLE,
				"Select child nodes in common");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.PREFERRED_MENU, "Ontology Viewer");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.ENABLE_FOR, "selectedNodesOrEdges");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.MENU_GRAVITY, "3.0");
		FindCommonChildNodesTaskFactory findCommonChildNodesTaskFactory = new FindCommonChildNodesTaskFactory();
		registerService(context, findCommonChildNodesTaskFactory,
				NodeViewTaskFactory.class, myNodeViewTaskFactoryProps);

		myNodeViewTaskFactoryProps = new Properties();
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.TITLE,
				"Select all children ontology items");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.PREFERRED_MENU, "Ontology Viewer");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.ENABLE_FOR, "selectedNodesOrEdges");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.MENU_GRAVITY, "4.0");
		SelectChildNodeTaskFactory selectChildNodeTaskFactory = new SelectChildNodeTaskFactory();
		registerService(context, selectChildNodeTaskFactory,
				NodeViewTaskFactory.class, myNodeViewTaskFactoryProps);

		myNodeViewTaskFactoryProps = new Properties();
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.TITLE,
				"Select direct children ontology items");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.PREFERRED_MENU, "Ontology Viewer");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.ENABLE_FOR, "selectedNodesOrEdges");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.MENU_GRAVITY, "5.0");
		SelectDirectChildNodeTaskFactory selectDirectChildNodeTaskFactory = new SelectDirectChildNodeTaskFactory();
		registerService(context, selectDirectChildNodeTaskFactory,
				NodeViewTaskFactory.class, myNodeViewTaskFactoryProps);

		myNodeViewTaskFactoryProps = new Properties();
		myNodeViewTaskFactoryProps
				.setProperty(ServiceProperties.TITLE,
						"Select in other networks the gene nodes belonging to selected GO iterms");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.PREFERRED_MENU, "Ontology Viewer");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.ENABLE_FOR, "selectedNodesOrEdges");
		myNodeViewTaskFactoryProps.setProperty(ServiceProperties.MENU_GRAVITY, "6.0");
		SearchGeneIdBelongToSelectedGoItemInOtherNetworkTaskFactory findSelectedNodeInOtherNetworkTaskFactory = new SearchGeneIdBelongToSelectedGoItemInOtherNetworkTaskFactory();
		registerService(context, findSelectedNodeInOtherNetworkTaskFactory,
				NodeViewTaskFactory.class, myNodeViewTaskFactoryProps);
		
		OntologyPluginPanel ontologyPluginPanel = new OntologyPluginPanel();
		registerService(context, ontologyPluginPanel, CytoPanelComponent.class,
				new Properties());
		OntologyPanelController ontologyPanelController = new OntologyPanelController(
				ontologyPluginPanel, null);
		MyApplicationManager.getInstance().setOntologyPluginPanelController(
				ontologyPanelController);

		registerService(context, ontologyPanelController,
				RowsSetListener.class, new Properties());

		registerService(context, ontologyPanelController,
				NetworkAboutToBeDestroyedListener.class, new Properties());
		registerService(context, ontologyPanelController,
				NetworkViewAboutToBeDestroyedListener.class, new Properties());

		OntologyControlPanelAction controlPanelAction = new OntologyControlPanelAction(
				cytoscapeDesktopService, ontologyPluginPanel);
		registerService(context, controlPanelAction, CyAction.class,
				new Properties());

		DEBUG_GenerateFakeNodeData generateFakeNodeData = new DEBUG_GenerateFakeNodeData(
				"Generate dummy data");
		registerService(context, generateFakeNodeData, CyAction.class,
				new Properties());

	}

}
