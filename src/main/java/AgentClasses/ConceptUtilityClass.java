package AgentClasses;

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
	/*public static void calculateConceptUtilityFunction(String inputClassName, String InputFileName, ArrayList<String> candidateOntologies) throws OWLException, IOException, TranslatedUnloadedImportException{
		//for(int i=0; i<candidateOntologies.size(); i++) 
		for(String ontologyId: candidateOntologies){
			double conceptUtilityScore=0;
			String candidateOntologyFileName=getOntologyFileName(ontologyId); 	
			conceptUtilityScore=getMapping(inputClassName, InputFileName, candidateOntologyFileName);	
		}
	}*/	
	
	////////////////////////////////////////////////////////////////////////
	//to calculate the mean score of candidate ontologies total utility score
	public static double meanScore(ArrayList<CandidateOntologyClass> candidateOntologies) throws OWLException, IOException{
		double mean=0;
		for(CandidateOntologyClass candidateOntology: candidateOntologies){
			mean+=candidateOntology.getOntologyUtilityScore().getOntologyTotalUtilityScore();
		}
		return (double)mean/candidateOntologies.size();
	}

	///////////////////////////////////////////////////////////////////////
	//consider 10 ontologies that has candidate concepts for extension to save time
	public static ArrayList<CandidateOntologyClass> calculateConceptUtilityFunction10(String inputClassName, String InputFileName, ArrayList<CandidateOntologyClass> candidateOntologies) throws OWLException, IOException, TranslatedUnloadedImportException{
		for(CandidateOntologyClass candidateOntology: candidateOntologies){
			ArrayList<ConceptUtilityScoreClass> conceptsForExtension=new ArrayList<ConceptUtilityScoreClass>();
			double mean=meanScore(candidateOntologies);
			//if the ontology Utility Score> a given threshold, say the mean value 
			if(candidateOntology.getOntologyUtilityScore().getOntologyTotalUtilityScore() > 0.1) {
			String candidateOntologyFileName=getOntologyFileName(candidateOntology.getOntologyID()); 
			conceptsForExtension=getMapping(inputClassName, InputFileName, candidateOntologyFileName);		
			candidateOntology.setConceptUtilityScores(conceptsForExtension);
			//to calculate the total score for an ontology
			candidateOntology.setTotalUtilityScore(conceptsForExtension);			
			}
			else 
				candidateOntology.setConceptUtilityScores(conceptsForExtension);
				//to calculate the total score for an ontology
				candidateOntology.setTotalUtilityScore(conceptsForExtension);
		}
		return candidateOntologies;
	}	
//////////////////////////////////////////////////////////////////////////
/*	private static double calculateConceptUtilityScore(String inputClassName, String InputFileName, String candidateOntologyFileName) throws OWLException, IOException, TranslatedUnloadedImportException
	{
		double conceptContextScore=0.0;
		conceptContextScore=getMapping(inputClassName, InputFileName, candidateOntologyFileName);	
		return conceptContextScore;
	}*/
	////////////////////////////////////////////////////////////////////////////
	public static String getOntologyFileName(String candidateOntology) {
		String name=candidateOntology.substring(candidateOntology.lastIndexOf('/')+1,candidateOntology.length());
		return "OWLOntologies/"+name+".owl";
	}
	//////////////////////////////////////////////////////////////////////////////
	//This Function takes a class name and the names of two owl files, it calculates the AML mappings 
	//then call the getSimilarclasses() function to get the contextual mappings and calculate the 
	//conceptContextMatchingScore and returns it
	public static ArrayList<ConceptUtilityScoreClass> getMapping(String inputClassName, String filename1, String filename2) throws OWLException, IOException, TranslatedUnloadedImportException {
		ArrayList<ConceptUtilityScoreClass> conceptsForExtension=new ArrayList<ConceptUtilityScoreClass>();
		
		double conceptUtilityScore=0.0;
		double conceptContextMatchingScore=0.0;
		double conceptSemanticRichnessScore=0.0;
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
				ConceptUtilityScoreClass candidateConceptForExtension=testCase.getSimilarclasses(mapping, mappings.getMappings());
				conceptsForExtension.add(candidateConceptForExtension);
				// used memory
				System.out.println("Used Memory: "
						+ (instance.totalMemory() - instance.freeMemory()) / mb);	
				System.out.println();
			}
			testCase.displayFinalMappings();
			conceptContextMatchingScore=testCase.getconceptContextMatchingScore();	
			conceptSemanticRichnessScore=testCase.getConceptSemanticRichnessScore();		} 
		else {
			// if no mappings then maybe there us a semantic richness score for the concept
			System.out.println("There are no mappings!!");	
			conceptUtilityScore=0.5 * (testCase.getConceptSemanticRichnessScore());
			System.out.println("The concept Utility Score is: "+ conceptUtilityScore);
			}
		System.out.println("-----------------------------------------");
		}
		catch(UnloadableImportException e) {
			System.out.println(e.getMessage());		
			}
		return conceptsForExtension;
	}
}
