package octopus.semantic.similarity.msr.vector;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import octopus.semantic.similarity.msr.IMSR;
import octopus.semantic.similarity.msr.VectorBasedMSR;
import octopus.semantic.similarity.resource.CorpusResource;
import octopus.semantic.similarity.resource.IMSRResource;
import octopus.semantic.similarity.resource.IMSRResource.ResourceType;
import pitt.search.semanticvectors.CompoundVectorBuilder;
import pitt.search.semanticvectors.FlagConfig;
import pitt.search.semanticvectors.LuceneUtils;
import pitt.search.semanticvectors.VectorStoreReaderLucene;
import pitt.search.semanticvectors.vectors.Vector;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;

public class MSRLSA extends VectorBasedMSR{
	public MSRLSA() throws Exception {
		super();
	}

	static Logger logger = Logger.getLogger("MSRLSA");

	public double calculateSimilarity(CorpusResource resource, String word1,
			String word2) throws IOException {
		LuceneUtils luceneUtils = null;

		VectorStoreReaderLucene vecReader = null;
		FlagConfig flagConfig = FlagConfig.parseFlagsFromString("-luceneindexpath "+resource.getLuceneIndexFile()+
				" -termvectorsfile svd_termvectors.bin -docvectorsfile svd_docvectors");
		
		try {
			vecReader = new VectorStoreReaderLucene(flagConfig.termvectorsfile(), flagConfig);
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
			String word2) throws IOException {
		return calculateSimilarity((CorpusResource)resource, word1, word2);
	}

	@Override
	public String getMSRName() {
		return "LSA";
	}


}
