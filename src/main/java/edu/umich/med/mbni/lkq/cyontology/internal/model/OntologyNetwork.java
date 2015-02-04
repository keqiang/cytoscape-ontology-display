package edu.umich.med.mbni.lkq.cyontology.internal.model;

import java.util.Collection;
import java.util.Map;

import org.cytoscape.model.CyNetwork;

public class OntologyNetwork {
	
	private CyNetwork underlyingNetwork;
	private Map<Long, ExpandableNode> allExpandableNodes;
	
	public OntologyNetwork(CyNetwork underlyingNetwork, Map<Long, ExpandableNode> allExpandableNodes) {
		this.underlyingNetwork = underlyingNetwork;
		this.allExpandableNodes = allExpandableNodes;
	}
	
	public CyNetwork getUnderlyingNetwork() {
		return underlyingNetwork;
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
}
