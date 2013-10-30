package octopus.semantic.similarity.resource.graph;

import java.util.HashMap;

import octopus.semantic.similarity.resource.GraphResource;
import slib.sglib.io.util.GFormat;
import slib.utils.ex.SLIB_Exception;

public class WordNet extends GraphResource{
	HashMap<String, String> wordSenseIdMap = new HashMap<String, String>();
	public static void main(String[] args) throws SLIB_Exception{
		WordNet wn = new WordNet();
		System.out.println(wn);
	}
	public WordNet() throws SLIB_Exception{
        String dataloc = "resources\\data\\wordnet\\";
        String data_noun = dataloc + "data.noun";
        String data_verb = dataloc + "data.verb";
        String data_adj = dataloc + "data.adj";
        String data_adv = dataloc + "data.adv";

		loadGraph(data_noun, GFormat.WORDNET_DATA);
		loadGraph(data_verb, GFormat.WORDNET_DATA);
		loadGraph(data_adj, GFormat.WORDNET_DATA);
		loadGraph(data_adv, GFormat.WORDNET_DATA);
	}
	
	public String getResourceName() {
		return "wordnet";
	}

}
