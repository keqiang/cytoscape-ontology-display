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

	public boolean hasCorrespondingOntologyNetwork(CyNetwork network) {
		Long networkSUID = network.getSUID();
		return allOntologyNetwork.containsKey(networkSUID);
	}
	
	public OntologyNetwork getCorrespondingOntologyNetwork(CyNetwork network) {
		Long networkSUID = network.getSUID();
		return allOntologyNetwork.get(networkSUID);
	}
	
	public OntologyNetwork getEncapsulatingOntologyNetwork(CyNetwork network) {
		Long networkSUID = network.getSUID();
		for (OntologyNetwork ontologyNetwork : allOntologyNetwork.values()) {
			if (ontologyNetwork.getUnderlyingNetwork().getSUID() == networkSUID)
				return ontologyNetwork;
		}
		return null;
	}

	public boolean hasEncapsulatingOntologyNetwork(CyNetwork network) {
		return getEncapsulatingOntologyNetwork(network) != null;
	}

	public ExpandableNode getExpandableNode(OntologyNetwork ontologyNetwork,
			Long nodeSUID) {
		return ontologyNetwork.getNode(nodeSUID);
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
