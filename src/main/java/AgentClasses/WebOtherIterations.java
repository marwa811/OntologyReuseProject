package AgentClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import BioOntologiesRepo.TermSearchUsingBioportal;
import OntologyExtractionPackage.EntityExtractionClass;
import OntologyExtractionPackage.OntologyModularity;
import AgentClasses.IterationClass;

public class WebOtherIterations {
	static Logger log = Logger.getLogger(WebOtherIterations.class);
	private static ArrayList<String> owlFilesNames=new ArrayList<String>();
	private static ArrayList<String> largeOntologiesFilesNames=new ArrayList<String>();
	private static FinalResultList finalResultList=new FinalResultList();
	private static List<IterationClass> iterationsQueue = new ArrayList<IterationClass>();

	public static void main( String[] args ) throws Exception
    {
    	log.info("OtherIterations is strating:");
    	getAllFilesinaFolder();
    	otherIterations();
    }
	
	//---------------------------------------------------------------------
	public static void otherIterations() throws Exception {	
	    ArrayList<IterationClass> iterations=new ArrayList<IterationClass>();
	    
	    //get the iterations from the iteration.json file
	    iterations= getIterationsFromJSONFile();
	    
	    //get the finalResultList from finalResultList.json file
	    finalResultList=getfinalResultListFromJSONFile();
	    
	    System.out.println("Iterations Size=  "+iterations.size());
	    IterationClass lastIteration=new IterationClass();
	    if(iterations.size()==1)
	    	//if the list contains one iteration only
	    	lastIteration=iterations.get(0);
	    else
	    	//if the list contains more than one iteration 
	    	lastIteration=iterations.get(iterations.size()-1);
	    
	    lastIteration.DispalyIteration();
	    
	    IterationClass iteration =new IterationClass(iterations.size()+1);
	    //copy user preferences and ad them in the new iteration
	    iteration.setUserPreferences(lastIteration.getUserPreferences());
		//load the updated input ontology and 
		//retrieve its class in order to beging the reuse process
		//return a string of all classes names seprated by commas to be used in the ontology utility class
		
		String classNames=EntityExtractionClass.getClassesLabelsFromInputOntology(iteration.getUserPreferences().getInputFileName());
	    
	    /* Input a class (from the input ontology) to begin the reuse proess and begin 
		 * creating a user profile
		 */
		
		String className="";
		String inputClassIRI="";
    	//get the class name from the OtherIterationsSelectedClassName file
		BufferedReader br = new BufferedReader(new FileReader("OtherIterationsSelectedClassName.txt"));
	  	String line = null;
	  	while ((line = br.readLine()) != null) {
	  		String[] values = line.split("\\|");
	  		inputClassIRI=values[0];
	  		className=values[1];
	  	}
	  	br.close();
	  	
    	if(className.equals(""))
    		className=inputClassIRI.substring(inputClassIRI.lastIndexOf('#')+1);
    	
    	iteration.setInputClassName(inputClassIRI);
		
	    /*System.out.println("Please Select a Class to Reuse:");
		String className= sc.nextLine();
		System.out.println("You selected: "+className+ " class");
		String inputClassIRI=EntityExtractionClass.getClassIRI(iteration.getUserPreferences().getInputFileName(),className);
		iteration.setInputClassName(inputClassIRI);*/
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
		
	   	String selectedOntology="";
    	String selectedClass="";
    	BufferedReader br1 = new BufferedReader(new FileReader("ClassName.txt"));
  		line = null;
  		while ((line = br1.readLine()) != null) {
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
  		br1.close();
  		
	//	System.out.println("Please Select the ontology you want to use in the Reuse Process: (select from a ranked list)");
//		String selectedOntology= sc.nextLine();
		iteration.setSelectedOntology(selectedOntology);
		iteration.displayRewardValue();
	//	iterationToJSON(iteration);
	//	iteration.printMatchedClassesOfSelectedOntology(selectedOntology);
		//System.out.println("Please Select the class you want to reuse: (select from a ranked list)");
		//String selectedClass= sc.nextLine();
		
		EntityExtractionClass.addClassInformationToSourceOntology(iteration.getUserPreferences().getInputFileName(),
			iteration.getInputClassName(), selectedOntology, selectedClass);
		
		updateCandidateOntologyScore(iteration);   	
		updateFinalResultListScores(); 
		printFinalResult();
		iterations.add(iteration);
		Iterations iter=new Iterations(iterations);
		//save all Iterations to a file
		try {
    		ObjectMapper mapper = new ObjectMapper();

    		// create an instance of DefaultPrettyPrinter
    		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

    	    // convert book object to JSON file
    		writer.writeValue(Paths.get("Iterations.json").toFile(), iter);

    		} catch (Exception ex) {
    	    ex.printStackTrace();
    		}
	}
	
	//------------------------------------------------------------------
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
	  	    	//iterationsQueue.removeFirst();
	  	    	iterationsQueue.add(firstIteration);
	  	    	iterationsQueue=iterationsQueue.subList(Math.max(iterationsQueue.size() - 3, 0), iterationsQueue.size());   
	  	    }
	  	}
	  	//-------------------------------------------------------------------------
	  	//This function agregates the ontology scores and generate a final candidate ontology 
	  	//list with their scores
	  	private static void updateFinalResultListScores() {
	  		IterationClass iteration= new IterationClass();
	  		ArrayList<CandidateOntologyClass> agregatedCandidateOntology=new ArrayList<CandidateOntologyClass>();		
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
	  		
	  		//save Final results to a json file 
	  		try {
	      		ObjectMapper mapper = new ObjectMapper();

	      		// create an instance of DefaultPrettyPrinter
	      		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

	      	    // convert book object to JSON file
	      		writer.writeValue(Paths.get("finalResultList.json").toFile(), finalResultList);

	      		} catch (Exception ex) {
	      	    ex.printStackTrace();
	      		}
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
	//----------------------------------------------------------------
	  	private static ArrayList<IterationClass> getIterationsFromJSONFile(){
	  		ArrayList<IterationClass> iterations=new ArrayList<IterationClass>();
	  		try {
		     // create a reader
		        Reader reader = Files.newBufferedReader(Paths.get("Iterations.json"));
         	        
		        //create ObjectMapper instance
		        ObjectMapper objectMapper = new ObjectMapper();
		        //read Iterations.json file into tree model
		        JsonNode parser = objectMapper.readTree(reader);
		       
		        // read iteration details
		        for(JsonNode iteration : parser.path("iterations")) {
		        	IterationClass iter=new IterationClass();
		        	System.out.println(iteration.path("iterationNo").asInt());
		        	System.out.println(iteration.path("inputClassName").asText());
		        	System.out.println(iteration.path("selectedOntology").asText());
		        	System.out.println(iteration.path("rankingNo").asInt());
		        	System.out.println(iteration.path("rewardValue").asDouble());
		        
		        	iter.setIterationNo(iteration.path("iterationNo").asInt());
		        	iter.setInputClassName(iteration.path("inputClassName").asText());
		        	iter.setSelectedOntology(iteration.path("selectedOntology").asText());
		        	iter.setrankingNo(iteration.path("rankingNo").asInt());
		        	iter.setRewardValue(iteration.path("rewardValue").asDouble());
		        
		        	// read userPreferences
		        	JsonNode preferences = iteration.path("userPreferences");
		        	UserPreferencesModel uPref=new UserPreferencesModel();
		        	System.out.println(preferences.path("inputFileName").asText());
		        	uPref.setInputFileName(preferences.path("inputFileName").asText());
		        	// read userPrefDomain
		        	ArrayList<String> userPrefDomain =new ArrayList<String>();
		        	for (JsonNode uDomain : preferences.path("userPrefDomain")) {
		        		System.out.println(uDomain.asText());
		        		userPrefDomain.add(uDomain.asText());
		        	}
		        	uPref.setUserPrefDomain(userPrefDomain);
		        
		        	System.out.println(preferences.path("userPrefOntologyPopularity").asInt());
		        	uPref.setUserPrefOntologyPopularity(preferences.path("userPrefOntologyPopularity").asInt());
		        	System.out.println(preferences.path("userPrefOntologyCoverage").asInt());
		        	uPref.setUserPrefOntologyCoverage(preferences.path("userPrefOntologyCoverage").asInt());
		        	System.out.println(preferences.path("userPrefOntologyType").asInt());
		        	uPref.setUserPrefOntologyType(preferences.path("userPrefOntologyType").asInt());
		        	// read userPrefOntology
		        	ArrayList<String> userPrefOnto =new ArrayList<String>();
		        	for (JsonNode uOntology : preferences.path("userPrefOntologies")) {
		        		System.out.println(uOntology.asText());
		        		userPrefOnto.add(uOntology.asText());
		        	}
		        	uPref.setUserPrefOntologies(userPrefOnto);
		        	iter.setUserPreferences(uPref);
		        
		        	ArrayList<CandidateOntologyClass> candidateOntologs =new ArrayList<CandidateOntologyClass>();
		        	for (JsonNode candidateOntologies : iteration.path("candidateOntologies")) {
		        		CandidateOntologyClass candOnt= new CandidateOntologyClass();
		        		System.out.println(candidateOntologies.path("ontologyID").asText());
		        		candOnt.setOntologyID(candidateOntologies.path("ontologyID").asText());
		        		OntologyUtilityScoreClass ontUS=new OntologyUtilityScoreClass();
		        		JsonNode ontologyUtilityScore = candidateOntologies.path("ontologyUtilityScore");
		        		System.out.println(ontologyUtilityScore.path("ontologyDomainScore").asDouble());
		        		System.out.println(ontologyUtilityScore.path("ontologyPopularityScore").asDouble());
		        		System.out.println(ontologyUtilityScore.path("ontologyCoverageScore").asDouble());
		        	 	System.out.println(ontologyUtilityScore.path("prefOntologyScore").asDouble());
		        	 	System.out.println(ontologyUtilityScore.path("ontologyTypeScore").asDouble());
		        	 	System.out.println(ontologyUtilityScore.path("ontologyTotalUtilityScore").asDouble());
		        	 	ontUS.setOntologyDomainScore(ontologyUtilityScore.path("ontologyDomainScore").asDouble());
		        	 	ontUS.setOntologyPopularityScore(ontologyUtilityScore.path("ontologyPopularityScore").asDouble());
		        	 	ontUS.setOntologyCoverageScore(ontologyUtilityScore.path("ontologyCoverageScore").asDouble());
		        	 	ontUS.setPrefOntologyScore(ontologyUtilityScore.path("prefOntologyScore").asDouble());
		        	 	ontUS.setOntologyTypeScore(ontologyUtilityScore.path("ontologyTypeScore").asDouble());
		        	 	ontUS.setOntologyTotalUtilityScore(ontologyUtilityScore.path("ontologyTotalUtilityScore").asDouble());
		        	 	candOnt.setOntologyUtilityScore(ontUS);	 
		        
		        	 	ArrayList<ConceptUtilityScoreClass> conceptUSs=new ArrayList<ConceptUtilityScoreClass>();
		        	 	for (JsonNode conceptUtilityScores : candidateOntologies.path("conceptUtilityScores")) {
		        	 		ConceptUtilityScoreClass conceptScore = new ConceptUtilityScoreClass();
		        	 		System.out.println(conceptUtilityScores.path("matchedConceptName").asText());
		        	 		System.out.println(conceptUtilityScores.path("conceptContextMatchingScore").asDouble());
		        	 		System.out.println(conceptUtilityScores.path("conceptSemanticRichnessScore").asDouble());
		        	 		System.out.println(conceptUtilityScores.path("conceptUtilityScore").asDouble());
		        	 		System.out.println(conceptUtilityScores.path("matchedConceptLabel").asText());
		        	 		conceptScore.setMatchedConceptName(conceptUtilityScores.path("matchedConceptName").asText());
		        	 		conceptScore.setConceptContextMatchingScore(conceptUtilityScores.path("conceptContextMatchingScore").asDouble());
		        	 		conceptScore.setConceptSemanticRichnessScore(conceptUtilityScores.path("conceptSemanticRichnessScore").asDouble());
		        	 		conceptScore.setConceptUtilityScore(conceptUtilityScores.path("conceptUtilityScore").asDouble());
		        	 		conceptScore.setMatchedConceptLabel(conceptUtilityScores.path("matchedConceptLabel").asText());
		        	 	conceptUSs.add(conceptScore);
		        	 	}
		        	 	candOnt.setConceptUtilityScores(conceptUSs);
		        
		        	 	System.out.println(candidateOntologies.path("ontologyAggregatedScore").asDouble());
		        	 	System.out.println(candidateOntologies.path("totalUtilityScore").asDouble());
		        	 	System.out.println(candidateOntologies.path("ontologyScore").asInt());
		        	 	candOnt.setOntologyAggregatedScore(candidateOntologies.path("ontologyAggregatedScore").asDouble());
		        	 	candOnt.setTotalUtilityScore(candidateOntologies.path("totalUtilityScore").asDouble());
		        	 	candOnt.setOntologyScore(candidateOntologies.path("ontologyScore").asInt());
		        	 	candidateOntologs.add(candOnt);
		        	}
		        	iter.setCandidateOntologies(candidateOntologs);
		        	iterations.add(iter);
		        }
		        System.out.println("Iterations Size=  "+iterations.size());
		        //close reader
		        reader.close();  
		    
		    } catch (Exception ex) {
		        ex.printStackTrace();
		    }
		      		
	  		return iterations;
	  	}
	  	
	  	//-------------------------------------------------------------------------------
	  	private static FinalResultList	getfinalResultListFromJSONFile() {
	  		FinalResultList tempFinalResultList=new FinalResultList();
	  		try {
			     // create a reader
			        Reader reader = Files.newBufferedReader(Paths.get("finalResultList.json"));
	         	        
			        //create ObjectMapper instance
			        ObjectMapper objectMapper = new ObjectMapper();
			        //read Iterations.json file into tree model
			        JsonNode parser = objectMapper.readTree(reader);
			       
			        // read finalCandidateOntologyList details
			        ArrayList<CandidateOntologyClass> candidateOntologs =new ArrayList<CandidateOntologyClass>();
			        for (JsonNode finalCandidateOntology : parser.path("finalCandidateOntologyList")) {
			        	CandidateOntologyClass candOnt= new CandidateOntologyClass();
			        	
			        	System.out.println(finalCandidateOntology.path("ontologyID").asText());
				        candOnt.setOntologyID(finalCandidateOntology.path("ontologyID").asText());
				        
				        OntologyUtilityScoreClass ontUS=new OntologyUtilityScoreClass();
				        JsonNode ontologyUtilityScore = finalCandidateOntology.path("ontologyUtilityScore");
				        System.out.println(ontologyUtilityScore.path("ontologyDomainScore").asDouble());
				        System.out.println(ontologyUtilityScore.path("ontologyPopularityScore").asDouble());
				        System.out.println(ontologyUtilityScore.path("ontologyCoverageScore").asDouble());
				        System.out.println(ontologyUtilityScore.path("prefOntologyScore").asDouble());
				        System.out.println(ontologyUtilityScore.path("ontologyTypeScore").asDouble());
				        System.out.println(ontologyUtilityScore.path("ontologyTotalUtilityScore").asDouble());
				        ontUS.setOntologyDomainScore(ontologyUtilityScore.path("ontologyDomainScore").asDouble());
				        ontUS.setOntologyPopularityScore(ontologyUtilityScore.path("ontologyPopularityScore").asDouble());
				        ontUS.setOntologyCoverageScore(ontologyUtilityScore.path("ontologyCoverageScore").asDouble());
				        ontUS.setPrefOntologyScore(ontologyUtilityScore.path("prefOntologyScore").asDouble());
				        ontUS.setOntologyTypeScore(ontologyUtilityScore.path("ontologyTypeScore").asDouble());
				        ontUS.setOntologyTotalUtilityScore(ontologyUtilityScore.path("ontologyTotalUtilityScore").asDouble());
				        candOnt.setOntologyUtilityScore(ontUS);	 
				        
				        ArrayList<ConceptUtilityScoreClass> conceptUSs=new ArrayList<ConceptUtilityScoreClass>();
				        for (JsonNode conceptUtilityScores : finalCandidateOntology.path("conceptUtilityScores")) {
				        	ConceptUtilityScoreClass conceptScore = new ConceptUtilityScoreClass();
				        	System.out.println(conceptUtilityScores.path("matchedConceptName").asText());
				        	 System.out.println(conceptUtilityScores.path("conceptContextMatchingScore").asDouble());
				        	 System.out.println(conceptUtilityScores.path("conceptSemanticRichnessScore").asDouble());
				        	 System.out.println(conceptUtilityScores.path("conceptUtilityScore").asDouble());
				        	 System.out.println(conceptUtilityScores.path("matchedConceptLabel").asText());
				        	 conceptScore.setMatchedConceptName(conceptUtilityScores.path("matchedConceptName").asText());
				        	 conceptScore.setConceptContextMatchingScore(conceptUtilityScores.path("conceptContextMatchingScore").asDouble());
				        	 conceptScore.setConceptSemanticRichnessScore(conceptUtilityScores.path("conceptSemanticRichnessScore").asDouble());
				        	 conceptScore.setConceptUtilityScore(conceptUtilityScores.path("conceptUtilityScore").asDouble());
				        	 conceptScore.setMatchedConceptLabel(conceptUtilityScores.path("matchedConceptLabel").asText());
				        	 conceptUSs.add(conceptScore);
				        }
				        candOnt.setConceptUtilityScores(conceptUSs);
				        
				        System.out.println(finalCandidateOntology.path("ontologyAggregatedScore").asDouble());
				        System.out.println(finalCandidateOntology.path("totalUtilityScore").asDouble());
				        System.out.println(finalCandidateOntology.path("ontologyScore").asInt());
				        candOnt.setOntologyAggregatedScore(finalCandidateOntology.path("ontologyAggregatedScore").asDouble());
				        candOnt.setTotalUtilityScore(finalCandidateOntology.path("totalUtilityScore").asDouble());
				        candOnt.setOntologyScore(finalCandidateOntology.path("ontologyScore").asInt());
				        candidateOntologs.add(candOnt);
				     }
			        tempFinalResultList.setFinalCandidateOntologyList(candidateOntologs);
			        
			        ArrayList<Double> rewardScores =new ArrayList<Double>();
			        for (JsonNode rewardScore : parser.path("rewardScore")) {
			        	System.out.println(rewardScore.asDouble());
			        	rewardScores.add(rewardScore.asDouble());
			        }
			        tempFinalResultList.setRewardScore(rewardScores);
					
			        ArrayList<String> selectedOntologies =new ArrayList<String>();
			        for (JsonNode selectedOntology : parser.path("selectedOntology")) {
			            System.out.println(selectedOntology.asText());
			           selectedOntologies.add(selectedOntology.asText());
			        }    
			        tempFinalResultList.setSelectedOntology(selectedOntologies);
			        
			        //close reader
			        reader.close();  
			    
			    } catch (Exception ex) {
			        ex.printStackTrace();
			    }
			      		
			return tempFinalResultList;	
	  	}
}
//----------------------------------------------------------------------------
