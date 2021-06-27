package UserPreferences;

import java.io.File;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import BioOntologiesRepo.Ontology;
import BioOntologiesRepo.TermSearchUsingBioportal;

public class OntologyUtilityClass {
	static Logger log = Logger.getLogger(OntologyUtilityClass.class);
	private static String[] generalDomainOntologies= {"Vocabularies","Upper_Level_Ontology","Taxonomic_Classification", 
			"Other","Experimental_Conditions","All_Organisms","Yeast","Physicochemical","Phenotype"
			,"Molecule","Immunology","Imaging","Health","Ethology","Dysfunction","Chemical",
			"Biomedical_Resources","Biological_Process","Arabadopsis","Cell"};
	
	private static String[] parentDomainOntologies= {"Subcellular","Plant","Neurological_Disorder","Anatomy",
			"Human","Genomic_and_Proteomic","Development"};
	
	private static String[] subDomainOntologies= {"Subcellular_anatomy","Plant_Development","Plant_Anatomy"
			,"Neurologic_Disease","Mouse_Anatomy","Microbial_Anatomy","Gross_Anatomy","Fish_Anatomy"
			,"Cellular_anatomy_","Human_Developmental_Anatomy","Gene_Product","Animal_Development"};
	
	private static String[] subSubDomainOntologies= {"Protein","Animal_Gross_Anatomy"};
	
	
	public static float calculateOntologyUtilityFunction(String classesNames, int userPrefOntologyType, ArrayList<String> UserPrefOntologies,
			ArrayList<String> candidateOntologies, UserPreferencesModel userPref) {
		//read the ontology.json file and get a list of Ontology contain all information about ontologies
		Ontology[] ontologiesInfo  = getOntologyInfoFromJsonFile();
		Map<String,String> recommenderResult= getRecommenderCoverageScore(classesNames,ontologiesInfo);
		
		//for each candidate ontology
		for(int i=0; i<candidateOntologies.size(); i++) {
			calculateDomainScore(i+1,candidateOntologies.get(i),ontologiesInfo,userPref.getUserPrefDomain());
			calculatePopularityScore(candidateOntologies.get(i),ontologiesInfo);
			calculateCoverageScore(recommenderResult,candidateOntologies.get(i),ontologiesInfo);
			calculatePrefOntologyScore(UserPrefOntologies,candidateOntologies.get(i),ontologiesInfo);
			calculateOntologyTypeScore(userPrefOntologyType,candidateOntologies.get(i),ontologiesInfo);
		}
		return 0;	
	}
	//////////////////////////////////////////////
	//The function takes two concept names as input and returns the difference between their levels
	//Example: 0 means they dont have any relations
	//         1 means they are sub or super direct classes
	//		   2 means they have 1 level in between
	private static int getCategoryLevel(String userPrefDomain, String ontologyCategory) {
		int difflevel=0;
		//if the userPrefDomain and OntologyCategory are general Domains, then level difference =0.
		//if the userPrefDomain is general Domain and the OntologyCategory is parent domain, or vice verse, then level difference =0.
		//if the userPrefDomain is general Domain and the OntologyCategory is sub domain, or vice verse, then level difference =0.
		//if the userPrefDomain is parent Domain and the OntologyCategory is sub domain, or vice verse, then level difference =1.
		if((contains(userPrefDomain,parentDomainOntologies) && contains(ontologyCategory,subDomainOntologies))||
			(contains(userPrefDomain,subDomainOntologies) && contains(ontologyCategory,parentDomainOntologies)))
			difflevel=1;
		if((contains(userPrefDomain,subSubDomainOntologies) && contains(ontologyCategory,subDomainOntologies))||
			(contains(userPrefDomain,subDomainOntologies) && contains(ontologyCategory,subSubDomainOntologies)))
			difflevel=1;
		if((contains(userPrefDomain,subSubDomainOntologies) && contains(ontologyCategory,parentDomainOntologies))||
			(contains(userPrefDomain,parentDomainOntologies) && contains(ontologyCategory,subSubDomainOntologies)))
			difflevel=2;
		return difflevel;
	}
	///////////////////////////////////////////////////////////////
	//Function to test if an array of strings contains a string or not
	private static boolean contains(String str, String[] strArray) {
		for(String a: strArray)
			if(str.equals(a))
				return true;
		return false;
	}
	////////////////////////////////////////////////////////////////
	//read the ontology.json file and get a list of Ontology contain all information about ontologies
	private static Ontology[] getOntologyInfoFromJsonFile(){
		Ontology[] ontologiesInfo  = null;
		try {
		    // create object mapper instance
		    ObjectMapper mapper = new ObjectMapper();

		    // convert JSON file to map
		    ontologiesInfo = mapper.readValue(new File("C:\\Users\\marwa\\eclipse-workspace-photon\\OntologyReuseProject\\ontology.json"), Ontology[].class);
		    log.info("Getting information from JSON File");
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		return ontologiesInfo;
	}

	///////////////////////////////////////////////
	public static float calculateDomainScore(int count,String candidateOntologyID, Ontology[] ontologiesInfo,
			ArrayList<String> userPrefDomains){
		float score=0;
		String[] ontologyCategories=null;
		System.out.println("Ontology "+count+": "+ candidateOntologyID);
		if(userPrefDomains.size()!=0) {
			try {				
				//if user has no preferred domain
				if(userPrefDomains.size()==0) {
					System.out.println("user has no preffred domain, score =0");
					score+=0;
				}			
				else{	
				// loop for all user preferred domains 	
				for (int d=0; d<userPrefDomains.size(); d++) {
					//get the bioportal categories for each ontology
					ontologyCategories=getOntologyCategories(candidateOntologyID,ontologiesInfo);
					//if the candidate ontology has no bioportal categories, score =0
					if(ontologyCategories.length==0) {
						score+=0;
					}	
					//if the candidate ontology from the search term list has only one bioportal category
					//which exactly match the user preferred domain, score=1, else if no match score=0
					//if partial match score=
					else 
						//Find the difference in level between the user preferred domain and the ontology category
						if(ontologyCategories.length==1) 
							if(ontologyCategories[0].equals(userPrefDomains.get(d)))
								score+=1;
						else
						{
							int diffLevel=getCategoryLevel(userPrefDomains.get(d), ontologyCategories[0]);
							if(diffLevel==1)
								score+=0.5;
							else if(diffLevel==2)
								score+=0.25;
							else
								score+=0;
						}
					//if the candidate ontology-from the search term list- has more than one bioportal 
					//category and one of them exactly match the user preferred domain, score=1/n, where 
					//n is number of bioportal categories
					else if(ontologyCategories.length>1) {
						for(int k=0; k<ontologyCategories.length; k++) {
							if(ontologyCategories[k].equals(userPrefDomains.get(d)))
								score+= 1.0/ontologyCategories.length;
							else if(getCategoryLevel(userPrefDomains.get(d), ontologyCategories[k])==1)
								score+= 0.5/ontologyCategories.length;
							else if(getCategoryLevel(userPrefDomains.get(d), ontologyCategories[k])==2)
								score+= 0.25/ontologyCategories.length;
						}
					}
				}	
			}				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("User didn't enter a preferred domain!!");
		}
		System.out.println("The domain score for ontology: "+candidateOntologyID+ " is :"+ score);
		return score;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////
	//The function takes the ontology id and returns the bioportal categories
	private static String[] getOntologyCategories(String ontologyId,Ontology[] ontologiesInfo ){
		String[] ontologyCategories = null;
		//loop for all ontology objects from the JSON file get the ontology with the input ID and get its categories
		//return them
		for(int i=0; i<ontologiesInfo.length; i++){
			if(ontologiesInfo[i].getId().equals(ontologyId)) {
		    	ontologyCategories=ontologiesInfo[i].getCategories();
			}	
		}	
		return ontologyCategories;
	}
	////////////////////////////////////////////////////////////
	private static int getMaxNoProject(Ontology[] ontologiesInfo) {
		int max= ontologiesInfo[0].getProjects();
		for(int i=1; i<ontologiesInfo.length; i++) {
			if (ontologiesInfo[i].getProjects() > max) {
	            max = ontologiesInfo[i].getProjects();   // new maximum)
			}
		}
		return max;
	}
	////////////////////////////////////////////////////////////
	private static int getMaxNoView(Ontology[] ontologiesInfo) {
		int max= ontologiesInfo[0].getVeiws();
		for(int i=1; i<ontologiesInfo.length; i++) {
			if (ontologiesInfo[i].getVeiws() > max) {
	            max = ontologiesInfo[i].getVeiws();   // new maximum)
			}
		}
		return max;
	}
	//////////////////////////////////////////////
	private static float calculatePopularityScore(String candidateOntologyID, Ontology[] ontologiesInfo){
		float score=0;
		float MaxProjectNo=getMaxNoProject(ontologiesInfo);
		float MaxViewNo=getMaxNoView(ontologiesInfo);
		
		for(int i=0; i <ontologiesInfo.length; i++) {
			if(ontologiesInfo[i].getId().equals(candidateOntologyID))
				score=(ontologiesInfo[i].getVeiws()/MaxViewNo)+(ontologiesInfo[i].getProjects()/MaxProjectNo);
		}
		System.out.println("The popularity score for ontology: "+candidateOntologyID+ " is :"+ score);
		return score;
	}
	//////////////////////////////////////////////
	private static float calculateOntologyTypeScore(int userPrefOntologyType,String candidateOntologyID, Ontology[] ontologiesInfo){
		float score=0;
		
		/*for(int i=0; i<ontologiesInfo.length; i++) {
			if(ontologiesInfo[i].getId().equals(candidateOntologyID)) {
				if(ontologiesInfo[i].getClassNo()==0)
					score=0;
				else
					score=(float)(ontologiesInfo[i].getObjPropertyNo()/ontologiesInfo[i].getClassNo());
			}
		}*/
		for(int i=0; i<ontologiesInfo.length; i++) {
			if(ontologiesInfo[i].getId().equals(candidateOntologyID)) {
				if(userPrefOntologyType==3 || ontologiesInfo[i].getClassNo()==0)
					score=0;
				else if(ontologiesInfo[i].getObjPropertyNo()==0 && userPrefOntologyType==1) 
					score=1;
				else if(ontologiesInfo[i].getObjPropertyNo()!=0 && userPrefOntologyType==2) 
					score=1;
				else 
					score=0;
			}
		}
		System.out.println("The Ontology Type score for ontology: "+candidateOntologyID+ " is: " +score);
		return score;
	}
	//////////////////////////////////////////////////////////////////
	//If the ontology is prefered by the user, it gets a higher score than the others candidate ontologies
	private static float calculatePrefOntologyScore(ArrayList<String> UserPrefOntologies,String candidateOntologyID, Ontology[] ontologiesInfo) {
		float score=0;
		for(int i=0; i<ontologiesInfo.length; i++) {
			if(ontologiesInfo[i].getId().equals(candidateOntologyID)) {
				for(int j=0; j<UserPrefOntologies.size(); j++) {
					if(ontologiesInfo[i].getAcronym().equals(UserPrefOntologies.get(j)))
						score=1;
				}
			}
		}
		System.out.println("The Preferred Ontology score for ontology: "+candidateOntologyID+ " is: " +score);
		return score;
	}
	///////////////////////////////////////////////////////////////////
	//Using Bioportal recommender service, as input a string of input terms and a string of ontologies Acronyms
	//returns a Map of each ontologyId and its coverage score
	private static Map<String,String> getRecommenderCoverageScore(String classesNames,Ontology[] ontologiesInfo) {	
		Map<String,String> recommenderResult=
				TermSearchUsingBioportal.useBioportalRecommender(classesNames, getOntologiesNamesAsString(ontologiesInfo));
		return recommenderResult;
	}
	///////////////////////////////////////////////////////////////////////
	//Convert the ontologies Acronym to a string seprated by commans to used it in the recommender
	private static String getOntologiesNamesAsString(Ontology[] ontologiesInfo) {
		String ontologies="";
		for(int i=0; i<ontologiesInfo.length; i++) {
			if(i+1<ontologiesInfo.length)
				ontologies+=ontologiesInfo[i].getAcronym()+",";
			else
				ontologies+=ontologiesInfo[i].getAcronym();
		}
		return ontologies;
	}
	////////////////////////////////////////////////////////////////////////////////////
	//For each ontology get its coverage score
	private static float calculateCoverageScore(Map<String,String> recommenderResult,String candidateOntologyID, Ontology[] ontologiesInfo){
		float score=0;
		for(String id : recommenderResult.keySet()) {
			if(id.equals(candidateOntologyID))
				score=Float.parseFloat(recommenderResult.get(id));
		}
		System.out.println("The coverage score for ontology: "+candidateOntologyID+ " is :"+ score);
		return score;
	}
}