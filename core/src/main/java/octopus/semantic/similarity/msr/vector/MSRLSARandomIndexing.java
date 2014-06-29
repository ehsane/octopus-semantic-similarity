package octopus.semantic.similarity.msr.vector;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import octopus.semantic.similarity.msr.VectorBasedMSR;
import octopus.semantic.similarity.resource.CorpusResource;
import octopus.semantic.similarity.resource.IMSRResource;
import octopus.semantic.similarity.resource.TextualCorpusResource;
import pitt.search.semanticvectors.CompoundVectorBuilder;
import pitt.search.semanticvectors.FlagConfig;
import pitt.search.semanticvectors.LuceneUtils;
import pitt.search.semanticvectors.VectorStoreReaderLucene;
import pitt.search.semanticvectors.vectors.Vector;

public class MSRLSARandomIndexing extends VectorBasedMSR{
	public MSRLSARandomIndexing() throws Exception {
		super();
	}

	static Logger logger = Logger.getLogger("MSRLSA");


	public double calculateSimilarity(TextualCorpusResource resource, String word1,
			String word2) throws IOException {
		Vector vec1 = resource.getWordVector(word1, "termvectors.bin", "docvectors.bin");
		Vector vec2 = resource.getWordVector(word2, "termvectors.bin", "docvectors.bin");

		vec1.normalize();
		vec2.normalize();
		
		double simScore = vec1.measureOverlap(vec2);

		return simScore;
	}

	public double calculateSimilarity(IMSRResource resource, String word1,
			String word2) throws IOException {
		return calculateSimilarity((CorpusResource)resource, word1, word2);
	}

	@Override
	public String getMSRName() {
		return "LSA";
	}

}
