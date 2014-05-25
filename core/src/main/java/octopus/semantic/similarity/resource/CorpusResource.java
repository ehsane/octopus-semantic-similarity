package octopus.semantic.similarity.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import pitt.search.semanticvectors.BuildIndex;
import pitt.search.semanticvectors.FlagConfig;
import pitt.search.semanticvectors.LSA;
import pitt.search.semanticvectors.VectorStoreReaderLucene;

public abstract class CorpusResource implements IMSRResource{
	static Logger logger = Logger.getLogger("CorpusResource");
	
	final ResourceType resourceType = ResourceType.CORPUS;
	String rootPath;
	String resourceName;

	private VectorStoreReaderLucene vecReader;

	public CorpusResource(String name){
		resourceName = name;
	}
	
	public String getResourceName() { 
		return resourceName;
	}
	
	public ResourceType getResourceType() {
		return resourceType;
	}
	
	@Override
	public String getSearchableContentForWord(String word) {
		return word;
	}

	public abstract long  getCorpusSize();
	
	public abstract long getMatchedDocsCount(String word);
	
	public abstract long getMatchedDocsCount(String[] word);

}
