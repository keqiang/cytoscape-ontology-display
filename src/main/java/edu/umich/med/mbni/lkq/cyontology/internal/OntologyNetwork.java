package edu.umich.med.mbni.lkq.cyontology.internal;

import java.util.Map;

import org.cytoscape.model.CyNetwork;

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
}
