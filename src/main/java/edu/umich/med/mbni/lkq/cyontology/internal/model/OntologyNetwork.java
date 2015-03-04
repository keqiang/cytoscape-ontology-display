package edu.umich.med.mbni.lkq.cyontology.internal.model;

import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyNodeExpansionListener;
import edu.umich.med.mbni.lkq.cyontology.internal.util.DelayedVizProp;

public class OntologyNetwork {

	private final String interactionType;

	// the original network based on which the ontology network has been
	// generated
	private final CyNetwork originalCyNetwork;

	// the network newly generated to represent the ontology network
	private final CyNetwork underlyingCyNetwork;

	private final Map<Long, ExpandableNode> originalNodeToExpandableNode;

	private Map<Long, ExpandableNode> underlyingNodeToExpandableNode;

	private final Set<ExpandableNode> allRootNodes;

	private final List<DelayedVizProp> visualProps;

	private LinkedList<OntologyNodeExpansionListener> expansionListeners = new LinkedList<OntologyNodeExpansionListener>();

	public OntologyNetwork(CyNetwork originalCyNetwork,
			CyNetwork underlyingCyNetwork,
			Map<Long, ExpandableNode> originalNodeToExpandableNode,
			Set<ExpandableNode> allRootNodes, String interactionType, List<DelayedVizProp> visualProps) {
		this.originalCyNetwork = originalCyNetwork;
		this.underlyingCyNetwork = underlyingCyNetwork;
		this.originalNodeToExpandableNode = originalNodeToExpandableNode;
		this.allRootNodes = allRootNodes;
		this.interactionType = interactionType;
		this.visualProps = visualProps;

		linkNodes();
	}

	private void linkNodes() {
		underlyingNodeToExpandableNode = new HashMap<Long, ExpandableNode>();
		for (ExpandableNode expandableNode : getAllNodes()) {
			underlyingNodeToExpandableNode.put(expandableNode.getSUID(),
					expandableNode);
		}
	}

	public CyNetwork getOriginalCyNetwork() {
		return originalCyNetwork;
	}

	public void addNodeExpansionListener(
			OntologyNodeExpansionListener newListener) {
		expansionListeners.add(newListener);
	}

	public void removeNodeExpansionListener(
			OntologyNodeExpansionListener newListener) {
		expansionListeners.remove(newListener);
	}

	public void fireNodeExpansionEvent(ExpandableNode expandableNode) {
		EventObject event = new EventObject(expandableNode);
		for (OntologyNodeExpansionListener ontologyNodeExpansionListener : expansionListeners) {
			ontologyNodeExpansionListener.expansionPerformed(event);
		}
	}

	public CyNetwork getUnderlyingCyNetwork() {
		return underlyingCyNetwork;
	}

	public ExpandableNode getNode(CyNode node) {
		return underlyingNodeToExpandableNode.get(node.getSUID());
	}

	public ExpandableNode getNode(Long nodeSUID) {
		return underlyingNodeToExpandableNode.get(nodeSUID);
	}

	public Collection<ExpandableNode> getAllNodes() {
		return originalNodeToExpandableNode.values();
	}

	public Collection<ExpandableNode> getAllRootNodes() {
		return allRootNodes;
	}

	public Collection<ExpandableNode> findCommonNodes(ExpandableNode node1,
			ExpandableNode node2) {
		Set<ExpandableNode> childNodes1 = node1.getAllChildNodes();
		Set<ExpandableNode> childNodes2 = node2.getAllChildNodes();

		childNodes1.retainAll(childNodes2);
		return childNodes1;
	}

	public Collection<ExpandableNode> findCommonNodes(List<ExpandableNode> nodes) {
		if (nodes.isEmpty())
			return new HashSet<ExpandableNode>();

		Set<ExpandableNode> result = nodes.get(0).getAllChildNodes();
		for (int i = 1; i < nodes.size(); ++i) {
			Set<ExpandableNode> childNodes = nodes.get(i).getAllChildNodes();
			result.retainAll(childNodes);
		}

		return result;
	}

	public List<ExpandableNode> getCorrespondingExpandableNodes(
			List<CyNode> nodes) {
		List<ExpandableNode> correspondingExpandableNodes = new LinkedList<ExpandableNode>();
		for (CyNode node : nodes) {
			correspondingExpandableNodes.add(getNode(node));
		}
		return correspondingExpandableNodes;
	}

	public String getInteractionType() {
		return interactionType;
	}

	public List<DelayedVizProp> getVisualProps() {
		return visualProps;
	}
}
