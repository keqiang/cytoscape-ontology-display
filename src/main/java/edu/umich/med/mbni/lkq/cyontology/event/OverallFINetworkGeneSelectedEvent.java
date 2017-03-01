package edu.umich.med.mbni.lkq.cyontology.event;

import java.util.Collection;
import java.util.EventObject;

public class OverallFINetworkGeneSelectedEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5642777284975963382L;
	private Collection<String> selectedGenes;
	
	public OverallFINetworkGeneSelectedEvent(Object source, Collection<String> selectedGenes) {
		super(source);
		this.selectedGenes = selectedGenes;
	}
	
	public Collection<String> getSelectedGenes() {
		return selectedGenes;
	}

}
