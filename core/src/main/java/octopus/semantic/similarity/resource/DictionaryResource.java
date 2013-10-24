package octopus.semantic.similarity.resource;

import java.util.HashMap;


public abstract class DictionaryResource implements IMSRResource{
	final ResourceType resourceType = ResourceType.DICTIONARY;

	public DictionaryResource(){
	}

	public ResourceType getResourceType() {
		return resourceType;
	}
	
	public abstract String getGlossDefinition(String word);
	public abstract boolean hasWord(String word);
	public abstract HashMap<String, String> getAllDicEntries();
	
}
