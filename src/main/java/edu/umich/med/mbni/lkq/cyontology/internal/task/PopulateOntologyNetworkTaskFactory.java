package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

public class PopulateOntologyNetworkTaskFactory extends AbstractNetworkTaskFactory {

	private String interactionType;
	private boolean retainOtherInteraction;
	public PopulateOntologyNetworkTaskFactory(String interactionType, boolean retainOtherInteraction) {
		this.interactionType = interactionType;
		this.retainOtherInteraction = retainOtherInteraction;
	}
	
	@Override
	public TaskIterator createTaskIterator(CyNetwork network) {
		return new TaskIterator(new PopulateOntologyNetworkTask(network, interactionType, retainOtherInteraction));
	}

}
