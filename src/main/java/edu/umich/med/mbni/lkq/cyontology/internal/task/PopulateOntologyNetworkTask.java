package edu.umich.med.mbni.lkq.cyontology.internal.task;

import java.util.Collection;
import java.util.LinkedList;

import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.DelayedVizProp;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.OntologyNetworkUtils;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.ViewOperationUtils;
import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyViewerControlPanel;

public class PopulateOntologyNetworkTask extends AbstractNetworkTask {

	private CyNetwork underlyingNetwork;
	private String interactionType;

	public PopulateOntologyNetworkTask(CyNetwork network, String interactionType) {
		super(network);
		this.underlyingNetwork = network;
		this.interactionType = interactionType;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {

		taskMonitor.setTitle("Generating Ontology Network");
		taskMonitor.setStatusMessage("cleaning up old ontology network");

		MyApplicationManager appManager = MyApplicationCenter.getInstance()
				.getApplicationManager();

		OntologyNetwork generatedOntologyNetwork;

		if (MyApplicationCenter.getInstance().hasEncapsulatingOntologyNetwork(
				underlyingNetwork)) {

			MyApplicationCenter.getInstance().removeOntologyNetwork(
					underlyingNetwork);
		}

		Collection<CyNetworkView> networkViews = appManager
				.getCyNetworkViewManager().getNetworkViews(underlyingNetwork);
		CyNetworkView networkView;

		if (networkViews.isEmpty()) {
			networkView = appManager.getCyNetworkViewFactory()
					.createNetworkView(underlyingNetwork);
			appManager.getCyNetworkViewManager().addNetworkView(networkView);
		} else {
			networkView = networkViews.iterator().next();
		}

		taskMonitor.setProgress(0.1);

		LinkedList<DelayedVizProp> vizProps = new LinkedList<DelayedVizProp>();

		for (CyEdge edge : underlyingNetwork.getEdgeList()) {
			DelayedVizProp vizProp = new DelayedVizProp(edge,
					BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE,
					ArrowShapeVisualProperty.NONE, true);
			vizProps.add(vizProp);
			vizProp = new DelayedVizProp(edge, BasicVisualLexicon.EDGE_WIDTH,
					1.0, true);
			vizProps.add(vizProp);
			vizProp = new DelayedVizProp(edge,
					BasicVisualLexicon.EDGE_LINE_TYPE,
					LineTypeVisualProperty.LONG_DASH, true);
			vizProps.add(vizProp);
			vizProp = new DelayedVizProp(edge,
					BasicVisualLexicon.EDGE_TRANSPARENCY, 120, true);
			vizProps.add(vizProp);
		}

		appManager.getCyEventHelper().flushPayloadEvents();
		DelayedVizProp.applyAll(networkView, vizProps);

		taskMonitor.setProgress(0.3);

		vizProps.clear();

		taskMonitor.setStatusMessage("populating all ontology items");
		generatedOntologyNetwork = OntologyNetworkUtils
				.convertNetworkToOntology(appManager.getCyApplicationManager()
						.getCurrentNetwork(), vizProps, interactionType);

		taskMonitor.setProgress(0.8);

		MyApplicationCenter.getInstance().addOntologyNetwork(
				generatedOntologyNetwork);

		appManager.getCyEventHelper().flushPayloadEvents();
		DelayedVizProp.applyAll(networkView, vizProps);

		taskMonitor.setStatusMessage("relayouting the ontology network");
		ViewOperationUtils.reLayoutNetwork(
				appManager.getCyLayoutAlgorithmManager(), networkView,
				"grid");

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

		taskMonitor.setProgress(1.0);

	}

}
