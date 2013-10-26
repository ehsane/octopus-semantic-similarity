package octopus.semantic.similarity.msr;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import octopus.semantic.similarity.resource.CorpusResource;
import octopus.semantic.similarity.resource.IMSRResource;
import octopus.semantic.similarity.resource.IMSRResource.ResourceType;
import pitt.search.semanticvectors.CompoundVectorBuilder;
import pitt.search.semanticvectors.FlagConfig;
import pitt.search.semanticvectors.LuceneUtils;
import pitt.search.semanticvectors.VectorStoreReaderLucene;
import pitt.search.semanticvectors.vectors.Vector;

public class MSRLSA implements IMSR{
	static Logger logger = Logger.getLogger("MSRLSA");
	public ResourceType getRequiredResourceType() {
		return ResourceType.CORPUS;
	}

	public double calculateSimilarity(CorpusResource resource, String word1,
			String word2) throws IOException {
		LuceneUtils luceneUtils = null;

		VectorStoreReaderLucene vecReader = null;
		FlagConfig flagConfig = FlagConfig.parseFlagsFromString("-ludeneindex "+resource.getLuceneIndexFile());
		
		
		try {
			vecReader = new VectorStoreReaderLucene(flagConfig.queryvectorfile(), flagConfig);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to open vector store from file: " + flagConfig.queryvectorfile());
			throw e;
		}

		Vector vec1 = CompoundVectorBuilder.getQueryVectorFromString(vecReader,
				luceneUtils, flagConfig, word1);
		Vector vec2 = CompoundVectorBuilder.getQueryVectorFromString(vecReader,
				luceneUtils, flagConfig, word2);
		vec1.normalize();
		vec2.normalize();
		vecReader.close();
		
		double simScore = vec1.measureOverlap(vec2);

		return simScore;
	}

	public double calculateSimilarity(IMSRResource resource, String word1,
			String word2) {
		return calculateSimilarity(resource, word1, word2);
	}

}
