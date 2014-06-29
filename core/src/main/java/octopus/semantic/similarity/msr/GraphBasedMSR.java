package octopus.semantic.similarity.msr;

import java.io.IOException;
import java.util.logging.Logger;

import octopus.semantic.similarity.resource.GraphResource;
import octopus.semantic.similarity.resource.IMSRResource;
import octopus.semantic.similarity.resource.IMSRResource.ResourceType;

import org.openrdf.model.URI;

import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;

public abstract class GraphBasedMSR implements IMSR{
	static Logger logger = Logger.getLogger("GraphBasedMSR");
	protected String msrName = "";
	@Override
	public String getMSRName(){
		return msrName;
	}
	public ResourceType getRequiredResourceType() {
		return ResourceType.GRAPH;
	}
	
	public GraphBasedMSR() throws Exception{
		icConf = new IC_Conf_Topo("Sanchez", 
				SMConstants.FLAG_ICI_SANCHEZ_2011);
	}

	public abstract SMconf getSMConf() throws SLIB_Ex_Critic;
	ICconf icConf = null;

	public double calculateSimilarity(GraphResource resource, String word1,
			String word2) throws IOException, SLIB_Ex_Critic {

		
		SM_Engine engine = resource.getSMEngine();

		/*
		 * Now the Semantic similarity computation
		 * We will use an Lin measure using the information content 
		 * definition proposed by Sanchez et al.
		 * 
		 */
		// First we define the information content (IC) we will use

		// Then we define the Semantic measure configuration
		SMconf smConf = getSMConf();
		smConf.setICconf(icConf);

		// Finally, we compute the similarity between the concepts Horse and Whale
		URI word1URI = resource.getWordURI(word1);
		URI word2URI = resource.getWordURI(word2);
		if(word1URI.stringValue().endsWith("null") ||
				word2URI.stringValue().endsWith("null")){
			System.out.println("didn't found a match in graph for ("+word1+" or "+word2+")");
			return 0;
		}
			
		double sim = getSimilarity(engine, smConf, word1URI, word2URI);
		System.out.println("Similarity "+msrName+" ("+word1+","+word2+")="+sim);
		return sim;
	}

	protected double getSimilarity(SM_Engine engine, SMconf smConf, URI word1uri,
			URI word2uri) throws SLIB_Ex_Critic {
		return engine.computePairwiseSim(smConf, word1uri, word2uri);
	}
	public double calculateSimilarity(IMSRResource resource, String word1,
			String word2) throws SLIB_Ex_Critic, IOException {
		String[] words1 = word1.split(" ");
		String[] words2 = word2.split(" ");
		Double count = 0.0;
		Double totalSimilarity=0.0;
		for(String w1: words1)
			for(String w2: words2){
				totalSimilarity += calculateSimilarity((GraphResource)resource, w1, w2);
				count++;
			}
		if(count ==0 ) return 0;
		return totalSimilarity/count;
	}
	

		
}
