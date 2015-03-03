package edu.umich.med.mbni.lkq.cyontology.internal.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork_old;

public class OntologyNetworkUtils {

	public static final String INTERACTION_IS_A = "is_a";
	public static final String INTERACTION_HAS_PART = "has_part";
	public static final String INTERACTION_PART_OF = "part_of";
	public static final String INTERACTION_REGULATES = "regulates";
	public static final String INTERACTION_NEGATIVELY_REGULATES = "negatively_regulates";
	public static final String INTERACTION_POSITIVELY_REGULATES = "positively_regulates";
	public static final String INTERACTION_OCCURS_IN = "occurs_in";
	public static final String INTERACTION_HAPPENS_DURING = "happens_during";
	
	public static MyApplicationManager appManager = MyApplicationCenter.getInstance().getApplicationManager();

	/**
	 * @param originalCyNetwork
	 *            the original network
	 * @param networkFactory
	 *            network factory used to create a new ontology network
	 * @return the newly created network
	 */
	public static OntologyNetwork_old convertNetworkToOntology(
			CyNetwork originalCyNetwork, LinkedList<DelayedVizProp> vizProps,
			String keepInteraction) {
		
		if (MyApplicationCenter.getInstance().hasOntologyNetworkFromOriginalCyNetwork(
				originalCyNetwork)) {
			MyApplicationCenter.getInstance().removeOntologyNetworkByOriginalNetwork(originalCyNetwork);
		}
				
		String underlyingNetworkName = originalCyNetwork.getRow(originalCyNetwork).get(
				CyNetwork.NAME, String.class);
		
		if (!underlyingNetworkName.endsWith("Ontology View")) {
			underlyingNetworkName += " Ontology View";
			originalCyNetwork.getRow(originalCyNetwork).set(CyNetwork.NAME,
					underlyingNetworkName);
		}		

		List<CyNode> allNodes = originalCyNetwork.getNodeList();
		
		HashMap<Long, ExpandableNode> createdNodes = new HashMap<Long, ExpandableNode>();
		HashSet<Long> allRootNodes = new HashSet<Long>();
		
		for (CyNode node : allNodes) {
			String nodeName = originalCyNetwork.getRow(node).get(
					CyNetwork.NAME, String.class);
			ExpandableNode expandableNode = new ExpandableNode(node, nodeName);
			allRootNodes.add(node.getSUID());
			createdNodes.put(node.getSUID(), expandableNode);
		}

		for (CyNode sourceNode : allNodes) {
			ExpandableNode sourceExpandableNode = createdNodes.get(sourceNode.getSUID());
			String sourceNodeName = originalCyNetwork.getRow(sourceNode).get(
					CyNetwork.NAME, String.class);

			setNodeProp(sourceNode, vizProps, sourceNodeName);

			List<CyNode> neighborNodes = originalCyNetwork.getNeighborList(
					sourceNode, CyEdge.Type.DIRECTED);

			for (CyNode targetNode : neighborNodes) {
				ExpandableNode targetExpandableNode = createdNodes.get(targetNode.getSUID());

				String targetNodeName = originalCyNetwork.getRow(targetNode)
						.get(CyNetwork.NAME, String.class);
				setNodeProp(targetNode, vizProps, targetNodeName);

				List<CyEdge> allEdges = originalCyNetwork
						.getConnectingEdgeList(sourceNode, targetNode,
								CyEdge.Type.DIRECTED);

				for (CyEdge edge : allEdges) {
					
					String interactionType = originalCyNetwork.getRow(edge)
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
		
		return new OntologyNetwork_old(originalCyNetwork, null, createdNodes, allRootNodes, keepInteraction);
	}
	
	public static OntologyNetwork generateNewOntologyNetwork(
			CyNetwork originalCyNetwork, LinkedList<DelayedVizProp> vizProps,
			String keepInteraction) {
		
		CyNetworkFactory networkFactory = appManager.getCyNetworkFactory();
		CyNetwork underlyingCyNetwork = networkFactory.createNetwork();
		
		String underlyingNetworkName = originalCyNetwork.getRow(originalCyNetwork).get(
				CyNetwork.NAME, String.class);
		underlyingCyNetwork.getRow(underlyingCyNetwork).set(CyNetwork.NAME,
				underlyingNetworkName + " Ontology View");	

		List<CyNode> allNodes = originalCyNetwork.getNodeList();
		
		HashMap<Long, ExpandableNode> createdNodes = new HashMap<Long, ExpandableNode>();
		HashSet<ExpandableNode> allRootNodes = new HashSet<ExpandableNode>();
		
		for (CyNode node : allNodes) {
			String nodeName = originalCyNetwork.getRow(node).get(
					CyNetwork.NAME, String.class);
			CyNode generatedNode = underlyingCyNetwork.addNode();
			underlyingCyNetwork.getRow(generatedNode).set(CyNetwork.NAME, nodeName);
			ExpandableNode expandableNode = new ExpandableNode(generatedNode, nodeName);
			
			allRootNodes.add(expandableNode);
			createdNodes.put(node.getSUID(), expandableNode);
		}

		for (CyNode sourceNode : allNodes) {
			ExpandableNode sourceExpandableNode = createdNodes.get(sourceNode.getSUID());
			String sourceNodeName = originalCyNetwork.getRow(sourceNode).get(
					CyNetwork.NAME, String.class);

			setNodeProp(sourceExpandableNode.getCyNode(), vizProps, sourceNodeName);

			List<CyNode> neighborNodes = originalCyNetwork.getNeighborList(
					sourceNode, CyEdge.Type.DIRECTED);

			for (CyNode targetNode : neighborNodes) {
				ExpandableNode targetExpandableNode = createdNodes.get(targetNode.getSUID());

				String targetNodeName = originalCyNetwork.getRow(targetNode)
						.get(CyNetwork.NAME, String.class);
				setNodeProp(targetExpandableNode.getCyNode(), vizProps, targetNodeName);

				List<CyEdge> allEdges = originalCyNetwork
						.getConnectingEdgeList(sourceNode, targetNode,
								CyEdge.Type.DIRECTED);

				for (CyEdge edge : allEdges) {
					
					String interactionType = originalCyNetwork.getRow(edge)
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
								CyEdge newEdge = underlyingCyNetwork.addEdge(targetExpandableNode.getCyNode(), sourceExpandableNode.getCyNode(), true);
								setEdgeProp(newEdge, vizProps);
								// this node is a child node of some other node, so remove it from the root list.
								allRootNodes.remove(targetExpandableNode);
							}
						}
					} else if (containingInteraction(keepInteraction)) {
						if (edge.getSource() == sourceNode
								&& edge.getTarget() == targetNode) {

							if (!sourceExpandableNode
									.hasChild(targetExpandableNode)) {
								sourceExpandableNode
										.addChildNode(targetExpandableNode);
								CyEdge newEdge = underlyingCyNetwork.addEdge(sourceExpandableNode.getCyNode(), targetExpandableNode.getCyNode(), true);
								setEdgeProp(newEdge, vizProps);
								allRootNodes.remove(targetExpandableNode);

							}
						}
					}
				}
			}
		}
		
		return new OntologyNetwork(originalCyNetwork, underlyingCyNetwork, createdNodes, allRootNodes, keepInteraction);
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
