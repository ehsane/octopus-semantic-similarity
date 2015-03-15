package octopus.semantic.similarity;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import octopus.semantic.similarity.msr.IMSR;
import octopus.semantic.similarity.resource.IMSRResource;
import octopus.semantic.similarity.resource.IMSRResource.ResourceType;
import rainbownlp.core.FeatureValuePair;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.machinelearning.MLExampleFeature;
import rainbownlp.util.HibernateUtil;
import rainbownlp.util.StringUtil;
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
	private static boolean synchronous = true; 
	
	public static void initialize() throws Exception{
		msrs = Configuration.config.getMsrList();
		resources = Configuration.config.getResourceList();
		for(IMSRResource resource: resources){
			for(IMSR msr  : msrs){
				if(msr.getRequiredResourceType().equals(resource.getResourceType())
						|| (msr.getRequiredResourceType()==ResourceType.CORPUS && resource.getResourceType()==ResourceType.TEXTUAL_CORPUS)){
					SimpleEntry<IMSR, IMSRResource> newCombination = new SimpleEntry<IMSR, IMSRResource>(msr, resource);
					msrResorceCombinations.add(newCombination);
				}
			}
		}
	}
	
	
	/**
	 * calculate semantic similarity using each combination of MSR and resource
	 * @param corpusName 
	 * 
	 * @param word1
	 * @param word2
	 * @return an array of similarities corresponding to array of combinations
	 * @throws Exception 
	 */
	public static HashMap<String, Double> calculateAllSimilarities(String corpusName,
			MLExample example, String word1, String word2) throws Exception{
		if(msrResorceCombinations.size()==0)
			initialize();
		HashMap<String, Double> results = new HashMap<String, Double>();
		if(Configuration.config.isTrimBeforeSimilarity()){
			word1 = StringUtil.removeNonAlphaAndLowercase(word1);
			word2 = StringUtil.removeNonAlphaAndLowercase(word2);
		}
		
		final String word1Trimmed = word1;
		final String word2Trimmed = word2;;
		
		MLExampleFeature.deleteExampleFeatures(example);
	    ExecutorService executor = Executors.newFixedThreadPool(10);
	    HashMap<String, Future<Double>> simResults = new HashMap<String, Future<Double>>();
		for(int i=0;i<msrResorceCombinations.size();i++){
			SimpleEntry<IMSR, IMSRResource> combination = msrResorceCombinations.get(i);
			final IMSR msr = combination.getKey();
			final IMSRResource resource = combination.getValue();
			String wordsPair = word1Trimmed+"-"+word2Trimmed;
			if(wordsPair.length()>100) 
				wordsPair = StringUtil.getStringDigest(word1Trimmed+"-"+word2Trimmed);
			final String cacheKey = "MSR-"+msr.getMSRName()+"-"+resource.getResourceName()+"-"+wordsPair;
			CacheEntry similarityFromCache = CacheEntry.get(cacheKey);
			String featureName = "MSR_"+msr.getMSRName()+"-"+resource.getResourceName();
			if(similarityFromCache == null) {
					try {
						if(synchronous ){
							Double similarity = msr.calculateSimilarity(resource, word1Trimmed, word2Trimmed);
							addSimilarityForExample(example, featureName, similarity, corpusName);
							CacheEntry.createInstance(cacheKey, similarity.toString());
							results.put(featureName, similarity);
						}else{
							Callable<Double> simCalculator = new  Callable<Double>() {
						          @Override
						          public Double call() throws Exception {
						        	  Double similarity = msr.calculateSimilarity(resource, word1Trimmed, word2Trimmed);
										System.out.println("Similarity "+cacheKey+" : "+similarity);
										if(similarity==null || similarity.isNaN()  || similarity.isInfinite()) similarity=0.0;
										CacheEntry similarityFromCache = CacheEntry.getInstance(cacheKey);
										similarityFromCache.setValue(similarity.toString());
										HibernateUtil.save(similarityFromCache);
										return similarity;
							      }
						        };
							
							simResults.put(featureName, executor.submit(simCalculator));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
			}else{
				Double similarity = Double.parseDouble(similarityFromCache.getValue());
				addSimilarityForExample(example, featureName, similarity, corpusName);
				results.put(featureName, similarity);
			}
		}
		if(!synchronous){
			for(String featureName : simResults.keySet()){
				Double similarity = simResults.get(featureName).get();
				addSimilarityForExample(example, featureName, similarity, corpusName);
				results.put(featureName, similarity);
			}
		}
		return results;
	}

	
	public static HashMap<String, Double> calculateAllSimilarities(String word1, String word2) throws Exception{
		if(msrResorceCombinations.size()==0)
			initialize();
		HashMap<String, Double> results = new HashMap<String, Double>();
		final String word1Trimmed = word1.replaceAll("[^0-9a-zA-Z]+", " ");
		final String word2Trimmed = word2.replaceAll("[^0-9a-zA-Z]+", " ");
	    for(int i=0;i<msrResorceCombinations.size();i++){
	    	SimpleEntry<IMSR, IMSRResource> combination = msrResorceCombinations.get(i);
	    	final IMSR msr = combination.getKey();
	    	final IMSRResource resource = combination.getValue();
	    	String featureName = "MSR_"+msr.getMSRName()+"-"+resource.getResourceName();
	    	try {
	    		Double similarity = msr.calculateSimilarity(resource, word1Trimmed, word2Trimmed);
	    		results.put(featureName, similarity);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
		return results;
	}


	private static void addSimilarityForExample(MLExample example, String featureName,
			Double similarity, String corpusName) {
		addFeature(example, featureName, similarity);
		
		MLExample.hibernateSession = null;//force to create a new session
		// Create example for oneByone evaluation
		MLExample msrResourceExample = MLExample.getInstanceForLink(example.getRelatedPhraseLink(), 
				corpusName+"-"+featureName);
		msrResourceExample.setExpectedClass(example.getExpectedClass());
		msrResourceExample.setPredictedClass(similarity.toString());
		MLExample.saveExample(msrResourceExample);
	}


	private static void addFeature(MLExample example, String featureName,
			Double value) {
		if(value==null) return;
		FeatureValuePair newFeature = FeatureValuePair.getInstance(featureName, value.toString());
		
		if(newFeature.getTempFeatureIndex()==-1){
			newFeature.setTempFeatureIndex(FeatureValuePair.getMinIndexForAttribute(featureName));
			HibernateUtil.save(newFeature);
		}
		
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
