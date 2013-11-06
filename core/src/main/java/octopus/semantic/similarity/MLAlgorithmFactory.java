package octopus.semantic.similarity;

import rainbownlp.machinelearning.ILearnerEngine;
import rainbownlp.machinelearning.SVMLightRegression;

public class MLAlgorithmFactory {

	public static ILearnerEngine getRegressionEngine(String modelName) {
		// TODO Auto-generated method stub
		return SVMLightRegression.getLearnerEngine(modelName);
	}

}
