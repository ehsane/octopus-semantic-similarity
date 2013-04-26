package octopus.semantic.similarity.msr;

import java.util.ArrayList;
import java.util.List;

import octopus.semantic.similarity.resource.IMSRResource;

/**
 * This is the common interface that each MSR algorithm should implement
 * @author eemadzadeh
 *
 */
public interface IMSR {
	List<IMSRResource> requiredResources = new ArrayList<IMSRResource>();

	public List<IMSRResource> getRequiredResources();
}
