package edu.umich.med.mbni.lkq.cyontology.internal.task;

import java.util.Collection;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;

public class SelectDirectChildNodeTask extends AbstractNodeViewTask {

	public SelectDirectChildNodeTask(View<CyNode> nodeView,
			CyNetworkView netView) {
		super(nodeView, netView);
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (!MyApplicationManager.getInstance().hasOntologyNetworkFromUnderlyingCyNetwork(
				netView.getModel()))
			return;

		taskMonitor.setProgress(0.0);

		CyNetwork underlyingNetwork = netView.getModel();

		OntologyNetwork ontologyNetwork = MyApplicationManager.getInstance()
				.getOntologyNetworkFromUnderlyingCyNetwork(underlyingNetwork);
		ExpandableNode expandableNode = ontologyNetwork.getNodeFromUnderlyingNode(nodeView
				.getModel());

		Collection<ExpandableNode> allChildNodes = expandableNode
				.getDirectChildNodes();

		taskMonitor.setProgress(0.3);

		for (ExpandableNode node : allChildNodes) {
			if (netView.getNodeView(node.getCyNode()).getVisualProperty(
					BasicVisualLexicon.NODE_VISIBLE)) {
				netView.getModel().getRow(node.getCyNode())
						.set("selected", true);
			}
		}

		taskMonitor.setProgress(0.8);

		netView.updateView();
		taskMonitor.setProgress(1.0);

	}

}
