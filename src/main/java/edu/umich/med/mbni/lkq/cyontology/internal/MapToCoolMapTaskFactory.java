package edu.umich.med.mbni.lkq.cyontology.internal;

import java.util.LinkedList;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

public class MapToCoolMapTaskFactory implements NetworkTaskFactory{

	public MapToCoolMapTaskFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public TaskIterator createTaskIterator(final CyNetwork network) {
		Task[] initialTasks = new Task[1];
		initialTasks[0] = new Task() {
			
			@Override
			public void run(TaskMonitor arg0) throws Exception {
				// TODO Auto-generated method stub

				if (network == null) return;
			    
			    List<CyNode> selectedNodes = CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true);
			    if (selectedNodes == null || selectedNodes.size() == 0) return;
			    
			    LinkedList<String> results = new LinkedList<>();
			    
			    for (CyNode node : selectedNodes) {
			    	results.add(network.getRow(node).get(CyNetwork.NAME, String.class));
			    }
			    
			    System.out.println("test");
			}
			
			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}
		};
		
		
		return new TaskIterator(initialTasks);
	}

	@Override
	public boolean isReady(CyNetwork arg0) {
		// TODO Auto-generated method stub
		return true;
	}

}
