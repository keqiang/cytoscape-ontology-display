package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;

public class ExpandableNodeExpandTaskFactory extends AbstractNodeViewTaskFactory {

	@Override
	public TaskIterator createTaskIterator(View<CyNode> nodeView, CyNetworkView networkView) {
		ExpandableNodeExpandTask task = new ExpandableNodeExpandTask(nodeView, networkView);
		Task[] initialTasks = new Task[1];
		initialTasks[0] = task;
		return new TaskIterator(initialTasks);
	}

}

