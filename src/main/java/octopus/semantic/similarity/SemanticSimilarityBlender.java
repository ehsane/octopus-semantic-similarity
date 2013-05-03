package octopus.semantic.similarity;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import octopus.semantic.similarity.msr.IMSR;
import octopus.semantic.similarity.msr.MSRLSA;
import octopus.semantic.similarity.resource.IMSRResource;

/**
 * This class match every MR with available resources and calculate the semantic 
 * similarity for each pair using all possible method&resource combination 
 * @author Ehsan
 *
 */
public class SemanticSimilarityBlender {
	static IMSR[] msrs = new IMSR[]{new MSRLSA()}; 
	static IMSRResource[] resources = new IMSRResource[]{};
	private static List<SimpleEntry<IMSR, IMSRResource>> msrResorceCombinations
	 	= new ArrayList<SimpleEntry<IMSR,IMSRResource>>(); 
	
	public static void initialize(){
		getMsrResorceCombinations().clear();
		for(IMSR msr : msrs){
			for(IMSRResource resource : resources){
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
	 */
	public static List<Double> calculateAllSimilarities(String word1, String word2){
		List<Double> results = new ArrayList<Double>();
		for(int i=0;i<msrResorceCombinations.size();i++){
			SimpleEntry<IMSR, IMSRResource> combination = msrResorceCombinations.get(i);
			IMSR msr = combination.getKey();
			IMSRResource resource = combination.getValue();
			
			Double similarity = CacheManager.getSimilarity(resource, word1, word2);
			if(similarity == null) {
					similarity = msr.calculateSimilarity(resource, word1, word2);
					CacheManager.setSimilarity(resource, word1, word2, similarity);
			}
			
			results.add(similarity);
		}
		return results;
	}


	public static List<SimpleEntry<IMSR, IMSRResource>> getMsrResorceCombinations() {
		return msrResorceCombinations;
	}

}
