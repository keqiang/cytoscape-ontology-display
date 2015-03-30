package edu.umich.med.mbni.lkq.cyontology.task;

import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

import edu.umich.med.mbni.lkq.cyontology.model.OntologyNetwork;

public class PopulateOntologyNetworkViewTaskFactory extends AbstractNetworkViewTaskFactory {

	private final OntologyNetwork ontologyNetwork;
	
	public PopulateOntologyNetworkViewTaskFactory(OntologyNetwork ontologyNetwork) {
		this.ontologyNetwork = ontologyNetwork;
	}
	
	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView) {
		return new TaskIterator(new PopulateOntologyNetworkViewTask(networkView, ontologyNetwork));
	}

}
