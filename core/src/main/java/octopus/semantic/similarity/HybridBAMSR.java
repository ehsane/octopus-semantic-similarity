package octopus.semantic.similarity;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import octopus.semantic.similarity.benchmark.loader.BenchmarkSetLoader;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import com.amazonaws.services.elasticmapreduce.model.HadoopJarStepConfig;

import rainbownlp.core.FeatureValuePair;
import rainbownlp.core.Phrase;
import rainbownlp.core.PhraseLink;
import rainbownlp.machinelearning.LearnerEngine;
import rainbownlp.machinelearning.MLExample;
import rainbownlp.util.FileUtil;
import rainbownlp.util.StringUtil;
import rainbownlp.util.ConfigurationUtil;

public class HybridBAMSR extends LearnerEngine implements Serializable{
	private static final boolean USE_SPARK = false;
	public String modelName = ConfigurationUtil.getValue("model_name");
	LearnerEngine regressionEngine;
	static SparkConf conf = new SparkConf().setAppName("oss").setMaster("local");
	static Logger logger = Logger.getLogger("HybridBAMSR");
	
	public List<MLExample> createExamples(String corpusName) throws Exception {
		List<SimpleEntry<Double, SimpleEntry<String, String>>> trainingInstances = 
				loadWordPairRating();

		List<MLExample> examples = createRegressionExamples(trainingInstances, corpusName);
//		 FeatureValuePair.resetIndexes();
     	
		return examples;
	}

	/**
	 * returns features for each item in the argument in a corresponding index
	 * @param corpusName 
	 * @param trainingInstances
	 * @return
	 * @throws Exception 
	 */
	public List<MLExample> createRegressionExamples(List<SimpleEntry<Double, SimpleEntry<String, String>>> exampleEntries,
			final String corpusName) throws Exception {
		List<MLExample> examples=null;
		if(USE_SPARK){
			JavaSparkContext sc = new JavaSparkContext(conf);
			JavaRDD<SimpleEntry<Double, SimpleEntry<String, String>>> distData = sc.parallelize(exampleEntries);
			JavaRDD<MLExample> examplesRDD = distData.map(new Function<SimpleEntry<Double, SimpleEntry<String, String>>, MLExample>() {
				public MLExample call(SimpleEntry<Double, SimpleEntry<String, String>> entry) { 
					SimpleEntry<String, String> wordPair = entry.getValue();
					Phrase p1 = Phrase.createIndependentPhrase(wordPair.getKey());
					Phrase p2 = Phrase.createIndependentPhrase(wordPair.getValue());
					PhraseLink pl = PhraseLink.getInstance(p1, p2);
					MLExample newExample = MLExample.getInstanceForLink(pl, corpusName);
					newExample.setExpectedClass(entry.getKey().toString());

					return newExample; 
				}
			});
			examples = examplesRDD.collect();
		}
		else{
			examples = new ArrayList<MLExample>();

			for(SimpleEntry<Double, SimpleEntry<String, String>> entry : exampleEntries){
				SimpleEntry<String, String> wordPair = entry.getValue();
				String word1 = StringUtil.removeNonAlphaAndLowercase(wordPair.getKey());
				String word2 = StringUtil.removeNonAlphaAndLowercase(wordPair.getValue());
				Phrase p1 = Phrase.createIndependentPhrase(word1);
				Phrase p2 = Phrase.createIndependentPhrase(word2);
				FileUtil.logLine("pairs.txt", "["+word1 + "] -- ["+word2+"]");
				PhraseLink pl = PhraseLink.getInstance(p1, p2);
				MLExample newExample = MLExample.getInstanceForLink(pl, corpusName);
				newExample.setExpectedClass(entry.getKey().toString());

				examples.add(newExample);
			}
		}
		FileUtil.logLine("pairs.txt", "****************");
		
		return examples;
	}
	
	public void calculateExampleFeatures(String corpusName, List<MLExample> examples){
//		HadoopJarStepConfig hadoopConfig1 = new HadoopJarStepConfig()
//		.withJar("s3://mybucket/my-jar-location1")
//		.withMainClass("com.my.Main1") // optional main class, this can be omitted if jar above has a manifest
//		.withArgs("--verbose"); // optional list of arguments
		for(MLExample example : examples){
			try {
				String word1 = example.getRelatedPhraseLink().getFromPhrase().getPhraseContent();
				String word2 = example.getRelatedPhraseLink().getToPhrase().getPhraseContent();
				SemanticSimilarityBlender.calculateAllSimilarities(corpusName, example,	word1, word2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
		UMNSRS_REL, Rubenstein_Goodenough_1965, WS_353, WS_353_REL
	}
	private static List<SimpleEntry<Double, SimpleEntry<String, String>>>	loadWordPairRating() {
		BenchmarkSetLoader loader = Configuration.config.getBenchmarkLoader();
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

	@Override
	public void train(List<MLExample> pTrainExamples) throws Exception {
//		SVMLightFormatConvertor.excludeAttributeIds.add("MSR_PMI-YahooAsCorpus");
//		SVMLightFormatConvertor.excludeAttributeIds.add("MSR_NGD-YahooAsCorpus");
//		SVMLightFormatConvertor.excludeAttributeIds.add("MSR_NGD-PubmedDentalJournalsReviews");
//		SVMLightFormatConvertor.excludeAttributeIds.add("MSR_NGD-PubmedNurseJournalsReviews");
//		SVMLightFormatConvertor.excludeAttributeIds.add("MSR_NGD-PubmedSystematicReviews");
		regressionEngine = MLAlgorithmFactory.getRegressionEngine(modelName);
		regressionEngine.train(pTrainExamples);
//		modelName = regressionEngine.getModelFile().replace(".model", "");//Model file name can change after training
		logger.log(Level.INFO, "Training done, model created: "+ modelName);
		
	}

	@Override
	public void test(List<MLExample> pTestExamples) throws Exception {
		if(regressionEngine==null)
			regressionEngine = MLAlgorithmFactory.getRegressionEngine(modelName);
		regressionEngine.modelFile = modelFile;
//		((SVMLightBasedLearnerEngine)regressionEngine).setAdjustingMargin(-232.0);
		regressionEngine.test(pTestExamples);
		logger.log(Level.INFO, "Testing done, model used: "+ modelName);
	}

}
