package edu.umich.med.mbni.lkq.cyontology.listener;

import edu.umich.med.mbni.lkq.cyontology.event.OntologyNetworkChangedEvent;

public interface OntologyNetworkChangedListener {
	public void ontologyNetworkChanged(OntologyNetworkChangedEvent event);
}
