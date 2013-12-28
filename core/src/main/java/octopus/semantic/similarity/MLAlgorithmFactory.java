package octopus.semantic.similarity;

import rainbownlp.machinelearning.LearnerEngine;
import rainbownlp.machinelearning.SVMLightRegression;

public class MLAlgorithmFactory {

	public static LearnerEngine getRegressionEngine(String modelName) {
		// TODO Auto-generated method stub
		return SVMLightRegression.getLearnerEngine(modelName);
	}

}
