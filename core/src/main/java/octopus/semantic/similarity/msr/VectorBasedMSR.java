package octopus.semantic.similarity.msr;

import java.util.HashMap;
import java.util.logging.Logger;

import octopus.semantic.similarity.resource.IMSRResource.ResourceType;
import octopus.semantic.similarity.resource.TextualCorpusResource;
import octopus.semantic.similarity.resource.TextualCorpusResource.TermVectorType;
import pitt.search.semanticvectors.vectors.Vector;

public abstract class VectorBasedMSR implements IMSR{
	static Logger logger = Logger.getLogger("VectorBasedMSR");
	protected String msrName = "";
	@Override
	public String getMSRName(){
		return msrName;
	}
	public ResourceType getRequiredResourceType() {
		return ResourceType.TEXTUAL_CORPUS;
	}
	public VectorBasedMSR() throws Exception{
	}
	@Override
	public Double call() throws Exception {
		if(resource == null ||
				word1 == null ||
				word2 == null)
			throw(new Exception("resource/word1/word2 is null"));
		return calculateSimilarity(resource, word1, word2);
	}

		
}
