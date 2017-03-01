package edu.umich.med.mbni.lkq.cyontology.event;

import java.util.EventObject;

public class OntologyAggregationColumnChangeEvent extends EventObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3035709397950059250L;
	private final String aggregationMethod;
	private final String aggregationColumn;

	public OntologyAggregationColumnChangeEvent(Object source, String aggregationMethod, String aggregationColumn) {
		super(source);
		this.aggregationColumn = aggregationColumn;
		this.aggregationMethod = aggregationMethod;
	}

	public String getAggregationMethod() {
		return aggregationMethod;
	}

	public String getAggregationColumn() {
		return aggregationColumn;
	}
}
