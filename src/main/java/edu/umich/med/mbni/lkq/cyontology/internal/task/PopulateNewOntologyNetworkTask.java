package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.util.OntologyNetworkUtils;

/**
 * @author keqiangli task to generate a new ontology network based on an existing network
 *
 */
public class PopulateNewOntologyNetworkTask extends AbstractNetworkTask  {
	
	// what type of interaction to retain
	private final String interactionType;

	public PopulateNewOntologyNetworkTask(final CyNetwork network, String interactionType) {
		super(network);
		this.interactionType = interactionType;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		
		// this is the original network based on which this function is going to generate an ontology network
		CyNetwork originalNetwork = network;
		
		taskMonitor.setTitle("Generating Ontology Network");

		// if an ontology network based on this original network exists
		if (MyApplicationManager.getInstance().hasOntologyNetworkFromOriginalCyNetwork(
				originalNetwork)) {
			OntologyNetwork ontologyNetwork = MyApplicationManager.getInstance().getOntologyNetworkFromOriginalCyNetwork(originalNetwork);
			// if this ontology network is the same as what we're going to generate
			if (interactionType.equals(ontologyNetwork.getInteractionType())) {
				MyApplicationManager.getInstance().getOntologyPanelController().setOntologyNetwork(ontologyNetwork);
				return;
			}
			// if interaction type is different, just remove this ontology and generated a new one
			else {
				MyApplicationManager.getInstance().removeOntologyNetworkByOriginalNetwork(originalNetwork);
			}			
		}
		
		// generate a new ontology originalNetwork and view based on the underlying network and interaction type
		
		taskMonitor.setStatusMessage("populating all ontology items");
		
		OntologyNetwork generatedOntologyNetwork = OntologyNetworkUtils
				.generateNewOntologyNetwork(originalNetwork, interactionType);

		MyApplicationManager.getInstance().addOntologyNetwork(
				generatedOntologyNetwork);
		
		taskMonitor.setStatusMessage("cleaning up old ontology network");
		
		MyApplicationManager.getInstance().getOntologyPanelController().setOntologyNetwork(generatedOntologyNetwork);
		
	}

}
