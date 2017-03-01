package edu.umich.med.mbni.lkq.cyontology.event;

import java.util.EventObject;

import edu.umich.med.mbni.lkq.cyontology.model.OntologyNetwork;

public class OntologyNetworkChangedEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3624279126248323479L;
	
	private final OntologyNetwork oldOntologyNetwork;
	private final OntologyNetwork newOntologyNetwork;

	public OntologyNetworkChangedEvent(Object source, OntologyNetwork oldOntologyNetwork, OntologyNetwork newOntologyNetwork) {
		super(source);
		this.oldOntologyNetwork = oldOntologyNetwork;
		this.newOntologyNetwork = newOntologyNetwork;
	}

	public OntologyNetwork getOldOntologyNetwork() {
		return oldOntologyNetwork;
	}

	public OntologyNetwork getNewOntologyNetwork() {
		return newOntologyNetwork;
	}

}
