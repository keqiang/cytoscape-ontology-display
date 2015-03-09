package edu.umich.med.mbni.lkq.cyontology.internal.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class ExpandableNode {
	private String nodeName;
	private String definition;
	
	private int referCount = 0;
	private CyNode node;

	private boolean isCollapsed;

	private HashMap<Long, ExpandableNode> directChildNodes;

	public ExpandableNode(CyNode node, String nodeName, String definition) {
		this.node = node;
		directChildNodes = new HashMap<>();
		isCollapsed = false;
		this.nodeName = nodeName;
		this.definition = definition;
	}
	
	public String getNodeName() {
		return nodeName;
	}
	
	public String getDefinition() {
		return definition;
	}
	
	public String getToolTip() {
		String toolTip = "[name : " + nodeName;
		if (definition != null && !definition.isEmpty()) {
			toolTip += "; definition : " + definition; 
		}
		toolTip += "]";
		return toolTip;
	}

	public Collection<ExpandableNode> getDirectChildNodes() {
		return directChildNodes.values();
	}
	
	public boolean isReferred() {
		return getReferenceCount() > 0;
	}

	public CyNode getCyNode() {
		return node;
	}

	public int getReferenceCount() {
		return referCount;
	}

	public Long getSUID() {
		return node.getSUID();
	}

	public void addChildNode(ExpandableNode otherNode) {
		directChildNodes.put(otherNode.getSUID(), otherNode);
		otherNode.increaseReferenceCount();
	}

	public CyNetwork getNetwork() {
		return node.getNetworkPointer();
	}

	public void expand() {

		for (ExpandableNode childNode : directChildNodes.values()) {

			if (isCollapsed) {
				childNode.increaseReferenceCount();
			}
		
			childNode.expand();
		}

		isCollapsed = false;
	}
	
	public void expandOneLevel() {

		for (ExpandableNode childNode : directChildNodes.values()) {
			if (isCollapsed) {
				childNode.increaseReferenceCount();
			}
		}

		isCollapsed = false;
	}

	public void collapse() {
		if (isCollapsed)
			return;

		for (ExpandableNode childNode : directChildNodes.values()) {

			if (!childNode.isCollapsed) {
				childNode.collapse();
			}
			
			childNode.decreaseReferenceCount();
		}

		isCollapsed = true;
	}

	public boolean isCollapsed() {
		return isCollapsed;
	}

	public void decreaseReferenceCount() {
		if (referCount > 0)
			referCount--;
	}

	public void increaseReferenceCount() {
		referCount++;
	}

	public boolean hasChild(ExpandableNode targetExpandableNode) {
		return directChildNodes.containsKey(targetExpandableNode.getSUID());
	}
	
	@Override 
	public String toString() {
		return nodeName;
	}
	
	@Override
	public boolean equals(Object otherNode) {
		if (otherNode instanceof ExpandableNode) {
			ExpandableNode other = (ExpandableNode)otherNode;
			return this.nodeName.equals(other.nodeName);
		}
		return false;
			
	}
	
	public Set<ExpandableNode> getAllChildNodes() {
		Set<ExpandableNode> allChildNodes = new HashSet<ExpandableNode>();
		LinkedList<ExpandableNode> queue = new LinkedList<ExpandableNode>();
		queue.add(this);
		
		allChildNodes.add(this);
		while (!queue.isEmpty()) {
			ExpandableNode currentRoot = queue.poll();
			allChildNodes.addAll(currentRoot.getDirectChildNodes());
			queue.addAll(currentRoot.getDirectChildNodes());
		}
		return allChildNodes;
	}

}
