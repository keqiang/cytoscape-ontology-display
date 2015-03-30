package edu.umich.med.mbni.lkq.cyontology.task;

import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

public class ClearAggregationTaskFactory extends AbstractNetworkViewTaskFactory {

	@Override
	public TaskIterator createTaskIterator(CyNetworkView view) {
		return new TaskIterator(new ClearAggregationTask(view));
	}

}
