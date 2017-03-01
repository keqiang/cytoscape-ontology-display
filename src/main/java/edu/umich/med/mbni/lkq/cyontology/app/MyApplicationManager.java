package edu.umich.med.mbni.lkq.cyontology.app;

import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewAboutToBeDestroyedListener;

import edu.umich.med.mbni.lkq.cyontology.controller.OntologyPanelController;
import edu.umich.med.mbni.lkq.cyontology.listener.OntologyNetworkRemovedListener;
import edu.umich.med.mbni.lkq.cyontology.model.OntologyNetwork;

public class MyApplicationManager implements NetworkAboutToBeDestroyedListener, NetworkViewAboutToBeDestroyedListener, OntologyNetworkRemovedListener {

	private static MyApplicationManager instance = new MyApplicationManager();
	private static CytoscapeServiceManager cytoscapeServiceManager;
	private HashMap<Long, OntologyNetwork> originalNetworkToOntologyNetwork;
	private HashMap<Long, OntologyNetwork> underlyingNetworkToOntologyNetwork;
	private OntologyPanelController ontologyPanelController;
	
	private List<OntologyNetworkRemovedListener> ontologyNetworkRemovedListeners;
	
	private String globalLayoutAlgorithm;

	public static MyApplicationManager getInstance() {
		return instance;
	}

	private MyApplicationManager() {
		this.originalNetworkToOntologyNetwork = new HashMap<Long, OntologyNetwork>();
		this.underlyingNetworkToOntologyNetwork = new HashMap<Long, OntologyNetwork>();
		this.globalLayoutAlgorithm = "hierarchical";
		this.ontologyNetworkRemovedListeners = new LinkedList<OntologyNetworkRemovedListener>();
		addOntologyNetworkRemovedListener(this);
	}
	
	public void addOntologyNetworkRemovedListener(OntologyNetworkRemovedListener listener) {
		ontologyNetworkRemovedListeners.add(listener);
	}
	
	public String getLayoutAlgorithmName() {
		return globalLayoutAlgorithm;
	}
	
	public void setLayoutAlgorithmName(String name) {
		this.globalLayoutAlgorithm = name;
	}

	public CytoscapeServiceManager getCytoscapeServiceManager() {
		return cytoscapeServiceManager;
	}

	public void addOntologyNetwork(OntologyNetwork ontologyNetwork) {
		originalNetworkToOntologyNetwork.put(ontologyNetwork.getOriginalCyNetwork().getSUID(),
				ontologyNetwork);
		underlyingNetworkToOntologyNetwork.put(ontologyNetwork.getUnderlyingCyNetwork().getSUID(), ontologyNetwork);
	}
	
	public void removeOntologyNetworkByOriginalNetwork(CyNetwork network) {
		OntologyNetwork removedNetwork = originalNetworkToOntologyNetwork.remove(network.getSUID());
		underlyingNetworkToOntologyNetwork.remove(removedNetwork.getUnderlyingCyNetwork().getSUID());
		EventObject event = new EventObject(removedNetwork);
		fireOntologyRemovedEvent(event);
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

	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		Long networkSUID = e.getNetwork().getSUID();
		removeNetwork(networkSUID);
	}

	public static void registerApplicationManager(
			CytoscapeServiceManager myApplicationManager) {
		cytoscapeServiceManager = myApplicationManager;

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
	
	public void fireOntologyRemovedEvent(EventObject event) {
		for (OntologyNetworkRemovedListener listener : ontologyNetworkRemovedListeners) {
			listener.ontologyNetworkRemoved(event);
		}
	}

	@Override
	public void ontologyNetworkRemoved(EventObject event) {
		OntologyNetwork removedNetwork = (OntologyNetwork)event.getSource();
		
		CyNetworkViewManager networkViewManager = cytoscapeServiceManager.getCyNetworkViewManager();
		Collection<CyNetworkView> underlyingNetworkViews = networkViewManager.getNetworkViews(removedNetwork.getUnderlyingCyNetwork());
		for (CyNetworkView networkView : underlyingNetworkViews) {
			networkViewManager.destroyNetworkView(networkView);
		}
		
		CyNetworkManager networkManager = cytoscapeServiceManager.getCyNetworkManager();
		networkManager.destroyNetwork(removedNetwork.getUnderlyingCyNetwork());
	}
	
	
}
