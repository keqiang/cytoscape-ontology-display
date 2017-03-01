package edu.umich.med.mbni.lkq.cyontology.listener;

import edu.umich.med.mbni.lkq.cyontology.event.OntologyAggregationColumnChangeEvent;

public interface OntologyAggregationColumnChangeListener {
	public void ontologyAggregationColumnChangePerformed(OntologyAggregationColumnChangeEvent event);
}
