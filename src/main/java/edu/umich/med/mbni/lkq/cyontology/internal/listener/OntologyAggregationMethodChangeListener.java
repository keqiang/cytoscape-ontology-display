package edu.umich.med.mbni.lkq.cyontology.internal.listener;

import edu.umich.med.mbni.lkq.cyontology.internal.event.OntologyAggregationMethodChangeEvent;

public interface OntologyAggregationMethodChangeListener {
	public void ontologyAggreagationMethodChangePerformed(OntologyAggregationMethodChangeEvent event);
}
