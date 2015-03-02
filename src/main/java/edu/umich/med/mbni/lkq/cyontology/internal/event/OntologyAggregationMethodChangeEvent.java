package edu.umich.med.mbni.lkq.cyontology.internal.event;

import java.util.EventObject;

public class OntologyAggregationMethodChangeEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8558653219944026699L;
	private String aggregationColumn;
	private String aggregationMethod;

	public OntologyAggregationMethodChangeEvent(Object source, String aggregationMethod, String aggregationColumn) {
		super(source);
		this.setAggregationColumn(aggregationColumn);
		this.setAggregationMethod(aggregationMethod);
	}

	public String getAggregationColumn() {
		return aggregationColumn;
	}

	public void setAggregationColumn(String aggregationColumn) {
		this.aggregationColumn = aggregationColumn;
	}

	public String getAggregationMethod() {
		return aggregationMethod;
	}

	public void setAggregationMethod(String aggregationMethod) {
		this.aggregationMethod = aggregationMethod;
	}
	
	

}
