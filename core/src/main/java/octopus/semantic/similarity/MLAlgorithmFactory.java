package octopus.semantic.similarity;

import octopus.semantic.similarity.ml.SVMLight;

public class MLAlgorithmFactory {

	public static IRegressionEngine getRegressionEngine() {
		// TODO Auto-generated method stub
		return new SVMLight();
	}

}
