package edu.umich.med.mbni.lkq.cyontology.internal;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.group.CyGroup;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

/**
 * Creates a new menu item under Apps menu section.
 *
 */
public class RefactorOntologyDisplayAction extends AbstractCyAction {

	private CyApplicationManager applicationManager;
	private CyGroupFactory groupFactory;
	private CyGroupManager groupManager;

	public static final String INTERACTION_IS_A = "is_a";
	public static final String INTERACTION_HAS_PART = "has_part";
	public static final String INTERACTION_PART_OF = "part_of";
	public static final String INTERACTION_REGULATES = "regulates";
	public static final String INTERACTION_OCCURS_IN = "occurs_in";

	/**
	 * 
	 */
	private static final long serialVersionUID = 7170161875226705765L;

	public RefactorOntologyDisplayAction(CySwingApplication desktopApp,
			CyApplicationManager applicationManager, CyGroupFactory groupFactory, CyGroupManager groupManager) {
		super("Create collapsable and expandable ontology network");

		this.applicationManager = applicationManager;
		this.groupFactory = groupFactory;
		this.groupManager = groupManager;

		setPreferredMenu("Apps.Ontology X");
	}

	public void actionPerformed(ActionEvent e) {

		// get the current network model
		CyNetwork currentNetwork = applicationManager.getCurrentNetwork();
		// get the default edge table
		CyTable edgeTable = currentNetwork.getDefaultEdgeTable();

		List<CyNode> allNodes = currentNetwork.getNodeList();
		System.out.println(allNodes);
		
		for (CyNode sourceNode : allNodes) {
			List<CyNode> neighborNodes = currentNetwork.getNeighborList(sourceNode, Type.DIRECTED);
			List<CyNode> childNodes = new LinkedList<CyNode>();
			
			for (CyNode targetNode : neighborNodes) {
				List<CyEdge> outGoingEdges = currentNetwork.getConnectingEdgeList(sourceNode, targetNode, Type.OUTGOING);
				List<CyEdge> inComingEdges = currentNetwork.getConnectingEdgeList(sourceNode, targetNode, Type.INCOMING);
				
				for (CyEdge outEdge : outGoingEdges) {
					String interactionType = currentNetwork.getRow(outEdge).get(
							CyEdge.INTERACTION, String.class);
					
					if (interactionType.equalsIgnoreCase(INTERACTION_HAS_PART) || interactionType.equalsIgnoreCase(INTERACTION_REGULATES)) {
						childNodes.add(targetNode);
					}
				}
				
				for (CyEdge inEdge : inComingEdges) {
					String interactionType = currentNetwork.getRow(inEdge).get(
							CyEdge.INTERACTION, String.class);
					
					if (interactionType.equalsIgnoreCase(INTERACTION_IS_A) || interactionType.equalsIgnoreCase(INTERACTION_PART_OF) || interactionType.equalsIgnoreCase(INTERACTION_OCCURS_IN)) {
						childNodes.add(targetNode);
					}
				}
				
			}
			
			if (childNodes.isEmpty()) continue;
			
			CyGroup group = groupFactory.createGroup(currentNetwork, sourceNode, childNodes, null, true);
			
			System.out.println(group);
		}
	}

}
