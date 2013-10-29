package octopus.semantic.similarity.msr.graph;

import octopus.semantic.similarity.msr.GraphBasedMSR;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;

public class MSRWuPalmer extends GraphBasedMSR {

	@Override
	public SMconf getSMConf() throws SLIB_Ex_Critic {
        return new 
        		SMconf("WuPalmer", SMConstants.SIM_PAIRWISE_DAG_EDGE_WU_PALMER_1994  );
	}

}
