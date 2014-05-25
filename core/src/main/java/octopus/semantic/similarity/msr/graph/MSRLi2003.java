package octopus.semantic.similarity.msr.graph;

import octopus.semantic.similarity.msr.GraphBasedMSR;
import octopus.semantic.similarity.resource.graph.WordNet;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;

public class MSRLi2003 extends GraphBasedMSR {
	public MSRLi2003() throws Exception {
		super();
		msrName = "Li2003";
	}
	public static void main(String[] args) throws Exception{
		WordNet wn = new WordNet();
		System.out.println(wn);
		MSRLi2003 lin = new MSRLi2003();
		System.out.println(lin.calculateSimilarity(wn, "apple", "tree"));
	}
	@Override
	public SMconf getSMConf() throws SLIB_Ex_Critic {
        return new 
        		SMconf("Li2003", SMConstants.FLAG_SIM_PAIRWISE_DAG_EDGE_LI_2003 );
	}

}
