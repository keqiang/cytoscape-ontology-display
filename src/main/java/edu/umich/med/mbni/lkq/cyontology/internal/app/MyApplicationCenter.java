package edu.umich.med.mbni.lkq.cyontology.internal.app;

import java.util.HashMap;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;

import edu.umich.med.mbni.lkq.cyontology.internal.controller.OntologyPanelController;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;

public class MyApplicationCenter implements NetworkAboutToBeDestroyedListener, NetworkViewAboutToBeDestroyedListener {

	private static MyApplicationCenter instance = null;
	private static MyApplicationManager appManager;
	private HashMap<Long, OntologyNetwork> originalNetworkToOntologyNetwork;
	private HashMap<Long, OntologyNetwork> underlyingNetworkToOntologyNetwork;
	private OntologyPanelController ontologyPanelController;
	
	private String globalLayoutAlgorithm;

	public static MyApplicationCenter getInstance() {

		if (instance == null) {
			instance = new MyApplicationCenter();
		}
		return instance;
	}

	private MyApplicationCenter() {
		this.originalNetworkToOntologyNetwork = new HashMap<Long, OntologyNetwork>();
		this.underlyingNetworkToOntologyNetwork = new HashMap<Long, OntologyNetwork>();
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

	public void addOntologyNetwork(OntologyNetwork ontologyNetwork) {
		originalNetworkToOntologyNetwork.put(ontologyNetwork.getOriginalCyNetwork().getSUID(),
				ontologyNetwork);
		underlyingNetworkToOntologyNetwork.put(ontologyNetwork.getUnderlyingCyNetwork().getSUID(), ontologyNetwork);
	}
	
	public void removeOntologyNetworkByOriginalNetwork(CyNetwork network) {
		OntologyNetwork removedNetwork = originalNetworkToOntologyNetwork.remove(network.getSUID());
		underlyingNetworkToOntologyNetwork.remove(removedNetwork.getUnderlyingCyNetwork().getSUID());
	}

	public OntologyNetwork getOntologyNetworkFromOriginalCyNetwork(CyNetwork network) {
		Long networkSUID = network.getSUID();
		return originalNetworkToOntologyNetwork.get(networkSUID);
	}
	
	public OntologyNetwork getOntologyNetworkFromUnderlyingCyNetwork(CyNetwork network) {
		Long networkSUID = network.getSUID();
		return underlyingNetworkToOntologyNetwork.get(networkSUID);
	}

	public boolean hasOntologyNetworkFromOriginalCyNetwork(CyNetwork network) {
		return originalNetworkToOntologyNetwork.containsKey(network.getSUID());
	}
	
	public boolean hasOntologyNetworkFromUnderlyingCyNetwork(CyNetwork network) {
		return underlyingNetworkToOntologyNetwork.containsKey(network.getSUID());
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
	
	public void setOntologyPluginPanelController(OntologyPanelController controller) {
		this.ontologyPanelController = controller;
	}

	@Override
	public void handleEvent(NetworkViewAboutToBeDestroyedEvent e) {
		Long networkSUID = e.getNetworkView().getModel().getSUID();
		removeNetwork(networkSUID);
	}

	private void removeNetwork(Long networkSUID) {
		OntologyNetwork removedNetwork = underlyingNetworkToOntologyNetwork.remove(networkSUID);
		originalNetworkToOntologyNetwork.remove(removedNetwork.getOriginalCyNetwork().getSUID());
	}
	
	public OntologyPanelController getOntologyPanelController() {
		return ontologyPanelController;
	}
	
	
}
