package edu.umich.med.mbni.lkq.cyontology.internal.util;

import java.util.Collection;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;

/**
 * @author keqiangli an utility class to perform operations on the node and edge
 *         views
 *
 */
public class ViewOperationUtils {

	public static void showSubTree(ExpandableNode rootNode,
			CyNetworkView networkView) {
		for (ExpandableNode childNode : rootNode.getDirectChildNodes()) {
			try {
				networkView.getNodeView(childNode.getCyNode()).setVisualProperty(
						BasicVisualLexicon.NODE_VISIBLE, true);
				setEdgeVisibleBetweenNodes(rootNode.getCyNode(), childNode.getCyNode(), networkView, true);
				showSubTree(childNode, networkView);
			} catch (Exception e) {
				
			}
		}
	}

	public static void hideSubTree(ExpandableNode rootNode,
			CyNetworkView networkView) {
		for (ExpandableNode childNode : rootNode.getDirectChildNodes()) {
			if (!childNode.isReferred()) {
				networkView.getNodeView(childNode.getCyNode())
						.setVisualProperty(BasicVisualLexicon.NODE_VISIBLE,
								false);
			} else {
				setEdgeVisibleBetweenNodes(rootNode.getCyNode(), childNode.getCyNode(), networkView, false);
			}
			hideSubTree(childNode, networkView);
		}
	}

	public static void showOneLevel(ExpandableNode rootNode,
			CyNetworkView networkView) {
		for (ExpandableNode childNode : rootNode.getDirectChildNodes()) {
			try {
				boolean visible = networkView.getNodeView(childNode.getCyNode()).getVisualProperty(BasicVisualLexicon.NODE_VISIBLE);
				if (!visible) {
					networkView.getNodeView(childNode.getCyNode()).setVisualProperty(
							BasicVisualLexicon.NODE_VISIBLE, true);
				}
				setEdgeVisibleBetweenNodes(rootNode.getCyNode(), childNode.getCyNode(), networkView, true);
			} catch (Exception e) {
				
			}
		}

	}

	/**
	 * @param nodes
	 *            nodes to hide or to show
	 * @param networkView
	 *            the network view contains these nodes
	 * @param visible
	 *            flag variable indicates whether to hide or show the nodes
	 */
	// public static void setVisibleNodes(Collection<CyNode> nodes,
	// CyNetworkView networkView, boolean visible) {
	// if (networkView == null)
	// return;
	//
	// final CyNetwork network = networkView.getModel();
	//
	// for (CyNode node : nodes) {
	// networkView.getNodeView(node).setVisualProperty(
	// BasicVisualLexicon.NODE_VISIBLE, visible);
	// for (CyNode neighborNode : network.getNeighborList(node,
	// CyEdge.Type.ANY)) {
	// for (CyEdge edge : network.getConnectingEdgeList(node,
	// neighborNode, CyEdge.Type.ANY)) {
	// networkView.getEdgeView(edge).setVisualProperty(
	// BasicVisualLexicon.EDGE_VISIBLE, visible);
	// }
	// }
	// }
	//
	// networkView.updateView();
	// }

	/**
	 * @param edges
	 *            edges to hide or to show
	 * @param networkView
	 *            the network view contains these edges
	 * @param visible
	 *            flag variable indicates whether to hide or show the edges
	 */
	private static void setVisibleEdges(Collection<CyEdge> edges,
			CyNetworkView networkView, boolean visible) {
		for (CyEdge edge : edges) {
			networkView.getEdgeView(edge).setVisualProperty(
					BasicVisualLexicon.EDGE_VISIBLE, visible);
		}
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

	public static void reLayoutNetwork(
			CyLayoutAlgorithmManager layoutAlgorithmManager,
			CyNetworkView networkView, String layoutAlgorithmName, Set<View<CyNode>> nodesToLayout) {

		DialogTaskManager taskManager = MyApplicationCenter.getInstance()
				.getApplicationManager().getTaskManager();

		final CyLayoutAlgorithm layout = layoutAlgorithmManager
				.getLayout(layoutAlgorithmName);
		if (layout == null) {
			return;
		}

		final TaskIterator itr = layout.createTaskIterator(networkView,
				layout.getDefaultLayoutContext(),
				nodesToLayout, "");
		taskManager.execute(itr);

	}

}
