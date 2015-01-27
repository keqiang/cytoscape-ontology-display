package edu.umich.med.mbni.lkq.cyontology.internal;

import java.io.IOException;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

//Define a task
public class CreateTableTask extends AbstractTask {
	public static int numImports = 0;
	private CyTableFactory tableFactory;

	public CreateTableTask(CyTableFactory tableFactory) {
		this.tableFactory = tableFactory;
	}

	@Override
	public void run(TaskMonitor tm) throws IOException {
		// Step 1: create a new table
		CyTable table = tableFactory.createTable(
				"MyAttrTable " + Integer.toString(numImports++), "name",
				String.class, true, true);

		// create a column for the table
		String attributeName = "MyAttributeName";
		table.createColumn(attributeName, Integer.class, false);

		// Step 2: populate the table with some data
		String[] keys = { "YLL021W", "YBR170C", "YLR249W" }; // map to the the
																// "name" column
		CyRow row = table.getRow(keys[0]);
		row.set(attributeName, new Integer(2));

		row = table.getRow(keys[1]);
		row.set(attributeName, new Integer(3));

		row = table.getRow(keys[2]);
		row.set(attributeName, new Integer(4));

		// We are loading node attribute
//		Class<? extends CyTableEntry> type = CyNode.class;

		// Step 3: pass the new table to MapNetworkAttrTask
//		super.insertTasksAfterCurrentTask(new MapNetworkAttrTask(type, table,
//				netMgr, appMgr, rootNetworkManager));
	}
}
