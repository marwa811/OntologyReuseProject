package AgentClasses;

import java.util.ArrayList;
import java.util.Comparator;

public class FinalResultList {
	private ArrayList<CandidateOntologyClass> finalCandidateOntologyList;
	private ArrayList<Double> rewardScore;
	private ArrayList<String> selectedOntology;
	
	public FinalResultList(ArrayList<CandidateOntologyClass> finalCandidateOntologyList,
			ArrayList<Double> rewardScore, ArrayList<String> selectedOntology) {
		super();
		this.finalCandidateOntologyList = finalCandidateOntologyList;
		this.rewardScore = rewardScore;
		this.selectedOntology = selectedOntology;
	}

	public FinalResultList() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<CandidateOntologyClass> getFinalCandidateOntologyList() {
		return finalCandidateOntologyList;
	}

	public void setFinalCandidateOntologyList(ArrayList<CandidateOntologyClass> finalCandidateOntologyList) {
		this.finalCandidateOntologyList = finalCandidateOntologyList;
	}

	public ArrayList<Double> getRewardScore() {
		return rewardScore;
	}

	public void setRewardScore(ArrayList<Double> rewardScore) {
		this.rewardScore = rewardScore;
	}

	public ArrayList<String> getSelectedOntology() {
		return selectedOntology;
	}

	public void setSelectedOntology(ArrayList<String> selectedOntology) {
		this.selectedOntology = selectedOntology;
	}
	
}
