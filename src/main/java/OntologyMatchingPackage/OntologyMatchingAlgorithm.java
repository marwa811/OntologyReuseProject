package OntologyMatchingPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import AgentClasses.ConceptUtilityScoreClass;

import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.apache.log4j.Logger;

/**
 * OntologyMatchingAlgorithm.java Purpose: Make additional semantic matching for a
 * pair of classes
 * 
 * @author marwa
 * @version 0.1 7/12/2018
 */

public class OntologyMatchingAlgorithm {
	final static Logger log = Logger.getLogger(OntologyMatchingAlgorithm.class);

	// set for final source classes that have a match with target classes
	Set<OWLClass> semanticSimilarClasses = new HashSet<>();
	
	//a list of Final mappings output by the algorithm
	AMLMappings Finalmappings=new AMLMappings();
	
	//The conceptContextMatching Score 
	double conceptContextMatchingScore;
	
	//The conceptSemanticRichness Score 
	double conceptSemanticRichnessScore;
	
	//The concetUtilityScore 
	double conceptUtilityScore;
	
	static OWLOntologyInformation sourceOntology=new OWLOntologyInformation();
	static OWLOntologyInformation targetOntology=new OWLOntologyInformation();
	
	public OntologyMatchingAlgorithm(String sourceFile,String targetFile) throws OWLOntologyCreationException, OWLException {
		sourceOntology.laodOntology(sourceFile);
		targetOntology.laodOntology(targetFile);
		conceptContextMatchingScore=0.0;
		conceptSemanticRichnessScore=0.0;
		conceptUtilityScore=0.0;
	}
	
	public Set<OWLClass> getSemanticSimilarClasses() {
		return semanticSimilarClasses;
	}

	public void setSemanticSimilarClasses(Set<OWLClass> semanticRelatedClasses) {
		this.semanticSimilarClasses = semanticRelatedClasses;
	}
	
	public void setFinalmappings(AMLMappings Finalmappings1) {
		this.Finalmappings = Finalmappings1;
	}

	public AMLMappings getFinalMappings() {
		return Finalmappings;
	}
	
	public double getConceptSemanticRichnessScore() {
		System.out.println("the conceptSemanticRichnessScore is:"+ conceptSemanticRichnessScore);
		return conceptSemanticRichnessScore;
	}
	
	public double getconceptContextMatchingScore() {
		System.out.println("the conceptContextMatchingScore is:"+ conceptContextMatchingScore);
		return conceptContextMatchingScore;
	}
	
	public double getConceptUtilityScore() {
		//System.out.println("the conceptSemanticRichnessScore is:"+ conceptSemanticRichnessScore);
		return conceptUtilityScore;
	}
	
	public void displayFinalMappings() {
		Finalmappings.displayMappings();
	}
	
	public void printSemanticSimilarClasses(OWLOntologyInformation sourceOntology) throws IOException {
		System.out.println("Classes that can be extended are:");
		for (OWLClass c : semanticSimilarClasses) {
			System.out.println(sourceOntology.getClassLabel(c));
		}
	}



	/**
	 * A method to get classes that have common data properties.
	 * 
	 * @param The source ontology, source class , target ontology, target class.
	 * @return set of source classes that have common data properties between the
	 *         target classes.
	 */
/*	public Set<OWLClass> getCommonDataProperties(OWLOntologyInformation sourceOntology, OWLClass sourceClass,
			OWLOntologyInformation targetOntology, OWLClass targetClass) throws IOException {

		Set<OWLClass> commonClasses = new HashSet<OWLClass>();
		for (OWLDataProperty db1 : sourceOntology.getDataProperties(sourceClass)) {
			// if there is a common object property between the two ontologies
			if (existin(db1, targetOntology.getDataProperties(targetClass)))
				commonClasses.add(sourceClass);
		}

		for (OWLClass c : commonClasses)
			System.out.println("classes with Common data properties are:" + c.getIRI().getFragment());

		return commonClasses;
	}
*/
	// ---------------------------------------------------------------------------------//

	/**
	 * A method to test if a mapping exists between two classes or not 
	 * by test their existence in a list of mappings between two ontologies or not
	 * 
	 * @param The source class , target class, A set of mappings.
	 * @return boolean if true then the source and target classes 
	 * has a mapping in the given set of mappings.
	 */

public boolean testMappingExistence(OWLClass class1, OWLClass class2, List<AMLMapping> mappings) throws IOException {
		boolean exists=false;
		Iterator<AMLMapping> mappingIterator = mappings.iterator();
		while (mappingIterator.hasNext()) {
			AMLMapping m = mappingIterator.next();
			if(m.getSourceURI().trim().equals(class1.getIRI().toString()) &&
					m.getTargetURI().trim().equals(class2.getIRI().toString()))
				exists=true;
		}
		return exists;
	}

	/**
	 * A method to test if a class exists in a set of classes
	 * 
	 * @param OWLClass , and a set of classes.
	 * @return boolean if true then the class exists in the given set of classes.
	 */
	public boolean existin(OWLClass class1, Set<OWLClass> setOFClasses) {
		boolean exist = false;
		for (OWLClass c1 : setOFClasses) {
			if (class1.getIRI().getFragment().equals(c1.getIRI().getFragment()))
				exist = true;
		}
		return exist;
	}

	/**
	 * A method to test if an object property exists in a set of object properties
	 * By comparing the names
	 * 
	 * @param OWLObjectProperty , and a set of OWLObjectProperties.
	 * @return boolean if true then the object property exists in the given set of
	 *         classes.
	 */
	public boolean existin(OWLObjectProperty ob, Set<OWLObjectProperty> setOFobs) {
		boolean exist = false;
		for (OWLObjectProperty tempOb : setOFobs) {
			if (tempOb.getIRI().getFragment().toLowerCase().equals(ob.getIRI().getFragment().toLowerCase()))
				exist = true;
		}
		return exist;
	}

	/**
	 * A method to test if an object property equals another object property
	 * By comparing the names
	 * 
	 * @param TWO OWLObjectProperties.
	 * @return boolean return true if they are the same
	 */
	public boolean existin(OWLObjectProperty ob1, OWLObjectProperty ob2) {
		boolean exist = false;
		if (ob1.getIRI().getFragment().toLowerCase().equals(ob2.getIRI().getFragment().toLowerCase()))
				exist = true;
		return exist;
	}

	/**
	 * A method to test if an data property exists in a set of data properties By
	 * comparing the names
	 * 
	 * @param OWLDataProperty , and a set of OWLDataProperties.
	 * @return boolean if true then the data property exists in the given set of
	 *         classes.
	 */
	public  boolean existin(OWLDataProperty db, Set<OWLDataProperty> setOFdbs) {
		boolean exist = false;
		for (OWLDataProperty tempDb : setOFdbs) {
			if ((tempDb.getIRI().getFragment()).toLowerCase().equals((db.getIRI().getFragment()).toLowerCase()))
				exist = true;
		}
		return exist;
	}

	/**
	 * A method to print a set of classes (used for testing)
	 * 
	 * @param OWLOntology , and a set of OWLClasses.
	 */
	public  void printSetofClasses(OWLOntologyInformation sourceOntology,Set<OWLClass> classes) throws IOException {
		for (OWLClass c : classes)
			System.out.println(sourceOntology.getClassLabel(c));
	}
	
	
	
	public ConceptUtilityScoreClass getSimilarclasses(AMLMapping m, ArrayList<AMLMapping> mappings) throws IOException, OWLOntologyCreationException, OWLException {
		// Condition 1: if the two classes have equivalent classes with mappings, then they can
		// be semantically the same
		getEquivalentClassesWithMappings (m,mappings );
		
		// Condition 2: if two classes has subclasses with mappings, then they can be
		// semantically the same
		getSubClassesWithMappings(m,mappings);
				
		// Condition 3: if two classes has direct superclass with mappings, then they can be
		// semantically the same
		getParentClassWithMappings(m, mappings);
		
		// Condition 4: if two classes has common non-direct superclass with mappings, then they can be
		// semantically the same
		getNotDirectParentClassWithMappings( m, mappings);

		// Condition 5: if two classes has common sibling classes with mappings, then they can be
		// semantically the same
		getSiblingClassesWithMappings( m, mappings);
		
		// Condition 6: if two classes has common object properties && has domain/range classes that have mappings 
		// then they are semantically the same.
		getCommonObjectProperties(m, mappings);
		
		getSynonymsandDefinitionScores(m,mappings);
		
		conceptUtilityScore=0.5* conceptContextMatchingScore+ 0.5*conceptSemanticRichnessScore;
		
		ConceptUtilityScoreClass conceptForExtension=new ConceptUtilityScoreClass(
				m.getTargetURI(), conceptContextMatchingScore,conceptSemanticRichnessScore,conceptUtilityScore); 
		
		/*System.out.println("The concept Context Matching Score is: "+ conceptContextMatchingScore );
		System.out.println("The concept Semantic Richness Score is: "+ conceptSemanticRichnessScore);
		System.out.println("The concept Utility Score is: "+ conceptUtilityScore);*/
		mappings=null;
		conceptContextMatchingScore=0.0;
		conceptSemanticRichnessScore=0.0;
		conceptUtilityScore=0.0;
		return conceptForExtension;
	}

	/**
	 * A method to get equivalent classes that has a mapping between source class and target
	 * class. For example, if ClassA is equivalent to ClassC and ClassB is
	 * equivalent to ClassC' so, ClassA and ClassB are equivalent iff classC and classC' has a mappings.
	 * This function test if classC and classC' has a mapping
	 * 
	 * @param source class ,target class, and list of mappings.
	 * @return The source class if there is a mapping between equivalent classes, else return null.
	 */

	public void getEquivalentClassesWithMappings(AMLMapping m, List<AMLMapping> mappings ) throws IOException {
	
	OWLClass sourceClass=sourceOntology.getOWLClassfromIRI(m.getSourceURI());
	OWLClass targetClass = targetOntology.getOWLClassfromIRI(m.getTargetURI());	
	Set<OWLClass> sourceClassEquavilantClasses= sourceOntology.getEquavilantClasses(sourceClass);
	Set<OWLClass> targetClassEquavilantClasses= targetOntology.getEquavilantClasses(targetClass);
	for (OWLClass c1 : sourceClassEquavilantClasses) 
		for (OWLClass c2 : targetClassEquavilantClasses) 
			if(testMappingExistence(c1, c2, mappings)) {
				System.out.println("Added for common Equivalent classes");
				Finalmappings.add(m);
			}
	sourceClassEquavilantClasses=null;
	targetClassEquavilantClasses=null;
	}
	/**
	 * A method to get sub-classes that has mappings between source class and target class.
	 * get subclasses of source class
	 * get subclasses of target class
	 * test if there is a match between the two sets of subclasses.
	 *
	 * @param The source class, target class, and list of mappings.
	 * @return The source class that has mappings between the input classes.
	 */
	public void getSubClassesWithMappings(AMLMapping m, List<AMLMapping> mappings ) throws IOException {
		boolean temp=false;
		OWLClass sourceClass=sourceOntology.getOWLClassfromIRI(m.getSourceURI());
		OWLClass targetClass = targetOntology.getOWLClassfromIRI(m.getTargetURI());
		Set<OWLClass> sourceClassSubClasses= sourceOntology.getSubClasses(sourceClass);
		Set<OWLClass> targetClassSubClasses= targetOntology.getSubClasses(targetClass);
		if(targetClassSubClasses.size()>0)
			conceptSemanticRichnessScore+=0.2;
		for (OWLClass c1 : sourceClassSubClasses) { 
			if (Finalmappings.contains(m))
				break;
			
			for (OWLClass c2 : targetClassSubClasses) {
				if(testMappingExistence(c1, c2, mappings)) {
					System.out.println("Added for common sub classes");
					temp=true;
					Finalmappings.add(m);
				}
			}
		}
		if(temp==true)
			conceptContextMatchingScore+=0.2;
		sourceClassSubClasses=null;
		targetClassSubClasses=null;
		}
	
	/**
	 * A method to get (direct) parent class that has mapping between source class and target class, if exists.
	 * get superclass of source class
	 * get superclass of target class
	 * test if there is a match between the two superclasses.
	 * 
	 * @param The source class , target class, and list of mappings.
	 * @return The source class that have common (direct) parent with mappings with the target superclasses.
	 */
	
	public void getParentClassWithMappings(AMLMapping m, List<AMLMapping> mappings ) throws IOException {
		boolean temp=false;
		OWLClass sourceClass=sourceOntology.getOWLClassfromIRI(m.getSourceURI());
		OWLClass targetClass = targetOntology.getOWLClassfromIRI(m.getTargetURI());
		Set<OWLClass> sourceClassDirectSuperClass= sourceOntology.getDirectSuperClass(sourceClass);
		Set<OWLClass> targetClassDirectSuperClass= targetOntology.getDirectSuperClass(targetClass);
		if(targetClassDirectSuperClass.size()>0)
			conceptSemanticRichnessScore+=0.2;
		for (OWLClass c1 : sourceClassDirectSuperClass) {
			for (OWLClass c2 : targetClassDirectSuperClass) {	
			// if direct parent is "Thing" don't count it
			if (c1.getIRI().getFragment() == "Thing" || c2.getIRI().getFragment() == "Thing")
				break;
			if(testMappingExistence(c1, c2, mappings)) {
				System.out.println("Added for common direct parent classes");
				temp=true;
				Finalmappings.add(m);
			}
			}
		}
		if(temp==true)
			conceptContextMatchingScore+=0.2;
		sourceClassDirectSuperClass=null;
		targetClassDirectSuperClass=null;
	}

	/**
	 * A method to get (all) not direct parent classes that has mapping between source class and target class, if exists.
	 * get  all superclasses of source class
	 * get all superclasses of target class
	 * test if there is a match between any two superclasses.
	 * 
	 * @param The source class, target class, and list of mappings.
	 * @return The source class that have common any not direct parents with mappings with the target superclasses.
	 */
	
	public void getNotDirectParentClassWithMappings(AMLMapping m, List<AMLMapping> mappings) throws IOException {
		boolean temp=false;
		OWLClass sourceClass=sourceOntology.getOWLClassfromIRI(m.getSourceURI());
		OWLClass targetClass = targetOntology.getOWLClassfromIRI(m.getTargetURI());
		Set<OWLClass> sourceClassAllSuperClass= sourceOntology.getAllSuperClasses(sourceClass);
		Set<OWLClass> targetClassAllSuperClass= targetOntology.getAllSuperClasses(targetClass);
		
		for (OWLClass c1 : sourceClassAllSuperClass) {
			if (Finalmappings.contains(m))
				break;
			for (OWLClass c2 : targetClassAllSuperClass) {	
			// if direct parent is "Thing" don't count it
			if(c1.getIRI().getFragment() == "Thing" || c2.getIRI().getFragment() == "Thing")
				continue;
			if(testMappingExistence(c1, c2, mappings)) {
				System.out.println("Added for common non-direct parent classes");
				temp=true;
				Finalmappings.add(m);
			}
			}
		}
		if(temp==true)
			conceptContextMatchingScore+=0.2;
		sourceClassAllSuperClass=null;
		targetClassAllSuperClass=null;
	}
	/**
	 * A method to get sibling classes that has mapping between source class and target class, if exists.
	 * get sibling classes of source class
	 * get sibling classes of target class
	 * test if there is a match between the two sibling classes.
	 * 
	 * @param The source class, target class, and list of mappings.
	 * @return The source class that have common siblings with mappings with the target superclasses.
	 */
	
	public void getSiblingClassesWithMappings(AMLMapping m, List<AMLMapping> mappings) throws IOException {
		OWLClass sourceClass=sourceOntology.getOWLClassfromIRI(m.getSourceURI());
		OWLClass targetClass = targetOntology.getOWLClassfromIRI(m.getTargetURI());
		Set<OWLClass> sourceClassSiblingClasses= sourceOntology.getSiblingClasses(sourceClass);
		Set<OWLClass> targetClassSiblingClasses= targetOntology.getSiblingClasses(targetClass);
		
		for (OWLClass c1 : sourceClassSiblingClasses) {
			if (Finalmappings.contains(m))
				break;
			for (OWLClass c2 : targetClassSiblingClasses) {	
			// if sibling class is "Thing" don't count it
			if(c1.getIRI().getFragment() == "Thing" || c2.getIRI().getFragment() == "Thing")
				continue;
			if(c1.getIRI().getFragment() == sourceClass.getIRI().getFragment() || 
					c2.getIRI().getFragment() == targetClass.getIRI().getFragment())
				continue;
			if(testMappingExistence(c1, c2, mappings)) {
				System.out.println("Added for common sibling classes");
				conceptContextMatchingScore+=0.2;
				Finalmappings.add(m);
				}
			}
		}
		sourceClassSiblingClasses=null;
		targetClassSiblingClasses=null;
	}
	
	/**
	 * A method to get source class if the two input classes have common object properties,
	 * and there is a mapping between their domain/target classes
	 * 
	 * @param The source class , target class, and list of mappings.
	 * @return The source class
	 */
	public void getCommonObjectProperties(AMLMapping m, List<AMLMapping> mappings) throws IOException {
		OWLClass sourceClass=sourceOntology.getOWLClassfromIRI(m.getSourceURI());
		OWLClass targetClass = targetOntology.getOWLClassfromIRI(m.getTargetURI());
		boolean temp=false;
		// get all object properties connected to the source class from the source ontology
		// get all object properties connected to the target class from the target ontology
		Set<OWLObjectProperty> targetOWLObjectProperty=targetOntology.getObjectProperties(targetClass);
		if(targetOWLObjectProperty.size()>0)
			conceptSemanticRichnessScore+=0.2;
		for (OWLObjectProperty obSource : sourceOntology.getObjectProperties(sourceClass)) {
			// if there is a common object property between the two classes (lexical similarity)
			for (OWLObjectProperty obTarget : targetOWLObjectProperty) {
				if (existin(obSource, obTarget)) {
					//if there are any common object properties between the two classes
					//check for any domain/range classes that are mapped
					for(OWLClass cS : sourceOntology.getClassesRelatedToObjectProperty(obSource))
						for(OWLClass cT : targetOntology.getClassesRelatedToObjectProperty(obTarget)) {
								temp=true;
								Finalmappings.add(m);
							}
						}
				}			
			}
		if(temp==true)
			conceptContextMatchingScore+=0.2;
		}
	
	/**
	 * A method to get sub-classes that has mappings between source class and target class.
	 * get subclasses of source class
	 * get subclasses of target class
	 * test if there is a match between the two sets of subclasses.
	 *
	 * @param The source class, target class, and list of mappings.
	 * @return The source class that has mappings between the input classes.
	 */
	public void getSynonymsandDefinitionScores(AMLMapping m,  List<AMLMapping> mappings ) throws IOException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		StructuralReasonerFactory structFactory = new StructuralReasonerFactory();
		OWLOntology ontology=targetOntology.getOntology();
		OWLReasoner reasoner = structFactory.createReasoner(ontology);
		reasoner.precomputeInferences();
		boolean temp=false;
		//OWLClass targetClass = targetOntology.getOWLClassfromIRI(m.getTargetURI());		
		OWLClass targetClass = factory.getOWLClass(IRI.create(m.getTargetURI()));

		Iterator<OWLAnnotation> iterator = EntitySearcher.getAnnotationObjects(targetClass, ontology.getImportsClosure()).iterator();
		while (iterator.hasNext()) {
			final OWLAnnotation annotat = iterator.next();
			if(annotat.getProperty().getIRI().getFragment().contains("rdfs:label")) {
				System.out.println("Label: "+annotat.getValue());
				temp=true;
			}		
			if(annotat.getProperty().getIRI().getFragment().contains("rdfs:comment")) {
				System.out.println("Comment: "+annotat.getValue());
				temp=true;
			}
			if(annotat.getProperty().getIRI().getFragment().contains("hasExactSynonym")) {
				System.out.println("Exact Synonym: "+ annotat.getValue());
				temp=true;
			}
			if(annotat.getProperty().getIRI().getFragment().contains("hasNarrowSynonym")) {
				System.out.println("Narrow Synonym: "+annotat.getValue());
				temp=true;
			}
			if(annotat.getProperty().getIRI().getFragment().contains("hasDefinition")) {
				System.out.println("Definition: "+annotat.getValue());
				temp=true;
			}			
		}
		if(temp==true)
			conceptSemanticRichnessScore+=0.2;
		iterator=null;
		//targetClassSubClasses=null;
		}
	
}