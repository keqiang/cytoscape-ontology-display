package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;

public class HideOrShowDanglingNodesTaskFactory extends AbstractNetworkViewTaskFactory {
	private boolean isShowing;
	
	public HideOrShowDanglingNodesTaskFactory(boolean isShowing) {
		this.isShowing = isShowing;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView) {
		
		Task[] initialTasks = new Task[1];
		initialTasks[0] = new HideOrShowDanglingNodesTask(networkView, isShowing);
		return new TaskIterator(initialTasks);
		
	}

}
