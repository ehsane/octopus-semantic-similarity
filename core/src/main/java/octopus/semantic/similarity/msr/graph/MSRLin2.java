package octopus.semantic.similarity.msr.graph;

import octopus.semantic.similarity.msr.GraphBasedMSR;
import octopus.semantic.similarity.resource.graph.WordNet;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;

public class MSRLin2 extends GraphBasedMSR {
	public MSRLin2() throws Exception {
		super();
		msrName = "Lin2";
	}
	public static void main(String[] args) throws Exception{
		WordNet wn = new WordNet();
		System.out.println(wn);
		MSRLin2 lin = new MSRLin2();
		System.out.println(lin.calculateSimilarity(wn, "apple", "tree"));
	}
	@Override
	public SMconf getSMConf() throws SLIB_Ex_Critic {
		 // First we define the information content (IC) we will use
        ICconf icConf = new IC_Conf_Topo("Sanchez", SMConstants.FLAG_ICI_SANCHEZ_2011);
         
        // Then we define the Semantic measure configuration
        SMconf smConf = new SMconf("Lin2", SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_LIN_1998_GRASM);
        smConf.setICconf(icConf);
        return smConf;
	}


}
