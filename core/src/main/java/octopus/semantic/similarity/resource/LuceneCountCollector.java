package octopus.semantic.similarity.resource;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;

public class LuceneCountCollector extends Collector { 

	private int count=0; 

	public int getCount() { 
		return count; 
	}

	@Override
	public void setScorer(Scorer scorer) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void collect(int doc) throws IOException {
		count++; 
	}

	@Override
	public void setNextReader(AtomicReaderContext context) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean acceptsDocsOutOfOrder() {
		// TODO Auto-generated method stub
		return false;
	} 

}
