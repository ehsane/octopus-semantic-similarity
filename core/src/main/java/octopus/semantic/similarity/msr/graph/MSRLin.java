package octopus.semantic.similarity.msr.graph;

import java.util.List;

import octopus.semantic.similarity.msr.GraphBasedMSR;
import octopus.semantic.similarity.resource.graph.WordNet;
import rainbownlp.machinelearning.MLExample;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;

public class MSRLin extends GraphBasedMSR {
	public MSRLin() throws Exception {
		super();
		msrName = "Lin";
	}
	public static void main(String[] args) throws Exception{
		WordNet wn = new WordNet();
		System.out.println(wn);
		MSRLin lin = new MSRLin();
		System.out.println(lin.calculateSimilarity(wn, "apple", "tree"));
	}
	@Override
	public SMconf getSMConf() throws SLIB_Ex_Critic {
		 // First we define the information content (IC) we will use
        ICconf icConf = new IC_Conf_Topo("Sanchez", SMConstants.FLAG_ICI_SANCHEZ_2011);
         
        // Then we define the Semantic measure configuration
        SMconf smConf = new SMconf("Lin", SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_LIN_1998);
        smConf.setICconf(icConf);
        return smConf;
	}



}
