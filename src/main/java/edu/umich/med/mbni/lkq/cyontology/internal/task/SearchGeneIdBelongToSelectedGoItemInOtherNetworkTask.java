package edu.umich.med.mbni.lkq.cyontology.internal.task;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.util.GeneOntologyIdToSymbolMapping;

/**
 * @author keqiangli use this task to find selected nodes in other networks
 *         current in the session
 *
 */
public class SearchGeneIdBelongToSelectedGoItemInOtherNetworkTask extends AbstractNodeViewTask {

	public SearchGeneIdBelongToSelectedGoItemInOtherNetworkTask(View<CyNode> nodeView,
			CyNetworkView netView) {
		super(nodeView, netView);
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {

		CyNetwork curNetwork = netView.getModel();
		
		if (!MyApplicationManager.getInstance().hasOntologyNetworkFromUnderlyingCyNetwork(curNetwork)) {
			return;
		}
		
		taskMonitor.setTitle("Searching for nodes in other networks");
		
		List<CyNode> nodes = CyTableUtil.getNodesInState(netView.getModel(),
				"selected", true);

		taskMonitor.setProgress(0.1);
		
		List<String> nodeNames = new LinkedList<String>();

		for (CyNode node : nodes) {
			nodeNames.add(curNetwork.getRow(node).get(CyNetwork.NAME,
					String.class));
		}

		taskMonitor.setProgress(0.2);
		
		Set<String> symbols = GeneOntologyIdToSymbolMapping.MapGoIdToSymbol(nodeNames);

		MyApplicationManager appManager = MyApplicationManager.getInstance();

		Set<CyNetwork> allNetworks = appManager.getCytoscapeServiceManager()
				.getCyNetworkManager().getNetworkSet();

		if (allNetworks.size() < 2)
			return;

		double curProgress = 0.2;
		double increment = 0.8 / allNetworks.size() - 1;

		for (CyNetwork network : allNetworks) {
			if (network == curNetwork)
				continue;
			for (String symbol : symbols) {
				Collection<CyRow> matchingRows = network.getDefaultNodeTable()
						.getMatchingRows(CyNetwork.NAME, symbol);
				for (CyRow row : matchingRows) {
					row.set("selected", true);
				}
			}
			curProgress += increment;
			taskMonitor.setProgress(curProgress);
		}
		
		taskMonitor.setProgress(1.0);
	}

}
