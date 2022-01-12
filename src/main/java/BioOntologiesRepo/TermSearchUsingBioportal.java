package BioOntologiesRepo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import AgentClasses.AdditionalClassInfo;

import org.apache.log4j.Logger;

public class TermSearchUsingBioportal {
	final static Logger log = Logger.getLogger(TermSearchUsingBioportal.class);		
	    
	static final String REST_URL = "http://data.bioontology.org";
	static final String API_KEY = "aa404b9e-c096-4f22-84b8-ef9f105e0931";
	static final ObjectMapper mapper = new ObjectMapper();
	static final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

	/*This method calls BioPortal Rest API, As input it takes a string for the class name 
	 * (search query) and returns an object of type TermSearchResultInformation 
	 Which contains the search result*/
	 public static ArrayList<String> searchByTermBioportal(String classLabel) throws Exception {
	    	
	 //An array list of type TermSearchResultInformation for the results 
	 // ArrayList<TermSearchResultInformation> searchResults = new ArrayList<TermSearchResultInformation>();
	if(classLabel.contains(" "))	 
		classLabel=classLabel.replace(" ", "%20");
	if(classLabel.contains("_"))
		classLabel=classLabel.replace("_", "%20");
	 System.out.println("You are searching class: "+ classLabel);
	 log.info("Searching bioportal for ontologies...");
	 JsonNode results = jsonToNode(get(REST_URL + "/search?q=" + classLabel)).get("collection");
	 if(results.size()==0) {
		 System.out.println("No ontologies found to match this class.");
	     return null;
	     }
	 else { 
	  /*iterate over each JsonNode in the result which contains information about: 
	  * each class such as, id , label, definitions, and synonyms
	  * and the ontology it belongs to such as, its id, name, and acronym
	  * return a list of ontology ids
	  */
		 ArrayList<String> searchResults=new ArrayList<String>();
	     for (JsonNode result : results) {
	       //if the result item is not obsolete
	    	 if(result.get("obsolete").asText()=="false"){   
	        	//searchResults.add(result.get("links").get("ontology").asText());
	        	JsonNode ontology = jsonToNode(get(result.get("links").get("ontology").asText()));
	        	String ontologyId=ontology.get("@id").asText();
	        	searchResults.add(ontologyId);
	        	} 
	        }      
	     return searchResults;
	     }
	 }

	 //////////////////////////////////////////////////////////////////////////
	 //This function uses the Bioportal Recommender to get the coverage score and returns a 
	 // Map<ontology id, Coverage Score>
	 public static Map<String,String> useBioportalRecommender(String inputTermSet, String ontologiesSet) {
		 Map<String,String> recommenderResult=new HashMap<String, String>();
		 JsonNode results = jsonToNode(get(REST_URL + "/recommender?input=" + inputTermSet 
	    			+"&input_type=2"+"&ontologies="+ontologiesSet));
	    	for (JsonNode result : results) {   
	    		  recommenderResult.put(result.get("ontologies").findValue("@id").asText(), 
	    				  result.get("coverageResult").findValue("normalizedScore").asText());
	    	}
	    	return recommenderResult;
	 }
	 //////////////////////////////////////////////////////////////////////////////////////
	 //This function takes the Acronym of a an ontology and returns a Map of classId and LabelName
	 //This function uses the Bioportal Recommender to get the coverage score and returns a 
	 // Map<ontology id, Coverage Score>
	 public static Map<String,String> getClassIdAndLabel(String OntAcronym) {
		 Map<String,String> classResult=new HashMap<String, String>();
		 JsonNode results = jsonToNode(get(REST_URL + "/ontologies/" + OntAcronym.toUpperCase()+"/classes/")).get("collection");
	    	for (JsonNode result : results) {
	    		if(result.get("obsolete").asText().equals("false")) 
	    		{
	    		classResult.put(result.get("@id").asText(), result.get("prefLabel").asText());
	    	//	System.out.println("GET Class ID and LAbel: "+ result.get("@id").asText()+"   ,   "+ result.get("prefLabel").asText());
	    		}
	    	}
	    	return classResult;
	 }
	 /////////////////////////////////////////////////////////////////////////////////////
	 //This function returns the details of a class (preflabel, synonyms, defination, subclasses) in an ontology
	 public static AdditionalClassInfo getClassInfo(String classId, String ontologyId) {
		 AdditionalClassInfo classObj = null;
		 boolean classExist=false;
	/*	 String fileName=ontologyId.substring(ontologyId.lastIndexOf("/")+1) ;
		 String acronym="";
		if(fileName.contains("."))
			acronym=fileName.substring(0,fileName.indexOf("."));
		else 
			acronym=fileName;*/
		System.out.println("The ontology Name"+ontologyId);
		String fileName= ontologyId.substring(ontologyId.lastIndexOf("/")+1);
		String acronym="";
		if(fileName.contains("."))
			acronym=fileName.substring(0,fileName.indexOf("."));
		System.out.println("The ontology Name"+ontologyId +"   " +acronym);
		//if(getAllFilesinaFolder().contains(acronym.toUpperCase()+".owl"))
		//{

	    try {
	    	
	    	String label="";
	        ArrayList<String> synonym = new ArrayList<String>();
	        ArrayList<String> definations = new ArrayList<String>();
	        ArrayList<String> subClasses = new ArrayList<String>();
	        ArrayList<String> subClassesId=new ArrayList<String>();
	        classId=classId.replace(":", "%3A");
	        classId=classId.replaceAll("/", "%2F");
	        classId=classId.replace("#", "%23");
	        String theLink= REST_URL + "/ontologies/" +acronym.toUpperCase()+"/classes/"+classId;
	        JsonNode result = jsonToNode(get(theLink));
	        if(result.get("obsolete").asText()=="false"){   
	          	classExist=true;
	           	if(result.has("prefLabel")) {
	            	label=result.get("prefLabel").asText();
	            	System.out.println("Class Pref Label: "+ label);
	            }
	            if(result.has("definition"))
	            {
	            	Iterator<JsonNode> definitions = result.get("definition").elements();
	                while (definitions.hasNext()) { 
	                    JsonNode fieldName = definitions.next();
	                    definations.add(fieldName.asText());
	                    System.out.println("Class definition: "+ fieldName.asText());
	                }
	            }
	            if(result.has("synonym"))
	            {
	            	Iterator<JsonNode> synonyms = result.get("synonym").elements();
	                while (synonyms.hasNext()){ 
	                    JsonNode fieldName = synonyms.next();
	                    synonym.add(fieldName.asText());
	                    System.out.println("Class synonyms: "+ fieldName.asText());
	                }
	            }
	            	  
		        if(result.get("links").has("children")) {
		        	JsonNode subclassesResults=jsonToNode(get(result.get("links").get("children").asText()));
		        	 for (JsonNode subclassesResult : subclassesResults.get("collection")) { 
		                    if(subclassesResult.get("obsolete").asText()=="false"){  
		                    	if (!subclassesResult.get("prefLabel").isNull()) {
		                            subClasses.add(subclassesResult.get("prefLabel").asText());
		                            subClassesId.add(subclassesResult.get("@id").asText());
		                            System.out.println("subclasses: "+ subclassesResult.get("prefLabel").asText());
		                        }
		                    }
		                 }
		            }
		         }
	      classObj=new AdditionalClassInfo(classId,label,synonym,definations,subClasses,subClassesId,classExist);
	    	} catch(Exception e) {
	    		System.out.println("The class not exist" +e.getStackTrace().toString());
	    		classExist=false;
	    	}
		/*}
		else { 
	
			return classObj;
		}*/
	    return classObj;
	 }	 	 
	 /////////////////////////////////////////////////////////////////////////////////
	 // This function the ontology ID and returns it acronym
	 {
		 
	 }
	 //to avoid adding two ontologies twice in the returned list
/*	 private static ArrayList<TermSearchResultInformation> checkForDuplicateOntologies
	 (ArrayList<TermSearchResultInformation> searchResults){
			
	    	List<String> ontologyAcronyms= new ArrayList<String>();
			ArrayList<TermSearchResultInformation> searchResultsNoDuplicate=new ArrayList<TermSearchResultInformation>();
			  for(TermSearchResultInformation temp : searchResults) {
				  if(!ontologyAcronyms.contains(temp.getAcronym())) {
					  ontologyAcronyms.add(temp.getAcronym());
					  searchResultsNoDuplicate.add(temp);
				  }		  
				  else continue;
		      }	
		return searchResultsNoDuplicate;		
	    }*/
	    //------------------------------------------------------------
		
	    //to retrieve/get ontology domains if exists 
	  /*  public static ArrayList<String> getOntologyDomain(String ontologyID) {
	    	String acronym = ontologyID.substring(ontologyID.lastIndexOf('/')+1);
	    	JsonNode categories = jsonToNode(get(REST_URL + "/ontologies/" +acronym+"/categories"));
	    	ArrayList<String> categoryList = null;
	        if(categories.size()!=0) {
	        	categoryList = new ArrayList<String>();
	            for(JsonNode category : categories) {
	            	if (!category.get("name").isNull()) {
	            		categoryList.add(category.get("name").asText() +"\n"); 
	            	}
	            } 	  
	        }
	        else 
	        	System.out.println("No categories for this ontology"); 
	        return categoryList;
		}*/
		//-------------------------------------------------------------------
	    
	    private static JsonNode jsonToNode(String json) {
	        JsonNode root = null;
	        try {
	            root = mapper.readTree(json);
	        } catch (JsonProcessingException e) {
	            e.printStackTrace();
	        } 
	        return root;
	    }

	    public static void printOntologyNames(ArrayList<String> searchResults) {
	    	if(!(searchResults.size()==0))
	    	{	
	    		for(int i=0; i<searchResults.size(); i++) 
	    			System.out.println("Ontology "+ (i+1) + ": "+ searchResults.get(i));
	    	}
	    }
	    
	    private static String get(String urlToGet) {
	        URL url;
	        HttpURLConnection conn;
	        BufferedReader rd;
	        String line;
	        String result = "";
	        try {
	            url = new URL(urlToGet);
	            conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");
	            conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
	            conn.setRequestProperty("Accept", "application/json");
	            rd = new BufferedReader(
	                    new InputStreamReader(conn.getInputStream()));
	            while ((line = rd.readLine()) != null) {
	                result += line;
	            }
	            rd.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return result;
	    }
	    ////////////////////////////////////////////////////
		 /*This method is used temporary as AML import problem is fixed, it
		  * calls BioPortal Rest API, As input it takes a string for the class name 
		  * (search query) and returns an Map of OntologyId, classId*/
			 public static Map<String,String> tempSearchByTermBioportal(String classLabel) throws Exception {
			    	
			 //An array list of type TermSearchResultInformation for the results 
			 // ArrayList<TermSearchResultInformation> searchResults = new ArrayList<TermSearchResultInformation>();
			 System.out.println("You are searching class: "+ classLabel);
			 log.info("Searching bioportal for ontologies...");
			 JsonNode results = jsonToNode(get(REST_URL + "/search?q=" + classLabel)).get("collection");
			 if(results.size()==0) {
				 System.out.println("No ontologies found to match this class.");
			     return null;
			     }
			 else { 
			  /*iterate over each JsonNode in the result which contains information about: 
			  * each class such as, id , label, definitions, and synonyms
			  * and the ontology it belongs to such as, its id, name, and acronym
			  * return a list of ontology ids
			  */
				 Map<String,String> searchResults=new HashMap<String,String>();
			     for (JsonNode result : results) {
			       //if the result item is not obsolete
			    	 if(result.get("obsolete").asText()=="false"){   
			        	//searchResults.add(result.get("links").get("ontology").asText());
			        	JsonNode ontology = jsonToNode(get(result.get("links").get("ontology").asText()));
			        	String ontologyId=ontology.get("@id").asText();
			        	String classId=result.get("@id").asText();
			        	searchResults.put(ontologyId,classId);
			        	} 
			        } 
			     
			 //    for(String id : searchResults.keySet()) 
			 //   	 System.out.println(id + "       " + searchResults.get(id));
			     return searchResults;
			 	}
			 }
	 //----------------------------------------------------
			 public static ArrayList<String> getAllFilesinaFolder() {
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
		        ArrayList<String> filenames=new ArrayList<String>();
		        int k=0;
		        int f=0;
		        for(String fileName : oWLFilesList) {
		           //System.out.println(fileName);
		        	filenames.add(k++,fileName);
		        }
		       return filenames;
		  	}
	}
