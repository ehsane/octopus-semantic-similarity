package octopus.semantic.similarity.msr;

import java.io.IOException;
import java.util.logging.Logger;

import octopus.semantic.similarity.resource.GraphResource;
import octopus.semantic.similarity.resource.IMSRResource;
import octopus.semantic.similarity.resource.IMSRResource.ResourceType;

import org.openrdf.model.URI;

import slib.sglib.model.graph.G;
import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;

public abstract class GraphBasedMSR implements IMSR{
	static Logger logger = Logger.getLogger("GraphBasedMSR");
	public ResourceType getRequiredResourceType() {
		return ResourceType.GRAPH;
	}

	public abstract SMconf getSMConf() throws SLIB_Ex_Critic;
	
	public double calculateSimilarity(GraphResource resource, String word1,
			String word2) throws IOException, SLIB_Ex_Critic {
		   
	        G graph = resource.getGraph();
	        // General information about the graph
	        System.out.println(graph.toString());
	        SM_Engine engine = new SM_Engine(graph);
	       
	        /*
	         * Now the Semantic similarity computation
	         * We will use an Lin measure using the information content 
	         * definition proposed by Sanchez et al.
	         * 
	         */
	        // First we define the information content (IC) we will use
	        ICconf icConf = new IC_Conf_Topo("Sanchez", 
	        		SMConstants.FLAG_ICI_SANCHEZ_2011);
	         
	        // Then we define the Semantic measure configuration
	        SMconf smConf = getSMConf();
	        smConf.setICconf(icConf);
	         
	        // Finally, we compute the similarity between the concepts Horse and Whale
	        URI word1URI = resource.getWordURI(word1);
	        URI word2URI = resource.getWordURI(word2);
	        
	        double sim = engine.computePairwiseSim(smConf, word1URI, word2URI);
	        return sim;
	}

	public double calculateSimilarity(IMSRResource resource, String word1,
			String word2) throws SLIB_Ex_Critic, IOException {
		return calculateSimilarity((GraphResource)resource, word1, word2);
	}

}
