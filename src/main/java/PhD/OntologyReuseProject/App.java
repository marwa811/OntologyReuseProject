package PhD.OntologyReuseProject;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.log4j.Logger;

import BioOntologiesRepo.TermSearchUsingBioportal;
import OntologyExtractionPackage.EntityExtractionClass;
import UserPreferences.*;


public class App 
{
	static Logger log = Logger.getLogger(App.class);
	
	private static Scanner sc;
//	private static int mb = 1024 * 1024; 
	 
	// get Runtime instance
//	private static Runtime instance;

    public static void main( String[] args ) throws Exception
    {
    	log.info("Your application is strating:");
    	
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
    	ArrayList<String> resultedOntologies= TermSearchUsingBioportal.searchByTermBioportal(className);
    	if(resultedOntologies.size()==0)
    		System.out.println("This class can not be extended, no matching ontologies found.");
    	else 
    	{
    		//TermSearchUsingBioportal.printOntologyNames(resultedOntologies);
    		
    		OntologyUtilityClass.calculateOntologyUtilityFunction(classNames,userPreferences.getUserPrefOntologyType(),
    				userPreferences.getUserPrefOntologies(),resultedOntologies,userPreferences);
    	}
    	System.out.println("Please Select the ontology you want to use in the Reuse Process: (if more than one use ',' to seprate)");
    	String ontologies= sc.nextLine();
    	ArrayList<String> ontologyFilesNames= getOntologyFileNames(ontologies);
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
