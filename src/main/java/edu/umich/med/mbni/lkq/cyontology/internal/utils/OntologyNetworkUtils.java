package edu.umich.med.mbni.lkq.cyontology.internal.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;

public class OntologyNetworkUtils {

	public static final String INTERACTION_IS_A = "is_a";
	public static final String INTERACTION_HAS_PART = "has_part";
	public static final String INTERACTION_PART_OF = "part_of";
	public static final String INTERACTION_REGULATES = "regulates";
	public static final String INTERACTION_NEGATIVELY_REGULATES = "negatively_regulates";
	public static final String INTERACTION_POSITIVELY_REGULATES = "positively_regulates";
	public static final String INTERACTION_OCCURS_IN = "occurs_in";
	public static final String INTERACTION_HAPPENS_DURING = "happens_during";

	/**
	 * @param underlyingNetwork
	 *            the original network
	 * @param networkFactory
	 *            network factory used to create a new ontology network
	 * @return the newly created network
	 */
	public static OntologyNetwork convertNetworkToOntology(
			CyNetwork underlyingNetwork, LinkedList<DelayedVizProp> vizProps,
			String keepInteraction) {
		
		if (MyApplicationCenter.getInstance().hasEncapsulatingOntologyNetwork(
				underlyingNetwork)) {
			MyApplicationCenter.getInstance().removeOntologyNetwork(underlyingNetwork);
		}
				
		String networkName = underlyingNetwork.getRow(underlyingNetwork).get(
				CyNetwork.NAME, String.class);
		
		if (!networkName.endsWith("Ontology View")) {
			networkName += " Ontology View";
			underlyingNetwork.getRow(underlyingNetwork).set(CyNetwork.NAME,
					networkName);
		}		

		List<CyNode> allNodes = underlyingNetwork.getNodeList();
		
		HashMap<Long, ExpandableNode> createdNodes = new HashMap<Long, ExpandableNode>();
		HashSet<Long> allRootNodes = new HashSet<Long>();
		
		for (CyNode node : allNodes) {
			String nodeName = underlyingNetwork.getRow(node).get(
					CyNetwork.NAME, String.class);
			ExpandableNode expandableNode = new ExpandableNode(node, nodeName);
			allRootNodes.add(node.getSUID());
			createdNodes.put(node.getSUID(), expandableNode);
		}

		for (CyNode sourceNode : allNodes) {
			ExpandableNode sourceExpandableNode = createdNodes.get(sourceNode.getSUID());
			String sourceNodeName = underlyingNetwork.getRow(sourceNode).get(
					CyNetwork.NAME, String.class);

			setNodeProp(sourceNode, vizProps, sourceNodeName);

			List<CyNode> neighborNodes = underlyingNetwork.getNeighborList(
					sourceNode, CyEdge.Type.DIRECTED);

			for (CyNode targetNode : neighborNodes) {
				ExpandableNode targetExpandableNode = createdNodes.get(targetNode.getSUID());

				String targetNodeName = underlyingNetwork.getRow(targetNode)
						.get(CyNetwork.NAME, String.class);
				setNodeProp(targetNode, vizProps, targetNodeName);

				List<CyEdge> allEdges = underlyingNetwork
						.getConnectingEdgeList(sourceNode, targetNode,
								CyEdge.Type.DIRECTED);

				for (CyEdge edge : allEdges) {
					
					String interactionType = underlyingNetwork.getRow(edge)
							.get(CyEdge.INTERACTION, String.class);

					if (!interactionType.equalsIgnoreCase(keepInteraction))
						continue;

					if (containedInteraction(keepInteraction)) {
						if (edge.getSource() == targetNode
								&& edge.getTarget() == sourceNode) {

							if (!sourceExpandableNode
									.hasChild(targetExpandableNode)) {
								sourceExpandableNode
										.addChildNode(targetExpandableNode);
								setEdgeProp(edge, vizProps);
								// this node is a child node of some other node, so remove it from the root list.
								allRootNodes.remove(targetNode.getSUID());
							}
						}
					} else if (containingInteraction(keepInteraction)) {
						if (edge.getSource() == sourceNode
								&& edge.getTarget() == targetNode) {

							if (!sourceExpandableNode
									.hasChild(targetExpandableNode)) {
								sourceExpandableNode
										.addChildNode(targetExpandableNode);
								setEdgeProp(edge, vizProps);
								allRootNodes.remove(targetNode.getSUID());

							}
						}
					}
				}
			}
			
			//sourceExpandableNode.collapse();
		}
		
		return new OntologyNetwork(underlyingNetwork, createdNodes, allRootNodes);
	}

	private static void setEdgeProp(CyEdge connectingEdge,
			LinkedList<DelayedVizProp> vizProps) {
		DelayedVizProp vizProp = new DelayedVizProp(connectingEdge,
				BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE,
				ArrowShapeVisualProperty.DELTA, true);
		vizProps.add(vizProp);

		vizProp = new DelayedVizProp(connectingEdge,
				BasicVisualLexicon.EDGE_WIDTH, 2.0, true);
		vizProps.add(vizProp);
		
		vizProp = new DelayedVizProp(connectingEdge,
				BasicVisualLexicon.EDGE_LINE_TYPE,
				LineTypeVisualProperty.SOLID, true);
		vizProps.add(vizProp);
		vizProp = new DelayedVizProp(connectingEdge,
				BasicVisualLexicon.EDGE_TRANSPARENCY,
				255, true);
		vizProps.add(vizProp);
	}

	private static void setNodeProp(CyNode node,
			LinkedList<DelayedVizProp> vizProps, String nodeName) {

		DelayedVizProp vizProp = new DelayedVizProp(node,
				BasicVisualLexicon.NODE_LABEL, nodeName, true);
		vizProps.add(vizProp);

		vizProp = new DelayedVizProp(node, BasicVisualLexicon.NODE_SHAPE,
				NodeShapeVisualProperty.ELLIPSE, true);
		vizProps.add(vizProp);
		
		vizProp = new DelayedVizProp(node, BasicVisualLexicon.NODE_SIZE, 120.0,
				true);
		vizProps.add(vizProp);
		
		vizProp = new DelayedVizProp(node,
				BasicVisualLexicon.NODE_LABEL_FONT_SIZE, 18, true);
		vizProps.add(vizProp);
		vizProp = new DelayedVizProp(node, BasicVisualLexicon.NODE_FILL_COLOR,
				Color.WHITE, true);
		vizProps.add(vizProp);
		vizProp = new DelayedVizProp(node, BasicVisualLexicon.NODE_LABEL_COLOR,
				Color.BLACK, true);
		vizProps.add(vizProp);
		vizProp = new DelayedVizProp(node,
				BasicVisualLexicon.NODE_BORDER_LINE_TYPE,
				LineTypeVisualProperty.SOLID, true);
		vizProps.add(vizProp);
		
		vizProp = new DelayedVizProp(node,
				BasicVisualLexicon.NODE_BORDER_WIDTH, 3.0, true);
		vizProps.add(vizProp);
	}

	private static boolean containedInteraction(String interactionType) {
		return interactionType.equalsIgnoreCase(INTERACTION_PART_OF)
				|| interactionType.equalsIgnoreCase(INTERACTION_OCCURS_IN)
				|| interactionType.equalsIgnoreCase(INTERACTION_IS_A) || interactionType.equalsIgnoreCase(INTERACTION_HAPPENS_DURING);
	}

	private static boolean containingInteraction(String interactionType) {
		return interactionType.equalsIgnoreCase(INTERACTION_HAS_PART)
				|| interactionType.equalsIgnoreCase(INTERACTION_REGULATES)
				|| interactionType.equalsIgnoreCase(INTERACTION_NEGATIVELY_REGULATES) || interactionType.equalsIgnoreCase(INTERACTION_POSITIVELY_REGULATES);
	}
}
