package edu.umich.med.mbni.lkq.cyontology.internal.listener;

import edu.umich.med.mbni.lkq.cyontology.internal.event.OntologyInteractionChangeEvent;

public interface OntologyInteractionChangeListener {
	public void interactionChangePerformed(OntologyInteractionChangeEvent event);
}
