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
import rainbownlp.util.HibernateUtil;
import rainbownlp.util.caching.CacheEntry;

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
			String cacheKey = "MSR-"+msr.getMSRName()+"-"+resource.getResourceName()+"-"+word1+"-"+word2;
			CacheEntry similarityFromCache = CacheEntry.get(cacheKey);
			Double similarity = 0D;
			if(similarityFromCache == null) {
					try {
						similarity = msr.calculateSimilarity(resource, word1, word2);
						if(similarity==null || similarity.isNaN()) continue;
						similarityFromCache = CacheEntry.getInstance(cacheKey);
						similarityFromCache.setValue(similarity.toString());
						HibernateUtil.save(similarityFromCache);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}else{
				similarity = Double.parseDouble(similarityFromCache.getValue());
			}
			
			results.add(similarity);
			
			String featureName = "MSR_"+msr.getMSRName()+"-"+resource.getResourceName();
			addFeature(example, featureName, similarity);
			
			MLExample msrResourceExample = MLExample.getInstanceForLink(example.getRelatedPhraseLink(), featureName);
			msrResourceExample.setExpectedClass(example.getExpectedClass());
			msrResourceExample.setPredictedClass(similarity);
			MLExample.saveExample(msrResourceExample);
		}
		return results;
	}


	private static void addFeature(MLExample example, String featureName,
			Double value) {
		if(value==null) return;
		FeatureValuePair newFeature = FeatureValuePair.getInstance(featureName, value.toString());
		MLExampleFeature.setFeatureExample(example, newFeature);
	}
	public static List<String> getMSRFeatures(){
		List<String> features = FeatureValuePair.getAllFeatureNames();
		ArrayList<String> msrFeatures = new ArrayList<String>();
		for(String feature : features){
			if(feature.startsWith("MSR_"))
				msrFeatures.add(feature);
		}
		return msrFeatures;
	}


	public static List<SimpleEntry<IMSR, IMSRResource>> getMsrResorceCombinations() {
		return msrResorceCombinations;
	}

}
