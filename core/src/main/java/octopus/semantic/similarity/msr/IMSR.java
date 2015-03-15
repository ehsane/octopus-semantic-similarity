package octopus.semantic.similarity.msr;

import java.util.concurrent.Callable;

import octopus.semantic.similarity.resource.IMSRResource;
import octopus.semantic.similarity.resource.IMSRResource.ResourceType;
import rainbownlp.machinelearning.LearnerEngine;

/**
 * This is the common interface that each MSR algorithm should implement
 * @author eemadzadeh
 *
 */
public interface IMSR extends Callable<Double> {
	public IMSRResource resource = null;
	public String word1 = null;
	public String word2 = null;
	
	public ResourceType getRequiredResourceType();
	public double calculateSimilarity(IMSRResource resource, String word1, String word2) 
		throws Exception;
	public String getMSRName();
}
