package edu.umich.med.mbni.lkq.cyontology.internal.app;

import java.util.HashMap;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;

public class MyApplicationCenter implements NetworkAboutToBeDestroyedListener {

	private static MyApplicationCenter instance = null;
	private static MyApplicationManager appManager;
	private HashMap<Long, OntologyNetwork> allOntologyNetwork;

	public static MyApplicationCenter getInstance() {

		if (instance == null) {
			instance = new MyApplicationCenter();
		}
		return instance;
	}

	private MyApplicationCenter() {
		this.allOntologyNetwork = new HashMap<Long, OntologyNetwork>();
	}

	public MyApplicationManager getApplicationManager() {
		return appManager;
	}

	public void addNewOntologyNetwork(OntologyNetwork network) {
		allOntologyNetwork.put(network.getOriginNetwork().getSUID(), network);
	}

	public OntologyNetwork getOntologyNetwork(Long networkSUID) {
		return allOntologyNetwork.get(networkSUID);
	}

	public boolean hasOntologyNetwork(Long networkSUID) {
		return allOntologyNetwork.containsKey(networkSUID);
	}

	public ExpandableNode getExpandableNode(OntologyNetwork ontologyNetwork,
			Long nodeSUID) {
		return ontologyNetwork.getNode(nodeSUID);
	}

	public OntologyNetwork getCorrespondingOntologyNetwork(CyNetwork network) {
		for (OntologyNetwork ontologyNetwork : allOntologyNetwork.values()) {
			if (ontologyNetwork.getUnderlyingNetwork().getSUID() == network
					.getSUID())
				return ontologyNetwork;
		}
		return null;
	}

	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		Long networkSUID = e.getNetwork().getSUID();
		for (OntologyNetwork ontologyNetwork : allOntologyNetwork.values()) {
			if (ontologyNetwork.getUnderlyingNetwork().getSUID() == networkSUID) {
				allOntologyNetwork.remove(ontologyNetwork.getOriginNetwork()
						.getSUID());
			}
		}
	}

	public static void registerApplicationManager(
			MyApplicationManager myApplicationManager) {
		appManager = myApplicationManager;
		
	}
}
