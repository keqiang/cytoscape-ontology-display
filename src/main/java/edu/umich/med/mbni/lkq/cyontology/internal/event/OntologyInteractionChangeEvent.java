package edu.umich.med.mbni.lkq.cyontology.internal.event;

import java.util.EventObject;

public class OntologyInteractionChangeEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7803492621939070275L;
	private String interactionType;
	public OntologyInteractionChangeEvent(Object source, String interactionType) {
		super(source);
		this.interactionType = interactionType;
	}
	public String getInteractionType() {
		return interactionType;
	}
}
