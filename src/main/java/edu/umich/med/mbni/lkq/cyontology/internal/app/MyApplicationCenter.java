package edu.umich.med.mbni.lkq.cyontology.internal.app;

import java.util.HashMap;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;

import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;

public class MyApplicationCenter implements NetworkAboutToBeDestroyedListener, NetworkViewAboutToBeDestroyedListener {

	private static MyApplicationCenter instance = null;
	private static MyApplicationManager appManager;
	private HashMap<Long, OntologyNetwork> allOntologyNetwork;
	
	private String globalLayoutAlgorithm;

	public static MyApplicationCenter getInstance() {

		if (instance == null) {
			instance = new MyApplicationCenter();
		}
		return instance;
	}

	private MyApplicationCenter() {
		this.allOntologyNetwork = new HashMap<Long, OntologyNetwork>();
		this.globalLayoutAlgorithm = "hierarchical";
	}
	
	public String getLayoutAlgorithmName() {
		return globalLayoutAlgorithm;
	}
	
	public void setLayoutAlgorithmName(String name) {
		this.globalLayoutAlgorithm = name;
	}

	public MyApplicationManager getApplicationManager() {
		return appManager;
	}

	public void addOntologyNetwork(OntologyNetwork network) {
		allOntologyNetwork.put(network.getUnderlyingNetwork().getSUID(),
				network);
	}
	
	public void removeOntologyNetwork(CyNetwork network) {
		allOntologyNetwork.remove(network.getSUID());
	}

	public OntologyNetwork getEncapsulatingOntologyNetwork(CyNetwork network) {
		Long networkSUID = network.getSUID();
		return allOntologyNetwork.get(networkSUID);
	}

	public boolean hasEncapsulatingOntologyNetwork(CyNetwork network) {
		return allOntologyNetwork.containsKey(network.getSUID());
	}

	public ExpandableNode getExpandableNode(OntologyNetwork ontologyNetwork,
			CyNode node) {
		return ontologyNetwork.getNode(node);
	}

	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		Long networkSUID = e.getNetwork().getSUID();
		removeNetwork(networkSUID);
	}

	public static void registerApplicationManager(
			MyApplicationManager myApplicationManager) {
		appManager = myApplicationManager;

	}

	@Override
	public void handleEvent(NetworkViewAboutToBeDestroyedEvent e) {
		Long networkSUID = e.getNetworkView().getModel().getSUID();
		removeNetwork(networkSUID);
	}

	private void removeNetwork(Long networkSUID) {
		for (OntologyNetwork ontologyNetwork : allOntologyNetwork.values()) {
			if (ontologyNetwork.getUnderlyingNetwork().getSUID() == networkSUID) {
				allOntologyNetwork.remove(ontologyNetwork
						.getUnderlyingNetwork().getSUID());
			}
		}
	}
	
	
}
