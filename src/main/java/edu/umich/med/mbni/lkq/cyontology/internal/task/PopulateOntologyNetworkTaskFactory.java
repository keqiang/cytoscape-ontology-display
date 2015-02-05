package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;

public class PopulateOntologyNetworkTaskFactory extends AbstractNetworkTaskFactory {

	private String interactionType;
	public PopulateOntologyNetworkTaskFactory(String interactionType) {
		this.interactionType = interactionType;
	}
	
	@Override
	public TaskIterator createTaskIterator(CyNetwork network) {
		Task[] tasks = new Task[1];
		tasks[0] = new PopulateOntologyNetworkTask(network, interactionType);
		return new TaskIterator(tasks);
	}

}
