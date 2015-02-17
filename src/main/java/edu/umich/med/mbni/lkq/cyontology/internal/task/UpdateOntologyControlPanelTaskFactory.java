package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyControlPanel;

public class UpdateOntologyControlPanelTaskFactory extends AbstractNetworkTaskFactory {
	private final OntologyControlPanel ontologyControlPanel;
	private final UpdateOntologyControlPanelTask.UpdateOntologyControlOptions options;
	
	public UpdateOntologyControlPanelTaskFactory(final OntologyControlPanel ontologyControlPanel, final UpdateOntologyControlPanelTask.UpdateOntologyControlOptions options) {
		this.ontologyControlPanel = ontologyControlPanel;
		this.options = options;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetwork network) {
		return new TaskIterator(new UpdateOntologyControlPanelTask(network, ontologyControlPanel, options));
	}

}
