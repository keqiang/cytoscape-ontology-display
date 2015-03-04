package edu.umich.med.mbni.lkq.cyontology.internal.task;

import java.util.HashSet;
import java.util.List;

import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.swing.DialogTaskManager;

import edu.umich.med.mbni.lkq.cyontology.internal.app.CytoscapeServiceManager;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateOntologyControlPanelTask.UpdateOntologyControlOptions;
import edu.umich.med.mbni.lkq.cyontology.internal.util.DelayedVizProp;
import edu.umich.med.mbni.lkq.cyontology.internal.util.ViewOperationUtils;
import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyPluginPanel;

public class PopulateOntologyNetworkViewTask extends AbstractNetworkViewTask {
	
	private final OntologyNetwork ontologyNetwork;

	public PopulateOntologyNetworkViewTask(CyNetworkView view, OntologyNetwork ontologyNetwork) {
		super(view);
		this.ontologyNetwork = ontologyNetwork;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		
		taskMonitor.setTitle("Generating Ontology Network View");
		
		taskMonitor.setStatusMessage("Generating ontology network view");
		
		CytoscapeServiceManager cytoscapeServiceManager = MyApplicationManager.getInstance().getCytoscapeServiceManager();
	
		List<DelayedVizProp> visualProps = ontologyNetwork.getVisualProps();
		
		if (visualProps != null) {
			DelayedVizProp.applyAll(view, visualProps);
			cytoscapeServiceManager.getCyEventHelper().flushPayloadEvents();
		}

		taskMonitor.setStatusMessage("Relayouting the ontology network");

		HashSet<View<CyNode>> nodesToLayout = new HashSet<View<CyNode>>();

		for (ExpandableNode expandableNode : ontologyNetwork.getAllRootNodes()) {
			expandableNode.collapse();
			ViewOperationUtils.hideSubTree(expandableNode, view);
			if (!expandableNode.getDirectChildNodes().isEmpty()) {
				expandableNode.expandOneLevel();
				nodesToLayout.add(view.getNodeView(expandableNode
						.getCyNode()));
				for (ExpandableNode childNode : expandableNode.getDirectChildNodes()) {
					View<CyNode> nodeView = view.getNodeView(childNode
							.getCyNode());
					nodesToLayout.add(nodeView);
				}
				ViewOperationUtils.showOneLevel(expandableNode, view);
			} else {
				view.getNodeView(expandableNode.getCyNode())
						.setVisualProperty(BasicVisualLexicon.NODE_VISIBLE,
								false);
			}
		}

		ViewOperationUtils.reLayoutNetwork(
				cytoscapeServiceManager.getCyLayoutAlgorithmManager(), view,
				MyApplicationManager.getInstance().getLayoutAlgorithmName(), nodesToLayout);

		CytoPanel cytoPanelWest = MyApplicationManager.getInstance()
				.getCytoscapeServiceManager().getCyDesktopService()
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

		UpdateOntologyControlOptions options = new UpdateOntologyControlOptions(true, true, true, ontologyNetwork.getInteractionType());
		
		UpdateOntologyControlPanelTaskFactory updateOntologyControlPanelTaskFactory = new UpdateOntologyControlPanelTaskFactory(ontologyViewerControlPanel, options);
		DialogTaskManager taskManager = cytoscapeServiceManager.getTaskManager();
		taskManager.execute(updateOntologyControlPanelTaskFactory
				.createTaskIterator(view.getModel()));
		
		view.updateView();
		cytoscapeServiceManager.getCyEventHelper().flushPayloadEvents();

	}

}
