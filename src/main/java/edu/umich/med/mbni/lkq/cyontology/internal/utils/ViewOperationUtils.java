package edu.umich.med.mbni.lkq.cyontology.internal.utils;

import java.util.Collection;
import java.util.LinkedList;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskIterator;

import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.task.HeadlessTaskMonitor;

/**
 * @author keqiangli an utility class to perform operations on the node and edge
 *         views
 *
 */
public class ViewOperationUtils {

	/**
	 * @param nodes
	 *            nodes to hide or to show
	 * @param networkView
	 *            the network view contains these nodes
	 * @param visible
	 *            flag variable indicates whether to hide or show the nodes
	 */
	public static void setVisibleNodes(Collection<CyNode> nodes,
			CyNetworkView networkView, boolean visible) {
		if (networkView == null)
			return;

		final CyNetwork network = networkView.getModel();

		for (CyNode node : nodes) {
			networkView.getNodeView(node).setVisualProperty(
					BasicVisualLexicon.NODE_VISIBLE, visible);
			for (CyNode neighborNode : network.getNeighborList(node,
					CyEdge.Type.ANY)) {
				for (CyEdge edge : network.getConnectingEdgeList(node,
						neighborNode, CyEdge.Type.ANY)) {
					networkView.getEdgeView(edge).setVisualProperty(
							BasicVisualLexicon.EDGE_VISIBLE, visible);
				}
			}
		}

		networkView.updateView();
	}

	/**
	 * @param edges
	 *            edges to hide or to show
	 * @param networkView
	 *            the network view contains these edges
	 * @param visible
	 *            flag variable indicates whether to hide or show the edges
	 */
	public static void setVisibleEdges(Collection<CyEdge> edges,
			CyNetworkView networkView, boolean visible) {
		for (CyEdge edge : edges) {
			networkView.getEdgeView(edge).setVisualProperty(
					BasicVisualLexicon.EDGE_VISIBLE, visible);
		}

		networkView.updateView();
	}

	/**
	 * @param node1
	 *            source node
	 * @param node2
	 *            target node
	 * @param networkView
	 *            the network view contains both nodes
	 * @param visible
	 *            flag variable indicates whether to hide or show the edges
	 *            between these two nodes
	 */
	public static void setEdgeVisibleBetweenNodes(CyNode node1, CyNode node2,
			CyNetworkView networkView, boolean visible) {

		CyNetwork network = networkView.getModel();
		setVisibleEdges(
				network.getConnectingEdgeList(node1, node2, CyEdge.Type.ANY),
				networkView, visible);

	}

	/**
	 * @param nodes
	 * @param networkView
	 * @param isExpanding
	 */
	public static void updateOntologyNetworkView(
			Collection<ExpandableNode> nodes, CyNetworkView networkView,
			boolean isExpanding) {
		LinkedList<CyNode> nodesToChange = new LinkedList<CyNode>();

		if (isExpanding) {
			for (ExpandableNode node : nodes) {
				if (node.isVisible()) {
					nodesToChange.add(node.getCyNode());
				}
			}

		} else {
			for (ExpandableNode node : nodes) {
				if (!node.isVisible()) {
					nodesToChange.add(node.getCyNode());
				}
			}
		}

		setVisibleNodes(nodesToChange, networkView, isExpanding);
	}

	public static void reLayoutNetwork(
			CyLayoutAlgorithmManager layoutAlgorithmManager,
			CyNetworkView networkView, String layoutAlgorithmName) {

		final CyLayoutAlgorithm layout = layoutAlgorithmManager
				.getLayout(layoutAlgorithmName);
		if (layout == null) {
			return;
		}

		final TaskIterator itr = layout.createTaskIterator(networkView,
				layout.getDefaultLayoutContext(),
				CyLayoutAlgorithm.ALL_NODE_VIEWS, "");

		try {
			itr.next().run(new HeadlessTaskMonitor());
		} catch (Exception e) {
			return;
		}

	}
}
