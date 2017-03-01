package edu.umich.med.mbni.lkq.cyontology.listener;

import edu.umich.med.mbni.lkq.cyontology.event.OntologyInteractionChangeEvent;

public interface OntologyInteractionChangeListener {
	public void interactionChangePerformed(OntologyInteractionChangeEvent event);
}
