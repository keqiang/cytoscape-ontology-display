package edu.umich.med.mbni.lkq.cyontology.internal.model;

public class PathwayEvent {
	String name;
    Long dbId;
    boolean isPathway;
    boolean hasDiagram;
    
    @Override
    public String toString() {
        return name;
    }

	public String getName() {
		return name;
	}

	public Long getDbId() {
		return dbId;
	}

	public boolean isPathway() {
		return isPathway;
	}

	public boolean isHasDiagram() {
		return hasDiagram;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDbId(Long dbId) {
		this.dbId = dbId;
	}

	public void setPathway(boolean isPathway) {
		this.isPathway = isPathway;
	}

	public void setHasDiagram(boolean hasDiagram) {
		this.hasDiagram = hasDiagram;
	}
}
