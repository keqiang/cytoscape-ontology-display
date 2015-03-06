package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

public class DEBUG_MapDataToColorTaskFactory extends AbstractNetworkViewTaskFactory {

	@Override
	public TaskIterator createTaskIterator(CyNetworkView view) {
		return new TaskIterator(new DEBUG_MapDataToColorTask(view));
	}

}
