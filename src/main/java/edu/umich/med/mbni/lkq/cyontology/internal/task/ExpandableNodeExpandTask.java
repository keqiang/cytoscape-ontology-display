package edu.umich.med.mbni.lkq.cyontology.internal.task;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.AbstractCyEdit;

import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.edit.ExpandNodeEdit;

public class ExpandableNodeExpandTask implements Task {

	private CyNetworkView networkView;
	private CyNode nodeToExpand;
	
	public ExpandableNodeExpandTask(CyNode node, CyNetworkView networkView) {
		this.networkView = networkView;
		this.nodeToExpand = node;
	}
	
	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		if (!MyApplicationCenter.getInstance().hasEncapsulatingOntologyNetwork(networkView.getModel())) return;
		
		AbstractCyEdit expanding = new ExpandNodeEdit("expand", networkView, nodeToExpand);
		expanding.redo();
		MyApplicationCenter.getInstance().getApplicationManager().getCyUndoSupport().postEdit(expanding);
	}

}

