package edu.umich.med.mbni.lkq.cyontology.internal.app;

import java.util.HashMap;

import org.cytoscape.model.CyNetwork;

import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;

public class MyApplicationCenter {
	
	private static MyApplicationManager appManager;
	private static HashMap<Long, OntologyNetwork> allOntologyNetwork = new HashMap<Long, OntologyNetwork>();
	
	public static void registerApplicationManager(MyApplicationManager myApplicationManager) {
		appManager = myApplicationManager;
	}
	
	public static MyApplicationManager getApplicationManager() {
		return appManager;
	}
	
	public static void addNewOntologyNetwork(OntologyNetwork network) {
		allOntologyNetwork.put(network.getOriginNetwork().getSUID(), network);
	}
	
	public static OntologyNetwork getOntologyNetwork(Long networkSUID) {
		return allOntologyNetwork.get(networkSUID);
	}
	
	public static boolean hasOntologyNetwork(Long networkSUID) {
		return allOntologyNetwork.containsKey(networkSUID);
	}
	
	public static ExpandableNode getExpandableNode(OntologyNetwork ontologyNetwork, Long nodeSUID) {
		return ontologyNetwork.getNode(nodeSUID);
	}
	
	public static OntologyNetwork getCorrespondingOntologyNetwork(CyNetwork network) {
		for (OntologyNetwork ontologyNetwork : allOntologyNetwork.values()) {
			if (ontologyNetwork.getUnderlyingNetwork().getSUID() == network.getSUID())
				return ontologyNetwork;
		}
		return null;
	}
}
