package edu.umich.med.mbni.lkq.cyontology.internal.listener;

import edu.umich.med.mbni.lkq.cyontology.internal.event.OntologyHideDanglingNodesEvent;

public interface OntologyHideDanglingNodesListener {
	public void ontologyHideDanglingNodesPerformed(OntologyHideDanglingNodesEvent event);
}
