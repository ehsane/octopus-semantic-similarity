package octopus.semantic.similarity.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import octopus.semantic.similarity.resource.IMSRResource.ResourceType;
import octopus.semantic.similarity.word2vec.util.LSAFixed;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pitt.search.semanticvectors.CompoundVectorBuilder;
import pitt.search.semanticvectors.FlagConfig;
import pitt.search.semanticvectors.LuceneUtils;
import pitt.search.semanticvectors.VectorStoreReaderLucene;
import pitt.search.semanticvectors.vectors.Vector;

public class TextualCorpusResource extends CorpusResource{
	static Logger logger = Logger.getLogger("TextualCorpusResource");
	
	final ResourceType resourceType = ResourceType.TEXTUAL_CORPUS;
	private String luceneIndexFile;
	String rootPath;

	private VectorStoreReaderLucene docVecReader;
	private VectorStoreReaderLucene termVecReader;
	private Version luceneVersion = Version.LUCENE_CURRENT;
	Analyzer analyzer = new StandardAnalyzer(luceneVersion);

	private IndexReader indexReader;

	public static enum CorpusFormat{
		TEXT_FILES,
		SGM,
		PUBMED_ABSTRACTS
	}
	@Override
	public ResourceType getResourceType() {
		return resourceType;
	}
	
	CorpusFormat format = CorpusFormat.TEXT_FILES;

	public TextualCorpusResource(String name, String pRootPath){
		super(name);
		luceneIndexFile = resourceName + ".lucene";
		System.out.println("luceneIndexFile : "+luceneIndexFile);
		rootPath = pRootPath;
		createLuceneIndex();
	}
	public TextualCorpusResource(String name, String pRootPath, CorpusFormat pFormat){
		super(name);
		luceneIndexFile = resourceName + ".lucene";
		System.out.println("luceneIndexFile : "+luceneIndexFile);
		rootPath = pRootPath;
		format = pFormat;
		createLuceneIndex();
	}

	private void createLuceneIndex()
	{
		File luceneFile = new File(getLuceneIndexFile());
		
		final File docDir = new File(rootPath);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out.println("Document directory '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			if(!luceneFile.exists()){
				System.out.println("Indexing to directory '" + getLuceneIndexFile() + "'...");
	
				IndexWriterConfig iwc = new IndexWriterConfig(luceneVersion, analyzer);
				iwc.setOpenMode(OpenMode.CREATE);
	
				iwc.setRAMBufferSizeMB(256.0);
				File luceneIndexRoot = new File(getLuceneIndexFile());
				if(luceneIndexRoot.exists())
					FileUtils.forceDelete(luceneIndexRoot);
				Directory dir = FSDirectory.open(luceneIndexRoot);
				IndexWriter writer = new IndexWriter(dir, iwc);
				indexDocs(writer, docDir);
	
				writer.close();
	
				Date end = new Date();
				System.out.println(end.getTime() - start.getTime() + " total milliseconds");
				
//				//create random indexing index
//				BuildIndex.main(new String[]{"-luceneindexpath", getLuceneIndexFile(), "-contentsfields","contents"});
			}

			if(!(new File(getLuceneIndexFile()+"/svd_termvectors.bin")).exists() ||
					!(new File(getLuceneIndexFile()+"/svd_docvectors.bin")).exists()){
				logger.log(Level.INFO, "Building Semantic Vector Index...");
				//create SVD index
				LSAFixed.main(new String[]{"-luceneindexpath", getLuceneIndexFile(), "-termvectorsfile",  getLuceneIndexFile()+"/svd_termvectors.bin", "-docvectorsfile",  getLuceneIndexFile()+"/svd_docvectors", "-contentsfields","contents"});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Load all files in the given directory
	 * @param writer
	 * @param file
	 * @throws IOException
	 */
	private void indexDocs(IndexWriter writer, File file)
			throws IOException {
		// do not try to index files that cannot be read
		if (!file.canRead()) return; 
		if (file.isDirectory()) {
			String[] files = file.list();
			// an IO error could occur
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					indexDocs(writer, new File(file, files[i]));
				}
			}
		} else {
			switch (format) {
			case TEXT_FILES:
				addTextFile(writer, file);
				break;
			case SGM:
				addSGMFile(writer, file);
			case PUBMED_ABSTRACTS:
				addFromPubMedAbstract(writer, file);
			default:
				break;
			}
			
		}
	}

	private void addFromPubMedAbstract(IndexWriter writer, File file) throws IOException {
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException fnfe) {
			// at least on windows, some temporary files raise this exception with an "access denied" message
			// checking if the file can be read doesn't help
			logger.log(Level.INFO, "indexDocs file not found exception: "+file);
			
			return;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
		try {

			String curLine = br.readLine();
			StringBuilder curSection = new StringBuilder();
			String lastSection = "";
			while(curLine!=null){
				curSection.append(curLine);
				curSection.append(" ");
				
				if(curLine.isEmpty())//section ended
				{
					lastSection = curSection.toString();
					curSection = new StringBuilder();
				}else if(curLine.matches("^PMID: \\d+  .*")){
					//last section is abstract
					// make a new, empty document
					Document doc = new Document();

					// Add the path of the file as a field named "path".  Use a
					// field that is indexed (i.e. searchable), but don't tokenize 
					// the field into separate words and don't index term frequency
					// or positional information:
					Field pathField = new Field("path", file.getPath(), 
							Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
					doc.add(pathField);

					// Add the last modified date of the file a field named "modified".
					// Use a NumericField that is indexed (i.e. efficiently filterable with
					// NumericRangeFilter).  This indexes to milli-second resolution, which
					// is often too fine.  You could instead create a number based on
					// year/month/day/hour/minutes/seconds, down the resolution you require.
					// For example the long value 2011021714 would mean
					// February 17, 2011, 2-3 PM.
					LongField modifiedField = new LongField("modified", file.lastModified(), 
							Field.Store.YES);
					doc.add(modifiedField);

					// Add the contents of the file to a field named "contents".  Specify a Reader,
					// so that the text of the file is tokenized and indexed, but not stored.
					// Note that FileReader expects the file to be in UTF-8 encoding.
					// If that's not the case searching for special characters will fail.
					doc.add(new Field("contents", lastSection, Field.Store.YES, Field.Index.ANALYZED));

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						// New index, so we just add the document (no old document can be there):
						System.out.println("adding " + curLine.replaceAll("\\D", ""));
						writer.addDocument(doc);
					} else {
						// Existing index (an old copy of this document may have been indexed) so 
						// we use updateDocument instead to replace the old one matching the exact 
						// path, if present:
						System.out.println("updating " + curLine.replaceAll("\\D", ""));
						writer.updateDocument(new Term("path", file.getPath()), doc);
					}
				}
				curLine = br.readLine();
				
			}

		} finally {
			fis.close();
			br.close();
		}
		
	}
	private void addTextFile(IndexWriter writer,File file) throws IOException {
		if(!file.getName().endsWith(".txt")) return;
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException fnfe) {
			// at least on windows, some temporary files raise this exception with an "access denied" message
			// checking if the file can be read doesn't help
			logger.log(Level.INFO, "indexDocs file not found exception: "+file);
			
			return;
		}

		try {

			// make a new, empty document
			Document doc = new Document();

			// Add the path of the file as a field named "path".  Use a
			// field that is indexed (i.e. searchable), but don't tokenize 
			// the field into separate words and don't index term frequency
			// or positional information:
			Field pathField = new Field("path", file.getPath(), 
					Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
			doc.add(pathField);

			// Add the last modified date of the file a field named "modified".
			// Use a NumericField that is indexed (i.e. efficiently filterable with
			// NumericRangeFilter).  This indexes to milli-second resolution, which
			// is often too fine.  You could instead create a number based on
			// year/month/day/hour/minutes/seconds, down the resolution you require.
			// For example the long value 2011021714 would mean
			// February 17, 2011, 2-3 PM.
			LongField modifiedField = new LongField("modified", file.lastModified(), 
					Field.Store.YES);
			doc.add(modifiedField);

			// Add the contents of the file to a field named "contents".  Specify a Reader,
			// so that the text of the file is tokenized and indexed, but not stored.
			// Note that FileReader expects the file to be in UTF-8 encoding.
			// If that's not the case searching for special characters will fail.
			doc.add(new Field("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8"))));

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				// New index, so we just add the document (no old document can be there):
				System.out.println("adding " + file);
				writer.addDocument(doc);
			} else {
				// Existing index (an old copy of this document may have been indexed) so 
				// we use updateDocument instead to replace the old one matching the exact 
				// path, if present:
				System.out.println("updating " + file);
				writer.updateDocument(new Term("path", file.getPath()), doc);
			}

		} finally {
			fis.close();
		}
		
	}
	
	private void addSGMFile(IndexWriter writer,File file) throws IOException {
		if(!file.getName().endsWith(".sgm")) return;
		org.jsoup.nodes.Document sgmDoc = Jsoup.parse(file, "UTF-8");
		Elements el = sgmDoc.select("TEXT");
		StringBuilder textualContent = new StringBuilder();
		for(Element e : el){
			textualContent.append(e.text().toLowerCase());
			textualContent.append("\n\n");
		}
		// make a new, empty document
		Document doc = new Document();

		// Add the path of the file as a field named "path".  Use a
		// field that is indexed (i.e. searchable), but don't tokenize 
		// the field into separate words and don't index term frequency
		// or positional information:
		Field pathField = new Field("path", file.getPath(), 
				Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
		doc.add(pathField);

		// Add the last modified date of the file a field named "modified".
		// Use a NumericField that is indexed (i.e. efficiently filterable with
		// NumericRangeFilter).  This indexes to milli-second resolution, which
		// is often too fine.  You could instead create a number based on
		// year/month/day/hour/minutes/seconds, down the resolution you require.
		// For example the long value 2011021714 would mean
		// February 17, 2011, 2-3 PM.
		LongField modifiedField = new LongField("modified", file.lastModified(), 
				Field.Store.YES);
		doc.add(modifiedField);

		// Add the contents of the file to a field named "contents".  Specify a Reader,
		// so that the text of the file is tokenized and indexed, but not stored.
		// Note that FileReader expects the file to be in UTF-8 encoding.
		// If that's not the case searching for special characters will fail.
		doc.add(new Field("contents", new BufferedReader(new StringReader(textualContent.toString()))));

		if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
			// New index, so we just add the document (no old document can be there):
			System.out.println("adding " + file);
			writer.addDocument(doc);
		} else {
			// Existing index (an old copy of this document may have been indexed) so 
			// we use updateDocument instead to replace the old one matching the exact 
			// path, if present:
			System.out.println("updating " + file);
			writer.updateDocument(new Term("path", file.getPath()), doc);
		}
	}
	
	public String getLuceneIndexFile() {
		return luceneIndexFile;
	}
	
	private VectorStoreReaderLucene getLuceneDocVectorReader() {
		if(docVecReader != null) return docVecReader;
		FlagConfig flagConfig = FlagConfig.parseFlagsFromString("-luceneindexpath "+getLuceneIndexFile() + 
				" -termvectorsfile "+ getLuceneIndexFile()+"/svd_termvectors.bin -docvectorsfile "+  getLuceneIndexFile()+"/svd_docvectors.bin");
		
		try {
			docVecReader = new VectorStoreReaderLucene(flagConfig.docvectorsfile(), flagConfig);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to open vector store from file: " + flagConfig.queryvectorfile());
		}
		
		return docVecReader;
	}
	private VectorStoreReaderLucene getLuceneTermVectorReader(String termVectorsFile) {
		if(termVecReader != null) return termVecReader;
		FlagConfig flagConfig = FlagConfig.parseFlagsFromString("-luceneindexpath "+getLuceneIndexFile() + 
				" -termvectorsfile "+ getLuceneIndexFile()+"/svd_termvectors.bin -docvectorsfile "+  getLuceneIndexFile()+"/svd_docvectors.bin");
		
		try {
			termVecReader = new VectorStoreReaderLucene(termVectorsFile, flagConfig);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to open vector store from file: " + flagConfig.queryvectorfile());
		}
		
		return termVecReader;
	}
	private IndexReader getIndexReader() throws IOException {
		if(indexReader != null) return indexReader;
		Directory dir = FSDirectory.open(new File(getLuceneIndexFile()));
		indexReader = DirectoryReader.open(dir);
		
		return indexReader;
	}
	Long corpusSize = null;
	@Override
	public long getCorpusSize() {
		if(corpusSize == null){
			logger.log(Level.ALL, "Corpus: "+resourceName);
			corpusSize = Long.valueOf(getLuceneDocVectorReader().getNumVectors());
		}
		return corpusSize.longValue();
	}

	@Override
	public long getMatchedDocsCount(String word) {
		String querystr = "\""+word+"\"";
		try {
			Query q = new QueryParser(luceneVersion, "contents", analyzer).parse(querystr);
			IndexSearcher searcher = new IndexSearcher(getIndexReader());
			LuceneCountCollector collector = new LuceneCountCollector();
			searcher.search(q, collector);
			return collector.getCount();
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public long getMatchedDocsCount(String[] words) {
		StringBuilder querystr = new StringBuilder();
		for(String w :words){
			if(querystr.length()!=0) querystr.append(" AND ");
			querystr.append("\""+w+"\"");
		}
		
		try {
			Query q = new QueryParser(luceneVersion, "contents", analyzer).parse(querystr.toString());
			IndexSearcher searcher = new IndexSearcher(getIndexReader());
			LuceneCountCollector collector = new LuceneCountCollector();
			searcher.search(q, collector);
			return collector.getCount();
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	public Vector getWordVector(String word1, String termsVectorsFile, String docVectorsFile) {
		FlagConfig flagConfig = FlagConfig.parseFlagsFromString("-luceneindexpath "+getLuceneIndexFile() + 
				" -termvectorsfile "+ getLuceneIndexFile()+"/"+termsVectorsFile+" -docvectorsfile "+  getLuceneIndexFile()+"/"+docVectorsFile);
		LuceneUtils luceneUtils = null;

		return CompoundVectorBuilder.getQueryVectorFromString(getLuceneTermVectorReader(flagConfig.termvectorsfile()),
				luceneUtils, flagConfig, word1);
	}



}
