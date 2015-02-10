package edu.umich.med.mbni.lkq.cyontology.internal.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class OntologyNetwork {
	
	private CyNetwork underlyingNetwork;
	private Map<Long, ExpandableNode> allExpandableNodes;
	private Set<Long> allRootNodes;
	
	public OntologyNetwork(CyNetwork underlyingNetwork, Map<Long, ExpandableNode> allExpandableNodes, Set<Long> allRootNodes) {
		this.underlyingNetwork = underlyingNetwork;
		this.allExpandableNodes = allExpandableNodes;
		this.allRootNodes = allRootNodes;
	}
	
	public CyNetwork getUnderlyingNetwork() {
		return underlyingNetwork;
	}
	
	public ExpandableNode getNode(CyNode node) {
		return allExpandableNodes.get(node.getSUID());
	}
	
	public ExpandableNode getNode(Long nodeSUID) {
		return allExpandableNodes.get(nodeSUID);
	}
	
	public Collection<ExpandableNode> getAllNodes() {
		return allExpandableNodes.values();
	}
	
	public Map<Long, ExpandableNode> getNodeMap() {
		return allExpandableNodes;
	}
	
	public Collection<Long> getAllRootNodes() {
		return allRootNodes;
	}
}
