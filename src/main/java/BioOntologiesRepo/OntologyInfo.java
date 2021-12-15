package BioOntologiesRepo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

//This class to access ontologies in Bioportal and get information about them and populate 
//those information to a JSON file and use them as a database for Bioportal ontologies.
//The JSON File called ontology.json in the project path
//It contains information about more than 800 ontology

public class OntologyInfo {

	static final String REST_URL = "http://data.bioontology.org";
    static final String API_KEY = "aa404b9e-c096-4f22-84b8-ef9f105e0931";
    static final ObjectMapper mapper = new ObjectMapper();

	public static void main(String[] args) throws FileNotFoundException {
		  // Get the available resources
        String resourcesString = get(REST_URL + "/");
        JsonNode resources = jsonToNode(resourcesString);

        // Follow the ontologies link by looking for the media type in the list of links
        String link = resources.get("links").findValue("ontologies").asText();

        // Get the ontologies from the link we found
        JsonNode ontologies = jsonToNode(get(link));

        // Get the information about the returned list of ontologies
        try {       
        	//create list of ontology objects;
        	List<Ontology> ontologyList = new ArrayList<Ontology>();

        	int i=1;
        	for (JsonNode ontology : ontologies) {	
        		System.out.println("\n" +"\n" + i++);
        	//	getOntologyCategory(ontology);
        	//	getAnalytics(ontology);
        	//	getMetrics(ontology);  
        		
        		//create new ontology object
        		Ontology ont = new Ontology(getOntologyName(ontology), getOntologyId(ontology), 
        				getOntologyAcronym(ontology), getOntologyCategory(ontology),
        				getViewsNo(ontology), getOntologyProjectsNo(ontology), getObjectPropertyNo(ontology), 
        				getClassesNo(ontology));
        		
        	/*	Ontology ont = new Ontology(getOntologyName(ontology), getOntologyId(ontology), 
        				getOntologyAcronym(ontology), getOntologyCategory(ontology),
        				getViewsNo(ontology), getOntologyProjectsNo(ontology), getObjectPropertyNo(ontology), 
        				getClassesNo(ontology), getClasses(ontology));*/
        				ontologyList.add(ont);
        	}
        	// create object mapper instance
            ObjectMapper mapper = new ObjectMapper();
            
         // create an instance of DefaultPrettyPrinter
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

            // write list of ontologies to JSON file
            writer.writeValue(Paths.get("ontology.json").toFile(), ontologyList);    	
        } catch (Exception ex) {
	    ex.printStackTrace();
        }
    }
	
	//return ontology name as a string 
	private static String getOntologyName(JsonNode ontology) throws IOException {	
		String ontName= ontology.get("name").asText();  
        // Print out all the labels
        System.out.println(ontName);
        return ontName;    
    }
	
	//return ontology id as a string 
	private static String getOntologyId(JsonNode ontology) throws IOException {	
		String ontId= ontology.get("@id").asText();  
        // Print out all the labels
        System.out.println(ontId);
        return ontId;    
    }
	
	//return ontology acronym as a string 
	private static String getOntologyAcronym(JsonNode ontology) throws IOException {	
		String ontAcronym= ontology.get("acronym").asText();  
        // Print out all the labels
        System.out.println(ontAcronym);
        return ontAcronym;    
    }
	///////////////////////////////////////////////////////////////////////////
	//return ontology category(ies) as a string[] 
	private static String[] getOntologyCategory(JsonNode ontology) throws IOException {	
		JsonNode categories = jsonToNode(get(ontology.get("links").get("categories").asText())); 	
		ArrayList<String> categoryList = new ArrayList<String>();
		for (JsonNode category : categories) {
		    if (!category.get("acronym").isNull()) {
		    	categoryList.add(category.get("acronym").asText()); 
		     }
		 }
		// an array of strings holding all the ontology categories
		String[] ontCategory= new String[categoryList.size()];
		int i=0;
		// Print out all the categories
		for (String cate : categoryList) {
		    ontCategory[i]=cate;
		    System.out.print(cate);
		    i++;
		    }  
	    return ontCategory;    
	}	
	//////////////////////////////////////////////////////////////////////////////////////////////	
	//return ontology projects number as an int 
	private static int getOntologyProjectsNo(JsonNode ontology) throws IOException {	
		JsonNode projects = jsonToNode(get(ontology.get("links").get("projects").asText())); 
        return projects.size();    
    }
	
	// This function returns total visits of any ontology from 2013 to 2021 as an int
		private static int getViewsNo(JsonNode ontology) {	
			int yearTotal=0;
	    	JsonNode analytics = jsonToNode(get(ontology.get("links").get("analytics").asText()));
	    	ArrayList<String> visitsList = new ArrayList<String>();
	        for (JsonNode analytic : analytics) {
	        	for(int i=2013; i<=2021; i++) {
	        		if (!analytic.get(""+i).isNull()) {
	        		for (int j=1; j<=12; j++) {
	        			yearTotal+= analytic.get(""+i).get(""+j).asInt();
	        			}
	        		}
	        	}
	      		visitsList.add(""+yearTotal); 
	      } 	  
	      // Print all the analytics "Visits"
	      for (String ontVisits : visitsList) {
	          System.out.print("\n"+ontVisits); 
	          }
	      return yearTotal;
	   }
	//--------------------------------------------------------------------
	private static	ArrayList<BioClass> getClasses(JsonNode ontology){
		ArrayList<BioClass> classes= new ArrayList<BioClass>();
		JsonNode analytics = jsonToNode(get(ontology.get("links").get("classes").asText()));
    	
		return classes;
	}
		
	//depricated function
	//get name, id, acronym of any ontology
/*	private static void getOntologyInfo(JsonNode ontology) throws IOException {	
		String ontInfo= ontology.get("name").asText() + "\n" + ontology.get("@id").asText() +
			"\n" + ontology.get("acronym").asText(); 
	 // Print out all the labels
	    System.out.println(ontInfo);
	 }*/

	//deprecated function	
	// This function calculates total visits of any ontology from 2013 to 2021
/*	private static void getAnalytics(JsonNode ontology) {	
		int yearTotal=0;
    	JsonNode analytics = jsonToNode(get(ontology.get("links").get("analytics").asText()));
    	ArrayList<String> visitsList = new ArrayList<String>();
        for (JsonNode analytic : analytics) {
        	for(int i=2013; i<=2021; i++) {
        		if (!analytic.get(""+i).isNull()) {
        		for (int j=1; j<=12; j++) {
        			yearTotal+= analytic.get(""+i).get(""+j).asInt();
        			}
        		}
        	}
      		visitsList.add(""+yearTotal); 
      } 	  
      // Print all the analytics "Visits"
      for (String ontVisits : visitsList) {
          System.out.print("\n"+ontVisits); 
          }
   }*/
	
	//deprecated function
	//get the categories for any ontology
 /*   private static void getCategory(JsonNode ontology) throws FileNotFoundException {
        JsonNode categories = jsonToNode(get(ontology.get("links").get("categories").asText()));   	
        ArrayList<String> categoryList = new ArrayList<String>();
          for (JsonNode category : categories) {
        	  if (!category.get("acronym").isNull()) {
        		  categoryList.add(category.get("acronym").asText() +" / "); 
        		  }
          }    		  
        // Print out all the categories
        for (String cate : categoryList) {
            System.out.print(cate);
        }
    }*/
    
	// get the object properties no. of an ontology returns it as int
    private static int getObjectPropertyNo(JsonNode ontology) throws FileNotFoundException{
    	int objPropertyNo=0;
    	try {
    	if(ontology.get("links").has("metrics"))
    	{
    		JsonNode metrics = jsonToNode(get(ontology.get("links").get("metrics").asText()));
    		objPropertyNo=  metrics.get("properties").asInt();
    		// Print out all the metrics
    		System.out.println(objPropertyNo);
    	}
    	else
    	    System.out.println("No available metrics for this ontology");
    	}
    	catch(NullPointerException e){
    		System.out.println("No available metrics for this ontology (null pointer)");
    	}
    	return objPropertyNo;
    }
    
    // get the classes no. of an ontology returns it as int
    private static int getClassesNo(JsonNode ontology) throws FileNotFoundException{
    	int classesNo=0;
    	try {
    	if(ontology.get("links").has("metrics"))
    	{
    		JsonNode metrics = jsonToNode(get(ontology.get("links").get("metrics").asText()));
    		classesNo=  metrics.get("classes").asInt();
    		// Print out all the metrics
    		System.out.println(classesNo);
    	}
    	else
    	    System.out.println("No available metrics for this ontology");
    	}
    	catch(NullPointerException e){
    		System.out.println("No available metrics for this ontology (null pointer)");
    	}
    	return classesNo;
    }
    
    //deprecated function
	// get all the `metrics` of an ontology (no. of classes, obj properties, etc.)
 /*   private static void getMetrics(JsonNode ontology) throws FileNotFoundException{
    	try {
    	if(ontology.get("links").has("metrics"))
    	{
    		JsonNode metrics = jsonToNode(get(ontology.get("links").get("metrics").asText()));
    		String ontmetrics= 
    			"Classes: "+metrics.get("classes").asText() + "\n" +
    			"Individuals: " +metrics.get("individuals").asText() +"\n" + 
    			"Properties: "+metrics.get("properties").asText() +"\n" +
    			"Max Depth: " + metrics.get("maxDepth").asText() + "\n"+   
    			"Max Child Count: "+metrics.get("maxChildCount").asText() +"\n" +
    			"Average Child Count: "+metrics.get("averageChildCount").asText() +"\n" +
    			"Classes With One Child: "+metrics.get("classesWithOneChild").asText() +"\n" +
    			"Classes With More Than 25 Children: "+metrics.get("classesWithMoreThan25Children").asText() +"\n" +
    			"Classes With No Definition: "+metrics.get("classesWithNoDefinition").asText() +"\n" ; 
    	
    		// Print out all the metrics
    		System.out.println(ontmetrics);
    	}
    	else
    	    System.out.println("No available metrics for this ontology");
    	}
    	catch(NullPointerException e){
    		System.out.println("No available metrics for this ontology (null pointer)");
    	}
    }*/
    
    //deprecated function
   /* private static void getClassesForOntology(String ontAcronym) {
    	 ArrayList<String> labels = new ArrayList<String>();

         // Get all ontologies from the REST service and parse the JSON
         String ontologies_string = get(REST_URL + "/ontologies");
         JsonNode ontologies = jsonToNode(ontologies_string);

         // Iterate looking for ontology with acronym BRO
         JsonNode temp = null;
         for (JsonNode ontology : ontologies) {
             if (ontology.get("acronym").asText().equalsIgnoreCase(ontAcronym)) {
                 temp = ontology;
             }
         }

         // Using the hypermedia link called `classes`, get the first page
         JsonNode page = jsonToNode(get(temp.get("links").get("classes").asText()));

         // From the returned page, get the hypermedia link to the next page
         String nextPage = page.get("links").get("nextPage").asText();

         // Iterate over the available pages adding labels from all classes
         // When we hit the last page, the while loop will exit
         while (nextPage.length() != 0) {
             for (JsonNode cls : page.get("collection")) {
                 if (!cls.get("prefLabel").isNull()) {
                     labels.add(cls.get("prefLabel").asText());
                 }
             }

             if (!page.get("links").get("nextPage").isNull()) {
                 nextPage = page.get("links").get("nextPage").asText();
                 page = jsonToNode(get(nextPage));
             } else {
                 nextPage = "";
             }
         }

         // Print out all the labels
         for (String label : labels) {
             System.out.println(label);
         }
    }*/

    private static JsonNode jsonToNode(String json) {
        JsonNode root = null;
        try {
            root = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return root;
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


