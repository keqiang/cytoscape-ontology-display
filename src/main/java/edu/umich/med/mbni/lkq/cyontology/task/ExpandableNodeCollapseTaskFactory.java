package edu.umich.med.mbni.lkq.cyontology.task;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

public class ExpandableNodeCollapseTaskFactory extends AbstractNodeViewTaskFactory {

	public TaskIterator createTaskIterator(View<CyNode> nodeView, CyNetworkView networkView) {
		return new TaskIterator(new ExpandableNodeCollapseTask(nodeView, networkView));
	}

}
