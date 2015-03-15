package octopus.semantic.similarity.resource.corpus;

import octopus.semantic.similarity.resource.CorpusResource;
import rainbownlp.util.StringUtil;

import com.jellymold.boss.WebSearch;
import com.jellymold.boss.util.BOSSException;

public class YahooAsCorpus extends CorpusResource{
	static WebSearch ws = new WebSearch();
	public static void main(String[] args){
		ws.search("Ehsan Emadzadeh");
		System.out.println(ws.getTotalResults());
	}
	private static final long YAHOO_TOTAL_INDEXED_DOCS = 15000000000L;

	public YahooAsCorpus(){
		super("YahooAsCorpus");
	}

	@Override
	public String getResourceName() {
		return "YahooAsCorpus";
	}

	public long getCorpusSize(){
		return YAHOO_TOTAL_INDEXED_DOCS;
	}
	
	public long getMatchedDocsCount(String word){
		try {
			ws.search(word);
			return ws.getTotalResults();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	public long getMatchedDocsCount(String[] words){
		String concat = StringUtil.concatArray(" ", words);
		return getMatchedDocsCount(concat);
	}
}
