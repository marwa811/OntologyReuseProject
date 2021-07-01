package PhD.OntologyReuseProject;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import BioOntologiesRepo.TermSearchUsingBioportal;
import OntologyExtractionPackage.EntityExtractionClass;
import OntologyExtractionPackage.OntologyModularity;
import OntologyMatchingPackage.AMLMapping;
import OntologyMatchingPackage.AMLMappings;
import OntologyMatchingPackage.OntologyMatchingAlgorithm;
import UserPreferences.*;

public class App 
{
	static Logger log = Logger.getLogger(App.class);
	private static int mb = 1024 * 1024; 
	 
	// get Runtime instance
	private static Runtime instance;
	
	private static Scanner sc;
	private static ArrayList<String> owlFilesNames=new ArrayList<String>();
	private static ArrayList<String> largeOntologiesFilesNames=new ArrayList<String>();
	 
	// get Runtime instance
//	private static Runtime instance;

    public static void main( String[] args ) throws Exception
    {
    	log.info("Your application is strating:");
    	startingApplication();
    	
        //prompt the user to get his user preferences 
        System.out.println("Please Provide us with your prefernces: ");
    	
    	//Prompt the user to enter his user preferences
    	UserPreferencesModel userPreferences= promptUserInputPreferences();
        
        //load the input ontology and retrieve its class in order to beging the reuse process
    	//return a string of all classes names seprated by commas to be used in the ontology utility class
        String classNames=EntityExtractionClass.getClassesLabelsFromInputOntology(userPreferences.getInputFileName());
        
        /*Input a class (from the input ontology) to begin the reuse proess and begin 
    	 * creating a user profile
    	 */
        System.out.println("Please Select a Class to begin the ontology Reuse Process:");
    	String className= sc.nextLine();
    	System.out.println("You selected: "+className+ " class");
    	System.out.println("Loading candidate ontologies...");

    	/*search the bioportal repository for a match to the selected class,
    	 * if found display the candidate ontologies to the user and begin the user profile
    	 * create the ontology Level preferences 
    	 */
    	
    	///
    	//get the class that have mapping with input class from bioportal seachTerm Function
    	ArrayList<String> termSearchResultOntologies= TermSearchUsingBioportal.searchByTermBioportal(className);
    	if(termSearchResultOntologies.size()==0)
    		System.out.println("This class can not be extended, no matching ontologies found.");
    	else 
    	{
    		//here we have two main issues with the list of candidate ontologies "termSearchResultOntologies"
    		//1. Very large ontology, extract a module using the input class name and append its IRI to the list
    		//2. Not OWL file ontology, exclude it from the list
    	//	ArrayList<String> modulesOfLaregFilesIRIs = getModulesIRIFromLargeOntologies(className,termSearchResultOntologies);
    		termSearchResultOntologies=excludeNonOWLOntologies(termSearchResultOntologies);
    	//	if(modulesOfLaregFilesIRIs.size()>0)
    		//	termSearchResultOntologies.addAll(modulesOfLaregFilesIRIs);
    		OntologyUtilityClass.calculateOntologyUtilityFunction(classNames,userPreferences.getUserPrefOntologyType(),
    				userPreferences.getUserPrefOntologies(),termSearchResultOntologies,userPreferences);
    		//ConceptUtilityClass.calculateConceptUtilityFunction(className, userPreferences.getInputFileName(), termSearchResultOntologies);
    	}
    	//System.out.println("Please Select the ontology you want to use in the Reuse Process: (if more than one use ',' to seprate)");
    	//String ontologies= sc.nextLine();
    	
    	// end main
    }
    
    
  //-------------------------------------------------------------------------------	
  	//The function takes an input string from the user, split it by ',' then uppercase 
  	//the file(s) name(s) and add .owl extension and return a list of files names 
  	public static ArrayList<String> getOntologyFileNames(String ontologies){
  		ArrayList<String> ontologyList=new ArrayList<String>();
  		if(ontologies.contains(",")) {
  			String[] result = ontologies.split(",");
  			  for (int x=0; x<result.length; x++) {
  			      ontologyList.add(result[x].trim().toUpperCase()+".owl");
  			      System.out.println("You Choose Ontology:  " +ontologyList.get(x));
  			    }
  		}
  		else {
  			ontologyList.add(ontologies+".owl");		
  			System.out.println("You selected: "+ontologies.trim().toUpperCase()+ " ontology");
  			}
  		return ontologyList;
  	}
  	//--------------------------------------------------------------------
  	//This function is to initialize some variables in the begining of the application
  	private static void startingApplication() {
    	getAllFilesinaFolder();
  	}
  	//----------------------------------------------------------------------
  //The function checks if a set of ontologies are very large OWL ontologies or not, 
  	//if yes get a module from each one and add its IRI in a list to append them in the termSearchResultOntologies
  	private static ArrayList<String> getModulesIRIFromLargeOntologies(String className ,ArrayList<String> termSearchResultOntologies) throws OWLOntologyCreationException{
  		String ontologyIRI="";
  		String owlFileName="";
  		String acronym="";
  		ArrayList<String> modulesIRI=new ArrayList<String>();
  		for(int i=0; i<termSearchResultOntologies.size() ; i++) {
  			ontologyIRI= termSearchResultOntologies.get(i);
  			acronym=ontologyIRI.substring(ontologyIRI.lastIndexOf('/')+1, ontologyIRI.length());
  			owlFileName=acronym+".owl";
  			if(largeOntologiesFilesNames.contains(owlFileName)) {
  				String moduleIRI=OntologyModularity.getModule(className, ontologyIRI);
  				modulesIRI.add(moduleIRI);
  			}		
  		}	
  		return modulesIRI;
  	}
  	//---------------------------------------------------------------------
  	//The function checks if a set of ontologies are OWL ontologies or not, if not execlude them from the set
	private static ArrayList<String> excludeNonOWLOntologies(ArrayList<String> setofOntologies){
  		String fileName="";
  		String newFileName="";
  		String acronym="";
  		ArrayList<String> newSetofOntologies = new ArrayList<String>();
  		for(int i=0; i<setofOntologies.size() ; i++) {
  			fileName= setofOntologies.get(i);
  			acronym=fileName.substring(fileName.lastIndexOf('/')+1, fileName.length());
  			newFileName=acronym+".owl";
  			if(owlFilesNames.contains(newFileName))
  				newSetofOntologies.add(fileName);
  		}
  		return newSetofOntologies;
  	}
  	
  	//--------------------------------------------------------------------
  	//This Function returns a list of the names of all files in OWLOntologies directory
	//It is used to get the names of the OWL files and the names of the very large files >50MB 
  	private static void getAllFilesinaFolder() {
  		 //Creating a File object for directory
        File directoryPathOWLFiles = new File("C://Users//marwa//eclipse-workspace-photon//OntologyReuseProject//OWLOntologies");
        File directoryPathLargeFiles = new File("C://Important files//large ontologies");
        FilenameFilter textFilefilter = new FilenameFilter(){
           public boolean accept(File dir, String name) {
              if (name.endsWith(".owl")) {
                 return true;
              } else {
                 return false;
              }
           }
        };
        //List of all the text files
        String oWLFilesList[] = directoryPathOWLFiles.list(textFilefilter);
        String largeFilesList[] = directoryPathLargeFiles.list(textFilefilter);
        //System.out.println("List of OWL text files in the specified directory:");
        int k=0;
        int f=0;
        for(String fileName : oWLFilesList) {
           //System.out.println(fileName);
           owlFilesNames.add(k++,fileName);
        }
        for(String fileName : largeFilesList) {
            //System.out.println(fileName);
        	largeOntologiesFilesNames.add(f++,fileName);
         }
  	}
  	//------------------------------------------------------------------
  	
  	public static UserPreferencesModel promptUserInputPreferences() {
  		
  		//A UserPreferencesModel object to collect user preferences
  		UserPreferencesModel userPreferencesModel=new UserPreferencesModel();
    	sc = new Scanner(System.in);   	
    	
    	//Ask the user to enter his input ontology/ in the application browse for it
    	String inputFileName="";
    	System.out.println("Please enter the name of your input ontology: "); 
    	String name= sc.nextLine();    //read input     
        inputFileName="OWLOntologies/"+name+".owl";
      
        //test the existance of such an owl file in the directory
        //if not found this may not be an OWL file 
        File file = new File(inputFileName);
        while(! file.isFile()) {
        	System.out.println("There is no ontology with this name, maybe not in OWL format");
        	System.out.println("Please enter the name of your input ontology: ");
        	name= sc.nextLine();    //read input     
            inputFileName="OWLOntologies/"+name+".owl";
            file = new File(inputFileName);
        }
        log.info("Your file is:"+ inputFileName);
        userPreferencesModel.setInputFileName(inputFileName);
        
        //get the user's preferred ontology domain(s)
        System.out.println("What is your preferred ontology domain(s): (select from a list)");
        String prefOntologyDomain;          //read input 
        ArrayList<String> prefOntologyDomains=new ArrayList<String>();
        while (!(prefOntologyDomain = sc.nextLine()).isEmpty()) 
        { 
        	prefOntologyDomains.add(prefOntologyDomain);
        }
        System.out.println("You entered: ");
        for(int j=0; j<prefOntologyDomains.size(); j++)
        {
        	System.out.print(prefOntologyDomains.get(j)+ "  "); 
        }
        System.out.println();
        userPreferencesModel.setUserPrefDomain(prefOntologyDomains);
        
        //get the user's preferred ontology popularity
        System.out.println("What is your preferred popularity degree: "+"\n"
        		+ "(1. Very popular    2. Medium popularity   3. Not an important factor): ");
        int prefOntologyPopularity= Integer.parseInt( sc.nextLine());
        userPreferencesModel.setUserPrefOntologyPopularity(prefOntologyPopularity);
        
        //get the user's preferred ontology coverage
        System.out.println("What is your preferred coverage degree: "+"\n"
        		+ "(1. Strong coverage    2. Medium coverage   3. Not an important factor): ");
        int prefOntologycoverage= Integer.parseInt( sc.nextLine());
        userPreferencesModel.setUserPrefOntologyCoverage(prefOntologycoverage);
        
        //get the user's preferred ontolog(ies)
        System.out.println("What is your preferred ontology(ies): (select from a list)");
        String prefOntology;          //read input 
        ArrayList<String> prefOntologies=new ArrayList<String>();
        while (!(prefOntology = sc.nextLine()).isEmpty()) 
        { 
        	prefOntologies.add(prefOntology);
        }
        System.out.println("You entered: ");
        for(int j=0; j<prefOntologies.size(); j++)
        {
        	System.out.print(prefOntologies.get(j)+ "  "); 
        }
        System.out.println();
        userPreferencesModel.setUserPrefOntologies(prefOntologies);
        
        //get the user's preferred ontology type
        System.out.println("What is your preferred ontology type: "+"\n"
        		+ "(1. Taxonomy   2. Full semantic ontology   3. Not an important factor): ");
        int prefOntologyType= Integer.parseInt( sc.nextLine());
        userPreferencesModel.setUserPrefOntologyType(prefOntologyType);
         
		return userPreferencesModel;
  	}  	
}