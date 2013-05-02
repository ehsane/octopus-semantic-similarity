package octopus.semantic.similarity;

import java.io.IOException;
import java.util.Properties;

import org.omg.CORBA.portable.ApplicationException;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

/**
 * The main entry point to the application
 *
 */
public class Main 
{
	static Properties properties = new Properties();
	 

    public static void main( String[] args ) throws JSAPException
    {
    	loadProperties();
    	
        JSAP jsap = new JSAP();
        
        // create a flagged option we'll access using the id "count".
        // it's going to be an integer, with a default value of 1.
        // it's required (which has no effect since there's a default value)
        // its short flag is "n", so a command line containing "-n 5"
        //    will print our message five times.
        // it has no long flag.
        FlaggedOption opt1 = new FlaggedOption("action")
                                .setStringParser(JSAP.STRING_PARSER)
                                .setDefault("train") 
                                .setRequired(true) 
                                .setShortFlag('a') 
                                .setLongFlag(JSAP.NO_LONGFLAG);
        
        FlaggedOption opt2 = new FlaggedOption("trainset")
	        .setStringParser(JSAP.STRING_PARSER)
	        .setRequired(true) 
	        .setShortFlag('t') 
	        .setLongFlag(JSAP.NO_LONGFLAG);

        FlaggedOption opt3 = new FlaggedOption("testset")
	        .setStringParser(JSAP.STRING_PARSER)
	        .setRequired(true) 
	        .setShortFlag('s') 
	        .setLongFlag(JSAP.NO_LONGFLAG);

        jsap.registerParameter(opt1);
        jsap.registerParameter(opt2);
        jsap.registerParameter(opt3);

        JSAPResult config = jsap.parse(args);
        String action = config.getString("action");
        if(action.equals("train")){
        	HybridBAMSR.train(config.getString("trainset"));
        }

    }
    
	private static void loadProperties() {
		try {
	           //load a properties file from class path, inside static method
			properties.load(Main.class.getClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException ex) {
			ex.printStackTrace();
	    }
	}
}
