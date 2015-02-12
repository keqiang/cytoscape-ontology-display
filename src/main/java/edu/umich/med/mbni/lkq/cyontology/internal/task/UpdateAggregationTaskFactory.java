package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

public class UpdateAggregationTaskFactory extends AbstractNetworkViewTaskFactory {
	
	private final String aggregationType;
	private final String columnName;
	
	public UpdateAggregationTaskFactory(final String aggregationType, final String columnName) {
		this.aggregationType = aggregationType;
		this.columnName = columnName;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView) {
		return new TaskIterator(new UpdateAggregationTask(networkView, aggregationType, columnName));
	}

}
