package edu.umich.med.mbni.lkq.cyontology.task;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

public class SelectDirectChildNodeTaskFactory extends AbstractNodeViewTaskFactory {

	@Override
	public TaskIterator createTaskIterator(View<CyNode> nodeView, CyNetworkView netView) {
		return new TaskIterator(new SelectDirectChildNodeTask(nodeView, netView));
	}

}
