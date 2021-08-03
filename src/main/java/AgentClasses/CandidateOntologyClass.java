package AgentClasses;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public  class CandidateOntologyClass {
	private String ontologyID;
	private OntologyUtilityScoreClass ontologyUtilityScore;
	private ArrayList<ConceptUtilityScoreClass> conceptUtilityScores;
	private double totalUtilityScore; 
	
	public CandidateOntologyClass() {
		super();
		this.ontologyID = "";
		this.ontologyUtilityScore = new OntologyUtilityScoreClass();
		this.conceptUtilityScores= new ArrayList<ConceptUtilityScoreClass>();
		this.totalUtilityScore= 0;
	}
	
	public CandidateOntologyClass(String ontologyID) {
		super();
		this.ontologyID = ontologyID;
	}
	
	public CandidateOntologyClass(String ontologyID, OntologyUtilityScoreClass ontologyUtilityScore,
			ArrayList<ConceptUtilityScoreClass> conceptUtilityScores) {
		super();
		this.ontologyID = ontologyID;
		this.ontologyUtilityScore = ontologyUtilityScore;
		this.conceptUtilityScores = conceptUtilityScores;
	}

	public String getOntologyID() {
		return ontologyID;
	}

	public void setOntologyID(String ontologyURI) {
		this.ontologyID = ontologyURI;
	}

	public OntologyUtilityScoreClass getOntologyUtilityScore() {
		return ontologyUtilityScore;
	}

	public void setOntologyUtilityScore(OntologyUtilityScoreClass ontologyUtilityScore) {
		this.ontologyUtilityScore = ontologyUtilityScore;
	}

	public List<ConceptUtilityScoreClass> getConceptUtilityScores() {
		return conceptUtilityScores;
	}

	public void setConceptUtilityScores(ArrayList<ConceptUtilityScoreClass> conceptUtilityScores) {
		this.conceptUtilityScores = conceptUtilityScores;
	}
	
	
	public double getTotalUtilityScore() {
		return totalUtilityScore;
	}

	public void setTotalUtilityScore(ArrayList<ConceptUtilityScoreClass> conceptUtilityScores) {
		double allConceptsScore=0;
		if(conceptUtilityScores!=null) {
			for(ConceptUtilityScoreClass candidateConcept: conceptUtilityScores) {
				allConceptsScore+= candidateConcept.getConceptUtilityScore();
			}		 
			this.totalUtilityScore = 0.5 * this.getOntologyUtilityScore().getOntologyTotalUtilityScore()+
					(0.5 * ((double) allConceptsScore/conceptUtilityScores.size()));
		}
		else
			this.totalUtilityScore = 0.5 * this.getOntologyUtilityScore().getOntologyTotalUtilityScore();
	}

	public void display() {
		System.out.println("OntologyId: "+ ontologyID+ " | "+ "OntologyUS: "+ontologyUtilityScore.getOntologyTotalUtilityScore()+'\n');
	}

	public void display1() {
		System.out.println("OntologyId: "+ ontologyID+ " | "+ "OntologyUS: "+ontologyUtilityScore.getOntologyTotalUtilityScore()+'\n');
		for(ConceptUtilityScoreClass concept:this.conceptUtilityScores) {
			System.out.println("ClassId: "+ concept.getMatchedConceptName()+ " | "+ "Concept Score: "+ concept.getConceptUtilityScore()+'\n');
		}
		System.out.println("Total Ontology Score: "+ totalUtilityScore+'\n');
	}
	
	/*public int compareTo(CandidateOntologyClass o) {
		return new Double(this.getOntologyUtilityScore().getOntologyTotalUtilityScore()).compareTo(o.getOntologyUtilityScore().getOntologyTotalUtilityScore());
	}*/
	
	public static Comparator<CandidateOntologyClass> sortByOntologyUtilityScore = new Comparator<CandidateOntologyClass>() {

		public int compare(CandidateOntologyClass o1, CandidateOntologyClass o2) {
		   double ontologyScore1 = o1.getOntologyUtilityScore().getOntologyTotalUtilityScore();
		   double  ontologyScore2 = o2.getOntologyUtilityScore().getOntologyTotalUtilityScore();

		   //descending order
		   return new Double(ontologyScore2).compareTo(ontologyScore1);
	    }};
	    
	    public static Comparator<CandidateOntologyClass> sortByTotalUtilityScore = new Comparator<CandidateOntologyClass>() {

			public int compare(CandidateOntologyClass o1, CandidateOntologyClass o2) {
			   double ontologyScore1 = o1.getTotalUtilityScore();
			   double  ontologyScore2 = o2.getTotalUtilityScore();

			   //descending order
			   return new Double(ontologyScore2).compareTo(ontologyScore1);
		    }};
	
}
