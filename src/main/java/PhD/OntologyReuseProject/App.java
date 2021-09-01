package PhD.OntologyReuseProject;

import org.apache.log4j.Logger;

import AgentClasses.*;

public class App 
{
	static Logger log = Logger.getLogger(App.class);
	private static int mb = 1024 * 1024; 
	 
	// get Runtime instance
	private static Runtime instance;
	
	 
	// get Runtime instance
//	private static Runtime instance;

    public static void main( String[] args ) throws Exception
    {
    	//System.out.println(System.getProperty("java.class.path"));
    	log.info("Your application is strating:");
    	startingApplication();
    	
        LearningClass test= new LearningClass();
        test.beginIterations(0);    	
    	// end main
    }
    
  	//--------------------------------------------------------------------
  	//This function is to initialize some variables in the begining of the application
  	private static void startingApplication() {
    	LearningClass.getAllFilesinaFolder();
  	}
  	
  
  	//------------------------------------------------------------------
  	
  	
}
