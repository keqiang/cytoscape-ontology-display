package edu.umich.med.mbni.lkq.cyontology.internal.model;

import java.util.Collection;
import java.util.Map;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class OntologyNetwork {
	
	private CyNetwork underlyingNetwork;
	private Map<Long, ExpandableNode> allExpandableNodes;
	private Map<Long, ExpandableNode> allRootNodes;
	
	public OntologyNetwork(CyNetwork underlyingNetwork, Map<Long, ExpandableNode> allExpandableNodes, Map<Long, ExpandableNode> allRootNodes) {
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
	
	public Collection<ExpandableNode> getAllNodes() {
		return allExpandableNodes.values();
	}
	
	public Map<Long, ExpandableNode> getNodeMap() {
		return allExpandableNodes;
	}
	
	public Collection<ExpandableNode> getAllRootNodes() {
		return allRootNodes.values();
	}
}
