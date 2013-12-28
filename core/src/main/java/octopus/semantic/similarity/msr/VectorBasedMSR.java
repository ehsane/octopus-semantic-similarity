package octopus.semantic.similarity.msr;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import octopus.semantic.similarity.resource.GraphResource;
import octopus.semantic.similarity.resource.IMSRResource;
import octopus.semantic.similarity.resource.IMSRResource.ResourceType;

import org.openrdf.model.URI;

import rainbownlp.machinelearning.MLExample;
import slib.sglib.model.graph.G;
import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;

public abstract class VectorBasedMSR implements IMSR{
	static Logger logger = Logger.getLogger("VectorBasedMSR");
	protected String msrName = "";
	@Override
	public String getMSRName(){
		return msrName;
	}
	public ResourceType getRequiredResourceType() {
		return ResourceType.CORPUS;
	}
	
	public VectorBasedMSR() throws Exception{
	}


		
}
