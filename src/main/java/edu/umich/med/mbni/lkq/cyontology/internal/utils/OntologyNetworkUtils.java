package edu.umich.med.mbni.lkq.cyontology.internal.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import edu.umich.med.mbni.lkq.cyontology.internal.ExpandableNode;
import edu.umich.med.mbni.lkq.cyontology.internal.OntologyNetwork;

public class OntologyNetworkUtils {
	
	public static final String INTERACTION_IS_A = "is_a";
	public static final String INTERACTION_HAS_PART = "has_part";
	public static final String INTERACTION_PART_OF = "part_of";
	public static final String INTERACTION_REGULATES = "regulates";
	public static final String INTERACTION_OCCURS_IN = "occurs_in";
	
	private static ExpandableNode getExpandableNodeInNetwork(Long nodeSUID, Map<Long, ExpandableNode> nodeMap, CyNetwork network) {
		ExpandableNode node = nodeMap.get(nodeSUID);
		if (node == null) {
			node = new ExpandableNode(network);
		}
		
		return node;
	}
	
	/**
	 * @param originNetwork the original network
	 * @param networkFactory network factory used to create a new ontology network
	 * @return the newly created network
	 */
	public static OntologyNetwork convertNetworkToOntology(CyNetwork originNetwork, CyNetworkFactory networkFactory, String networkName, CyEventHelper eventHelper) {
		LinkedList<DelayedVizProp> vizProps = new LinkedList<DelayedVizProp>();
		
		HashMap<Long, ExpandableNode> createdNodes = new HashMap<Long, ExpandableNode>();
		CyNetwork createdNetwork = networkFactory.createNetwork();
		createdNetwork.getRow(createdNetwork).set(CyNetwork.NAME, networkName);
		
		List<CyNode> allNodes = originNetwork.getNodeList();
		
		for (CyNode sourceNode : allNodes) {
			
			Long sourceNodeSUID = sourceNode.getSUID();
			String sourceNodeName = originNetwork.getRow(sourceNode).get(CyNetwork.NAME, String.class);
			ExpandableNode sourceExpandableNode = getExpandableNodeInNetwork(sourceNodeSUID, createdNodes, createdNetwork);
			
			DelayedVizProp vizProp = new DelayedVizProp(sourceExpandableNode.getCyNode(), BasicVisualLexicon.NODE_SIZE, 10, true);
			vizProps.add(vizProp);
			
			createdNetwork.getRow(sourceExpandableNode.getCyNode()).set(CyNetwork.NAME, sourceNodeName);
			
			List<CyNode> neighborNodes = originNetwork.getNeighborList(sourceNode, CyEdge.Type.DIRECTED);
			
			for (CyNode targetNode : neighborNodes) {
				
				Long targetNodeSUID = targetNode.getSUID();
				ExpandableNode targetExpandableNode = getExpandableNodeInNetwork(targetNodeSUID, createdNodes, createdNetwork);
				vizProp = new DelayedVizProp(targetExpandableNode.getCyNode(), BasicVisualLexicon.NODE_SIZE, 10, true);
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
		
		eventHelper.flushPayloadEvents();
		
		
		return new OntologyNetwork(originNetwork, createdNetwork, createdNodes);
	}
	
	/**
     * @author Keqiang Li.
     * @param propType to which property the settings will be applied
     * @param in the input stream from the OBO file
     * @return the imported OBO file as a tree structured data object
     *
     */
//    public static void importOntologyAsNetworkFromOBOFile(CyNetworkFactory networkFactory, InputStream in) {
//
//    	CyNetwork ontologyNetwork = networkFactory.createNetwork();
//    	HashMap<String ,CyNode> existingNodes = new HashMap<String, CyNode>();
//    	
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//
//        String line;
//        String currentChildTerm = null;
//        
//        CyNode newNode = null;
//
//        try {
//            while ((line = reader.readLine()) != null) {
//                if (line.startsWith("[Term]")) {
//                    newNode = null;
//                } else {
//                	if (newNode == null) newNode = ontologyNetwork.addNode();
//                    int index = line.indexOf(":");
//                    if (line.startsWith("id")) {
//                        currentChildTerm = line.substring(index + 1).trim();
//                        ontologyNetwork.getRow(newNode).set("id", currentChildTerm);
//                        existingNodes.put(currentChildTerm, newNode);
//                    } else if (line.startsWith(CyNetwork.NAME)) {
//                        String uniqueName = line.substring(index + 1).trim();
//                        ontologyNetwork.getRow(newNode).set(CyNetwork.NAME, uniqueName);
//                    } else if (line.startsWith("is_a")) {
//                        line = line.substring(index + 1);
//                        currentGroup.setParent(line);
//                        groupSetting.addGroupBranch(line, currentChildTerm);
//                    }
//                }
//            }
//            reader.close();
//        } catch (Exception e) {
//            return null;
//        }
//
//        if (currentGroup != null) {
//            groupSetting.addGroup(currentGroup);
//        }
//        groupSetting.validate();
//        return groupSetting;
//    }
	

}
