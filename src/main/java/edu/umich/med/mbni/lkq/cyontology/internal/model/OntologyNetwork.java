package edu.umich.med.mbni.lkq.cyontology.internal.model;

import java.util.Collection;
import java.util.EventObject;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import edu.umich.med.mbni.lkq.cyontology.internal.listener.OntologyNodeExpansionListener;

public class OntologyNetwork {
	
	private CyNetwork underlyingNetwork;
	private Map<Long, ExpandableNode> allExpandableNodes;
	private Set<Long> allRootNodes;
	
	private LinkedList<OntologyNodeExpansionListener> expansionListeners = new LinkedList<OntologyNodeExpansionListener>();
	
	public OntologyNetwork(CyNetwork underlyingNetwork, Map<Long, ExpandableNode> allExpandableNodes, Set<Long> allRootNodes) {
		this.underlyingNetwork = underlyingNetwork;
		this.allExpandableNodes = allExpandableNodes;
		this.allRootNodes = allRootNodes;
	}
	
	public void addNodeExpansionListener(OntologyNodeExpansionListener newListener) {
		expansionListeners.add(newListener);
	}
	
	public void removeNodeExpansionListener(OntologyNodeExpansionListener newListener) {
		expansionListeners.remove(newListener);
	}
	
	public void fireNodeExpansionEvent(ExpandableNode expandableNode) {
		EventObject event = new EventObject(expandableNode);
		for (OntologyNodeExpansionListener ontologyNodeExpansionListener : expansionListeners) {
			ontologyNodeExpansionListener.expansionPerformed(event);
		}
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

	public Collection<ExpandableNode> findCommonNodes(ExpandableNode node1, ExpandableNode node2) {
		Set<ExpandableNode> childNodes1 = node1.getAllChildNodes();
		Set<ExpandableNode> childNodes2 = node2.getAllChildNodes();
		
		childNodes1.retainAll(childNodes2);
		return childNodes1;
	}
	
	public Collection<ExpandableNode> findCommonNodes(List<ExpandableNode> nodes) {
		if (nodes.isEmpty()) return new HashSet<ExpandableNode>();
		
		Set<ExpandableNode> result = nodes.get(0).getAllChildNodes();
		for (int i = 1; i < nodes.size(); ++i) {
			Set<ExpandableNode> childNodes = nodes.get(i).getAllChildNodes();
			result.retainAll(childNodes);
		}
		
		return result;
	}
	
	public List<ExpandableNode> getCorrespondingExpandableNodes(List<CyNode> nodes) {
		List<ExpandableNode> correspondingExpandableNodes = new LinkedList<ExpandableNode>();
		for (CyNode node : nodes) {
			correspondingExpandableNodes.add(getNode(node));
		}
		return correspondingExpandableNodes;
	}
}
