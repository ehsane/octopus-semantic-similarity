package octopus.semantic.similarity.resource;


public abstract class GraphResource implements IMSRResource{
	final ResourceType resourceType = ResourceType.GRAPH;

	public GraphResource(){
	}

	public ResourceType getResourceType() {
		return resourceType;
	}
	
	
}
