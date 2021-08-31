package PhD.OntologyReuseProject;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import AgentClasses.*;
import BioOntologiesRepo.TermSearchUsingBioportal;
import OntologyExtractionPackage.EntityExtractionClass;
import OntologyExtractionPackage.OntologyModularity;

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
