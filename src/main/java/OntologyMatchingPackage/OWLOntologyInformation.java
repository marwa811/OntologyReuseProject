package OntologyMatchingPackage;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

/**
 * OWLOntologyInformation.java 
 * Purpose: Get a class's semantic information(its equivalent classes, 
 * its disjoint classes, its super and subclasses, its related properties, etc )
 * 
 * @author marwa
 * @version 0.1 7/12/2018
 */

public class OWLOntologyInformation {
	final static Logger log = Logger.getLogger(OWLOntologyInformation.class);
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	OWLOntology onto;
	String OntoID;
	OWLDataFactory factory = manager.getOWLDataFactory();
	StructuralReasonerFactory structFactory = new StructuralReasonerFactory();
	//OWLReasoner reasoner = structFactory.createReasoner(onto);
	
	/**
	 * A method to load an ontology using its file name. 
	 *
	 * @param A string for the file name.
	 * @return OWLOntology object for the ontology.
	 */
	public void laodOntology(String filename) throws OWLOntologyCreationException, OWLException {
		try {
			File file = new File(filename);
		//	manager.setSilentMissingImportsHandling(true);
			onto = manager.loadOntologyFromOntologyDocument(file);			
			//OWLOntologyID ontologyIRI = onto.getOntologyID();
			//OntoID = ontologyIRI.getOntologyIRI().toString();
			System.out.println(" Load ontology sucessfully!");
			IRI documentIRI = manager.getOntologyDocumentIRI(onto);
			System.out.println("The path comes from " + documentIRI);
			System.out.println("The OntoID is " + OntoID);
			log.info("Loading " + filename + "...");
			} 
		catch (OWLOntologyCreationException e) {
			log.error("The Ontology: " + e+ " is already loaded");
		}
	}
	
	public OWLOntology getOntology() {
		return onto;
	}

	public void setOntology(OWLOntology onto) {
		this.onto=onto;
	}
	/**
	 * A method to get the all imported ontologies for a given OWLOntology.
	 *
	 * @param An input OWLOntology.
	 * @return A Set of imported OWLOntologies.
	 */

	public Set<OWLOntology> getImportedOntologies() {
		Set<OWLOntology> allOntologies = new HashSet<OWLOntology>();
		allOntologies = onto.getOWLOntologyManager().getOntologies();
		return allOntologies;
	}
	
	
	/**
	 * A method to get OWLClass from an IRI string
	 *
	 * @param The IRI for the class (converted later to an OWLClass object)
	 * return OWLClass
	 */
	public OWLClass getOWLClassfromIRI(String iri) {
		OWLClass c= factory.getOWLClass(IRI.create(iri));
		return c;
	}

	/**
	 * A method to get all semantic information for a given class (call other
	 * methods in the class).
	 *
	 * @param The IRI for the class (converted later to an OWLClass object)
	 */
	public void getClassSemanticInformation(String classIRI) throws Exception {
		OWLDataFactory factory = onto.getOWLOntologyManager().getOWLDataFactory();
		OWLClass c = factory.getOWLClass(IRI.create(classIRI));

		getEquavilantClasses(c);
		getSubClasses(c);
		getDisjointClasses(c);
		getDirectSuperClass(c);
		getAllSuperClasses(c);
		getSiblingClasses(c);
		getObjectProperties(c);
		getDataProperties(c);
		getAnnotationProperties(c);
		getInferedOWLDisjointnessAxioms(c);
		getInferedOWLEquivalentAxioms(c);
	}

	/**
	 * A method to get equivalent classes for a given OWLClass.
	 *
	 * @param The OWLClass.
	 * @return Set of equivalent OWLClasses
	 */
	public Set<OWLClass> getEquavilantClasses(OWLClass c) throws IOException {
		Set<OWLClass> finalEquivalntClasses = new HashSet<OWLClass>();
		//Set<OWLEquivalentClassesAxiom> equavilantClasses = onto.getEquivalentClassesAxioms(c);
		if ((onto.getEquivalentClassesAxioms(c)).size() > 0) {
			for (OWLEquivalentClassesAxiom equivalentAxiom : onto.getEquivalentClassesAxioms(c)) {
				for (OWLClass cls : equivalentAxiom.getClassesInSignature()) {
					if (!cls.equals(c)) {
						finalEquivalntClasses.add(cls);
					}
				}	
			}
		}
		return finalEquivalntClasses;
	}

	/**
	 * A method to get equivalent classes for a given OWLClass using a reasoner.
	 *
	 * @param The OWLOntology (the class's ontology). The OWLClass.
	 * @return Set of equivalent OWLClasses
	 */
	public Set<OWLClass> getInferedOWLEquivalentAxioms(OWLClass c) {
		OWLReasoner reasoner = structFactory.createReasoner(onto);
		reasoner.precomputeInferences();

		Set<OWLClass> equivalentClasses = new HashSet<OWLClass>();

		for (OWLClass equivalentClass : reasoner.getEquivalentClasses(c).getEntities()) {
			if (!equivalentClass.equals(c)) {
				System.out.println("Equivalent class reasoner: " + equivalentClass.getIRI().getFragment());
				equivalentClasses.add(equivalentClass);
			}
		}
		System.out.println(equivalentClasses.size());
		return equivalentClasses;
	}

	/**
	 * A method to get disjoint classes for a given OWLClass.
	 *
	 * @param The OWLClass.
	 * @return Set of OWLDisjointClassesAxiom(s)
	 */

	public Set<OWLDisjointClassesAxiom> getDisjointClasses(OWLClass c) throws IOException {
		Set<OWLDisjointClassesAxiom> disjointClasses = onto.getDisjointClassesAxioms(c);
		for (OWLDisjointClassesAxiom disjointAxiom : disjointClasses) {
			for (OWLClass class1 : disjointAxiom.getClassesInSignature()) {
				if (!class1.equals(c))
					System.out.println("Disjoint class: " + getClassLabel(class1));
			}
		}
		return disjointClasses;
	}

	/**
	 * A method to get disjoint classes for a given OWLClass using a reasoner.
	 *
	 * @param The OWLClass.
	 * @return Set of disjoint OWLClasses
	 */
	public Set<OWLClass> getInferedOWLDisjointnessAxioms(OWLClass c) {
		//StructuralReasonerFactory structFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = structFactory.createReasoner(onto);
		reasoner.precomputeInferences();
		Set<OWLClass> disjointClasses = new HashSet<OWLClass>();

		for (Node<OWLClass> ax : reasoner.getDisjointClasses(c)) {
			for (OWLClass disjointClass : ax.getEntities()) {
				if (!disjointClass.isAnonymous() && !disjointClass.equals(c)) {
					System.out.println("Disjoint class: " + disjointClass.getIRI().getFragment());
					disjointClasses.add(disjointClass);
				}
			}
		}
		return disjointClasses;
	}
	
	/**
	 * A method to get sibling classes for a given OWLClass.
	 *
	 * @param The OWLClass.
	 * @return Set of subclasses
	 */
	public Set<OWLClass> getSiblingClasses(OWLClass c) throws IOException {	
		//StructuralReasonerFactory structFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = structFactory.createReasoner(onto);
		reasoner.precomputeInferences();
		Set<OWLClass> siblingclasses= new HashSet<OWLClass>();
		//NodeSet<OWLClass> nodeclasses = reasoner.getSuperClasses((OWLClassExpression) c, true);
		for (OWLClass superclass : reasoner.getSuperClasses((OWLClassExpression) c, true).getFlattened()) {
			if (!superclass.isAnonymous() && !superclass.equals(c)) {
			//	NodeSet<OWLClass> siblings = reasoner.getSubClasses((OWLClassExpression) superclass, true);
				siblingclasses = reasoner.getSubClasses((OWLClassExpression) superclass, true).getFlattened();
				for (OWLClass sibling : siblingclasses) {
					if (!sibling.isAnonymous() && !sibling.equals(c)) 
						siblingclasses.add(sibling);
					}
				}
			}
		return siblingclasses;
	}

	/**
	 * A method to get subclasses for a given OWLClass.
	 *
	 * @param The OWLClass.
	 * @return Set of subclasses
	 */
	public Set<OWLClass> getSubClasses(OWLClass c) throws IOException {	
		//StructuralReasonerFactory structFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = structFactory.createReasoner(onto);
	reasoner.precomputeInferences();
	Set<OWLClass> subClasses = new HashSet<OWLClass>();
	//NodeSet<OWLClass> nodeclasses = reasoner.getSubClasses((OWLClassExpression) c, true);
	for (OWLClass subclass : reasoner.getSubClasses((OWLClassExpression) c, true).getFlattened()) {
		if (!subclass.isAnonymous() && !subclass.equals(c))
			subClasses.add(subclass);
		}
	return subClasses;
	}
	
	/**
	 * A method to get object properties for a given OWLClass. Here it passes
	 * through all OWLObjectPropertyDomainAxioms to test if the given class is
	 * "Domain" or "Range", and if so, include this object property in the result
	 * set
	 * 
	 * @param The OWLClass.
	 * @return Set of object properties
	 */
	
	public Set<OWLObjectProperty> getObjectProperties(OWLClass c) {
		Set<OWLObjectProperty> objectProperties = new HashSet<OWLObjectProperty>();
		Set<OWLOntology> allOntologies = onto.getImportsClosure();
		for (OWLOntology o : allOntologies) {
			for (OWLObjectPropertyDomainAxiom dop : o.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
				if (dop.getDomain().equals(c)) {
					for (OWLObjectProperty oop : dop.getObjectPropertiesInSignature()) {
						// System.out.println("Domain for object property: " +
						// oop.getIRI().getFragment());
						objectProperties.add(oop);
					}
				}
			}
			for (OWLObjectPropertyRangeAxiom rop : o.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
				if (rop.getRange().equals(c)) {
					for (OWLObjectProperty oop : rop.getObjectPropertiesInSignature()) {
						// System.out.println("Range for object property: " +
						// oop.getIRI().getFragment());
						objectProperties.add(oop);
					}
				}
			}
		}
		return objectProperties;
	}
		
	/**
	 * A method to get Domain class(es) for a given OWLObjectProperty using a reasoner. 
	 * 
	 * @param The OWLObjectProperty. 
	 * @return Set of Domain OWLClass(es)
	 */
	public Set<OWLClass> getDomainClassForObjectProperty(OWLObjectProperty op) {
		Set <OWLClass> domainClasses=new HashSet<OWLClass>();
		//StructuralReasonerFactory factory = new StructuralReasonerFactory();
		OWLReasoner reasoner = structFactory.createReasoner(onto);
		reasoner.precomputeInferences();

		NodeSet<OWLClass> tempClasses=reasoner.getObjectPropertyDomains(op, true);
		for (OWLClass domainClass : tempClasses.getFlattened()) {
			//System.out.println("Domain class is: " +domainClass.getIRI().getFragment());
			domainClasses.add(domainClass);
		}
		return domainClasses;
	}

	/**
	 * A method to get Range class(es) for a given OWLObjectProperty using a reasoner. 
	 * 
	 * @param The OWLObjectProperty. 
	 * @return Set of Range OWLClass(es)
	 */
	public Set<OWLClass> getRangeClassForObjectProperty(OWLObjectProperty op) {
		Set <OWLClass> rangeClasses=new HashSet<OWLClass>();
		//StructuralReasonerFactory factory = new StructuralReasonerFactory();
		OWLReasoner reasoner = structFactory.createReasoner(onto);
		reasoner.precomputeInferences();

		//NodeSet<OWLClass> tempClasses=reasoner.getObjectPropertyRanges(op, true);
		for (OWLClass rangeClass : reasoner.getObjectPropertyRanges(op, true).getFlattened()) {
			//System.out.println("Range class is: " +rangeClass.getIRI().getFragment());
			rangeClasses.add(rangeClass);
		}
		return rangeClasses;
	}
	
	/**
	 * A method to get All class(es) related for a given OWLObjectProperty 
	 * (wither domain or range classes) using a reasoner. 
	 * 
	 * @param The OWLOntology, and the OWLObjectProperty. 
	 * @return Set of all related OWLClass(es)
	 */
	public Set<OWLClass> getClassesRelatedToObjectProperty(OWLObjectProperty op) {
		Set <OWLClass> relatedClasses=new HashSet<OWLClass>();
		for (OWLClass c : getDomainClassForObjectProperty(op))		
			relatedClasses.add(c);
		
		for (OWLClass c1 : getRangeClassForObjectProperty(op))		
			relatedClasses.add(c1);
		return relatedClasses;
	}

	/**
	 * A method to get data properties for a given OWLClass. Here it passes through
	 * all OWLDataPropertyDomainAxioms to test if the given class is "Domain", and
	 * if so, include this data property in the result set
	 * 
	 * @param The OWLClass.
	 * @return Set of data properties
	 */
	public Set<OWLDataProperty> getDataProperties(OWLClass c) {
		Set<OWLDataProperty> dataProperties = new HashSet<OWLDataProperty>();
		Set<OWLOntology> allOntologies = onto.getImportsClosure();
		for (OWLOntology o : allOntologies) {
			for (OWLDataPropertyDomainAxiom dp : o.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
				if (dp.getDomain().equals(c)) {
					for (OWLDataProperty odp : dp.getDataPropertiesInSignature()) {
						System.out.println("Data Property: " + odp.getIRI().getFragment());
						dataProperties.add(odp);
					}
				}
			}
		}
		return dataProperties;
	}

	/**
	 * A method to get Direct super classes for a given OWLClass.
	 * 
	 * @param The OWLClass.
	 * @return Set of direct superclasses.
	 */
	public Set<OWLClass> getDirectSuperClass(OWLClass c) throws IOException {
		//StructuralReasonerFactory structFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = structFactory.createReasoner(onto);
		reasoner.precomputeInferences();
		Set<OWLClass> superclasses = new HashSet<OWLClass>();
//		NodeSet<OWLClass> nodeclasses = reasoner.getSuperClasses((OWLClassExpression) c, true);
		for (OWLClass superclass : reasoner.getSuperClasses((OWLClassExpression) c, true).getFlattened()) {
			if (!superclass.isAnonymous() && !superclass.equals(c))
				superclasses.add(superclass);
			// System.out.println("SuperClass: " + OntologyParsing.getClassLabel(ontology,
			// superclass));
		}
		return superclasses;

	}

	/**
	 * A method to get all super classes (to the Thing class) for a given OWLClass.
	 * 
	 * @param The OWLClass.
	 * @return Set of all superclasses.
	 */
	public Set<OWLClass> getAllSuperClasses(OWLClass c) throws IOException {
	//	StructuralReasonerFactory structFactory = new StructuralReasonerFactory();
		OWLReasoner reasoner = structFactory.createReasoner(onto);
		reasoner.precomputeInferences();
		Set<OWLClass> superclasses = new HashSet<OWLClass>();
		//NodeSet<OWLClass> nodeclasses = reasoner.getSuperClasses((OWLClassExpression) c, false);
		for (OWLClass superclass : reasoner.getSuperClasses((OWLClassExpression) c, false).getFlattened()) {
			if (!superclass.isAnonymous() && !superclass.equals(c))
				superclasses.add(superclass);
			//System.out.println("SuperClass: " + getClassLabel(ontology, superclass));
		}
		return superclasses;
	}

	/**
	 * A method to get annotation properties for a given OWLClass.
	 * 
	 * @param The OWLClass.
	 * @return Set of OWLAnnotations.
	 */
	public Iterator<OWLAnnotation> getAnnotationProperties(OWLClass c) {
	Iterator<OWLAnnotation> iterator = EntitySearcher.getAnnotationObjects(c, onto.getImportsClosure())
				.iterator();
	//	Iterator<OWLAnnotation> iterator =c.getAnnotations(onto.getImportsClosure()).iterator();
		while (iterator.hasNext()) {
			final OWLAnnotation annotation = iterator.next();
			System.out.println("Annotations: " + annotation.getProperty().getIRI().getFragment() + " : "
					+ annotation.getValue().toString());
		}
		return iterator;
	}

	/**
	 * A method to get RDFS:label for a given OWLClass.
	 * 
	 * @param The OWLClass.
	 * @return The class label.
	 */
	public String getClassLabel(OWLClass owlclass) throws IOException {
		// Get the annotations on the class that use the label property (rdfs:label)
	/*	for (OWLAnnotation annotation : owlclass.getAnnotations(onto, factory.getRDFSLabel())) {
		  if (annotation.getValue() instanceof OWLLiteral) {
		    OWLLiteral val = (OWLLiteral) annotation.getValue();
		        //Get your String here
		        System.out.println(owlclass + " labelled " + val.getLiteral());
		   }
		}*/
		// OWLEntity.getIRI().getShortForm();
		Iterator<OWLAnnotation> iterator = EntitySearcher.getAnnotations(owlclass, onto).iterator();
		while (iterator.hasNext()) {
			final OWLAnnotation an = iterator.next();
			if (an.getProperty().isLabel()) {
				OWLAnnotationValue val = an.getValue();

				if (val instanceof IRI) {
					return ((IRI) val).toString();
				} else if (val instanceof OWLLiteral) {
					OWLLiteral lit = (OWLLiteral) val;
					return lit.getLiteral();
				} else if (val instanceof OWLAnonymousIndividual) {
					OWLAnonymousIndividual ind = (OWLAnonymousIndividual) val;
					return ind.toStringID();
				} else {
					throw new RuntimeException("Unexpected class" + val.getClass());
				}
			} else if (an.getProperty().getIRI().getFragment().equals("prefLabel"))
				return an.getValue().toString();
			else if (an.getProperty().getIRI().getFragment().equals("altLabel"))
				return an.getValue().toString();
		}
		return owlclass.getIRI().getFragment();
	}

	/**
	 * A method to Print all classes & their labels for a given OWLOntology.
	 * 
	 * @param The OWLOntology (the class's ontology).
	 */
	// Print all classes & their labels
	public void printOntologyClasses(OWLOntology ontology) throws IOException {
		int i = 0;
		Set<OWLClass> ontologyclasses = ontology.getClassesInSignature();
		for (OWLClass c : ontologyclasses) {
			i++;
			System.out.println("Class " + i + " : " + c.toStringID() + "  " + "Label: " + getClassLabel(c));
		}
	}
}

