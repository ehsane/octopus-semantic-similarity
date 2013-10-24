package octopus.semantic.similarity;

import java.util.List;

public class RegressionMLExample {
	private List<Double> features;
	private Double expectedValue;
	private Double predictedValue;
	
	public Double getExpectedValue() {
		return expectedValue;
	}
	public void setExpectedValue(Double expectedValue) {
		this.expectedValue = expectedValue;
	}
	public List<Double> getFeatures() {
		return features;
	}
	public void setFeatures(List<Double> features) {
		this.features = features;
	}
	public Double getPredictedValue() {
		return predictedValue;
	}
	public void setPredictedValue(Double predictedValue) {
		this.predictedValue = predictedValue;
	}
}
