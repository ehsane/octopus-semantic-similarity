package octopus.semantic.similarity.resource.corpus;

import java.math.BigInteger;

import octopus.semantic.similarity.resource.CorpusResource;
import pitt.search.semanticvectors.VectorStoreReaderLucene;

public class GoogleAsCorpus extends CorpusResource{

	private static final long GOOGLE_TOTAL_INDEXED_DOCS = 15000000000L;

	public GoogleAsCorpus(){
		super("GoogleAsCorpus");
	}

	@Override
	public String getResourceName() {
		return "GoogleAsCorpus";
	}

	public long getCorpusSize(){
		return GOOGLE_TOTAL_INDEXED_DOCS;
	}

	@Override
	public long getMatchedDocsCount(String word) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getMatchedDocsCount(String[] word) {
		// TODO Auto-generated method stub
		return 0;
	}
	
//	public BigInteger getMatchedDocsCount(String word){
//		VectorStoreReaderLucene vecReader = getLuceneVectorReader();
//		
//		return 1;
//	}
//	
//	public BigInteger getMatchedDocsCount(String[] word){
//		return 1;
//	}


}
