package octopus.semantic.similarity.resource.graph;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.dictionary.Dictionary;
import octopus.semantic.similarity.resource.GraphResource;
import rainbownlp.util.FileUtil;
import slib.sglib.io.util.GFormat;

public class WordNet extends GraphResource{
	HashMap<String, List<String>> wordSenseIdMap = new HashMap<String, List<String>>();
	Dictionary  database = null;
	FileInputStream inputStream;
	public static void main(String[] args) throws Exception{
		WordNet wn = new WordNet();
		System.out.println(wn);
	}
	public WordNet() throws Exception{
        String dataloc = this.getClass().getClassLoader().getResource("data/wordnet").getPath();
        String data_noun = dataloc + "\\data.noun";
//        String data_verb = dataloc + "data.verb";
//        String data_adj = dataloc + "data.adj";
//        String data_adv = dataloc + "data.adv";

		loadGraph(data_noun, GFormat.WORDNET_DATA);
//		loadGraph(data_verb, GFormat.WORDNET_DATA);
//		loadGraph(data_adj, GFormat.WORDNET_DATA);
//		loadGraph(data_adv, GFormat.WORDNET_DATA);
		
//		loadSenseIdMap(data_noun);
//		loadSenseIdMap(data_verb);
//		loadSenseIdMap(data_adj);
//		loadSenseIdMap(data_adv);
		
		inputStream = new 
		FileInputStream(
				this.getClass().getClassLoader().getResource("jwordnet/file_properties.xml").getPath()
				);
		JWNL.initialize(inputStream); 
		
		database =  Dictionary.getInstance();
	}
	
	private void loadSenseIdMap(String dataFile) { 
		List<String> lines = FileUtil.loadLineByLine(dataFile);
		
		for(int i=29;i<lines.size();i++){
			String line = lines.get(i);
			String[] lineParts = line.split(" ");
			//"00038913 04 n 01 res_gestae";
			String word = lineParts[4];
			List<String> senseIds = wordSenseIdMap.get(word);
			if(senseIds == null)
				senseIds = new ArrayList<String>();
			String senseId = lineParts[0];
			if(!senseIds.contains(senseId))
				senseIds.add(senseId);
			wordSenseIdMap.put(word.toLowerCase().trim(), senseIds);
		}
		
	}
	public String getResourceName() {
		return "wordnet";
	}
	@Override
	public String getSearchableContentForWord(String word) {
		try {
			IndexWord indexWord = database.getIndexWord(POS.NOUN, word);
			if(indexWord==null){
				database.getIndexWord(POS.ADJECTIVE, word);
			}
			if(indexWord==null){
				database.getIndexWord(POS.ADVERB, word);
			}
			if(indexWord==null){
				database.getIndexWord(POS.VERB, word);
			}
			Synset[] synsets = indexWord.getSenses();
			if(synsets == null || synsets.length==0){
				System.out.println("No sense found for: "+word);
				return null;
			}
			String offset = String.valueOf(synsets[0].getOffset());
			long tmpOffset = synsets[0].getOffset();
			while(tmpOffset<10000000){
				offset="0"+offset;
				tmpOffset*=10;
			}
			System.out.println("Word: "+word+" mapped to sense Id : "+offset);
			return offset;
		} catch (Exception e) {
			System.out.println("couldn't find word in wordnet: "+word);
			e.printStackTrace();
			return null;
		}
	}

}
