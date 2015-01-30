package edu.umich.med.mbni.lkq.cyontology.internal.model;

import java.util.Collection;
import java.util.Map;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class OntologyNetwork {
	private CyNetwork originNetwork;
	private CyNetwork underlyingNetwork;
	private Map<Long, ExpandableNode> allExpandableNodes;
	
	public OntologyNetwork(CyNetwork originNetwork, CyNetwork underlyingNetwork, Map<Long, ExpandableNode> allExpandableNodes) {
		this.originNetwork = originNetwork;
		this.underlyingNetwork = underlyingNetwork;
		this.allExpandableNodes = allExpandableNodes;
	}
	
	public CyNetwork getUnderlyingNetwork() {
		return underlyingNetwork;
	}
	
	public CyNetwork getOriginNetwork() {
		return originNetwork;
	}
	
	public ExpandableNode getNode(Long nodeSUID) {
		return allExpandableNodes.get(nodeSUID);
	}
	
	public ExpandableNode getCorrespondingNode(CyNode node) {
		for (ExpandableNode expandableNode : allExpandableNodes.values()) {
			if (node.getSUID() == expandableNode.getSUID())
				return expandableNode;
		}
		return null;
	}
	
	public Collection<ExpandableNode> getAllNodes() {
		return allExpandableNodes.values();
	}
}
