package octopus.semantic.similarity.msr.vector;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import octopus.semantic.similarity.msr.VectorBasedMSR;
import octopus.semantic.similarity.resource.CorpusResource;
import octopus.semantic.similarity.resource.IMSRResource;
import octopus.semantic.similarity.resource.TextualCorpusResource;
import pitt.search.semanticvectors.CompoundVectorBuilder;
import pitt.search.semanticvectors.FlagConfig;
import pitt.search.semanticvectors.LuceneUtils;
import pitt.search.semanticvectors.VectorStoreReaderLucene;
import pitt.search.semanticvectors.vectors.Vector;
import rainbownlp.util.StringUtil;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;

public class MSRLSA extends VectorBasedMSR implements SpellCheckListener {

	  private static String dictFile = MSRLSA.class.getClassLoader().getResource("dict/english.0").getPath();
	  private static String phonetFile = null;// MSRLSA.class.getClassLoader().getResource("dict/phonet.en").getPath();

	  private static SpellChecker spellCheck = null;
		public static void main(String[] args){
//			String retValue = "";
//			String word = "accelerants";
	//
//			String uri = String.Format( "http://www.google.com/complete/search?output=toolbar&q={0}", word );
	//
//			HttpWebRequest request = ( HttpWebRequest ) WebRequest.Create( uri );
//			HttpWebResponse response = ( HttpWebResponse ) request.GetResponse( );
	//
//			using ( StreamReader sr = new StreamReader( response.GetResponseStream( ) ) ) {
//			    retValue = sr.ReadToEnd( );
//			}
	//
//			XDocument doc = XDocument.Parse( retValue );
	//
//			XAttribute attr = doc.Root.Element( "CompleteSuggestion" ).Element( "suggestion" ).Attribute( "data" );
	//
//			string correctedWord = attr.Value;
			
			try {
			      SpellDictionary dictionary = new SpellDictionaryHashMap(new File(dictFile), null);

			      spellCheck = new SpellChecker(dictionary);
			      spellCheck.addSpellCheckListener(new MSRLSA());
			      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			      while (true) {
			        System.out.print("Enter text to spell check: ");
			        String line = in.readLine();

			        if (line.length() <= 0)
			          break;
			        spellCheck.checkSpelling(new StringWordTokenizer(line));
			      }
			    } catch (Exception e) {
			      e.printStackTrace();
			    }
			  }

			  public void spellingError(SpellCheckEvent event) {
			    List suggestions = event.getSuggestions();
			    if (suggestions.size() > 0) {
			      System.out.println("MISSPELT WORD: " + event.getInvalidWord());
			      for (Iterator suggestedWord = suggestions.iterator(); suggestedWord.hasNext();) {
			        System.out.println("\tSuggested Word: " + suggestedWord.next());
			      }
			    } else {
			      System.out.println("MISSPELT WORD: " + event.getInvalidWord());
			      System.out.println("\tNo suggestions");
			    }
			    //Null actions
			  }
	public MSRLSA() throws Exception {
		super();
	}
	
	static Logger logger = Logger.getLogger("MSRLSA");
	public double calculateSimilarity(TextualCorpusResource resource, String word1,
			String word2) throws IOException {

		Vector vec1 = resource.getPhraseVector(word1, "svd_termvectors.bin", "svd_docvectors.bin", null);
		Vector vec2 = resource.getPhraseVector(word2, "svd_termvectors.bin", "svd_docvectors.bin", null);
		
		if(vec1==null || vec2==null) return 0.0;
		
		double simScore = vec1.measureOverlap(vec2);

		return simScore;
	}

	



	public double calculateSimilarity(IMSRResource resource, String word1,
			String word2) throws IOException {
		return calculateSimilarity((TextualCorpusResource)resource, word1, word2);
	}

	@Override
	public String getMSRName() {
		return "LSA";
	}

}
