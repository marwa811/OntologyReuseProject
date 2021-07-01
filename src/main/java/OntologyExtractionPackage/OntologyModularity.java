package OntologyExtractionPackage;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

public class OntologyModularity {
	
	public static String getModule(String className, String ontologyIRI) throws OWLOntologyCreationException{
 		//OWLOntology targetOntology=laodOntology("C:\\Users\\marwa\\eclipse-workspace-photon\\OntologyReuseProject\\OWLOntologies\\OBOE-SBC.owl");
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLOntology targetOntology=manager.loadOntology(IRI.create(ontologyIRI));
		StructuralReasonerFactory structFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = structFactory.createReasoner(targetOntology);
		reasoner.precomputeInferences();
		Random rand = new Random(); //instance of random class
	    //generate random values from 0-24
	    String randomNoString=Integer.toString(rand.nextInt(1000));
			
		 //OWLClass oclass= factory.getOWLClass(IRI.create("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#CharacteristicQualifier"));
		 OWLClass oclass= factory.getOWLClass(IRI.create(className));
		 Set<OWLEntity> testclasses = new HashSet<OWLEntity>();
		 testclasses.add(oclass);
		 IRI iri=IRI.create("http://www.semanticweb.org/marwa/ontologies/2021/5/untitled-ontology-"+randomNoString);
		 SyntacticLocalityModuleExtractor sme = new SyntacticLocalityModuleExtractor(manager, targetOntology, ModuleType.STAR);
		 //to get all sub and super classes of the input class
		 OWLOntology module = sme.extractAsOntology(testclasses, iri, -1, -1, reasoner);
		// System.out.println("The ontology is: "+testOntology.toString());
		// File file = new File("C:\\Users\\marwa\\eclipse-workspace-photon\\OntologyReuseProject\\test.owl");
		// OutputStream os = new FileOutputStream(file);
		// manager.saveOntology(testOntology,os);
		 return module.getOntologyID().getOntologyIRI().toString();
	}
/*	protected Set<OWLAxiom> getADModule1(OWLOntology o, Set<OWLEntity> sig, ModuleType mt) {
		Atomicdecomposition ad = new Atomicdecomposition(o, mt, false);
	    return asSet(ad.getModule(sig.stream(), false, mt));
	}*/
}
