package edu.umich.med.mbni.lkq.cyontology.internal.task;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.swing.DialogTaskManager;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateOntologyControlPanelTask.UpdateOntologyControlOptions;
import edu.umich.med.mbni.lkq.cyontology.internal.util.DelayedVizProp;
import edu.umich.med.mbni.lkq.cyontology.internal.util.OntologyNetworkUtils;
import edu.umich.med.mbni.lkq.cyontology.internal.util.ViewOperationUtils;
import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyPluginPanel;

public class PopulateNewOntologyNetworkTask extends AbstractNetworkTask  {
	
	// what type of interaction to retain
	private final String interactionType;

	public PopulateNewOntologyNetworkTask(final CyNetwork network, String interactionType) {
		super(network);
		this.interactionType = interactionType;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		
		taskMonitor.setTitle("Generating Ontology Network");
		
		MyApplicationManager appManager = MyApplicationCenter.getInstance()
				.getApplicationManager();
		
		Collection<CyNetworkView> networkViews = appManager
				.getCyNetworkViewManager().getNetworkViews(network);

		if (MyApplicationCenter.getInstance().hasOntologyNetworkFromOriginalCyNetwork(
				network)) {
			OntologyNetwork ontologyNetwork = MyApplicationCenter.getInstance().getOntologyNetworkFromOriginalCyNetwork(network);
			if (interactionType.equals(ontologyNetwork.getInteractionType())) {
				if (!networkViews.isEmpty()) {
					return;
				}
				return; // view has been generated or already existed
			}
			else {
				MyApplicationCenter.getInstance().removeOntologyNetworkByOriginalNetwork(network);
			}			
		}
		

		// generate a new ontology network and view based on the underlying network and interaction type
		
		LinkedList<DelayedVizProp> otherVizProps = new LinkedList<DelayedVizProp>();
		
		taskMonitor.setStatusMessage("populating all ontology items");
		
		OntologyNetwork generatedOntologyNetwork = OntologyNetworkUtils
				.generateNewOntologyNetwork(network, otherVizProps, interactionType);

		MyApplicationCenter.getInstance().addOntologyNetwork(
				generatedOntologyNetwork);
		
		taskMonitor.setStatusMessage("cleaning up old ontology network");
		
		CyNetwork underlyingNetwork = generatedOntologyNetwork.getUnderlyingCyNetwork();
		appManager.getCyNetworkManager().addNetwork(underlyingNetwork);
		
		CyNetworkView networkView = appManager.getCyNetworkViewFactory()
					.createNetworkView(underlyingNetwork);
		appManager.getCyNetworkViewManager().addNetworkView(networkView);
		
		appManager.getCyEventHelper().flushPayloadEvents();
	
		DelayedVizProp.applyAll(networkView, otherVizProps);

		taskMonitor.setStatusMessage("relayouting the ontology network");

		HashSet<View<CyNode>> nodesToLayout = new HashSet<View<CyNode>>();

		for (ExpandableNode expandableNode : generatedOntologyNetwork.getAllRootNodes()) {
			expandableNode.collapse();
			ViewOperationUtils.hideSubTree(expandableNode, networkView);
			if (!expandableNode.getDirectChildNodes().isEmpty()) {
				expandableNode.expandOneLevel();
				nodesToLayout.add(networkView.getNodeView(expandableNode
						.getCyNode()));
				for (ExpandableNode childNode : expandableNode.getDirectChildNodes()) {
					View<CyNode> nodeView = networkView.getNodeView(childNode
							.getCyNode());
					nodesToLayout.add(nodeView);
				}
				ViewOperationUtils.showOneLevel(expandableNode, networkView);
			} else {
				networkView.getNodeView(expandableNode.getCyNode())
						.setVisualProperty(BasicVisualLexicon.NODE_VISIBLE,
								false);
			}
		}

		networkView.updateView();
		appManager.getCyEventHelper().flushPayloadEvents();

		ViewOperationUtils.reLayoutNetwork(
				appManager.getCyLayoutAlgorithmManager(), networkView,
				MyApplicationCenter.getInstance().getLayoutAlgorithmName(), nodesToLayout);

		CytoPanel cytoPanelWest = MyApplicationCenter.getInstance()
				.getApplicationManager().getCyDesktopService()
				.getCytoPanel(CytoPanelName.WEST);

		if (cytoPanelWest.getState() == CytoPanelState.HIDE) {
			cytoPanelWest.setState(CytoPanelState.DOCK);
		}

		int index = cytoPanelWest
				.indexOfComponent(OntologyPluginPanel.ONTOLOGY_PANEL_TITLE);

		if (index == -1)
			return;

		OntologyPluginPanel ontologyViewerControlPanel = (OntologyPluginPanel) cytoPanelWest
				.getComponentAt(index);

		cytoPanelWest.setSelectedIndex(index);

		UpdateOntologyControlOptions options = new UpdateOntologyControlOptions(true, true, true, interactionType);
		
		UpdateOntologyControlPanelTaskFactory updateOntologyControlPanelTaskFactory = new UpdateOntologyControlPanelTaskFactory(ontologyViewerControlPanel, options);
		DialogTaskManager taskManager = appManager.getTaskManager();
		taskManager.execute(updateOntologyControlPanelTaskFactory
				.createTaskIterator(networkView.getModel()));
		
		networkView.updateView();

		
	}

}
