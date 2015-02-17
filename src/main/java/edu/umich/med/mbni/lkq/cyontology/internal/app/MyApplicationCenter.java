package edu.umich.med.mbni.lkq.cyontology.internal.app;

import java.util.HashMap;

import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.work.swing.DialogTaskManager;

import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateOntologyControlPanelTaskFactory;
import edu.umich.med.mbni.lkq.cyontology.internal.task.UpdateOntologyControlPanelTask.UpdateOntologyControlOptions;
import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyControlPanel;

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
				.indexOfComponent(OntologyControlPanel.CONTROL_PANEL_TITLE);

		if (index == -1)
			return;

		OntologyControlPanel ontologyViewerControlPanel = (OntologyControlPanel) cytoPanelWest
				.getComponentAt(index);

		UpdateOntologyControlOptions options = new UpdateOntologyControlOptions(false, true, false, null);
		
		UpdateOntologyControlPanelTaskFactory updateOntologyControlPanelTaskFactory = new UpdateOntologyControlPanelTaskFactory(ontologyViewerControlPanel, options);
		DialogTaskManager taskManager = appManager.getTaskManager();
		taskManager.execute(updateOntologyControlPanelTaskFactory
				.createTaskIterator(e.getNetwork()));

	}

	public static void registerApplicationManager(
			MyApplicationManager myApplicationManager) {
		appManager = myApplicationManager;

	}
}
