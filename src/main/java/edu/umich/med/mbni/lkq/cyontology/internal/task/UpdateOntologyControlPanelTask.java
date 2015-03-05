package edu.umich.med.mbni.lkq.cyontology.internal.task;

import java.util.Collection;
import java.util.HashSet;

import javax.swing.tree.DefaultMutableTreeNode;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.view.OntologyPluginPanel;

public class UpdateOntologyControlPanelTask extends AbstractNetworkTask {

	public static class UpdateOntologyControlOptions {
		public boolean updateInteraction;
		public boolean updateAggregationColumns;
		public boolean updateOntologyTree;
		public String interactionType;

		public UpdateOntologyControlOptions(boolean updateInteraction,
				boolean updateAggregationColumns, boolean updateOntologyTree,
				String interactionType) {
			this.updateAggregationColumns = updateAggregationColumns;
			this.updateInteraction = updateInteraction;
			this.updateOntologyTree = updateOntologyTree;
			this.interactionType = interactionType;
		}
	}

	private final OntologyPluginPanel ontologyControlPanel;
	private final UpdateOntologyControlOptions options;

	public UpdateOntologyControlPanelTask(final CyNetwork network,
			final OntologyPluginPanel ontologyControlPanel,
			final UpdateOntologyControlOptions options) {
		super(network);
		this.ontologyControlPanel = ontologyControlPanel;
		this.options = options;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (options.updateAggregationColumns)
			rePopAggregationColumns();
		if (options.updateInteraction)
			rePopInteractionType();
		if (options.updateOntologyTree)
			rePopOntologyTree();
	}

	private void rePopOntologyTree() {
		if (!MyApplicationManager.getInstance().hasOntologyNetworkFromUnderlyingCyNetwork(
				network))
			return;
		
		OntologyNetwork ontologyNetwork = MyApplicationManager.getInstance()
				.getOntologyNetworkFromUnderlyingCyNetwork(network);

		DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(
				"Ontology Tree");

		Collection<ExpandableNode> allRootNodes = ontologyNetwork.getAllRootNodes();
		for (ExpandableNode root : allRootNodes) {
			if (root.getDirectChildNodes().isEmpty())
				continue;
			DefaultMutableTreeNode ontologyRoot = new DefaultMutableTreeNode(
					root);
			populateTree(ontologyRoot, root);
			treeRoot.add(ontologyRoot);
		}

		MyApplicationManager.getInstance().getOntologyPanelController()
				.setOntologyTree(treeRoot, ontologyNetwork);
	}

	private void populateTree(DefaultMutableTreeNode ontologyRoot,
			ExpandableNode root) {

		for (ExpandableNode child : root.getDirectChildNodes()) {
			DefaultMutableTreeNode childOntologyNode = new DefaultMutableTreeNode(
					child);
			populateTree(childOntologyNode, child);
			ontologyRoot.add(childOntologyNode);
		}

	}

	private void rePopInteractionType() {
		if (!MyApplicationManager.getInstance().hasOntologyNetworkFromUnderlyingCyNetwork(
				network))
			return;
		
		CyNetwork originaNetwork = MyApplicationManager.getInstance().getOntologyNetworkFromUnderlyingCyNetwork(network).getOriginalCyNetwork();
		
		Collection<CyRow> allRows = originaNetwork.getDefaultEdgeTable().getAllRows();

		HashSet<String> allTypes = new HashSet<String>();

		for (CyRow row : allRows) {
			String interactionType = row.get(CyEdge.INTERACTION, String.class);
			allTypes.add(interactionType);
		}

		ontologyControlPanel.setInteractionTypeChoice(allTypes,
				options.interactionType);
	}

	private void rePopAggregationColumns() {

		if (!MyApplicationManager.getInstance().hasOntologyNetworkFromUnderlyingCyNetwork(
				network))
			return;

		Collection<CyColumn> allColumns = network.getDefaultNodeTable()
				.getColumns();

		HashSet<String> allAggregationColumns = new HashSet<String>();

		for (CyColumn column : allColumns) {
			if (column.getType() == Double.class) {
				allAggregationColumns.add(column.getName());
			}
		}

		ontologyControlPanel.setAggregationColumnChoice(allAggregationColumns);
	}

}
