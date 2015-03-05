package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.util.OntologyNetworkUtils;

/**
 * @author keqiangli task to generate a new ontology network based on an
 *         existing network
 *
 */
public class PopulateOntologyNetworkTask extends AbstractNetworkTask {

	// what type of interaction to retain
	private final String interactionType;
	private boolean retainOtherInteraction;

	public PopulateOntologyNetworkTask(final CyNetwork network,
			String interactionType, boolean retainOtherInteraction) {
		super(network);
		this.interactionType = interactionType;
		this.retainOtherInteraction = retainOtherInteraction;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {

		taskMonitor.setTitle("Generating Ontology Network");

		OntologyNetwork ontologyNetwork = null;
		// if current network is already an ontology network, just create view
		// for it
		if (MyApplicationManager.getInstance()
				.hasOntologyNetworkFromUnderlyingCyNetwork(network)) {
			ontologyNetwork = MyApplicationManager.getInstance()
					.getOntologyNetworkFromUnderlyingCyNetwork(network);
		} else if (MyApplicationManager.getInstance()
				.hasOntologyNetworkFromOriginalCyNetwork(network)) {
			ontologyNetwork = MyApplicationManager.getInstance()
					.getOntologyNetworkFromOriginalCyNetwork(network);
			if (!interactionType.equals(ontologyNetwork.getInteractionType())) {
				MyApplicationManager.getInstance()
						.removeOntologyNetworkByOriginalNetwork(network);
				ontologyNetwork = null;
			}
		}

		if (ontologyNetwork == null) {
			// this is the original network based on which this function is
			// going to generate an ontology network
			CyNetwork originalNetwork = network;

			// generate a new ontology originalNetwork based on the underlying
			// network and interaction type

			taskMonitor.setStatusMessage("Populating All Ontology Items");

			ontologyNetwork = OntologyNetworkUtils.generateNewOntologyNetwork(
					originalNetwork, interactionType, retainOtherInteraction);

			MyApplicationManager.getInstance().addOntologyNetwork(
					ontologyNetwork);

			MyApplicationManager.getInstance().getCytoscapeServiceManager()
					.getCyNetworkManager()
					.addNetwork(ontologyNetwork.getUnderlyingCyNetwork());
		}

		taskMonitor.setStatusMessage("Updating Ontology Plugin Panel");

		MyApplicationManager.getInstance().getOntologyPanelController()
				.setOntologyNetwork(ontologyNetwork);
	}

}
