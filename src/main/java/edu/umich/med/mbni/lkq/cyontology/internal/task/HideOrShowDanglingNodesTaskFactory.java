package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

public class HideOrShowDanglingNodesTaskFactory extends AbstractNetworkViewTaskFactory {
	private boolean isHiding;
	
	public HideOrShowDanglingNodesTaskFactory(boolean isHiding) {
		this.isHiding = isHiding;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView) {
		return new TaskIterator(new HideOrShowDanglingNodesTask(networkView, isHiding));
	}

}
