package edu.umich.med.mbni.lkq.cyontology.internal.listener;

import edu.umich.med.mbni.lkq.cyontology.internal.event.OntologyNetworkChangedEvent;

public interface OntologyNetworkChangedListener {
	public void ontologyNetworkChanged(OntologyNetworkChangedEvent event);
}
