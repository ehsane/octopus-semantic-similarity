package octopus.semantic.similarity.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import pitt.search.semanticvectors.BuildIndex;

public abstract class CorpusResource implements IMSRResource{
	static Logger logger = Logger.getLogger("CorpusResource");
	
	final ResourceType resourceType = ResourceType.CORPUS;
	private String luceneIndexFile;
	String rootPath;
	String resourceName;

	public CorpusResource(String name, String pRootPath){
		resourceName = name;
		luceneIndexFile = resourceName + ".lucene";
		createLuceneIndex();
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public abstract String getDocByIndex(int index);
	public abstract List<String> getAllDocs();
	public abstract int getCorpusSize();


	private void createLuceneIndex()
	{
		final File docDir = new File(rootPath);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out.println("Document directory '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + getLuceneIndexFile() + "'...");

			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31, analyzer);
			iwc.setOpenMode(OpenMode.CREATE);

			iwc.setRAMBufferSizeMB(256.0);
			Directory dir = FSDirectory.open(new File(getLuceneIndexFile()));
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);

			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

			logger.log(Level.INFO, "Building Semantic Vector Index...");

			BuildIndex.main(new String[]{getLuceneIndexFile()});

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() +
					"\n with message: " + e.getMessage());
		}
	}
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
	}

	public String getLuceneIndexFile() {
		return luceneIndexFile;
	}
}
