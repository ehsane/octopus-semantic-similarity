package octopus.semantic.similarity.api.yahoo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import rainbownlp.util.HibernateUtil;
import rainbownlp.util.caching.CacheEntry;


/**
 * Sample code to use Yahoo! Search BOSS
 * 
 * Please include the following libraries 
 * 1. Apache Log4j
 * 2. oAuth Signpost
 * 
 * @author xyz
 */
public class YahooBossAPI {

	private static final Logger log = Logger.getLogger(YahooBossAPI.class);

	protected static String yahooServer = "http://yboss.yahooapis.com/ysearch/";
	protected static String yahooSpellCheckerUrl = "http://yboss.yahooapis.com/ysearch/spelling";

	// Please provide your consumer key here
	private static String consumer_key = "[%YAHOO API CONSUMER KEY%]";

	// Please provide your consumer secret here
	private static String consumer_secret = "[%YAHOO API CONSUMER SECRET%]";

	/** The HTTP request object used for the connection */
	private static StHttpRequest httpRequest = new StHttpRequest();

	/** Encode Format */
	private static final String ENCODE_FORMAT = "UTF-8";

	/** Call Type */
	private static final String callType = "limitedweb";

	private static final int HTTP_STATUS_OK = 200;
	
	public YahooBossAPI(){
		// Create oAuth Consumer 
		OAuthConsumer consumer = new DefaultOAuthConsumer(consumer_key, consumer_secret);

		// Set the HTTP request correctly
		httpRequest.setOAuthConsumer(consumer);
	}


	/**
	 * 
	 * @return
	 */
	public String spellCheck(String query) 
			throws UnsupportedEncodingException, 
			Exception{
		return doSearch(yahooSpellCheckerUrl, query);
	}

	
	/**
	 * 
	 * @return
	 */
	public String search(String query) 
			throws UnsupportedEncodingException, 
			Exception{
		return doSearch(yahooServer+callType, query);
	}


	private String doSearch(String yahooBOSSURL, String query) throws UnsupportedEncodingException {


		if(this.isConsumerKeyExists() && this.isConsumerSecretExists()) {

			// Start with call Type
			String params = "?q=";

			// Encode Query string before concatenating
			params = params.concat(URLEncoder.encode(query.replaceAll(" ", "&"), "UTF-8"));

			// Create final URL
			String url = yahooBOSSURL + params;
			
			try {
				CacheEntry responseFromCache = CacheEntry.get(url);
				if(responseFromCache != null){
					log.info("Cache hit: "+url);
					return responseFromCache.getValue();
				}
				
				responseFromCache = CacheEntry.getInstance(url);
				log.info("sending get request to" + URLDecoder.decode(url, ENCODE_FORMAT));
				int responseCode = httpRequest.sendGetRequest(url);
				// Send the request
				if(responseCode == HTTP_STATUS_OK) {
					log.info("Response ");
				} else {
					log.error("Error in response due to status code = " + responseCode);
				}
				log.info(httpRequest.getResponseBody());
				responseFromCache.setValue(httpRequest.getResponseBody());
				HibernateUtil.save(responseFromCache);

			} catch(UnsupportedEncodingException e) {
				log.error("Encoding/Decording error");
			} catch (IOException e) {
				log.error("Error with HTTP IO", e);
			} catch (Exception e) {
				log.error(httpRequest.getResponseBody(), e);
				return httpRequest.getResponseBody();
			}


		} else {
			log.error("Key/Secret does not exist");
		}
		return httpRequest.getResponseBody();
		
	}


	private boolean isConsumerKeyExists() {
		if(consumer_key.isEmpty()) {
			log.error("Consumer Key is missing. Please provide the key");
			return false;
		}
		return true;
	}

	private boolean isConsumerSecretExists() {
		if(consumer_secret.isEmpty()) {
			log.error("Consumer Secret is missing. Please provide the key");
			return false;
		}
		return true;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {


		BasicConfigurator.configure();

		try {

			YahooBossAPI signPostTest = new YahooBossAPI();

//			signPostTest.search("ehsan");
//			signPostTest.search("ehsan emadzadeh");
			System.out.println(signPostTest.spellCheck("debilatating"));

		} catch (Exception e) {
			log.info("Error", e);
		}
	}

}