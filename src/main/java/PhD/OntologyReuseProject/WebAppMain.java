package PhD.OntologyReuseProject;

import org.apache.log4j.Logger;

import AgentClasses.*;

public class WebAppMain {
	static Logger log = Logger.getLogger(WebAppMain.class);

    public static void main( String[] args ) throws Exception
    {
    	log.info("Your application is strating:");
    	startingApplication();
    	
        WebLearningClass test= new WebLearningClass();
        test.beginIterations(); 
        
    	// end main
        
    }
  	//--------------------------------------------------------------------
  	//This function is to initialize some variables in the begining of the application
  	private static void startingApplication() {
    	WebLearningClass.getAllFilesinaFolder();
  	} 
}
