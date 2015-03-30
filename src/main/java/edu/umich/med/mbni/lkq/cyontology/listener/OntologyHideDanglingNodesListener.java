package edu.umich.med.mbni.lkq.cyontology.listener;

import edu.umich.med.mbni.lkq.cyontology.event.OntologyHideDanglingNodesEvent;

public interface OntologyHideDanglingNodesListener {
	public void ontologyHideDanglingNodesPerformed(OntologyHideDanglingNodesEvent event);
}
