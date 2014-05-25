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
		DICTIONARY, 
		TEXTUAL_CORPUS
	}
	
	public ResourceType getResourceType();
	public String getResourceName();
	/**
	 * Do any required mapping between word and acceptable content in the index
	 * For example for wordnet corpus convert word to senseId
	 * @param word
	 * @return
	 */
	public String getSearchableContentForWord(String word);
}
