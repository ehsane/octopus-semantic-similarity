package octopus.semantic.similarity.benchmark.loader;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import rainbownlp.util.ConfigurationUtil;
import rainbownlp.util.FileUtil;

public class CSVBenchmarkLoader extends BenchmarkSetLoader {

	private String fileName;
	private int ratingIndex;
	private int word2Index;
	private int word1Index;

	public CSVBenchmarkLoader(String fileName, int ratingIndex, int word1Index, int word2Index) {
		this.fileName = fileName;
		this.ratingIndex = ratingIndex;
		this.word1Index = word1Index;
		this.word2Index = word2Index;
	}

	@Override
	public List<SimpleEntry<Double, SimpleEntry<String, String>>> loadEntries() throws Exception {
		String path = ConfigurationUtil.getResourcePath(fileName);
		Reader in = new FileReader(new File(path));
		Iterable<CSVRecord> parser = CSVFormat.EXCEL.parse(in);
		List<SimpleEntry<Double, SimpleEntry<String, String>>> entries =
				new ArrayList<SimpleEntry<Double,SimpleEntry<String,String>>>();
		for (CSVRecord record : parser) {
			try {
				Double rating = Double.parseDouble(record.get(ratingIndex));
				String word1 = record.get(word1Index);
				String word2 = record.get(word2Index);
				SimpleEntry<String, String> wordsPair = 
						new SimpleEntry<String, String>(word1, word2);
				SimpleEntry<Double, SimpleEntry<String, String>> entry
					= new SimpleEntry<Double, 
						SimpleEntry<String,String>>(rating, wordsPair);
				entries.add(entry);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return entries;
	}

}
