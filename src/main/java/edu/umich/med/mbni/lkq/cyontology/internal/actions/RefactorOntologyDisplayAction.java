package edu.umich.med.mbni.lkq.cyontology.internal.actions;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.model.CyNetworkView;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationCenter;
import edu.umich.med.mbni.lkq.cyontology.internal.app.MyApplicationManager;
import edu.umich.med.mbni.lkq.cyontology.internal.model.OntologyNetwork;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.DelayedVizProp;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.OntologyNetworkUtils;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.ViewOperationUtils;

public class RefactorOntologyDisplayAction extends AbstractCyAction {

	private static final long serialVersionUID = 7170161875226705765L;
	private MyApplicationManager appManager;
	private String curLayoutName;

	public RefactorOntologyDisplayAction(String layoutName) {
		super("Create collapsable and expandable ontology network");
		appManager = MyApplicationCenter.getInstance().getApplicationManager();
		curLayoutName = layoutName;
		setPreferredMenu("Apps.Ontology X");
	}

	public void actionPerformed(ActionEvent e) {

		Long originNetworkSUID = appManager.getCyApplicationManager()
				.getCurrentNetwork().getSUID();
		OntologyNetwork testOntologyNetwork;
		if (!MyApplicationCenter.getInstance().hasOntologyNetwork(originNetworkSUID)) {
	
			
			LinkedList<DelayedVizProp> vizProps = new LinkedList<DelayedVizProp>();
			testOntologyNetwork = OntologyNetworkUtils
					.convertNetworkToOntology(appManager.getCyApplicationManager()
							.getCurrentNetwork(), appManager.getCyNetworkFactory(), vizProps);
			MyApplicationCenter.getInstance().addNewOntologyNetwork(testOntologyNetwork);

			appManager.getCyNetworkManager().addNetwork(
					testOntologyNetwork.getUnderlyingNetwork());

			CyNetworkView networkView = appManager.getCyNetworkViewFactory()
					.createNetworkView(testOntologyNetwork.getUnderlyingNetwork());

			appManager.getCyNetworkViewManager().addNetworkView(networkView);

			appManager.getCyEventHelper().flushPayloadEvents();

			DelayedVizProp.applyAll(networkView, vizProps);
			ViewOperationUtils.reLayoutNetwork(
					appManager.getCyLayoutAlgorithmManager(), networkView,
					curLayoutName);
		} else {
			testOntologyNetwork = MyApplicationCenter.getInstance().getOntologyNetwork(originNetworkSUID);
		}
				

	}
}
