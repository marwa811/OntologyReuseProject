package AgentClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import BioOntologiesRepo.TermSearchUsingBioportal;
import OntologyExtractionPackage.EntityExtractionClass;
import OntologyExtractionPackage.OntologyModularity;


public class WebLearningClass {
	static Logger log = Logger.getLogger(LearningClass.class);
	private static Scanner sc;
	private static ArrayList<String> owlFilesNames=new ArrayList<String>();
	private static ArrayList<String> largeOntologiesFilesNames=new ArrayList<String>();
	private static FinalResultList finalResultList=new FinalResultList();
	private static Deque<IterationClass> iterationsQueue = new ArrayDeque<IterationClass>();
	
	public void beginIterations() throws Exception {
		int count=0;
		do {
		//if first iteration collect user preferences and calculate candidate ontologies
		if(count == 0) 
			firstIteration();
		else
			otherIterations();
		count++;
		}while(count<10);
	}	
	//////////////////////////////////////////////////////
	public static void  firstIteration() throws Exception {
		//prompt the user to get his user preferences 
        System.out.println("Please Provide us with your prefernces: ");
        IterationClass firstIteration =new IterationClass(1);
    	//Prompt the user to enter his user preferences
    	//firstIteration= promptUserInputPreferences(firstIteration);
    	firstIteration=promptUserInputPreferencesFromFile(firstIteration);
    	
    	//load the input ontology and 
    	//retrieve its class in order to beging the reuse process
    	//return a string of all classes names seprated by commas to be used in the ontology utility class
    	String classNames=EntityExtractionClass.getClassesLabelsFromInputOntology(firstIteration.getUserPreferences().getInputFileName());
        
        /* Input a class (from the input ontology) to begin the reuse proess and begin 
    	 * creating a user profile
    	 */
        //System.out.println("Please Select a Class to begin the ontology Reuse Process:");
    	//String className= sc.nextLine();
    	//System.out.println("You selected: "+className+ " class");
    	//String inputClassIRI=EntityExtractionClass.getClassIRI(firstIteration.getUserPreferences().getInputFileName(),className);
    	//firstIteration.setInputClassName(inputClassIRI);
    	System.out.println("Loading candidate ontologies...");

    	/* search the bioportal repository for a match to the selected class,
    	 * if found display the candidate ontologies to the user and begin the user profile
    	 * create the ontology Level preferences 
    	 */
    	
    	///
    	//get the class that have mapping with input class from bioportal seachTerm Function
    	String className=firstIteration.getInputClassName().substring(firstIteration.getInputClassName().lastIndexOf('#')+1);
    	ArrayList<String> bioPortalSearchResult= TermSearchUsingBioportal.searchByTermBioportal(className);   
    	if(bioPortalSearchResult.size()==0)
    		System.out.println("This class can not be extended, no matching ontologies found.");
    	else 
    	{
    		//to remove reprated ontology Id coming from Bioportal search service
    		bioPortalSearchResult=excludeRedundantOntologies(bioPortalSearchResult);
    		//here we have two main issues with the list of candidate ontologies "termSearchResultOntologies"
    		//1. Very large ontology, extract a module using the input class name and append its IRI to the list
    		//2. Not OWL file ontology, exclude it from the list
    		bioPortalSearchResult=excludeNonOWLOntologies(bioPortalSearchResult);
    		System.out.println("The size bioPortalSearchResult after excludeNonOWL: "+bioPortalSearchResult.size());
    		ArrayList<String> modulesOfLaregFilesIRIs = getModulesIRIFromLargeOntologies(className,bioPortalSearchResult);
    		if(modulesOfLaregFilesIRIs.size()>0)
    			bioPortalSearchResult.addAll(modulesOfLaregFilesIRIs);
    		
    		ArrayList<CandidateOntologyClass> candidateOntologies= populateCandidateOntologyIDs(bioPortalSearchResult);
    		
    		candidateOntologies= OntologyUtilityClass.calculateOntologyUtilityFunction(classNames,candidateOntologies,firstIteration.getUserPreferences());
    		Collections.sort(candidateOntologies,CandidateOntologyClass.sortByOntologyUtilityScore);
    		for(CandidateOntologyClass t: candidateOntologies){
    			t.display();
    		}	
    	   // ConceptUtilityClass.calculateConceptUtilityFunction(className, firstIteration.getUserPreferences().getInputFileName(), bioPortalSearchResult);
    		candidateOntologies=ConceptUtilityClass.calculateConceptUtilityFunction10(className, firstIteration.getUserPreferences().getInputFileName(), candidateOntologies);
    		Collections.sort(candidateOntologies,CandidateOntologyClass.sortByTotalUtilityScore);
    		// write the final result to a result JSON file
    	    // create object mapper instance
    		try {
    		ObjectMapper mapper = new ObjectMapper();

    		// create an instance of DefaultPrettyPrinter
    		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

    	    // convert book object to JSON file
    		writer.writeValue(Paths.get("results.json").toFile(), candidateOntologies);

    		} catch (Exception ex) {
    	    ex.printStackTrace();
    		}
    		for(CandidateOntologyClass t: candidateOntologies){
    			t.display1();
    		}
    		firstIteration.setCandidateOntologies(candidateOntologies);		
    	}
    	String selectedOntology="";
    	String selectedClass="";
    	BufferedReader br = new BufferedReader(new FileReader("ClassName.txt"));
  		String line = null;
  		while ((line = br.readLine()) != null) {
  		  String[] values = line.split("\\|");
  		  for (int i=0; i<values.length  ; i++) {
  			  //if third item in  the txt file == selected ontology ID
  			  if(i==2) 
  		        selectedOntology=values[i];
  			//if fourth item in the txt file == selected class ID
  			  if(i==3) 
  				selectedClass= values[1];   		//read input 
  		  }
  		}
    		
   // 	System.out.println("Please Select the ontology you want to use in the Reuse Process: (select from a ranked list)");
 //   	String selectedOntology= sc.nextLine();
    	firstIteration.setSelectedOntology(selectedOntology);
    	firstIteration.displayRewardValue();
    	firstIteration.printMatchedClassesOfSelectedOntology(selectedOntology);
    //	System.out.println("Please Select the class you want to reuse: (select from a ranked list)");
   // 	String selectedClass= sc.nextLine();
    	
    	//update the input ontology name to the new extended file 
    /*	EntityExtractionClass.addClassInformationToSourceOntology(firstIteration.getUserPreferences().getInputFileName(),
    		firstIteration.getInputClassName(), selectedOntology, selectedClass);*/
    	
    	//update the ontology score in the candidate ontology list in the first iteration
    	updateCandidateOntologyScore(firstIteration);   	  	   	
    	updateFinalResultListScores();
    	printFinalResult();
    	
	}
	//----------------------------------------------------------------------------
	public static void otherIterations() throws Exception {
		
        IterationClass iteration =new IterationClass(1);
        //get the last iteration information
        IterationClass lastIteration =iterationsQueue.getLast();
        //copy user preferences and ad them in the new iteration
        iteration.setUserPreferences(lastIteration.getUserPreferences());
    	//load the updated input ontology and 
    	//retrieve its class in order to beging the reuse process
    	//return a string of all classes names seprated by commas to be used in the ontology utility class
    	
    	String classNames=EntityExtractionClass.getClassesLabelsFromInputOntology(iteration.getUserPreferences().getInputFileName());
        
        /* Input a class (from the input ontology) to begin the reuse proess and begin 
    	 * creating a user profile
    	 */
        System.out.println("Please Select a Class to Reuse:");
    	String className= sc.nextLine();
    	System.out.println("You selected: "+className+ " class");
    	String inputClassIRI=EntityExtractionClass.getClassIRI(iteration.getUserPreferences().getInputFileName(),className);
    	iteration.setInputClassName(inputClassIRI);
    	System.out.println("Loading candidate ontologies...");

    	/* search the bioportal repository for a match to the selected class,
    	 * if found display the candidate ontologies to the user and begin the user profile
    	 * create the ontology Level preferences 
    	 */
    	
    	///
    	//get the class that have mapping with input class from bioportal seachTerm Function
    	ArrayList<String> bioPortalSearchResult= TermSearchUsingBioportal.searchByTermBioportal(className);
    	   
    	if(bioPortalSearchResult.size()==0)
    		System.out.println("This class can not be extended, no matching ontologies found.");
    	else 
    	{
    		//to remove reprated ontology Id coming from Bioportal search service
    		bioPortalSearchResult=excludeRedundantOntologies(bioPortalSearchResult);
    		
    		//here we have two main issues with the list of candidate ontologies "termSearchResultOntologies"
    		//1. Very large ontology, extract a module using the input class name and append its IRI to the list
    		//2. Not OWL file ontology, exclude it from the list
    		bioPortalSearchResult=excludeNonOWLOntologies(bioPortalSearchResult);
    		ArrayList<String> modulesOfLaregFilesIRIs = getModulesIRIFromLargeOntologies(className,bioPortalSearchResult);
    		if(modulesOfLaregFilesIRIs.size()>0)
    			bioPortalSearchResult.addAll(modulesOfLaregFilesIRIs);
   
    		ArrayList<CandidateOntologyClass> candidateOntologies= populateCandidateOntologyIDs(bioPortalSearchResult);
    		candidateOntologies= OntologyUtilityClass.calculateOntologyUtilityFunction(classNames,candidateOntologies,iteration.getUserPreferences());
    		//Collections.sort(candidateOntologies,CandidateOntologyClass.sortByOntologyUtilityScore);
    		/*for(CandidateOntologyClass t: candidateOntologies){
    			t.display();
    		}*/	
    		candidateOntologies= finalResultList.calculateOntologyAggregateScoreFunction(candidateOntologies);
    	   // ConceptUtilityClass.calculateConceptUtilityFunction(className, firstIteration.getUserPreferences().getInputFileName(), bioPortalSearchResult);
    		candidateOntologies=ConceptUtilityClass.calculateConceptUtilityFunction10(className, iteration.getUserPreferences().getInputFileName(), candidateOntologies);
    		Collections.sort(candidateOntologies,CandidateOntologyClass.sortByTotalUtilityScore);
    		// write the final result to a result JSON file
    	    // create object mapper instance
    		try {
    		ObjectMapper mapper = new ObjectMapper();

    		// create an instance of DefaultPrettyPrinter
    		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

    	    // convert book object to JSON file
    		writer.writeValue(Paths.get("results.json").toFile(), candidateOntologies);

    		} catch (Exception ex) {
    	    ex.printStackTrace();
    		}
    		for(CandidateOntologyClass t: candidateOntologies){
    			t.display1();
    		}
    		iteration.setCandidateOntologies(candidateOntologies);		
    	}
    	System.out.println("Please Select the ontology you want to use in the Reuse Process: (select from a ranked list)");
    	String selectedOntology= sc.nextLine();
    	iteration.setSelectedOntology(selectedOntology);
    	iteration.displayRewardValue();
    	iterationToJSON(iteration);
    	iteration.printMatchedClassesOfSelectedOntology(selectedOntology);
    	System.out.println("Please Select the class you want to reuse: (select from a ranked list)");
    	String selectedClass= sc.nextLine();
    	
    	EntityExtractionClass.addClassInformationToSourceOntology(iteration.getUserPreferences().getInputFileName(),
    		iteration.getInputClassName(), selectedOntology, selectedClass);
    	
    	updateCandidateOntologyScore(iteration);   	
    	updateFinalResultListScores(); 
    	printFinalResult();
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
		//------------------------------------------------------------
	  	//The function checks if the list contains redundant ontology names, if so remove them from the list
		private static ArrayList<String> excludeRedundantOntologies(ArrayList<String> setofOntologies){
	  		
	  		ArrayList<String> newSetofOntologies = new ArrayList<String>();
	  		for(String ontologyID: setofOntologies) {
	  			if(!newSetofOntologies.contains(ontologyID))
	  				newSetofOntologies.add(ontologyID);
	  		}
	  		return newSetofOntologies;
	  	}
	  	
	  	public static IterationClass promptUserInputPreferences(IterationClass firstIteration) throws OWLOntologyCreationException, OWLException {
	  		
	  		//A UserPreferencesModel object to collect user preferences
	  		UserPreferencesModel userPreferences=new UserPreferencesModel();
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
	        
	        //copy the file to the Temp_Working directory
	        //if exists a file with the same name, replace it by a new fresh copy
	        File tempFile = new File("Working_Folder/"+name+".owl");
	        try {
				Files.copy(file.toPath(), tempFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        //Set the input file path to the temporary working directory
	        userPreferences.setInputFileName("Working_Folder/"+name+".owl");
	                
	        //get the user's preferred ontology domain(s)
	        System.out.println("What is your preferred ontology domain(s): (select from a list)");
	        String prefOntologyDomain;          //read input 
	        ArrayList<String> prefOntologyDomains=new ArrayList<String>();
	        while (!(prefOntologyDomain = sc.nextLine()).isEmpty()) { 
	        	prefOntologyDomains.add(prefOntologyDomain);
	        }
	        System.out.println("You entered: ");
	        for(int j=0; j<prefOntologyDomains.size(); j++){
	        	System.out.print(prefOntologyDomains.get(j)+ "  "); 
	        }
	        System.out.println();
	        userPreferences.setUserPrefDomain(prefOntologyDomains);
	        
	        //get the user's preferred ontology popularity
	        System.out.println("Do you prefer popular ontologies: "+"\n"
	        		+ "(1. Yes   2. No, not an important factor): ");
	        int prefOntologyPopularity= Integer.parseInt( sc.nextLine());
	        userPreferences.setUserPrefOntologyPopularity(prefOntologyPopularity);
	        
	        //get the user's preferred ontology coverage
	        System.out.println("Do you prefer ontologies with high coverage score: "+"\n"
	        		+ "(1. Yes   2. No, not an important factor): ");
	        int prefOntologycoverage= Integer.parseInt( sc.nextLine());
	        userPreferences.setUserPrefOntologyCoverage(prefOntologycoverage);
	        
	        //get the user's preferred ontolog(ies)
	        System.out.println("What is your preferred ontology(ies): (select from a list)");
	        String prefOntology;          //read input 
	        ArrayList<String> prefOntologies=new ArrayList<String>();
	        while (!(prefOntology = sc.nextLine()).isEmpty()) { 
	        	prefOntologies.add(prefOntology);
	        }
	        System.out.println("You entered: ");
	        for(int j=0; j<prefOntologies.size(); j++){
	        	System.out.print(prefOntologies.get(j)+ "  "); 
	        }
	        System.out.println();
	        userPreferences.setUserPrefOntologies(prefOntologies);
	        
	        //get the user's preferred ontology type
	        System.out.println("What is your preferred ontology type: "+"\n"
	        		+ "(1. Taxonomy   2. Full semantic ontology   3. Not an important factor): ");
	        int prefOntologyType= Integer.parseInt( sc.nextLine());
	        userPreferences.setUserPrefOntologyType(prefOntologyType);
	        firstIteration.setUserPreferences(userPreferences); 
			return firstIteration;
	  	}
	  	//----------------------------------------------------------------------------
	  	//If the user preferences comes from a file (in the web app)
	  	public static IterationClass promptUserInputPreferencesFromFile(IterationClass firstIteration) throws OWLOntologyCreationException, OWLException, IOException {
	  		
	  		//A UserPreferencesModel object to collect user preferences
	  		UserPreferencesModel userPreferences=new UserPreferencesModel();
	  		String classIRI="";
	  		BufferedReader br = new BufferedReader(new FileReader("UserPreference.txt"));
	  		String line = null;
	  		while ((line = br.readLine()) != null) {
	  		  String[] values = line.split("\\|");
	  		  for (int i=0; i<values.length  ; i++) {
	  			  //if first item in  the txt file == input ontology name
	  			  if(i==0) {
	  				//Set the first item as input ontology name  
	  		        String inputFileName="OWLOntologies/"+values[i];
	  		        File file = new File(inputFileName);
	  		        File tempFile = new File("Working_Folder/"+values[i]);
	  		        try {
	  					Files.copy(file.toPath(), tempFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
	  				} catch (IOException e) {
	  					// TODO Auto-generated catch block
	  					e.printStackTrace();
	  				}
	  		        //Set the input file path to the temporary working directory
	  		        userPreferences.setInputFileName("Working_Folder/"+values[i]);
	  			  }
	  			//if Second item in the txt file == preferred domain(s)
	  			  if(i==1) {
	  				String[] allDomains = values[1].split("\\,");   		//read input 
	  		        ArrayList<String> prefOntologyDomains=new ArrayList<String>();
	  		        for(int j=0; j<allDomains.length; j++){
	  		        	prefOntologyDomains.add(allDomains[j]);
	  		        }
	  		        userPreferences.setUserPrefDomain(prefOntologyDomains);
	  			  }
	  			//if third item in the txt file == popularity
	  			  if(i==2) {
	  		        userPreferences.setUserPrefOntologyPopularity(Integer.parseInt( values[2]));
	  			  }
	  			//if fourth item in the txt file == coverage
	  			  if(i==3) {
	  		        userPreferences.setUserPrefOntologyCoverage(Integer.parseInt( values[3]));
	  			  }
	  			//if fifth item in the txt file == preferred ontology(s)
	  			  if(i==5) {
	  				String[] allontologies = values[5].split("\\,");   		//read input 
	  		        ArrayList<String> prefOntologies=new ArrayList<String>();
	  		        for(int j=0; j<allontologies.length; j++){
	  		        	prefOntologies.add(allontologies[j]);
	  		        }
	  		        userPreferences.setUserPrefOntologies(prefOntologies);
	  			  }
	  			//if sixth item in the txt file == ontology type
	  			  if(i==4) {
	  		        userPreferences.setUserPrefOntologyType(Integer.parseInt( values[4]));
	  			  }
	  			  //if seventh item in the txt file == Class IRI
	  			  if(i==6) {
	  				classIRI=values[6];
	  			  }
	  		  }
	  		}
	  		br.close(); 	
	        firstIteration.setUserPreferences(userPreferences); 
	        firstIteration.setInputClassName(classIRI);
			return firstIteration;
	  	}
	//-----------------------------------------------------------
		//create a candidate ontology object for each candidate ontology and add 1 to its score
	  	private static ArrayList<CandidateOntologyClass> populateCandidateOntologyIDs(ArrayList<String> bioPortalSearchResults)
		{
			ArrayList<CandidateOntologyClass> candidateOntologies= new ArrayList<CandidateOntologyClass>();
				for(String ontologyId: bioPortalSearchResults) {
					CandidateOntologyClass candidateOntology = new CandidateOntologyClass(ontologyId);
					candidateOntology.addToOntologyScore(1);
					candidateOntologies.add(candidateOntology);
				}
			return candidateOntologies;
		}
	//-------------------------------------------------------------------
		private static void iterationToJSON(IterationClass firstIteration) {
			try {
		      	// create object mapper instance
		        ObjectMapper mapper = new ObjectMapper();
		        
		        // create an instance of DefaultPrettyPrinter
		        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

		        // write list of ontologies to JSON file
		        writer.writeValue(Paths.get("UserIterations.json").toFile(), firstIteration);    	
		    	} catch (Exception ex) {
		    		ex.printStackTrace();
		    	}
		}
		
		//--------------------------------------------------------------------
	  	//This Function returns a list of the names of all files in OWLOntologies directory
		//It is used to get the names of the OWL files and the names of the very large files >50MB 
	  	public static void getAllFilesinaFolder() {
	  		 //Creating a File object for directory
	        File directoryPathOWLFiles = new File("OWLOntologies/");
	       // File directoryPathLargeFiles = new File("C:\\Important files\\large ontologies");
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
	       // String largeFilesList[] = directoryPathLargeFiles.list(textFilefilter);
	        //System.out.println("List of OWL text files in the specified directory:");

	        int k=0;
	        int f=0;
	        for(String fileName : oWLFilesList) {
	           //System.out.println(fileName);
	           owlFilesNames.add(k++,fileName);
	        }
	     /*   for(String fileName : largeFilesList) {
	            //System.out.println(fileName);
	        	largeOntologiesFilesNames.add(f++,fileName);
	         }*/
	  	}
	////////////////////////////////////////////////////////////////////////////
	//This function put the scores of the candidate ontology list 
	//For any candidate ontology the score is 1 
	//For the selected ontology 3 is added to the score
	private static void updateCandidateOntologyScore(IterationClass firstIteration){
		for(CandidateOntologyClass tempOntology: firstIteration.getCandidateOntologies()) {
			//tempOntology.addToOntologyScore(1);
			if(tempOntology.getOntologyID().equals(firstIteration.getSelectedOntology()))
				tempOntology.addToOntologyScore(3);
			}
		//sort the ontology list desc by the ontology score 
		Collections.sort(firstIteration.getCandidateOntologies(),CandidateOntologyClass.sortByOntologyScore);
		
		//if the Deque size is still less than 3 
	    if(iterationsQueue.size()< 3)
	    	iterationsQueue.add(firstIteration);
	    //if the Deque size is 3, remove the first one and add the new one as last item 
	    else if(iterationsQueue.size()==3)
	    {
	    	iterationsQueue.removeFirst();
	    	iterationsQueue.add(firstIteration);
	    }
	}
	//-------------------------------------------------------------------------
	//This function agregates the ontology scores and generate a final candidate ontology 
	//list with their scores
	private static void updateFinalResultListScores() {
		IterationClass iteration= new IterationClass();
		ArrayList<CandidateOntologyClass> agregatedCandidateOntology=new ArrayList<CandidateOntologyClass>();;		
		//to preserve the selected ontologies list
		ArrayList<String> selectedOntologyList=new ArrayList<String>();
		//to presevre the reward scores for each ontology
		ArrayList<Double> rewardScores=new ArrayList<Double>();
				
		// if the iterations deque is not empty/ contains at least one item
		if (iterationsQueue.size()>0) {	
			Iterator<IterationClass> it = iterationsQueue.iterator();
			//loop for each iteration in the deque
			int count=0;
	        while (it.hasNext()) { 
	        	iteration=it.next(); 
	        	count++;
        		//if this is the first iteration in the deque put all items in the list
	        	if(count==1) 
	        		for(CandidateOntologyClass temp: iteration.getCandidateOntologies())
	        			agregatedCandidateOntology.add(temp.copy());
	        	else
	        	{
	        		//if this is not the first iteration
	        		//put all final candidate ontology IDs in a list to test of a particular one exists or not
	        		ArrayList<String> agregatedCandidateOntologyIDs=new ArrayList<String>();
	        		for(CandidateOntologyClass tempOnto: agregatedCandidateOntology) 
	    				agregatedCandidateOntologyIDs.add(tempOnto.getOntologyID());
	        		
	        		for(CandidateOntologyClass temp: iteration.getCandidateOntologies()) {
	        			//if this ontology ID does not exist in the agregated list,add it with its score 
	        			if(!(agregatedCandidateOntologyIDs.contains(temp.getOntologyID()))) 
	        				agregatedCandidateOntology.add(temp.copy());	
	        			
	        			//if this ontology ID exists in the agregated list update its score
	        			else {
	        				for(CandidateOntologyClass tempCandOnto: agregatedCandidateOntology) 
	        					if(temp.getOntologyID().equals(tempCandOnto.getOntologyID())) 	        						
	        						tempCandOnto.addToOntologyScore(temp.getOntologyScore());						
	        				}	
	        			}
	        		}
	        	selectedOntologyList.add(iteration.getSelectedOntology());	
	        	rewardScores.add(iteration.getRewardValue());
	        }
		}
		//sort the ontology list desc by the ontology score 
		Collections.sort(agregatedCandidateOntology,CandidateOntologyClass.sortByOntologyScore);
		
		finalResultList.setFinalCandidateOntologyList(agregatedCandidateOntology);
		finalResultList.setSelectedOntology(selectedOntologyList);
		finalResultList.setRewardScore(rewardScores);
	}
	//------------------------------------------------------------------------
	//To print the final results after each iteration
	private static void printFinalResult(){
		int count=1;
		System.out.println("The Candidate ontologies List: ");
		for(CandidateOntologyClass tempOntology: finalResultList.getFinalCandidateOntologyList()) 			
	  	{
			System.out.println(count++ +". "+ tempOntology.getOntologyID()+ "   with score: "+ tempOntology.getOntologyScore());
	  	}
		System.out.println("The set of selected ontologies are: ");
		for(String temp: finalResultList.getSelectedOntology()) 			
	  	{
			System.out.println("1. "+ temp);
	  	}
		System.out.println("The set of reward scores are: ");
		for(double temp: finalResultList.getRewardScore()) 			
	  	{
			System.out.println("1. "+ temp);
	  	}
	}
	
}
