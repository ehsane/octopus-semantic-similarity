package octopus.semantic.similarity.msr.graph;

import org.openrdf.model.URI;

import octopus.semantic.similarity.msr.GraphBasedMSR;
import octopus.semantic.similarity.resource.graph.WordNet;
import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;

public class MSRRada extends GraphBasedMSR {
	public MSRRada() throws Exception {
		super();
		msrName = "Rada";
	}
	public static void main(String[] args) throws Exception{
		WordNet wn = new WordNet();
		System.out.println(wn);
		MSRRada lin = new MSRRada();
		System.out.println(lin.calculateSimilarity(wn, "apple", "tree"));
	}
	@Override
	public SMconf getSMConf() throws SLIB_Ex_Critic {
		 SMconf smConf = new 
        		SMconf("Rada", SMConstants.FLAG_SIM_PAIRWISE_DAG_EDGE_RADA_1989 );
		smConf.setPairwise_measure_id(SMConstants.FLAG_SIM_PAIRWISE_DAG_EDGE_RADA_1989);
		return smConf;
	}


}
