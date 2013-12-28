package octopus.semantic.similarity.resource.corpus;

import octopus.semantic.similarity.resource.CorpusResource;
import rainbownlp.util.ConfigurationUtil;

public class BioNLPCorpus extends CorpusResource{

	public BioNLPCorpus(){
		super("BioNLP", ConfigurationUtil.getValue("bioNLPRootPath"));
	}

	@Override
	public String getResourceName() {
		return "BioNLP";
	}



}
