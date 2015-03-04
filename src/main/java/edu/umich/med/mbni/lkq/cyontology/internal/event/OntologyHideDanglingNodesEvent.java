package edu.umich.med.mbni.lkq.cyontology.internal.event;

import java.util.EventObject;

public class OntologyHideDanglingNodesEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3202611251285214486L;
	private boolean isHiding;
	
	public OntologyHideDanglingNodesEvent(Object source, boolean isHiding) {
		super(source);
		this.isHiding = isHiding;
	}

	public boolean isHiding() {
		return isHiding;
	}
}
