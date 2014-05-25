package octopus.semantic.similarity.msr.graph;

import octopus.semantic.similarity.msr.GraphBasedMSR;
import octopus.semantic.similarity.resource.graph.WordNet;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;

public class MSRKyogokuBasic extends GraphBasedMSR {
	public MSRKyogokuBasic() throws Exception {
		super();
		msrName = "KyogokuBasic";
	}
	public static void main(String[] args) throws Exception{
		WordNet wn = new WordNet();
		System.out.println(wn);
		MSRKyogokuBasic lin = new MSRKyogokuBasic();
		System.out.println(lin.calculateSimilarity(wn, "apple", "tree"));
	}
	@Override
	public SMconf getSMConf() throws SLIB_Ex_Critic {
        return new 
        		SMconf("KyogokuBasic", SMConstants.FLAG_SIM_PAIRWISE_DAG_EDGE_KYOGOKU_BASIC_2011  );
	}

}
