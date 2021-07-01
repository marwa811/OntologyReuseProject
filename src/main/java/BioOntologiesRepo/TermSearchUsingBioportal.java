package BioOntologiesRepo;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
	}
