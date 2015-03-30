package edu.umich.med.mbni.lkq.cyontology.model;

public class OntologyTerm {
	String ontologyId;
	String definition;
	
	@Override
	public String toString() {
		return ontologyId;
	}

	public String getOntologyId() {
		return ontologyId;
	}

	public String getDefinition() {
		return definition;
	}

	public void setOntologyId(String ontologyId) {
		this.ontologyId = ontologyId;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	
}
