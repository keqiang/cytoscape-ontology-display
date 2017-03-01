package edu.umich.med.mbni.lkq.cyontology.action;

import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Random;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.swing.DialogTaskManager;

import edu.umich.med.mbni.lkq.cyontology.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.model.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.task.DEBUG_MapDataToColorTaskFactory;

public class DEBUG_GenerateFakeNodeData extends AbstractCyAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2750295913071192044L;

	public DEBUG_GenerateFakeNodeData(String name) {
		super(name);
		setPreferredMenu("Apps.Ontology Viewer.Debug");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String url = "http://reactomews.oicr.on.ca:8080/caBigR3WebApp2014/FIService/ReactomeRestful/"
				+ "pathwayHierarchy/Homo+sapiens";

		//PostMethod method = new PostMethod(url);

//		method.setRequestHeader("Accept", "text/plain, application/xml");
//		HttpClient client = new HttpClient();
//
//		try {
//		int responseCode = client.executeMethod(method);
//		if (responseCode == HttpStatus.SC_OK) {
//			InputStream is = method.getResponseBodyAsStream();
//			InputStreamReader isr = new InputStreamReader(is);
//			BufferedReader reader = new BufferedReader(isr);
//			StringBuilder builder = new StringBuilder();
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				builder.append(line).append("\n");
//			}
//			reader.close();
//			isr.close();
//			is.close();
//			// Remove the last new line
//			String rtn = builder.toString();
//			// Just in case an empty string is returned
//			System.out.println(rtn);
//		}} catch(Exception exception) {
//			
//		}

		MyApplicationManager appManager = MyApplicationManager.getInstance();

		CyNetwork underlyingNetwork = appManager.getCytoscapeServiceManager()
				.getCyApplicationManager().getCurrentNetwork();

		OntologyNetwork ontologyNetwork = appManager
				.getOntologyNetworkFromUnderlyingCyNetwork(underlyingNetwork);

		if (underlyingNetwork.getDefaultNodeTable().getColumn("test_data") == null) {
			underlyingNetwork.getDefaultNodeTable().createColumn("test_data",
					Double.class, false);
		}

		Random random = new Random(Calendar.getInstance().getTimeInMillis());
		for (CyNode node : underlyingNetwork.getNodeList()) {

			ExpandableNode expandableNode = ontologyNetwork
					.getNodeFromUnderlyingNode(node);

			// set value only for leaf nodes
			if (expandableNode.getDirectChildNodes().isEmpty())
				underlyingNetwork.getRow(node).set("test_data",
						random.nextDouble());
			else {
				underlyingNetwork.getRow(node).set("test_data", null);
			}
		}

		DEBUG_MapDataToColorTaskFactory taskFactory = new DEBUG_MapDataToColorTaskFactory();

		DialogTaskManager taskManager = appManager.getCytoscapeServiceManager()
				.getTaskManager();

		CyNetworkView networkView = appManager.getCytoscapeServiceManager()
				.getCyApplicationManager().getCurrentNetworkView();
		taskManager.execute(taskFactory.createTaskIterator(networkView));

	}

}
