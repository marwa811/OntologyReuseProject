package AgentClasses;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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

public class LearningClass {
	static Logger log = Logger.getLogger(LearningClass.class);
	private static Scanner sc;
	private static ArrayList<String> owlFilesNames=new ArrayList<String>();
	private static ArrayList<String> largeOntologiesFilesNames=new ArrayList<String>();
	private static FinalResultList finalResultList=new FinalResultList();
	private static ArrayList<IterationClass> iterations=new ArrayList<IterationClass>();
	
	public void beginIterations(int count) throws Exception {
		do {
		//if first iteration collect user preferences and calculate candidate ontologies
		if(count == 0) {
			count++; 
			firstIteration();
		}
		else {
			otherIterations();
		}
		}while(count<2);
	}	
	//////////////////////////////////////////////////////
	public static void firstIteration() throws Exception {
		//prompt the user to get his user preferences 
        System.out.println("Please Provide us with your prefernces: ");
    	
        IterationClass firstIteration =new IterationClass(1);
    	//Prompt the user to enter his user preferences
    	firstIteration= promptUserInputPreferences(firstIteration);
    	//load the input ontology and 
    	//retrieve its class in order to beging the reuse process
    	//return a string of all classes names seprated by commas to be used in the ontology utility class
    	
    	String classNames=EntityExtractionClass.getClassesLabelsFromInputOntology(firstIteration.getUserPreferences().getInputFileName());
        
        /* Input a class (from the input ontology) to begin the reuse proess and begin 
    	 * creating a user profile
    	 */
        System.out.println("Please Select a Class to begin the ontology Reuse Process:");
    	String className= sc.nextLine();
    	System.out.println("You selected: "+className+ " class");
    	String inputClassIRI=EntityExtractionClass.getClassIRI(firstIteration.getUserPreferences().getInputFileName(),className);
    	firstIteration.setInputClassName(inputClassIRI);
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
    		candidateOntologies= OntologyUtilityClass.calculateOntologyUtilityFunction(classNames,candidateOntologies,firstIteration.getUserPreferences());
    	//	Collections.sort(candidateOntologies,Collections.reverseOrder());
    		Collections.sort(candidateOntologies,CandidateOntologyClass.sortByOntologyUtilityScore);
    		for(CandidateOntologyClass t: candidateOntologies){
    			t.display();
    		}	
    	   // ConceptUtilityClass.calculateConceptUtilityFunction(className, firstIteration.getUserPreferences().getInputFileName(), bioPortalSearchResult);
    		candidateOntologies=ConceptUtilityClass.calculateConceptUtilityFunction10(className, firstIteration.getUserPreferences().getInputFileName(), candidateOntologies);
    		Collections.sort(candidateOntologies,CandidateOntologyClass.sortByTotalUtilityScore);
    		for(CandidateOntologyClass t: candidateOntologies){
    			t.display1();
    		}
    		firstIteration.setCandidateOntologies(candidateOntologies);		
    	}
    	System.out.println("Please Select the ontology you want to use in the Reuse Process: (select from a ranked list)");
    	String selectedOntology= sc.nextLine();
    	firstIteration.setSelectedOntology(selectedOntology);
    	firstIteration.displayRewardValue();
    	iterationToJSON(firstIteration);
    	firstIteration.printMatchedClassesOfSelectedOntology(selectedOntology);
    	System.out.println("Please Select the class you want to reuse: (select from a ranked list)");
    	String selectedClass= sc.nextLine();
    	
    //	EntityExtractionClass.addClassInformationToSourceOntology(firstIteration.getUserPreferences().getInputFileName(),
    	//	firstIteration.getInputClassName(), selectedOntology, selectedClass);
    	//update the input ontology name to the new extended file 
    	updateFirstFinalResultList(firstIteration);
    	printFinalResult();
    	iterations.add(firstIteration);
	}
	//----------------------------------------------------------------------------
	public static void otherIterations() throws Exception {
		
        IterationClass iteration =new IterationClass(1);
        //get the last iteration information
        IterationClass lastIteration =iterations.get(iterations.size()-1);
        //copy user preferences and add them in the new iteration
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
    	//	Collections.sort(candidateOntologies,Collections.reverseOrder());
    		Collections.sort(candidateOntologies,CandidateOntologyClass.sortByOntologyUtilityScore);
    		for(CandidateOntologyClass t: candidateOntologies){
    			t.display();
    		}	
    	   // ConceptUtilityClass.calculateConceptUtilityFunction(className, firstIteration.getUserPreferences().getInputFileName(), bioPortalSearchResult);
    		candidateOntologies=ConceptUtilityClass.calculateConceptUtilityFunction10(className, iteration.getUserPreferences().getInputFileName(), candidateOntologies);
    		Collections.sort(candidateOntologies,CandidateOntologyClass.sortByTotalUtilityScore);
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
    	
    	//EntityExtractionClass.addClassInformationToSourceOntology(firstIteration.getUserPreferences().getInputFileName(),
    	//	firstIteration.getInputClassName(), selectedOntology, selectedClass);
    	
    	updateNotFirstFinalResultList(iteration);
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
	        userPreferences.setInputFileName(inputFileName);
	                
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
	//-----------------------------------------------------------
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
	//----------------------------------------------------------------
	private static void updateFirstFinalResultList(IterationClass firstIteration) {
		ArrayList<CandidateOntologyClass> finalCandidateOntology=new ArrayList<CandidateOntologyClass>();
	  	//For the first iteration add the candidate ontologies to the final result list
		//adjust their scores
		for(CandidateOntologyClass tempOntology: firstIteration.getCandidateOntologies()) {
			CandidateOntologyClass temp= new CandidateOntologyClass(tempOntology.getOntologyID(),1);
			if(tempOntology.getOntologyID().equals(firstIteration.getSelectedOntology()))
				temp.addToOntologyScore(3);
			finalCandidateOntology.add(temp);	
		}
		Collections.sort(finalCandidateOntology,CandidateOntologyClass.sortByOntologyScore);
		finalResultList.setFinalCandidateOntologyList(finalCandidateOntology);
		
		ArrayList<String> selectedOntologyList=new ArrayList<String>();
		selectedOntologyList.add(firstIteration.getSelectedOntology());
		finalResultList.setSelectedOntology(selectedOntologyList);
		
		ArrayList<Double> rewardScores=new ArrayList<Double>();
		rewardScores.add(firstIteration.getRewardValue());
		finalResultList.setRewardScore(rewardScores);
		
	}
	//-------------------------------------------------------------------------
	private static void updateNotFirstFinalResultList(IterationClass iteration) {
		//get the already exist candidate ontology list to compare it with the iteration list
		ArrayList<CandidateOntologyClass> finalCandidateOntology=finalResultList.getFinalCandidateOntologyList();
		//put all final candidate ontology IDs in a list
		ArrayList<String> finalCandidateOntologyIDs=new ArrayList<String>();
		for(CandidateOntologyClass tempCandOntology:finalCandidateOntology)
			finalCandidateOntologyIDs.add(tempCandOntology.getOntologyID());
			
		for(CandidateOntologyClass tempOntology: iteration.getCandidateOntologies()) 			
	  	{
			//if this ontology not exist before in the final candidate ontology list
			//add it and set its score to 1
			//if it is the selected ontology increase its score by 3
			if(!(finalCandidateOntologyIDs.contains(tempOntology.getOntologyID())))
			{
				CandidateOntologyClass candOntology=new CandidateOntologyClass(tempOntology.getOntologyID(),1);
				if(candOntology.getOntologyID().equals(iteration.getSelectedOntology()))
					candOntology.addToOntologyScore(3);
				finalCandidateOntology.add(candOntology);	
			}
			//if this ontology already in the candidate ontology list then update its score by 1
			else	
			{
				for(CandidateOntologyClass tempCandOntology:finalCandidateOntology) {
					if(tempCandOntology.getOntologyID().equals(tempOntology.getOntologyID()))
					{
						tempCandOntology.addToOntologyScore(1);
						if(tempCandOntology.getOntologyID().equals(iteration.getSelectedOntology()))
							tempCandOntology.addToOntologyScore(3);
					}
				}
			}
		}
		Collections.sort(finalCandidateOntology,CandidateOntologyClass.sortByOntologyScore);
		finalResultList.setFinalCandidateOntologyList(finalCandidateOntology);
		
		//if the selected ontology is not in the selected ontology list add it
		//else no nothing
		ArrayList<String> selectedOntologies=finalResultList.getSelectedOntology();	
			
		if(!selectedOntologies.contains(iteration.getSelectedOntology()))
			finalResultList.getSelectedOntology().add(iteration.getSelectedOntology());
		
		finalResultList.getRewardScore().add(iteration.getRewardValue());
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
