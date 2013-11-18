package octopus.semantic.similarity;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import octopus.semantic.similarity.msr.IMSR;
import octopus.semantic.similarity.resource.IMSRResource;
import rainbownlp.core.FeatureValuePair;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.machinelearning.MLExampleFeature;
import rainbownlp.util.ConfigurationUtil;

/**
 * This class match every MR with available resources and calculate the semantic 
 * similarity for each pair using all possible method&resource combination 
 * @author Ehsan
 *
 */
public class SemanticSimilarityBlender {
	static List<IMSR> msrs = new ArrayList<IMSR>(); 
	static List<IMSRResource> resources = new ArrayList<IMSRResource>();
	
	private static List<SimpleEntry<IMSR, IMSRResource>> msrResorceCombinations
	 	= new ArrayList<SimpleEntry<IMSR,IMSRResource>>(); 
	
	public static void initialize() throws Exception{
		ConfigurationUtil.init("config.properties");
		String[] msrsClassNames = ConfigurationUtil.getArrayValues("msrs");
		String[] resourcesClassNames = ConfigurationUtil.getArrayValues("resources");
		
		getMsrResorceCombinations().clear();
		for(String msrClassName : msrsClassNames){
			IMSR msr = (IMSR) Class.forName(msrClassName).newInstance();
			msrs.add(msr);
		}
		for(String resourceClassName: resourcesClassNames){
			IMSRResource resource = (IMSRResource) Class.forName(resourceClassName).newInstance();
			resources.add(resource);
		}
		for(IMSRResource resource: resources){
			for(IMSR msr  : msrs){
				if(msr.getRequiredResourceType().equals(resource.getResourceType())){
					SimpleEntry<IMSR, IMSRResource> newCombination = new SimpleEntry<IMSR, IMSRResource>(msr, resource);
					getMsrResorceCombinations().add(newCombination);
				}
			}
		}
	}
	
	
	/**
	 * calculate semantic similarity using each combination of MSR and resource
	 * 
	 * @param word1
	 * @param word2
	 * @return an array of similarities corresponding to array of combinations
	 * @throws Exception 
	 */
	public static List<Double> calculateAllSimilarities(MLExample example, String word1, String word2) throws Exception{
		if(msrResorceCombinations.size()==0)
			initialize();
		List<Double> results = new ArrayList<Double>();
		for(int i=0;i<msrResorceCombinations.size();i++){
			SimpleEntry<IMSR, IMSRResource> combination = msrResorceCombinations.get(i);
			IMSR msr = combination.getKey();
			IMSRResource resource = combination.getValue();
			
			Double similarity = CacheManager.getSimilarity(resource, word1, word2);
			if(similarity == null) {
					try {
						similarity = msr.calculateSimilarity(resource, word1, word2);
						CacheManager.setSimilarity(resource, word1, word2, similarity);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
			
			results.add(similarity);
			
			String featureName = msr.getMSRName()+"-"+resource.getResourceName();
			addFeature(example, featureName, similarity);
		}
		return results;
	}


	private static void addFeature(MLExample example, String featureName,
			Double value) {
		if(value==null) return;
		FeatureValuePair newFeature = FeatureValuePair.getInstance(featureName, value.toString());
		MLExampleFeature.setFeatureExample(example, newFeature);
	}


	public static List<SimpleEntry<IMSR, IMSRResource>> getMsrResorceCombinations() {
		return msrResorceCombinations;
	}

}
