package edu.umich.med.mbni.lkq.cyontology.internal.task;

import java.awt.Color;
import java.util.LinkedList;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.util.AggregationMethodUtil;
import edu.umich.med.mbni.lkq.cyontology.internal.util.ValueToColorUtil;

public class UpdateAggregationTask extends AbstractNetworkViewTask {
	private final String aggregationType;
	private final String columnName;

	private ValueToColorUtil convertUtil;

	public UpdateAggregationTask(CyNetworkView view, String aggregationType,
			final String columnName) {
		super(view);
		this.aggregationType = aggregationType;
		this.columnName = columnName;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (view == null)
			return;

		CyNetwork network = view.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationCenter.getInstance()
				.getOntologyNetworkFromUnderlyingCyNetwork(network);
		if (ontologyNetwork == null)
			return;

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (CyNode node : network.getNodeList()) {
			if (network.getRow(node).isSet(columnName)) {

				Double nodeValue = network.getRow(node).get(columnName,
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

		convertUtil = new ValueToColorUtil(min, max);

		for (ExpandableNode root : ontologyNetwork.getAllRootNodes()) {
			popTreeAggregationValue(root, network);
		}

		view.updateView();
	}

	public Double popTreeAggregationValue(ExpandableNode root, CyNetwork network) {
		Double rootValue = null;

		if (root.getDirectChildNodes().isEmpty()) {
			if (network.getRow(root.getCyNode()).isSet(columnName)) {
				rootValue = network.getRow(root.getCyNode()).get(columnName,
						Double.class);
			}
		} else {
			LinkedList<Double> allChildValues = new LinkedList<Double>();
			for (ExpandableNode child : root.getDirectChildNodes()) {
				Double value = popTreeAggregationValue(child, network);
				if (value != null) {
					allChildValues.add(value);
				}
			}

			rootValue = AggregationMethodUtil.getAggregatedValue(
					allChildValues, aggregationType);
		}

		if (rootValue != null) {
			Color color = convertUtil.convertToColor(rootValue);
			view.getNodeView(root.getCyNode()).setLockedValue(
					BasicVisualLexicon.NODE_FILL_COLOR, color);
		}

		return rootValue;
	}
}
