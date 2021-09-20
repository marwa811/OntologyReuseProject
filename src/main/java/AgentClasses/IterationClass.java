package AgentClasses;

import java.util.ArrayList;
import java.util.List;

public class IterationClass {	
	private int iterationNo;
	private UserPreferencesModel userPreferences;
	private String inputClassName;
	private ArrayList<CandidateOntologyClass> candidateOntologies;
	private String selectedOntology;
	private int rankingNo;
	private double rewardValue;
	
	public IterationClass() {
		super();
		this.iterationNo = 0;
		this.userPreferences = new UserPreferencesModel();
		this.candidateOntologies = new ArrayList<CandidateOntologyClass>();
		this.selectedOntology = "";
		this.rankingNo=0;
		this.rewardValue=0;
	}
	
	public IterationClass(int iterationNo) {
		super();
		this.iterationNo = iterationNo;
	}
	
	public IterationClass(int iterationNo, UserPreferencesModel userPreferences, String inputClassName,
			ArrayList<CandidateOntologyClass> candidateOntologies, String selectedOntology) {
		super();
		this.iterationNo = iterationNo;
		this.userPreferences = userPreferences;
		this.inputClassName=inputClassName;
		this.candidateOntologies = candidateOntologies;
		this.selectedOntology = selectedOntology;
	}

	public int getIterationNo() {
		return iterationNo;
	}

	public void setIterationNo(int iterationNo) {
		this.iterationNo = iterationNo;
	}

	public UserPreferencesModel getUserPreferences() {
		return userPreferences;
	}

	public void setUserPreferences(UserPreferencesModel userPreferences) {
		this.userPreferences = userPreferences;
	}
	
	public String getInputClassName() {
		return inputClassName;
	}

	public void setInputClassName(String inputClassName) {
		this.inputClassName = inputClassName;
	}
	
	public ArrayList<CandidateOntologyClass> getCandidateOntologies() {
		return candidateOntologies;
	}

	public void setCandidateOntologies(ArrayList<CandidateOntologyClass> candidateOntologies) {
		this.candidateOntologies = candidateOntologies;
	}

	public String getSelectedOntology() {
		return selectedOntology;
	}

	public void setSelectedOntology(String selectedOntology) {
		this.selectedOntology = selectedOntology;
		setrankingNo();
	}
	
	public int getrankingNo() {
		return rankingNo;
	}

	public void setrankingNo() {
		for (int i=0; i<this.candidateOntologies.size(); i++) {
			if(this.candidateOntologies.get(i).getOntologyID().equals(this.selectedOntology))
				this.rankingNo = i+1;
		}
		setRewardValue();
	}

	public double getRewardValue() {
		return rewardValue;
	}

	public void setRewardValue() {
		this.rewardValue = 
				(this.candidateOntologies.size()-this.rankingNo+1.0)/this.candidateOntologies.size();
	}
	
	public void displayRewardValue() {
		System.out.println("The Reward Value= "+ this.rewardValue);
	}
	
	public void printMatchedClassesOfSelectedOntology(String ontologyID) {
		for(CandidateOntologyClass candidateOntology: this.candidateOntologies) {
			if(candidateOntology.getOntologyID().equals(ontologyID)) {
				System.out.println("Available concepts to reuse are: ");
				for(ConceptUtilityScoreClass concept: candidateOntology.getConceptUtilityScores()) {
					System.out.println("Concept: "+ concept.getMatchedConceptName());
				}				
			}	
		}
	}
	
	public void DispalyIteration() {
		System.out.println("The selected ontology is:  "+ this.selectedOntology);
		System.out.println("The ranking no. is:  "+ this.rankingNo);
	}
}


