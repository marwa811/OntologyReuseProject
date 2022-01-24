package OntologyMatchingPackage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLException;

import com.fasterxml.jackson.databind.ObjectMapper;

import BioOntologiesRepo.TermSearchUsingBioportal;
import OntologyExtractionPackage.EntityExtractionClass;
import aml.AML;

public class AMLMappings {
	
	public AMLMappings() {
	}

	private ArrayList<AMLMapping> mappings=new ArrayList<AMLMapping>();
	private ArrayList<AMLMapping> AMLAlignments=new ArrayList<AMLMapping>();
	
	public void setMappings(String inputClassName, String sourceFileName, String targetFileName) throws OWLException {
		final Logger log = Logger.getLogger(AMLMappings.class);
		
		AML aml = AML.getInstance();
		log.info("calling AML MyOntologyMatchingAlgorithm...");

		aml.openOntologies(sourceFileName, targetFileName);
		log.info("AML Opened the ontologies...");

		aml.matchAuto();
		log.info("AML made the matching step between the two ontologies...");
		
		//If there exist AML mappings put them in AMLMappings object (arraylist)
	/*	if(aml.getAlignment().size() > 0) { 
			int id=1;
			/*System.out.println("AML mappings are: "+ aml.getAlignment().size());
			for(int i=0; i<aml.getAlignment().size(); i++) {
				String[] mappingitems=aml.getAlignment().get(i).toString().split("\t");
				mappings.add(new AMLMapping(id,mappingitems[0].trim(),mappingitems[1].trim(),
						mappingitems[2].trim(),mappingitems[3].trim(),
						Double.parseDouble(mappingitems[4].trim())));
				id++;
				} */
			//to add mapping for only the class that is chosen by the user
	/*		for(int i=0; i<aml.getAlignment().size(); i++) {
				String[] mappingitems=aml.getAlignment().get(i).toString().split("\t");
				if(mappingitems[0].trim().endsWith(inputClassName)) {
					mappings.add(new AMLMapping(id,mappingitems[0].trim(),mappingitems[1].trim(),
							mappingitems[2].trim(),mappingitems[3].trim(),
							Double.parseDouble(mappingitems[4].trim())));
					id++;
				}
				else
					  System.out .println("There are no AML mappings for this input class!!"); 
			}
		} else
		  System.out .println("There are no AML mappings!!"); 
		}*/
	}
	
	public void setSavedMappings(String inputClassName, String sourceFileName, String targetFileName) throws IOException, Exception {
		final Logger log = Logger.getLogger(AMLMappings.class);
		
		String sourceAcronym= sourceFileName.substring(sourceFileName.indexOf("/")+1,sourceFileName.indexOf("."));
		String targetAcronym= targetFileName.substring(targetFileName.indexOf("/")+1,targetFileName.indexOf("."));
		String matchedFileName= "COB Matched Files/"+sourceAcronym+"2"+targetAcronym+".json";
		System.out.println("Saved File Matched Ontology:  "+ matchedFileName);
		
		//get the AML Alignmnets from the saved file
		AMLAlignments=getAlignments(matchedFileName);
		
		//get inputFile classids and labels
		Map<String,String> classIdandLabels=EntityExtractionClass.getOntolgyClassesLabels(EntityExtractionClass.laodOntology(sourceFileName));
		Iterator<Map.Entry<String, String>> itr = classIdandLabels.entrySet().iterator();
		//get the alignments that are related to the input class and add them to the "mappings"
		//for(AMLMapping m: AMLAlignments) {
			while(itr.hasNext())
	        {
	           Map.Entry<String, String> entry = itr.next();
	           if(entry.getValue().toLowerCase().equals(inputClassName.toLowerCase())) {
	        	   System.out.println("The Map Entry Key is : "+ entry.getKey() +"   "+entry.getValue());
	        	   for(AMLMapping m: AMLAlignments) {
	        		   if(m.getSourceURI().equals(entry.getKey()))
	        			   mappings.add(m);
	        	   }
	           }
	        }
	    //}
		if(mappings.size() > 0) { 
			displayMappings();
		}
		else
			System.out.println("There are no AML mappings for this input class!!"); 
		}
	
	
	
	public ArrayList<AMLMapping> getMappings() {
		return mappings;
	}
	
	public ArrayList<AMLMapping> getAlignments() {
		return AMLAlignments;
	}
	
	public AMLMapping getMappingAtIndex(int i)
	{
		return mappings.get(i);
	}
	
	public void displayMappings() {
		Iterator<AMLMapping> mappingIterator = mappings.iterator();
			while (mappingIterator.hasNext()) {
				AMLMapping mapping = mappingIterator.next();			
				System.out.println("Final mapping: "+mapping.getMappingId()+"  "+mapping.getSourceName()+"  "+
					mapping.getSourceURI()+"  "+mapping.getTargetName()+"  "+
					mapping.getTargetURI()+"  "+mapping.getSimilarityScore());
			}
		}	
	
	public int getSizeOfMappings() {
		return mappings.size();
	}

	public void add(AMLMapping m) {
		// TODO Auto-generated method stub
		if (!mappings.contains(m))
			mappings.add(m);
	}
	
	public boolean contains(AMLMapping m) {
		// TODO Auto-generated method stub
		if (mappings.contains(m))
			return true;
		else 
			return false;
	}
	
	public ArrayList<String> getMappingsAsRef() {
		ArrayList<String> newMappings=new ArrayList<>();
		Iterator<AMLMapping> mappingIterator = mappings.iterator();
		while (mappingIterator.hasNext()) {
			AMLMapping mapping = mappingIterator.next();			
			newMappings.add(mapping.getSourceURI().substring(mapping.getSourceURI().indexOf('#')+1)
					+","+mapping.getTargetURI().substring(mapping.getTargetURI().indexOf('#')+1));
		}
		return newMappings;
	}
	
	public ArrayList<AMLMapping> getAlignments(String matchedFileName){
		List<AMLMapping> alignments = null;
		ArrayList<AMLMapping> modifiedArrayList=new ArrayList<AMLMapping>();
		try {
		    // create object mapper instance
		    ObjectMapper mapper = new ObjectMapper();

		    // convert JSON array to list of books
		    alignments= Arrays.asList(mapper.readValue(Paths.get(matchedFileName).toFile(), AMLMapping[].class));
		    System.out.println("Alignments number is:  "+ alignments.size());
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
	    for(AMLMapping m: alignments) 
			modifiedArrayList.add(m);	    
		return modifiedArrayList;
	}
}
