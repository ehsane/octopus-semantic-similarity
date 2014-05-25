package octopus.semantic.similarity.msr.statistics;

import java.io.IOException;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import octopus.semantic.similarity.msr.IMSR;
import octopus.semantic.similarity.msr.VectorBasedMSR;
import octopus.semantic.similarity.resource.CorpusResource;
import octopus.semantic.similarity.resource.IMSRResource;
import octopus.semantic.similarity.resource.IMSRResource.ResourceType;
import octopus.semantic.similarity.resource.corpus.YahooAsCorpus;
import pitt.search.semanticvectors.CompoundVectorBuilder;
import pitt.search.semanticvectors.FlagConfig;
import pitt.search.semanticvectors.LuceneUtils;
import pitt.search.semanticvectors.VectorStoreReaderLucene;
import pitt.search.semanticvectors.vectors.Vector;

public class MSRPMI extends VectorBasedMSR{
	public static void main(String[] args) throws Exception{
		MSRPMI msr = new MSRPMI();
		System.out.println(msr.calculateSimilarity(new YahooAsCorpus(), "diabetes", "polyp"));
	}
	public MSRPMI() throws Exception {
		super();
	}

	static Logger logger = Logger.getLogger("MSRLSA");
	public ResourceType getRequiredResourceType() {
		return ResourceType.CORPUS;
	}

	public double calculateSimilarity(CorpusResource resource, String word1,
			String word2) throws IOException {
		long corpusSize = resource.getCorpusSize();
		long hitWord1 = resource.getMatchedDocsCount(word1);
		long hitWord2 = resource.getMatchedDocsCount(word2);
		long hitWord1And2 = resource.getMatchedDocsCount(new String[]{word1, word2});
		double pWord1 = (double)hitWord1 / (double)corpusSize;
		double pWord2 = (double)hitWord2 / (double)corpusSize;
		double pWord1And2 = (double)hitWord1And2 / (double)corpusSize;
		double sim = Math.log(pWord1And2 / (pWord1 * pWord2));
		return sim;
	}

	public double calculateSimilarity(IMSRResource resource, String word1,
			String word2) throws IOException {
		return calculateSimilarity((CorpusResource)resource, word1, word2);
	}

	@Override
	public String getMSRName() {
		return "PMI";
	}

}
