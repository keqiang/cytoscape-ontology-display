package edu.umich.med.mbni.lkq.cyontology.internal.model;

import java.util.Collection;
import java.util.HashMap;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class ExpandableNode {

	private int referCount = 0;
	private CyNode node;

	private boolean isCollapsed;

	private HashMap<Long, ExpandableNode> childNodes;

	public ExpandableNode(CyNetwork nestedNetwork) {
		node = nestedNetwork.addNode();
		childNodes = new HashMap<>();
		isCollapsed = false;
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

	public void removeChildNode(ExpandableNode otherNode) {
		childNodes.remove(otherNode.getSUID());
		otherNode.decreaseReferenceCount();
	}

	public void addChildNode(ExpandableNode otherNode) {
		childNodes.put(otherNode.getSUID(), otherNode);
		otherNode.increaseReferenceCount();
	}

	public CyNetwork getNetwork() {
		return node.getNetworkPointer();
	}

	public void expand() {

		if (!isCollapsed)
			return;

		for (ExpandableNode childNode : childNodes.values()) {

			childNode.increaseReferenceCount();
			if (childNode.isCollapsed)
				childNode.expand();
		}

		isCollapsed = false;
	}

	public void collapse() {
		if (isCollapsed)
			return;

		for (ExpandableNode childNode : childNodes.values()) {

			childNode.decreaseReferenceCount();

			if (!childNode.isCollapsed) {
				childNode.collapse();
			}
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

}
