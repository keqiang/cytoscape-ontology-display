package edu.umich.med.mbni.lkq.cyontology.internal.listener;

import edu.umich.med.mbni.lkq.cyontology.internal.event.OntologyAggregationColumnChangeEvent;

public interface OntologyAggregationColumnChangeListener {
	public void ontologyAggregationColumnChangePerformed(OntologyAggregationColumnChangeEvent event);
}
