package octopus.semantic.similarity;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public class HybridBAMSR {
	
	public static void train(String string) {
		List<SimpleEntry<Double, SimpleEntry<String, String>>> trainingInstances = 
				loadWordPairRating();
		
		List<List<Double>> features = calculateFeatures(trainingInstances);
		
		trainRegressionModel(features);
	}

	private static void trainRegressionModel(List<List<Double>> features) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * returns features for each item in the argument in a corresponding index
	 * @param trainingInstances
	 * @return
	 */
	private static List<List<Double>> calculateFeatures(List<SimpleEntry<Double, SimpleEntry<String, String>>> trainingInstances) {
		return null;
		// TODO Auto-generated method stub
		
	}

	private static List<SimpleEntry<Double, SimpleEntry<String, String>>> loadWordPairRating() {
		// TODO Auto-generated method stub
		return null;
	}

}
