package edu.umich.med.mbni.lkq.cyontology.internal.util;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;

import edu.umich.med.mbni.lkq.cyontology.internal.app.CytoscapeServiceManager;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
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
	 * @param originalCyNetwork
	 *            the original network
	 * @param keepInteraction
	 *            network factory used to create a new ontology network
	 * @param retainOtherInteraction
	 *            indicates if the newly generated network will retain all the
	 *            other interactions other than the spicified interaction type
	 * @return
	 */
	public static OntologyNetwork generateNewOntologyNetwork(
			CyNetwork originalCyNetwork, String keepInteraction,
			boolean retainOtherInteraction) {

		LinkedList<DelayedVizProp> vizProps = new LinkedList<DelayedVizProp>();
		CytoscapeServiceManager cytoscapeServiceManager = MyApplicationManager
				.getInstance().getCytoscapeServiceManager();

		CyNetworkFactory networkFactory = cytoscapeServiceManager
				.getCyNetworkFactory();
		CyNetwork underlyingCyNetwork = networkFactory.createNetwork();

		String originalNetworkName = originalCyNetwork.getRow(
				originalCyNetwork).get(CyNetwork.NAME, String.class);
		underlyingCyNetwork.getRow(underlyingCyNetwork).set(CyNetwork.NAME,
				originalNetworkName + " (Ontology View)");

		List<CyNode> allNodes = originalCyNetwork.getNodeList();

		HashMap<Long, ExpandableNode> createdNodes = new HashMap<Long, ExpandableNode>();
		HashSet<ExpandableNode> allRootNodes = new HashSet<ExpandableNode>();

		Collection<CyColumn> originalColumns = originalCyNetwork.getDefaultNodeTable().getColumns();
		for (CyColumn column : originalColumns) {
			String columnName = column.getName();
			if (underlyingCyNetwork.getDefaultNodeTable().getColumn(columnName) == null) {
				if (column.getType() == List.class) {
					underlyingCyNetwork.getDefaultNodeTable().createListColumn(columnName, column.getListElementType(), false);
				}
				else {
					underlyingCyNetwork.getDefaultNodeTable().createColumn(columnName, column.getType(), false);
				}
			}
		}
		
		
		for (CyNode node : allNodes) {
			
			String nodeName = originalCyNetwork.getRow(node).get(
					CyNetwork.NAME, String.class);
			CyNode generatedNode = underlyingCyNetwork.addNode();
			
			//underlyingCyNetwork.getRow(generatedNode).set(CyNetwork.NAME,
			//		nodeName);

			for (CyColumn column : originalColumns) {
				String columnName = column.getName();
				underlyingCyNetwork.getRow(generatedNode).set(columnName, originalCyNetwork.getRow(node).get(columnName, column.getType()));
			}
			
			String definition = underlyingCyNetwork.getRow(generatedNode).get("def", String.class);
			ExpandableNode expandableNode = new ExpandableNode(generatedNode,
					nodeName, definition);

			setNodeProp(generatedNode, vizProps, nodeName, definition);

			allRootNodes.add(expandableNode);
			createdNodes.put(node.getSUID(), expandableNode);

		}

		// get all the edges in the original network
		List<CyEdge> allEdges = originalCyNetwork.getEdgeList();
		for (CyEdge edge : allEdges) {
			if (!edge.isDirected())
				continue; // we only care about the directed nodes
			// get the interaction type of current edge
			String interactionType = originalCyNetwork.getRow(edge).get(
					CyEdge.INTERACTION, String.class);
			if (interactionType == null || interactionType.isEmpty())
				continue; // invalid interaction type

			CyNode sourceNode = edge.getSource();
			CyNode targetNode = edge.getTarget();
			ExpandableNode sourceExpandableNode = createdNodes.get(sourceNode
					.getSUID());
			ExpandableNode targetExpandableNode = createdNodes.get(targetNode
					.getSUID());

			// is not the interaction we care about
			if (!interactionType.equalsIgnoreCase(keepInteraction)) {
				if (retainOtherInteraction) {
					// create the corresponding edge in the underlying network
					CyEdge newEdge = underlyingCyNetwork.addEdge(
							targetExpandableNode.getCyNode(),
							sourceExpandableNode.getCyNode(), false);
					underlyingCyNetwork.getRow(newEdge).set("interaction",
							interactionType);
					setEdgeProp(newEdge, vizProps, interactionType, false);
				}
			} else {

				if (!containedInteraction(interactionType)
						&& !containingInteraction(interactionType))
					continue;
				boolean shouldAddEdge = false;

				if (containedInteraction(keepInteraction)) {
					if (!targetExpandableNode.hasChild(sourceExpandableNode)) {
						targetExpandableNode.addChildNode(sourceExpandableNode);
						shouldAddEdge = true;
						// this node is a child node of some other node, so
						// remove it from the root list.
						allRootNodes.remove(sourceExpandableNode);
					}

				} else {
					if (!sourceExpandableNode.hasChild(targetExpandableNode)) {
						sourceExpandableNode.addChildNode(targetExpandableNode);
						shouldAddEdge = true;
						allRootNodes.remove(targetExpandableNode);
					}
				}

				if (shouldAddEdge) {
					CyEdge newEdge = underlyingCyNetwork.addEdge(
							sourceExpandableNode.getCyNode(),
							targetExpandableNode.getCyNode(), true);
					underlyingCyNetwork.getRow(newEdge).set("interaction",
							keepInteraction);
					setEdgeProp(newEdge, vizProps, interactionType, true);
				}
			}
		}

		return new OntologyNetwork(originalCyNetwork, underlyingCyNetwork,
				createdNodes, allRootNodes, keepInteraction,
				retainOtherInteraction, vizProps);
	}

	private static void setEdgeProp(CyEdge connectingEdge,
			LinkedList<DelayedVizProp> vizProps, String interaction,
			boolean isInterestedInteraction) {
		DelayedVizProp vizProp;

		if (isInterestedInteraction) {
			vizProp = new DelayedVizProp(connectingEdge,
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
					BasicVisualLexicon.EDGE_TRANSPARENCY, 255, true);
			vizProps.add(vizProp);
		} else {
			vizProp = new DelayedVizProp(connectingEdge,
					BasicVisualLexicon.EDGE_WIDTH, 1.0, true);
			vizProps.add(vizProp);
			vizProp = new DelayedVizProp(connectingEdge,
					BasicVisualLexicon.EDGE_LINE_TYPE,
					LineTypeVisualProperty.LONG_DASH, true);
			vizProps.add(vizProp);
			vizProp = new DelayedVizProp(connectingEdge,
					BasicVisualLexicon.EDGE_TRANSPARENCY, 120, true);
			vizProps.add(vizProp);
		}

		vizProp = new DelayedVizProp(connectingEdge,
				BasicVisualLexicon.EDGE_TOOLTIP, interaction, true);
		vizProps.add(vizProp);
	}

	private static void setNodeProp(CyNode node,
			LinkedList<DelayedVizProp> vizProps, String nodeName, String definition) {

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

		String toolTip = nodeName;
		if (definition != null && !definition.isEmpty()) {
			toolTip = toolTip + " : " + definition;
		}
		vizProp = new DelayedVizProp(node, BasicVisualLexicon.NODE_TOOLTIP,
				toolTip, true);
		vizProps.add(vizProp);
	}

	private static boolean containedInteraction(String interactionType) {
		return interactionType.equalsIgnoreCase(INTERACTION_PART_OF)
				|| interactionType.equalsIgnoreCase(INTERACTION_OCCURS_IN)
				|| interactionType.equalsIgnoreCase(INTERACTION_IS_A)
				|| interactionType.equalsIgnoreCase(INTERACTION_HAPPENS_DURING);
	}

	private static boolean containingInteraction(String interactionType) {
		return interactionType.equalsIgnoreCase(INTERACTION_HAS_PART)
				|| interactionType.equalsIgnoreCase(INTERACTION_REGULATES)
				|| interactionType
						.equalsIgnoreCase(INTERACTION_NEGATIVELY_REGULATES)
				|| interactionType
						.equalsIgnoreCase(INTERACTION_POSITIVELY_REGULATES);
	}
}
