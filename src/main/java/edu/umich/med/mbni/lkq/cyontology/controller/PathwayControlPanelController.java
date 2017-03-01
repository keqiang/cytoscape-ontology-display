package edu.umich.med.mbni.lkq.cyontology.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.view.model.CyNetworkView;

import edu.umich.med.mbni.lkq.cyontology.event.OverallFINetworkGeneSelectedEvent;
import edu.umich.med.mbni.lkq.cyontology.listener.OverallFINetworkGeneSelectedListener;
import edu.umich.med.mbni.lkq.cyontology.view.PathwayControlPanel;

public class PathwayControlPanelController implements
		OverallFINetworkGeneSelectedListener, RowsSetListener {

	private PathwayControlPanel pathwayControlPanel;
	private CyNetworkView overallFINetworkView;

	private ArrayList<OverallFINetworkGeneSelectedListener> overallFINetworkGeneSelectedListeners;

	private static PathwayControlPanelController instance = new PathwayControlPanelController();

	private PathwayControlPanelController() {
		init();
	}

	private void init() {
		overallFINetworkGeneSelectedListeners = new ArrayList<OverallFINetworkGeneSelectedListener>();
		overallFINetworkGeneSelectedListeners.add(this);
	}

	public static PathwayControlPanelController getInstance() {
		return instance;
	}

	public void setPathwayControlPanel(PathwayControlPanel controlPanel) {
		this.pathwayControlPanel = controlPanel;
	}

	public void setOverallCyNetworkView(CyNetworkView view) {
		this.overallFINetworkView = view;
	}

	private void markHitGeneNumberInPathwayTree(Collection<String> hitGenes) {
		if (pathwayControlPanel != null) {
			pathwayControlPanel.markHitGeneNumberInPathwayTree(hitGenes);
		}
	}

	@Override
	public void overallFINetworkGeneSelected(
			OverallFINetworkGeneSelectedEvent event) {
		Collection<String> selectedGenes = event.getSelectedGenes();
		markHitGeneNumberInPathwayTree(selectedGenes);
	}

	@Override
	public void handleEvent(RowsSetEvent event) {

		if (!event.containsColumn(CyNetwork.SELECTED)
				|| overallFINetworkView == null
				|| overallFINetworkView.getModel() == null
				|| overallFINetworkView.getModel().getDefaultEdgeTable() == null
				|| overallFINetworkView.getModel().getDefaultNodeTable() == null) {
			return;
		}

		List<CyNode> nodes = CyTableUtil.getNodesInState(
				overallFINetworkView.getModel(), CyNetwork.SELECTED, true);
		if (nodes == null || nodes.isEmpty())
			return;
		CyTable table = event.getSource();
		Set<String> selectedGenes = new HashSet<String>();
		for (CyNode node : nodes) {
			String nodeName = table.getRow(node.getSUID()).get(CyNetwork.NAME,
					String.class);
			selectedGenes.add(nodeName);
		}
		if (!selectedGenes.isEmpty())
			fireOverallFINetworkGeneSelectedEvent(selectedGenes);
	}

	public void fireOverallFINetworkGeneSelectedEvent(
			Collection<String> selectedGenes) {
		OverallFINetworkGeneSelectedEvent event = new OverallFINetworkGeneSelectedEvent(
				overallFINetworkView.getModel(), selectedGenes);
		for (OverallFINetworkGeneSelectedListener listener : overallFINetworkGeneSelectedListeners) {
			listener.overallFINetworkGeneSelected(event);
		}
	}
}
