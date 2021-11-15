package AgentClasses;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public  class CandidateOntologyClass {
	private String ontologyID;
	private OntologyUtilityScoreClass ontologyUtilityScore;
	private ArrayList<ConceptUtilityScoreClass> conceptUtilityScores;
	private double ontologyAggregatedScore;
	private double totalUtilityScore;
	private int ontologyScore;
	

	public CandidateOntologyClass() {
		super();
		this.ontologyID = "";
		this.ontologyUtilityScore = new OntologyUtilityScoreClass();
		this.conceptUtilityScores= new ArrayList<ConceptUtilityScoreClass>();
		this.totalUtilityScore= 0;
		this.ontologyScore=0;
		this.ontologyAggregatedScore=0;
	}
	
	public CandidateOntologyClass(String ontologyID, int ontologyScore) {
		super();
		this.ontologyID = ontologyID;
		this.ontologyScore= ontologyScore;
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
		this.ontologyScore=this.ontologyScore+ 1;
	}

	public CandidateOntologyClass copy() {
		CandidateOntologyClass newClass=new CandidateOntologyClass();
		newClass.ontologyID=this.ontologyID;
		newClass.ontologyUtilityScore = this.ontologyUtilityScore;
		newClass.conceptUtilityScores=this.conceptUtilityScores;
		newClass.ontologyAggregatedScore=this.ontologyAggregatedScore;
		newClass.totalUtilityScore=this.totalUtilityScore;
		newClass.ontologyScore=this.ontologyScore;
		return newClass;
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

	public double getOntologyAggregatedScore() {
		return ontologyAggregatedScore;
	}

	public void setOntologyAggregatedScore(double ontologyAggregatedScore) {
		this.ontologyAggregatedScore = ontologyAggregatedScore;
	}
	
	public double getTotalUtilityScore() {
		return totalUtilityScore;
	}

	public void setTotalUtilityScore(ArrayList<ConceptUtilityScoreClass> conceptUtilityScores) {
		double allConceptsScore=0;
		if(conceptUtilityScores.size()!= 0) {
			for(ConceptUtilityScoreClass candidateConcept: conceptUtilityScores) {
				allConceptsScore+= candidateConcept.getConceptUtilityScore();
			}		 
			this.totalUtilityScore = ((double)1/(double)3)*(this.getOntologyUtilityScore().getOntologyTotalUtilityScore()+
					(allConceptsScore/conceptUtilityScores.size())+ this.getOntologyAggregatedScore());
		}
		else
			this.totalUtilityScore = ((double)1/(double)3)*(this.getOntologyUtilityScore().getOntologyTotalUtilityScore()+
						 this.getOntologyAggregatedScore());
	}

	public void display() {
		System.out.println("OntologyId: "+ ontologyID+ " | "+ "OntologyUS: "+ontologyUtilityScore.getOntologyTotalUtilityScore()+'\n');
	}

	public void display1() {
		System.out.println("OntologyId: "+ ontologyID +'\n');
		for(ConceptUtilityScoreClass concept:this.conceptUtilityScores) {
			System.out.println("ClassId: "+ concept.getMatchedConceptName()+ " | "+ "Concept Score: "+ concept.getConceptUtilityScore()+'\n');
		}
		System.out.println("Ontology Utility Score: "+ ontologyUtilityScore.getOntologyTotalUtilityScore());
		//System.out.println("Ontology Utility Score: "+ allConceptsScore/conceptUtilityScores.size());
		System.out.println("Ontology Aggregated Score: "+ getOntologyAggregatedScore());
		System.out.println("Total Ontology Score: "+ totalUtilityScore+'\n');
	}
	
	
	
	/*public int compareTo(CandidateOntologyClass o) {
		return new Double(this.getOntologyUtilityScore().getOntologyTotalUtilityScore()).compareTo(o.getOntologyUtilityScore().getOntologyTotalUtilityScore());
	}*/
	
	public int getOntologyScore() {
		return ontologyScore;
	}

	public void setOntologyScore(int ontologyScore) {
		this.ontologyScore = ontologyScore;
	}
	
	public void addToOntologyScore(int ontologyScore) {
		this.ontologyScore = this.ontologyScore+ ontologyScore;
	}

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
		    
		public static Comparator<CandidateOntologyClass> sortByOntologyScore = new Comparator<CandidateOntologyClass>() {

		public int compare(CandidateOntologyClass o1, CandidateOntologyClass o2) {
			int ontologyScore1 = o1.getOntologyScore();
			int ontologyScore2 = o2.getOntologyScore();

			//descending order
			return new Integer(ontologyScore2).compareTo(ontologyScore1);
			//return Integer.compare(ontologyScore2,ontologyScore1);
		}};


		public void setTotalUtilityScore(double totalUtilityScore) {
			this.totalUtilityScore=totalUtilityScore;
		}
		    
	
}
