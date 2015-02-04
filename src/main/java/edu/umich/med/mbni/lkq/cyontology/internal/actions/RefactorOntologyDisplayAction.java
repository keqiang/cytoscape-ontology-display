package edu.umich.med.mbni.lkq.cyontology.internal.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.DelayedVizProp;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.OntologyNetworkUtils;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.ViewOperationUtils;
import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyViewerControlPanel;

public class RefactorOntologyDisplayAction extends AbstractCyAction {

	private static final long serialVersionUID = 7170161875226705765L;
	private MyApplicationManager appManager;
	private String curLayoutName;

	public RefactorOntologyDisplayAction(String layoutName) {
		super("Create collapsable and expandable ontology network");
		appManager = MyApplicationCenter.getInstance().getApplicationManager();
		curLayoutName = layoutName;
		setPreferredMenu("Apps.Ontology Viewer");
	}

	public void actionPerformed(ActionEvent e) {

		CyNetwork underlyingNetwork = appManager.getCyApplicationManager()
				.getCurrentNetwork();

		OntologyNetwork generatedOntologyNetwork;
		
		if (!MyApplicationCenter.getInstance().hasEncapsulatingOntologyNetwork(
				underlyingNetwork)) {
			
			Collection<CyNetworkView> networkViews = appManager.getCyNetworkViewManager().getNetworkViews(underlyingNetwork);
			CyNetworkView networkView;
			
			if (networkViews.isEmpty()) {
				networkView = appManager.getCyNetworkViewFactory().createNetworkView(underlyingNetwork);
				appManager.getCyNetworkViewManager().addNetworkView(networkView);
			} else {
				networkView = networkViews.iterator().next();
			}

			LinkedList<DelayedVizProp> vizProps = new LinkedList<DelayedVizProp>();
			
			for (CyEdge edge : underlyingNetwork.getEdgeList()) {
				DelayedVizProp vizProp = new DelayedVizProp(edge,
						BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE,
						ArrowShapeVisualProperty.NONE, true);
				vizProps.add(vizProp);
				vizProp = new DelayedVizProp(edge,
						BasicVisualLexicon.EDGE_WIDTH,
						1.0, true);
				vizProps.add(vizProp);
				vizProp = new DelayedVizProp(edge,
						BasicVisualLexicon.EDGE_LINE_TYPE,
						LineTypeVisualProperty.LONG_DASH, true);
				vizProps.add(vizProp);
				vizProp = new DelayedVizProp(edge,
						BasicVisualLexicon.EDGE_TRANSPARENCY,
						120, true);
				vizProps.add(vizProp);
			}
			
			appManager.getCyEventHelper().flushPayloadEvents();
			DelayedVizProp.applyAll(networkView, vizProps);
			
			vizProps.clear();
			
			generatedOntologyNetwork = OntologyNetworkUtils
					.convertNetworkToOntology(appManager
							.getCyApplicationManager().getCurrentNetwork(),
							vizProps, OntologyNetworkUtils.INTERACTION_IS_A);
			
			MyApplicationCenter.getInstance().addOntologyNetwork(
					generatedOntologyNetwork);
						
			appManager.getCyEventHelper().flushPayloadEvents();
			DelayedVizProp.applyAll(networkView, vizProps);
			
			ViewOperationUtils.reLayoutNetwork(
					appManager.getCyLayoutAlgorithmManager(), networkView,
					curLayoutName);

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

		} else {
			generatedOntologyNetwork = MyApplicationCenter.getInstance()
					.getEncapsulatingOntologyNetwork(underlyingNetwork);
		}

	}
}
