package edu.umich.med.mbni.lkq.cyontology.internal.model;

import java.util.Collection;
import java.util.HashMap;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class ExpandableNode {
	private String displayName;
	private int referCount = 0;
	private CyNode node;

	private boolean isCollapsed;

	private HashMap<Long, ExpandableNode> childNodes;

	public ExpandableNode(CyNode node, String displayName) {
		this.node = node;
		childNodes = new HashMap<>();
		isCollapsed = false;
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public Collection<ExpandableNode> getChildNodes() {
		return childNodes.values();
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
		childNodes.put(otherNode.getSUID(), otherNode);
		otherNode.increaseReferenceCount();
	}

	public CyNetwork getNetwork() {
		return node.getNetworkPointer();
	}

	public void expand() {

		for (ExpandableNode childNode : childNodes.values()) {

			if (isCollapsed) {
				childNode.increaseReferenceCount();
			}
		
			childNode.expand();
		}

		isCollapsed = false;
	}
	
	public void expandOneLevel() {

		for (ExpandableNode childNode : childNodes.values()) {
			if (isCollapsed) {
				childNode.increaseReferenceCount();
			}
		}

		isCollapsed = false;
	}

	public void collapse() {
		if (isCollapsed)
			return;

		for (ExpandableNode childNode : childNodes.values()) {

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
		return childNodes.containsKey(targetExpandableNode.getSUID());
	}
	
	@Override 
	public String toString() {
		return displayName;
	}
	
	@Override
	public boolean equals(Object otherNode) {
		if (otherNode instanceof ExpandableNode) {
			ExpandableNode other = (ExpandableNode)otherNode;
			return this.displayName.equals(other.displayName);
		}
		return false;
			
	}

}
