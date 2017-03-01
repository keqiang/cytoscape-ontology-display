package edu.umich.med.mbni.lkq.cyontology.task;

import java.awt.Color;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.util.ValueToColorUtil;

public class DEBUG_MapDataToColorTask extends AbstractNetworkViewTask {

	public DEBUG_MapDataToColorTask(CyNetworkView view) {
		super(view);
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (view == null)
			return;

		CyNetwork network = view.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationManager.getInstance()
				.getOntologyNetworkFromUnderlyingCyNetwork(network);
		if (ontologyNetwork == null)
			return;

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (CyNode node : network.getNodeList()) {
			if (network.getRow(node).isSet("test_data")) {

				Double nodeValue = network.getRow(node).get("test_data",
						Double.class);
				if (nodeValue == null)
					continue;
				if (nodeValue < min) {
					min = nodeValue;
				}

				if (nodeValue > max) {
					max = nodeValue;
				}
			}
		}

		ValueToColorUtil convertUtil = new ValueToColorUtil(min, max);

		for (ExpandableNode root : ontologyNetwork.getAllNodes()) {
		
			Double rootValue = network.getRow(root.getCyNode()).get("test_data",
					Double.class);
			if (rootValue != null) {
				Color color = convertUtil.convertToColor(rootValue);
				view.getNodeView(root.getCyNode()).setLockedValue(
						BasicVisualLexicon.NODE_FILL_COLOR, color);
			} else {
				view.getNodeView(root.getCyNode()).setLockedValue(
						BasicVisualLexicon.NODE_FILL_COLOR, Color.white);
			}
		}

		view.updateView();
	}
}
