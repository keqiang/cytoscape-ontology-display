package edu.umich.med.mbni.lkq.cyontology.internal.event;

import java.util.EventObject;

public class OntologyAggregationMethodChangeEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8558653219944026699L;
	private final String aggregationColumn;
	private final String aggregationMethod;

	public OntologyAggregationMethodChangeEvent(Object source, String aggregationMethod, String aggregationColumn) {
		super(source);
		this.aggregationColumn = aggregationColumn;
		this.aggregationMethod = aggregationMethod;
	}

	public String getAggregationColumn() {
		return aggregationColumn;
	}

	public String getAggregationMethod() {
		return aggregationMethod;
	}	
}
