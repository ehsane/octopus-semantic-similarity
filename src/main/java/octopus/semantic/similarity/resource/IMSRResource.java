package octopus.semantic.similarity.resource;

/**
 * Any new resource should implement this interface
 * @author eemadzadeh
 *
 */
public interface IMSRResource {
	public static enum ResourceType{
		GRAPH,
		CORPUS,
		DICTIONARY
	}
	
	public ResourceType getResourceType();
	public String getResourceName();
}
