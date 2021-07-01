package UserPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.coode.owlapi.rdfxml.parser.TranslatedUnloadedImportException;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.UnloadableImportException;

import OntologyMatchingPackage.AMLMapping;
import OntologyMatchingPackage.AMLMappings;
import OntologyMatchingPackage.OntologyMatchingAlgorithm;

public class ConceptUtilityClass {
	static Logger log = Logger.getLogger(ConceptUtilityClass.class);
	private static int mb = 1024 * 1024; 
	 
	// get Runtime instance
	private static Runtime instance;
	//The function calculate the ConceptUtilityFunction() by calling 
	//the calculateConceptContextScore() and the calculateSemanticRichnessScore()
	public static void calculateConceptUtilityFunction(String inputClassName, String InputFileName, ArrayList<String> candidateOntologies) throws OWLException, IOException, TranslatedUnloadedImportException{
		for(int i=0; i<candidateOntologies.size(); i++) {
			String candidateOntologyFileName=getOntologyFileName(candidateOntologies.get(i)); 	
			calculateConceptContextScore(inputClassName, InputFileName, candidateOntologyFileName);
			calculateSemanticRichnessScore(inputClassName, candidateOntologyFileName);
		}
	}

	private static double calculateConceptContextScore(String inputClassName, String InputFileName, String candidateOntologyFileName) throws OWLException, IOException, TranslatedUnloadedImportException
	{
		double conceptContextScore=0.0;
		conceptContextScore=getMapping(inputClassName, InputFileName, candidateOntologyFileName);
    	return conceptContextScore;
	}
	/////////////////////////////////////////////////////////////////////////
	private static double calculateSemanticRichnessScore(String inputClassName, String candidateOntologyFileName){
		double conceptSemanticRichnessScore=0.0;
		//conceptSemanticRichnessScore=getSemanticRichnessScore(inputClassName,candidateOntologyFileName);
		return conceptSemanticRichnessScore;
	}
	////////////////////////////////////////////////////////////////////////////
	private static String getOntologyFileName(String candidateOntology) {
		String name=candidateOntology.substring(candidateOntology.lastIndexOf('/')+1,candidateOntology.length());
		return "OWLOntologies/"+name+".owl";
	}
	//////////////////////////////////////////////////////////////////////////////
	//This Function takes a class name and the names of two owl files, it calculates the AML mappings 
	//then call the getSimilarclasses() function to get the contextual mappings and calculate the 
	//conceptContextMatchingScore and returns it
	public static double getMapping(String inputClassName, String filename1, String filename2) throws OWLException, IOException, TranslatedUnloadedImportException {
		double conceptContextMatchingScore=0.0;
		try {
		AMLMappings mappings=new AMLMappings();
		mappings.setMappings(inputClassName,filename1, filename2);

		// used memory
		instance = Runtime.getRuntime();
		System.out.println("Used Memory: "+ (instance.totalMemory() - instance.freeMemory()) / mb);

		// for each AML Mapping object get the OWLClass of the source an target
		// ontologies (to be used in the algorithm)
		// convert the textual IRI to OWLClass

		log.info("The algorithm begins...");

		OntologyMatchingAlgorithm testCase= new OntologyMatchingAlgorithm(filename1,filename2);
		System.out.println("The output is:");
		//For each Mapping check to include it or not in our final mappings
		if(mappings.getSizeOfMappings() > 0) {		
			Iterator<AMLMapping> mappingIterator = mappings.getMappings().iterator();
			while (mappingIterator.hasNext()) {
				AMLMapping mapping = mappingIterator.next();
				System.out.println("The AML mappings:   "+mapping.getMappingId() + " " + mapping.getSourceURI() + "     " + mapping.getTargetURI());
				testCase.getSimilarclasses(mapping, mappings.getMappings());
				// used memory
				System.out.println("Used Memory: "
						+ (instance.totalMemory() - instance.freeMemory()) / mb);	
				System.out.println();
			}
			testCase.displayFinalMappings();
			conceptContextMatchingScore=testCase.getconceptContextMatchingScore();			
		} 
		else
			System.out.println("There are no mappings!!");	
		System.out.println("-----------------------------------------");
		}
		catch(UnloadableImportException e) {
			System.out.println(e.getMessage());		
			}
		return conceptContextMatchingScore;
	}
}
