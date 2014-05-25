package octopus.semantic.similarity.resource.corpus;

import octopus.semantic.similarity.resource.TextualCorpusResource;
import rainbownlp.util.ConfigurationUtil;

public class BioNLPCorpus extends TextualCorpusResource{

	public BioNLPCorpus(){
		super("BioNLP", ConfigurationUtil.getValue("bioNLPRootPath"));
	}

	@Override
	public long getMatchedDocsCount(String word) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getMatchedDocsCount(String[] word) {
		// TODO Auto-generated method stub
		return 0;
	}



}