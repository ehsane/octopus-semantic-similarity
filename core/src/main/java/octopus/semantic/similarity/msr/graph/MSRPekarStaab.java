package octopus.semantic.similarity.msr.graph;

import octopus.semantic.similarity.msr.GraphBasedMSR;
import octopus.semantic.similarity.resource.graph.WordNet;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;

public class MSRPekarStaab extends GraphBasedMSR {
	public MSRPekarStaab() throws Exception {
		super();
		msrName = "PekarStaab";
	}
	public static void main(String[] args) throws Exception{
		WordNet wn = new WordNet();
		System.out.println(wn);
		MSRPekarStaab lin = new MSRPekarStaab();
		System.out.println(lin.calculateSimilarity(wn, "apple", "tree"));
	}
	@Override
	public SMconf getSMConf() throws SLIB_Ex_Critic {
        return new 
        		SMconf("PekarStaab", SMConstants.FLAG_SIM_PAIRWISE_DAG_EDGE_PEKAR_STAAB_2002);
	}

}
