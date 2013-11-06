package octopus.semantic.similarity.benchmark.loader;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;

public abstract class BenchmarkSetLoader {
	public abstract List<SimpleEntry<Double, SimpleEntry<String, String>>>
				loadEntries() throws Exception;
	
}
