package octopus.semantic.similarity;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HybridBAMSR {
	static String modelName = Main.properties.getProperty("model_name");
	static Logger logger = Logger.getLogger("HybridBAMSR");
	public static void train(String trainsetFile) {
		List<SimpleEntry<Double, SimpleEntry<String, String>>> trainingInstances = 
				loadWordPairRating(trainsetFile);
		
		List<RegressionMLExample> examples = createRegressionExamples(trainingInstances);
		
		IRegressionEngine regressionEngine = MLAlgorithmFactory.getRegressionEngine();
		regressionEngine.setMLModelName(modelName);
		regressionEngine.train(examples);
		
		logger.log(Level.INFO, "Training done, model created: "+ modelName);
	}

	public static void test(String testsetFile) {
		List<SimpleEntry<Double, SimpleEntry<String, String>>> testingInstances = 
				loadWordPairRating(testsetFile);
		
		List<RegressionMLExample> examples = createRegressionExamples(testingInstances);
		
		IRegressionEngine regressionEngine = MLAlgorithmFactory.getRegressionEngine();
		regressionEngine.setMLModelName(modelName);
		regressionEngine.test(examples);
		
		logger.log(Level.INFO, "Testing done, model used: "+ modelName);
	}

	/**
	 * returns features for each item in the argument in a corresponding index
	 * @param trainingInstances
	 * @return
	 */
	private static List<RegressionMLExample> createRegressionExamples(List<SimpleEntry<Double, SimpleEntry<String, String>>> exampleEntries) {
		List<RegressionMLExample> examples = new ArrayList<RegressionMLExample>();
		for(SimpleEntry<Double, SimpleEntry<String, String>> entry : exampleEntries){
			SimpleEntry<String, String> wordPair = entry.getValue();
			List<Double> features =	SemanticSimilarityBlender.calculateAllSimilarities(wordPair.getKey(), wordPair.getValue());
			
			RegressionMLExample newExample = new RegressionMLExample();
			newExample.setFeatures(features);
			newExample.setExpectedValue(entry.getKey());
			
			examples.add(newExample);
		}
		return examples;
	}

	private static List<SimpleEntry<Double, SimpleEntry<String, String>>> loadWordPairRating(String datasetFile) {
		// TODO Auto-generated method stub
		return null;
	}

}
