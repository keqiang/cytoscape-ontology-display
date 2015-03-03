package edu.umich.med.mbni.lkq.cyontology.internal.task;

import java.util.Collection;
import java.util.List;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;

public class FindCommonChildNodesTask extends AbstractNodeViewTask {

	public FindCommonChildNodesTask(View<CyNode> nodeView, CyNetworkView netView) {
		super(nodeView, netView);
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		List<CyNode> nodes = CyTableUtil.getNodesInState(netView.getModel(), "selected", true);
		
		OntologyNetwork ontologyNetwork = MyApplicationCenter.getInstance().getOntologyNetworkFromUnderlyingCyNetwork(netView.getModel());
		if (ontologyNetwork == null) return;
		
		List<ExpandableNode> correspondingExpandableNodes = ontologyNetwork.getCorrespondingExpandableNodes(nodes);
		
		Collection<ExpandableNode> commonNodes = ontologyNetwork.findCommonNodes(correspondingExpandableNodes);
		
		for (ExpandableNode node : commonNodes) {
			if (netView.getNodeView(node.getCyNode()).getVisualProperty(
					BasicVisualLexicon.NODE_VISIBLE)) {
				netView.getModel().getRow(node.getCyNode())
						.set("selected", true);
			}
		}
		
		for (ExpandableNode node : correspondingExpandableNodes) {
			if (netView.getNodeView(node.getCyNode()).getVisualProperty(
					BasicVisualLexicon.NODE_VISIBLE)) {
				netView.getModel().getRow(node.getCyNode())
						.set("selected", false);
			}
		}
	}

}
