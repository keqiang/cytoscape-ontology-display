package edu.umich.med.mbni.lkq.cyontology.internal;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.group.CyGroupFactory;
import org.cytoscape.group.CyGroupManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;

import edu.umich.med.mbni.lkq.cyontology.internal.utils.DelayedVizProp;
import edu.umich.med.mbni.lkq.cyontology.internal.utils.OntologyNetworkUtils;

public class RefactorOntologyDisplayAction extends AbstractCyAction {

	private CyApplicationManager applicationManager;
	private CyGroupFactory groupFactory;
	private CyGroupManager groupManager;
	private CyNetworkViewManager networkViewManager;
	private VisualMappingManager vmMgr;
	private CyNetworkViewFactory networkViewFactory;
	private CyNetworkFactory networkFactory;
	private CyNetworkManager networkManager;
	private CyEventHelper eventHelper;

	public static int count = 0;
	
	public static final String INTERACTION_IS_A = "is_a";
	public static final String INTERACTION_HAS_PART = "has_part";
	public static final String INTERACTION_PART_OF = "part_of";
	public static final String INTERACTION_REGULATES = "regulates";
	public static final String INTERACTION_OCCURS_IN = "occurs_in";

	private static final long serialVersionUID = 7170161875226705765L;

	public RefactorOntologyDisplayAction(CySwingApplication desktopApp,
			CyApplicationManager applicationManager, CyGroupFactory groupFactory, CyGroupManager groupManager, CyNetworkViewManager networkViewManager, VisualMappingManager vmMgr, CyNetworkFactory networkFactory, CyNetworkManager networkManager, CyNetworkViewFactory networkViewFactory, CyEventHelper eventHelper) {
		super("Create collapsable and expandable ontology network");

		this.applicationManager = applicationManager;
		this.groupFactory = groupFactory;
		this.groupManager = groupManager;
		this.networkViewManager = networkViewManager;
		this.vmMgr = vmMgr;
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
		this.networkViewFactory = networkViewFactory;
		this.eventHelper = eventHelper;

		setPreferredMenu("Apps.Ontology X");
	}
	
	private static ExpandableNode getExpandableNodeInNetwork(Long nodeSUID, Map<Long, ExpandableNode> nodeMap, CyNetwork network) {
		ExpandableNode node = nodeMap.get(nodeSUID);
		if (node == null) {
			node = new ExpandableNode(network);
			nodeMap.put(nodeSUID, node);
		}
		
		return node;
	}

	public void actionPerformed(ActionEvent e) {

		LinkedList<DelayedVizProp> vizProps = new LinkedList<DelayedVizProp>();
		
		OntologyNetwork testOntologyNetwork = convertNetworkToOntology(applicationManager.getCurrentNetwork(), networkFactory, "test", vizProps);

		networkManager.addNetwork(testOntologyNetwork.getUnderlyingNetwork());
		
		CyNetworkView networkView = networkViewFactory.createNetworkView(testOntologyNetwork.getUnderlyingNetwork());
		
		networkViewManager.addNetworkView(networkView);
		
		eventHelper.flushPayloadEvents();
		

		DelayedVizProp.applyAll(networkView, vizProps);

	}
	
	public static OntologyNetwork convertNetworkToOntology(CyNetwork originNetwork, CyNetworkFactory networkFactory, String networkName, LinkedList<DelayedVizProp> vizProps) {
		
		HashMap<Long, ExpandableNode> createdNodes = new HashMap<Long, ExpandableNode>();
		CyNetwork createdNetwork = networkFactory.createNetwork();
		createdNetwork.getRow(createdNetwork).set(CyNetwork.NAME, networkName);
		
		List<CyNode> allNodes = originNetwork.getNodeList();
		
		for (CyNode sourceNode : allNodes) {
			
			Long sourceNodeSUID = sourceNode.getSUID();
			String sourceNodeName = originNetwork.getRow(sourceNode).get(CyNetwork.NAME, String.class);
			ExpandableNode sourceExpandableNode = getExpandableNodeInNetwork(sourceNodeSUID, createdNodes, createdNetwork);
			
			
			DelayedVizProp vizProp = new DelayedVizProp(sourceExpandableNode.getCyNode(), BasicVisualLexicon.NODE_WIDTH, 30.0, true);
			vizProps.add(vizProp);
			vizProp = new DelayedVizProp(sourceExpandableNode.getCyNode(), BasicVisualLexicon.NODE_HEIGHT, 30.0, true);
			vizProps.add(vizProp);
			
			createdNetwork.getRow(sourceExpandableNode.getCyNode()).set(CyNetwork.NAME, sourceNodeName);
			
			List<CyNode> neighborNodes = originNetwork.getNeighborList(sourceNode, CyEdge.Type.DIRECTED);
			
			for (CyNode targetNode : neighborNodes) {
				
				Long targetNodeSUID = targetNode.getSUID();
				ExpandableNode targetExpandableNode = getExpandableNodeInNetwork(targetNodeSUID, createdNodes, createdNetwork);
				vizProp = new DelayedVizProp(targetExpandableNode.getCyNode(), BasicVisualLexicon.NODE_WIDTH, 30.0, true);
				vizProps.add(vizProp);
				vizProp = new DelayedVizProp(targetExpandableNode.getCyNode(), BasicVisualLexicon.NODE_HEIGHT, 30.0, true);
				vizProps.add(vizProp);
				
				List<CyEdge> edges = originNetwork.getAdjacentEdgeList(sourceNode, CyEdge.Type.INCOMING);
				
				for (CyEdge edge : edges) {
					String interactionType = originNetwork.getRow(edge).get(
							CyEdge.INTERACTION, String.class);
					
					if (interactionType.equalsIgnoreCase(INTERACTION_IS_A)) {
						if (!sourceExpandableNode.hasChild(targetExpandableNode)) {
							sourceExpandableNode.addChildNode(targetExpandableNode);
							CyEdge connectingEdge = createdNetwork.addEdge(targetExpandableNode.getCyNode(), sourceExpandableNode.getCyNode(), true);
							createdNetwork.getRow(connectingEdge).set(CyEdge.INTERACTION, INTERACTION_IS_A);
							String targetNodeName = originNetwork.getRow(targetNode).get(CyNetwork.NAME, String.class);
							createdNetwork.getRow(targetExpandableNode.getCyNode()).set(CyNetwork.NAME, targetNodeName);
						}
					}
				}
			}
		}
		
		return new OntologyNetwork(originNetwork, createdNetwork, createdNodes);
	}
}
