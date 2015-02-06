package edu.umich.med.mbni.lkq.cyontology.internal.app;

import java.util.HashMap;

import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyViewerControlPanel;

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
		for (OntologyNetwork ontologyNetwork : allOntologyNetwork.values()) {
			if (ontologyNetwork.getUnderlyingNetwork().getSUID() == networkSUID) {
				allOntologyNetwork.remove(ontologyNetwork
						.getUnderlyingNetwork().getSUID());
			}
		}

		CytoPanel cytoPanelWest = MyApplicationCenter.getInstance()
				.getApplicationManager().getCyDesktopService()
				.getCytoPanel(CytoPanelName.WEST);

		int index = cytoPanelWest
				.indexOfComponent(OntologyViewerControlPanel.CONTROL_PANEL_TITLE);

		if (index == -1)
			return;

		OntologyViewerControlPanel ontologyViewerControlPanel = (OntologyViewerControlPanel) cytoPanelWest
				.getComponentAt(index);

		ontologyViewerControlPanel.rePopTheAggregationValues();

	}

	public static void registerApplicationManager(
			MyApplicationManager myApplicationManager) {
		appManager = myApplicationManager;

	}
}
