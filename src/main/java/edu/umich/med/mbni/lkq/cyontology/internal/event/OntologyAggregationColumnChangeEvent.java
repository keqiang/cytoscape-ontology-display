package edu.umich.med.mbni.lkq.cyontology.internal.event;

import java.util.EventObject;

public class OntologyAggregationColumnChangeEvent extends EventObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3035709397950059250L;
	private String aggregationMethod;
	private String aggregationColumn;

	public OntologyAggregationColumnChangeEvent(Object source, String aggregationMethod, String aggregationColumn) {
		super(source);
		this.setAggregationColumn(aggregationColumn);
		this.setAggregationMethod(aggregationMethod);
	}

	public String getAggregationMethod() {
		return aggregationMethod;
	}

	public void setAggregationMethod(String aggregationMethod) {
		this.aggregationMethod = aggregationMethod;
	}

	public String getAggregationColumn() {
		return aggregationColumn;
	}

	public void setAggregationColumn(String aggregationColumn) {
		this.aggregationColumn = aggregationColumn;
	}
	
	

}
