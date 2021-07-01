package OntologyExtractionPackage;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.model.OWLDataFactory;

public class BioPortalCategoryOntologyClass {
	static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	static OWLDataFactory factory = manager.getOWLDataFactory();
	static StructuralReasonerFactory structFactory = new StructuralReasonerFactory();
	
	//////////////////////////////////////////////
	//The function takes two concept names as input and returns the difference between their levels
	//using the Bioportal_categories ontology
	//Example: 0 means they dont have any relations
	//         1 means they are sub or super direct classes
	//		   2 means they have 1 level in between
	///////////////////////////////////////////////////////////////////
	public static int getCategoryLevel(String userPrefDomain, String ontologyCategory){	
		int diffLevel=0;
		try {
			OWLOntology ontology=laodOntology("C:\\Users\\marwa\\eclipse-workspace-photon\\OntologyReuseProject\\BioPortal_Categories.owl");
			OWLReasoner reasoner = structFactory.createReasoner(ontology);
			reasoner.precomputeInferences();

			String test=ontology.getOntologyID().toString();
			String newTest=test.substring(test.indexOf("http"),test.indexOf("74")+2);
			
			//OWLClass owlclass= getOWLClassfromIRI(ontology.getOntologyID().getOntologyIRI()+"#"+userPrefDomain);
			OWLClass owlclass= getOWLClassfromIRI(newTest+"#"+userPrefDomain);
			
			//if direct subclass
			for (OWLClass subclass : reasoner.getSubClasses(owlclass, true).getFlattened()) {
				if (!subclass.isAnonymous() && !subclass.equals(owlclass)) 
					if(subclass.getIRI().toString().substring(subclass.getIRI().toString().lastIndexOf('#')+1).equals(ontologyCategory)) {
						//System.out.println("Direct subclass: "+ subclass.getIRI().toString().substring(subclass.getIRI().toString().lastIndexOf('#')+1));
						return diffLevel=1;
					}
				}
			//if not direct subclass	
			for (OWLClass subSubclass : reasoner.getSubClasses(owlclass, false).getFlattened()) {
				if (!subSubclass.isAnonymous() && !subSubclass.equals(owlclass)) 
					if(subSubclass.getIRI().toString().substring(subSubclass.getIRI().toString().lastIndexOf('#')+1).equals(ontologyCategory)) {
						//System.out.println("Not direct subclass: "+ subSubclass.getIRI().toString().substring(subSubclass.getIRI().toString().lastIndexOf('#')+1));
						return diffLevel=2;
					}		
				}
			//if direct superclass
			for (OWLClass superclass : reasoner.getSuperClasses(owlclass, true).getFlattened()) {
				if (!superclass.isAnonymous() && !superclass.equals(owlclass)) 
					if(superclass.getIRI().toString().substring(superclass.getIRI().toString().lastIndexOf('#')+1).equals(ontologyCategory)) {
						return diffLevel=1;
						//System.out.println("Direct superclass: "+ superclass.getIRI().toString().substring(superclass.getIRI().toString().lastIndexOf('#')+1));
					}
				}
			//if not direct superclass	
			for (OWLClass superSuperclass : reasoner.getSuperClasses(owlclass, false).getFlattened()) {
				if (!superSuperclass.isAnonymous() && !superSuperclass.equals(owlclass)) 
					if(superSuperclass.getIRI().toString().substring(superSuperclass.getIRI().toString().lastIndexOf('#')+1).equals(ontologyCategory)) {
						return diffLevel=2;
						//System.out.println("Not direct superclass: "+ superSuperclass.getIRI().toString().substring(superSuperclass.getIRI().toString().lastIndexOf('#')+1));
					}		
				}	
			} catch (OWLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		return diffLevel;
		}
		///////////////////////////////////////////////////////////
		public static OWLClass getOWLClassfromIRI(String iri) {
		OWLClass c= factory.getOWLClass(IRI.create(iri));
		return c;
		}
		//////////////////////////////////////////////////////////////
		//to load an ontology given its file name
		//returns OWLOntology object
		public static OWLOntology laodOntology(String filename) throws OWLOntologyCreationException, OWLException {
			OWLOntology ontology = null;
			//IRIDocumentSource source=new IRIDocumentSource(IRI.create());
			try {
				File file = new File(filename);
				   if (! file.isFile()) 
				    System.out.println("There is not ontology with this name, maybe not in OWL format");
					OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
					OWLOntologyLoaderConfiguration loaderConfig = new OWLOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
					IRIDocumentSource source=new IRIDocumentSource(IRI.create(file));
					ontology = manager.loadOntologyFromOntologyDocument(source, loaderConfig);
					//log.info("Loading " + filename + " sucessfully");
				} 
			catch (OWLOntologyCreationException e) {
			//log.error("Error in loading the ontology: " + e);
			}
			return ontology;
		}
		
}
