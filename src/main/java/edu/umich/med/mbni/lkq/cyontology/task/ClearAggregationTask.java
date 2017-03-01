package edu.umich.med.mbni.lkq.cyontology.task;

import java.awt.Color;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.app.MyApplicationManager;

public class ClearAggregationTask extends AbstractNetworkViewTask {

	public ClearAggregationTask(CyNetworkView view) {
		super(view);
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		MyApplicationManager appManager = MyApplicationManager.getInstance();
		CyNetwork underlyingNetwork = view.getModel();
		if (appManager.hasOntologyNetworkFromUnderlyingCyNetwork(underlyingNetwork)) {
			for (CyNode node : underlyingNetwork.getNodeList()) {
				view.getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_FILL_COLOR, Color.WHITE);
			}
		}
		view.updateView();
	}

}
