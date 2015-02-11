package edu.umich.med.mbni.lkq.cyontology.internal.task;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.DelayedVizProp;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.OntologyNetworkUtils;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.ViewOperationUtils;
import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyViewerControlPanel;

public class PopulateOntologyNetworkTask extends AbstractNetworkTask {

	private String interactionType;

	public PopulateOntologyNetworkTask(CyNetwork network, String interactionType) {
		super(network);
		this.interactionType = interactionType;
	}

	@Override
	public void run(TaskMonitor taskMonitor) {

		taskMonitor.setTitle("Generating Ontology Network");
		taskMonitor.setStatusMessage("cleaning up old ontology network");

		MyApplicationManager appManager = MyApplicationCenter.getInstance()
				.getApplicationManager();

		if (MyApplicationCenter.getInstance().hasEncapsulatingOntologyNetwork(
				network)) {
			MyApplicationCenter.getInstance().removeOntologyNetwork(
					network);
		}

		LinkedList<DelayedVizProp> edgeVizProps = new LinkedList<DelayedVizProp>();

		for (CyEdge edge : network.getEdgeList()) {
			DelayedVizProp vizProp = new DelayedVizProp(edge,
					BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE,
					ArrowShapeVisualProperty.NONE, true);
			edgeVizProps.add(vizProp);
			vizProp = new DelayedVizProp(edge, BasicVisualLexicon.EDGE_WIDTH,
					1.0, true);
			edgeVizProps.add(vizProp);
			vizProp = new DelayedVizProp(edge,
					BasicVisualLexicon.EDGE_LINE_TYPE,
					LineTypeVisualProperty.LONG_DASH, true);
			edgeVizProps.add(vizProp);
			vizProp = new DelayedVizProp(edge,
					BasicVisualLexicon.EDGE_TRANSPARENCY, 120, true);
			edgeVizProps.add(vizProp);
		}
		
		LinkedList<DelayedVizProp> otherVizProps = new LinkedList<DelayedVizProp>();
		
		taskMonitor.setStatusMessage("populating all ontology items");
		
		OntologyNetwork generatedOntologyNetwork = OntologyNetworkUtils
				.convertNetworkToOntology(appManager.getCyApplicationManager()
						.getCurrentNetwork(), otherVizProps, interactionType);

		MyApplicationCenter.getInstance().addOntologyNetwork(
				generatedOntologyNetwork);
		
		Collection<CyNetworkView> networkViews = appManager
				.getCyNetworkViewManager().getNetworkViews(network);
		CyNetworkView networkView;

		if (networkViews.isEmpty()) {
			networkView = appManager.getCyNetworkViewFactory()
					.createNetworkView(network);
			appManager.getCyNetworkViewManager().addNetworkView(networkView);
		} else {
			networkView = networkViews.iterator().next();
		}

		appManager.getCyEventHelper().flushPayloadEvents();
		DelayedVizProp.applyAll(networkView, edgeVizProps);
		DelayedVizProp.applyAll(networkView, otherVizProps);

		taskMonitor.setStatusMessage("relayouting the ontology network");

		HashSet<View<CyNode>> nodesToLayout = new HashSet<View<CyNode>>();

		for (Long nodeSUID : generatedOntologyNetwork.getAllRootNodes()) {
			ExpandableNode expandableNode = generatedOntologyNetwork
					.getNode(nodeSUID);
			expandableNode.collapse();
			ViewOperationUtils.hideSubTree(expandableNode, networkView);
			if (!expandableNode.getChildNodes().isEmpty()) {
				expandableNode.expandOneLevel();
				nodesToLayout.add(networkView.getNodeView(expandableNode
						.getCyNode()));
				for (ExpandableNode childNode : expandableNode.getChildNodes()) {
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
				"hierarchical", nodesToLayout);

		CytoPanel cytoPanelWest = MyApplicationCenter.getInstance()
				.getApplicationManager().getCyDesktopService()
				.getCytoPanel(CytoPanelName.WEST);

		if (cytoPanelWest.getState() == CytoPanelState.HIDE) {
			cytoPanelWest.setState(CytoPanelState.DOCK);
		}

		int index = cytoPanelWest
				.indexOfComponent(OntologyViewerControlPanel.CONTROL_PANEL_TITLE);

		if (index == -1)
			return;

		OntologyViewerControlPanel ontologyViewerControlPanel = (OntologyViewerControlPanel) cytoPanelWest
				.getComponentAt(index);

		cytoPanelWest.setSelectedIndex(index);

		ontologyViewerControlPanel.rePopTheAggregationValues();

	}

}
