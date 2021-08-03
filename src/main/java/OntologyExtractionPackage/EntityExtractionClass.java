package OntologyExtractionPackage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;

public class EntityExtractionClass {
	
	final static Logger log = Logger.getLogger(EntityExtractionClass.class);
		
	//function that takes the file name and path and returns the labels of its classes 
	public static String getClassesLabelsFromInputOntology(String filename) throws OWLOntologyCreationException, OWLException, IOException{
		Map<String, String> allClasses=new HashMap<String,String>();
		OWLOntology ontology=null;
		ontology= laodOntology(filename);
		allClasses=getOntolgyClassesLabels(ontology);
		print(allClasses);
		return getClassesNamesinaString(allClasses);
	}
		
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
				log.info("Loading " + filename + " sucessfully");
			} 
		catch (OWLOntologyCreationException e) {
		log.error("Error in loading the ontology: " + e);
		}
		return ontology;
	}
			
	/*to get all classes of an OWLOntology (after loading it)
	first it load the imported ontologies (if exists)
	then adds classes of all ontologies in one Set<OWLCLasses>
	then check for duplicates  
	returns all classes and class labels for an ontology (including imported ones)*/
	public static Map<String, String>  getOntolgyClassesLabels(OWLOntology o) throws IOException{
		Map<String, String> classIdAndLabel = new HashMap<String, String>();
		String classLabel;			
			
		//get all ontologies that are imported in a given ontology//
		Set<OWLOntology> importedOntologies=o.getOWLOntologyManager().getOntologies();
		
		log.info("You have "+ importedOntologies.size() +" imported ontologies...");
			
		Set<OWLClass> allClasses=new HashSet<OWLClass>();	
		if(importedOntologies.size()>0) {
			for(OWLOntology ontology: importedOntologies) {
			    log.info("Ontology:  "+ ontology+ " , have "+ ontology.getClassesInSignature().size() +" classes...");
			    allClasses.addAll(ontology.getClassesInSignature());
			    }
			//get class_labels no duplicates, no classes without labels
			for (OWLClass c : allClasses) {
			    classLabel=getClassName(c);	    				    			
			    if(classLabel!=null) 
			    	if(!existsIn(classIdAndLabel, c.getIRI().toString(), classLabel)) {
			    			classIdAndLabel.put(c.getIRI().toString(), classLabel);			    		}
			    }
			  }
		else
			log.error("Error while getting classes...");
		return classIdAndLabel;
	}
			
			
	//Check for duplicates	
	private static boolean existsIn( Map<String, String> classIdSet, String classIRI, String classLabel)
	{
		boolean bool=false;
		if(classIdSet.size()==0)
			return bool;
		else {
			for (String i : classIdSet.keySet()) {
				if((i==classIRI) || ((classIdSet.get(i)).equals(classLabel)))
					bool=true;
			}
		}
		return bool;		
	}
		
	//Given an OWLClass retuens the class label
	private static String getClassName( OWLClass c) throws IOException {
		String classIRI=c.getIRI().toString();
		return classIRI.substring(classIRI.lastIndexOf('#')+1);
	}
	
	public static String getClassIRI(String fileName, String className) throws OWLOntologyCreationException, OWLException, IOException {
		Map<String, String> classIdAndLabel=new HashMap<String,String>();
		String classIRI="";
		OWLOntology ontology=null;
		ontology= laodOntology(fileName);
		classIdAndLabel=getOntolgyClassesLabels(ontology);
		for(String classId: classIdAndLabel.keySet())
		{
			if(classIdAndLabel.get(classId).equals(className))		
				classIRI=classId;
		}
		System.out.println("The Class IRI is: " + classIRI);
		return classIRI;
	}
			
	//Given a class and an ontology retuen the RDFS:Label for that class
	/*private static String getClassName(OWLOntology o, OWLClass c) throws IOException {
	Iterator<OWLAnnotation> iterator = EntitySearcher.getAnnotations(c, o).iterator();
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
			                throw new RuntimeException("Unexpected class "+ val.getClass());
			            }
			         }
			   }
			  return c.toStringID();
			}*/
		
	//print all classes
	public static void print(Map<String,String> map) {
		log.info("Classes are: "+ map.size() + " classes");
		int count=0;
		for (String i : map.keySet()) 
			//System.out.println(++count+ ": ClassIRI: " + i + " Label: " + map.get(i));
			System.out.println("Class "+ ++count+  ": " + map.get(i));
	}
	
	//The function takes the map of class Id and Name and return a string for all class names seperated by commas
	//to be used in the bioBortal recommender service
	public static String getClassesNamesinaString(Map<String,String> map) {
		String classNamesasInput="";
		int count=1;
		int size=map.size();
		for (String i : map.keySet()) {
			if(count<size) {
			classNamesasInput+=map.get(i)+",";
			count++;
			}
			else
				classNamesasInput+=map.get(i);
		}
		return classNamesasInput;
	}
}

