package octopus.semantic.similarity;

import java.util.List;

public interface IRegressionEngine {
	public void setMLModelName(String modelName);
	public void train(List<RegressionMLExample> trainingExamples); 
	public void test(List<RegressionMLExample> testingExamples); 
}
