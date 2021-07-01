package OntologyMatchingPackage;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLException;

import aml.AML;

public class AMLMappings {
	
	public AMLMappings() {
	}

	private ArrayList<AMLMapping> mappings=new ArrayList<AMLMapping>();
	
	public void setMappings(String inputClassName, String sourceFileName, String targetFileName) throws OWLException {
		final Logger log = Logger.getLogger(AMLMappings.class);
		
		AML aml = AML.getInstance();
		log.info("calling AML MyOntologyMatchingAlgorithm...");

		aml.openOntologies(sourceFileName, targetFileName);
		log.info("AML Opened the ontologies...");

		aml.matchAuto();
		log.info("AML made the matching step between the two ontologies...");
		
		//If there exist AML mappings put them in AMLMappings object (arraylist)
		if(aml.getAlignment().size() > 0) { 
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
			for(int i=0; i<aml.getAlignment().size(); i++) {
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
		}
	
	public ArrayList<AMLMapping> getMappings() {
		return mappings;
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
}
