package octopus.semantic.similarity;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import octopus.semantic.similarity.benchmark.loader.BenchmarkSetLoader;
import octopus.semantic.similarity.benchmark.loader.CSVBenchmarkLoader;
import rainbownlp.core.FeatureValuePair;
import rainbownlp.core.Phrase;
import rainbownlp.core.PhraseLink;
import rainbownlp.machinelearning.ILearnerEngine;
import rainbownlp.machinelearning.MLExample;

public class HybridBAMSR {
	static String modelName = Main.properties.getProperty("model_name");
	static Logger logger = Logger.getLogger("HybridBAMSR");
	public static void train(String trainsetFile) throws Exception {
		List<SimpleEntry<Double, SimpleEntry<String, String>>> trainingInstances = 
				loadWordPairRating(trainsetFile);

		List<MLExample> examples = createRegressionExamples(trainingInstances);

		ILearnerEngine regressionEngine = MLAlgorithmFactory.getRegressionEngine(modelName);
		FeatureValuePair.resetIndexes();
		regressionEngine.train(examples);

		logger.log(Level.INFO, "Training done, model created: "+ modelName);
	}

	public static void test(String testsetFile) throws Exception {
		List<SimpleEntry<Double, SimpleEntry<String, String>>> testingInstances = 
				loadWordPairRating(testsetFile);

		List<MLExample> examples = createRegressionExamples(testingInstances);

		ILearnerEngine regressionEngine = MLAlgorithmFactory.getRegressionEngine(modelName);
		regressionEngine.test(examples);

		logger.log(Level.INFO, "Testing done, model used: "+ modelName);
	}

	/**
	 * returns features for each item in the argument in a corresponding index
	 * @param trainingInstances
	 * @return
	 * @throws Exception 
	 */
	private static List<MLExample> createRegressionExamples(List<SimpleEntry<Double, SimpleEntry<String, String>>> exampleEntries) throws Exception {
		List<MLExample> examples = new ArrayList<MLExample>();
		for(SimpleEntry<Double, SimpleEntry<String, String>> entry : exampleEntries){
			SimpleEntry<String, String> wordPair = entry.getValue();
			Phrase p1 = Phrase.createIndependentPhrase(wordPair.getKey());
			Phrase p2 = Phrase.createIndependentPhrase(wordPair.getValue());
			PhraseLink pl = PhraseLink.createInstance(p1, p2);
			MLExample newExample = MLExample.getInstanceForLink(pl, "test");
			newExample.setExpectedClass(entry.getKey());

			SemanticSimilarityBlender.calculateAllSimilarities(newExample ,
					wordPair.getKey(), wordPair.getValue());

			examples.add(newExample);
		}
		return examples;
	}

	/**
	 * Shows all possible benchmark value in the configuration file entry trainset/testset
	 * @author Ehsan
	 *
	 */
	public enum BenchMark{
		MAYOSRS,
		MAYOSRS_MINI,
		UMNSRS_SIM,
		UMNSRS_REL, Rubenstein_Goodenough_1965
	}
	private static List<SimpleEntry<Double, SimpleEntry<String, String>>> 
	loadWordPairRating(String datasetName) {
		BenchMark benchmarkSet = BenchMark.valueOf(datasetName);
		BenchmarkSetLoader loader = null;
		String benchmarkFileRoot = "data/benchmarks/";
		switch(benchmarkSet){
			case MAYOSRS:
				 loader = new CSVBenchmarkLoader(benchmarkFileRoot + 
						 "MiniMayoSRS.csv",0, 4, 5);
				break;
			case MAYOSRS_MINI:
				 loader = new CSVBenchmarkLoader(benchmarkFileRoot + 
						 "MayoSRS.csv",0, 4, 5);
				break;
			case Rubenstein_Goodenough_1965:
				 loader = new CSVBenchmarkLoader(benchmarkFileRoot + 
						 "Rubenstein_Goodenough_1965.csv",2, 0, 1);
			
			default:
				break;
		}
		List<SimpleEntry<Double, SimpleEntry<String, String>>>
				annotations = null;
		if(loader!=null){
			try {
				annotations = loader.loadEntries();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return annotations;
	}

}
