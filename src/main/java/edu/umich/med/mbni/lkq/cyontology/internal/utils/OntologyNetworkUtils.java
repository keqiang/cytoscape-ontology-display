package edu.umich.med.mbni.lkq.cyontology.internal.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
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
	public static final String INTERACTION_OCCURS_IN = "occurs_in";
	public static final String INTERACTION_SUB_ONTOLOGY_ITEM = "sub_ontology_item";

	private static ExpandableNode getExpandableNodeInNetwork(Long nodeSUID,
			Map<Long, ExpandableNode> nodeMap, CyNetwork network) {
		ExpandableNode node = nodeMap.get(nodeSUID);
		if (node == null) {
			node = new ExpandableNode(network);
			nodeMap.put(nodeSUID, node);
		}

		return node;
	}

	/**
	 * @param originNetwork
	 *            the original network
	 * @param networkFactory
	 *            network factory used to create a new ontology network
	 * @return the newly created network
	 */
	public static OntologyNetwork convertNetworkToOntology(
			CyNetwork originNetwork, CyNetworkFactory networkFactory,
			LinkedList<DelayedVizProp> vizProps) {

		if (MyApplicationCenter.getInstance().hasCorrespondingOntologyNetwork(
				originNetwork)) {
			return MyApplicationCenter.getInstance()
					.getCorrespondingOntologyNetwork(originNetwork);
		}

		HashMap<Long, ExpandableNode> createdNodes = new HashMap<Long, ExpandableNode>();
		CyNetwork createdNetwork = networkFactory.createNetwork();

		String networkName = originNetwork.getRow(originNetwork).get(
				CyNetwork.NAME, String.class)
				+ " Ontology View";
		createdNetwork.getRow(createdNetwork).set(CyNetwork.NAME, networkName);

		List<CyNode> allNodes = originNetwork.getNodeList();

		for (CyNode sourceNode : allNodes) {

			Long sourceNodeSUID = sourceNode.getSUID();
			String sourceNodeName = originNetwork.getRow(sourceNode).get(
					CyNetwork.NAME, String.class);
			ExpandableNode sourceExpandableNode = getExpandableNodeInNetwork(
					sourceNodeSUID, createdNodes, createdNetwork);

			setNodeProp(sourceExpandableNode.getCyNode(), sourceNodeName,
					vizProps);

			createdNetwork.getRow(sourceExpandableNode.getCyNode()).set(
					CyNetwork.NAME, sourceNodeName);

			List<CyNode> neighborNodes = originNetwork.getNeighborList(
					sourceNode, CyEdge.Type.DIRECTED);

			for (CyNode targetNode : neighborNodes) {

				Long targetNodeSUID = targetNode.getSUID();
				ExpandableNode targetExpandableNode = getExpandableNodeInNetwork(
						targetNodeSUID, createdNodes, createdNetwork);

				String targetNodeName = originNetwork.getRow(targetNode).get(
						CyNetwork.NAME, String.class);
				setNodeProp(targetExpandableNode.getCyNode(), targetNodeName,
						vizProps);

				if (isParent(sourceNode, targetNode, originNetwork)) {
					if (!sourceExpandableNode.hasChild(targetExpandableNode)) {
						sourceExpandableNode.addChildNode(targetExpandableNode);

						CyEdge connectingEdge = createdNetwork.addEdge(
								targetExpandableNode.getCyNode(),
								sourceExpandableNode.getCyNode(), true);

						setEdgeProp(connectingEdge, targetNodeName, vizProps);

						createdNetwork.getRow(connectingEdge).set(
								CyEdge.INTERACTION, INTERACTION_SUB_ONTOLOGY_ITEM);

						createdNetwork.getRow(targetExpandableNode.getCyNode())
								.set(CyNetwork.NAME, targetNodeName);
					}

				}

			}
		}

		return new OntologyNetwork(originNetwork, createdNetwork, createdNodes);
	}

	private static void setEdgeProp(CyEdge connectingEdge,
			String targetNodeName, LinkedList<DelayedVizProp> vizProps) {
		DelayedVizProp vizProp = new DelayedVizProp(connectingEdge,
				BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE,
				ArrowShapeVisualProperty.DELTA, true);
		vizProps.add(vizProp);

		vizProp = new DelayedVizProp(connectingEdge,
				BasicVisualLexicon.EDGE_WIDTH, 1.0, true);
		vizProps.add(vizProp);
	}

	private static void setNodeProp(CyNode node, String name,
			LinkedList<DelayedVizProp> vizProps) {
		DelayedVizProp vizProp = new DelayedVizProp(node,
				BasicVisualLexicon.NODE_LABEL, name, true);
		vizProps.add(vizProp);
		vizProp = new DelayedVizProp(node, BasicVisualLexicon.NODE_SHAPE,
				NodeShapeVisualProperty.ELLIPSE, true);
		vizProps.add(vizProp);
		vizProp = new DelayedVizProp(node, BasicVisualLexicon.NODE_WIDTH, 50.0,
				true);
		vizProps.add(vizProp);
		vizProp = new DelayedVizProp(node, BasicVisualLexicon.NODE_HEIGHT,
				50.0, true);
		vizProps.add(vizProp);
		vizProp = new DelayedVizProp(node,
				BasicVisualLexicon.NODE_LABEL_FONT_SIZE, 7, true);
		vizProps.add(vizProp);
		vizProp = new DelayedVizProp(node, BasicVisualLexicon.NODE_FILL_COLOR,
				Color.RED, true);
		vizProps.add(vizProp);
		vizProp = new DelayedVizProp(node, BasicVisualLexicon.NODE_LABEL_COLOR,
				Color.BLACK, true);
		vizProps.add(vizProp);
		vizProp = new DelayedVizProp(node,
				BasicVisualLexicon.NODE_BORDER_LINE_TYPE,
				LineTypeVisualProperty.SOLID, true);
		vizProps.add(vizProp);

	}

	public static boolean isA(String interaction)
			throws IllegalArgumentException {
		switch (interaction) {
		case INTERACTION_IS_A:
		case INTERACTION_PART_OF:
		case INTERACTION_OCCURS_IN:
		case INTERACTION_SUB_ONTOLOGY_ITEM:
			return true;
		case INTERACTION_HAS_PART:
		case INTERACTION_REGULATES:
		case INTERACTION_NEGATIVELY_REGULATES:
			return false;
		default:
			throw new IllegalArgumentException();
		}
	}

	public static boolean isParent(CyNode parentNode, CyNode childNode,
			CyNetwork network) {
		List<CyEdge> allEdges = network.getConnectingEdgeList(parentNode,
				childNode, CyEdge.Type.DIRECTED);
		for (CyEdge edge : allEdges) {
			String interactionType = network.getRow(edge).get(
					CyEdge.INTERACTION, String.class);
			try {
				return isA(interactionType) ? edge.getSource() == childNode
						: edge.getSource() == parentNode;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * @author Keqiang Li.
	 * @param propType
	 *            to which property the settings will be applied
	 * @param in
	 *            the input stream from the OBO file
	 * @return the imported OBO file as a tree structured data object
	 *
	 */
	// public static void importOntologyAsNetworkFromOBOFile(CyNetworkFactory
	// networkFactory, InputStream in) {
	//
	// CyNetwork ontologyNetwork = networkFactory.createNetwork();
	// HashMap<String ,CyNode> existingNodes = new HashMap<String, CyNode>();
	//
	// BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	//
	// String line;
	// String currentChildTerm = null;
	//
	// CyNode newNode = null;
	//
	// try {
	// while ((line = reader.readLine()) != null) {
	// if (line.startsWith("[Term]")) {
	// newNode = null;
	// } else {
	// if (newNode == null) newNode = ontologyNetwork.addNode();
	// int index = line.indexOf(":");
	// if (line.startsWith("id")) {
	// currentChildTerm = line.substring(index + 1).trim();
	// ontologyNetwork.getRow(newNode).set("id", currentChildTerm);
	// existingNodes.put(currentChildTerm, newNode);
	// } else if (line.startsWith(CyNetwork.NAME)) {
	// String uniqueName = line.substring(index + 1).trim();
	// ontologyNetwork.getRow(newNode).set(CyNetwork.NAME, uniqueName);
	// } else if (line.startsWith("is_a")) {
	// line = line.substring(index + 1);
	// currentGroup.setParent(line);
	// groupSetting.addGroupBranch(line, currentChildTerm);
	// }
	// }
	// }
	// reader.close();
	// } catch (Exception e) {
	// return null;
	// }
	//
	// if (currentGroup != null) {
	// groupSetting.addGroup(currentGroup);
	// }
	// groupSetting.validate();
	// return groupSetting;
	// }

}
