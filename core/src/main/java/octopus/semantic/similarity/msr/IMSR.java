package octopus.semantic.similarity.msr;

import octopus.semantic.similarity.resource.IMSRResource;
import octopus.semantic.similarity.resource.IMSRResource.ResourceType;
import rainbownlp.machinelearning.LearnerEngine;

/**
 * This is the common interface that each MSR algorithm should implement
 * @author eemadzadeh
 *
 */
public interface IMSR{
	public ResourceType getRequiredResourceType();
	public double calculateSimilarity(IMSRResource resource, String word1, String word2) 
		throws Exception;
	public String getMSRName();
}
