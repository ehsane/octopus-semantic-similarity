package octopus.semantic.similarity.resource;

import java.util.List;

public abstract class CorpusResource implements IMSRResource{
	final ResourceType resourceType = ResourceType.CORPUS;

	public CorpusResource(){
	}

	public ResourceType getResourceType() {
		return resourceType;
	}
	
	public abstract String getDocByIndex(int index);
	public abstract List<String> getAllDocs();
	public abstract int getCorpusSize();
}
