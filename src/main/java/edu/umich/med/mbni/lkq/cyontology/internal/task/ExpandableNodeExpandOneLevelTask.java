package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.AbstractCyEdit;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.edit.ExpandOneLevelEdit;

public class ExpandableNodeExpandOneLevelTask extends AbstractNodeViewTask {
	private CyNetworkView networkView;
	private View<CyNode> nodeView;

	public ExpandableNodeExpandOneLevelTask(View<CyNode> nodeView,
			CyNetworkView netView) {
		super(nodeView, netView);

		this.networkView = netView;
		this.nodeView = nodeView;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (!MyApplicationCenter.getInstance().hasEncapsulatingOntologyNetwork(
				networkView.getModel()))
			return;

		AbstractCyEdit expanding = new ExpandOneLevelEdit("expand one level",
				networkView, nodeView);
		expanding.redo();
		MyApplicationCenter.getInstance().getApplicationManager()
				.getCyUndoSupport().postEdit(expanding);

	}

}
