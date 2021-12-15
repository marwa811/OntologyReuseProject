package AgentClasses;

import java.util.ArrayList;

public class ConceptUtilityScoreClass{
	private String matchedConceptName;
	private double conceptContextMatchingScore;
	private double conceptSemanticRichnessScore;
	private double conceptUtilityScore;
	private String matchedConceptLabel;
	private ArrayList<String> matchedConceptSynonyms;
	private ArrayList<String> matchedConceptDef;
	private ArrayList<String> matchedConceptSubClasses;
	private ArrayList<String> matchedConceptSubClassesIds;

	public ConceptUtilityScoreClass() {
		super();
		this.matchedConceptName="";
		this.conceptContextMatchingScore = 0;
		this.conceptSemanticRichnessScore = 0;
		this.conceptUtilityScore = 0;
		this.matchedConceptLabel="";
	}
	
	public ConceptUtilityScoreClass(String matchedConceptName, double conceptContextMatchingScore, double conceptSemanticRichnessScore,
			double conceptUtilityScore) {
		super();
		this.matchedConceptName= matchedConceptName;
		this.conceptContextMatchingScore = conceptContextMatchingScore;
		this.conceptSemanticRichnessScore = conceptSemanticRichnessScore;
		this.conceptUtilityScore = conceptUtilityScore;
	}
	

	public ConceptUtilityScoreClass(String matchedConceptName, double conceptContextMatchingScore,
			double conceptSemanticRichnessScore, double conceptUtilityScore, String matchedConceptLabel,
			ArrayList<String> matchedConceptSynonyms, ArrayList<String> matchedConceptDef,
			ArrayList<String> matchedConceptSubClasses, ArrayList<String> matchedConceptSubClassesIds) {
		super();
		this.matchedConceptName = matchedConceptName;
		this.conceptContextMatchingScore = conceptContextMatchingScore;
		this.conceptSemanticRichnessScore = conceptSemanticRichnessScore;
		this.conceptUtilityScore = conceptUtilityScore;
		this.matchedConceptLabel = matchedConceptLabel;
		this.matchedConceptSynonyms = matchedConceptSynonyms;
		this.matchedConceptDef = matchedConceptDef;
		this.matchedConceptSubClasses = matchedConceptSubClasses;
		this.matchedConceptSubClassesIds=matchedConceptSubClassesIds;
	}

	public String getMatchedConceptName() {
		return matchedConceptName;
	}

	public void setMatchedConceptName(String matchedConceptName) {
		this.matchedConceptName = matchedConceptName;
	}

	public double getConceptContextMatchingScore() {
		return conceptContextMatchingScore;
	}

	public void setConceptContextMatchingScore(double conceptContextMatchingScore) {
		this.conceptContextMatchingScore = conceptContextMatchingScore;
	}

	public double getConceptSemanticRichnessScore() {
		return conceptSemanticRichnessScore;
	}

	public void setConceptSemanticRichnessScore(double conceptSemanticRichnessScore) {
		this.conceptSemanticRichnessScore = conceptSemanticRichnessScore;
	}

	public double getConceptUtilityScore() {
		return conceptUtilityScore;
	}

	public void setConceptUtilityScore(double conceptUtilityScore) {
		this.conceptUtilityScore = conceptUtilityScore;
	}
	
	public String getMatchedConceptLabel() {
		return matchedConceptLabel;
	}

	public void setMatchedConceptLabel(String matchedConceptLabel) {
		this.matchedConceptLabel = matchedConceptLabel;
	}

	public ArrayList<String> getMatchedConceptSynonyms() {
		return matchedConceptSynonyms;
	}

	public void setMatchedConceptSynonyms(ArrayList<String> matchedConceptSynonyms) {
		this.matchedConceptSynonyms = matchedConceptSynonyms;
	}

	public ArrayList<String> getMatchedConceptSubClasses() {
		return matchedConceptSubClasses;
	}

	public void setMatchedConceptSubClasses(ArrayList<String> matchedConceptSubClasses) {
		this.matchedConceptSubClasses = matchedConceptSubClasses;
	}

	public void setMatchedConceptDef(ArrayList<String> matchedConceptDef) {
		this.matchedConceptDef = matchedConceptDef;
	}
	
	public ArrayList<String> getMatchedConceptDef() {
		return matchedConceptDef;
	}
	
	public ArrayList<String> getMatchedConceptSubClassesIds() {
		return matchedConceptSubClassesIds;
	}

	public void setMatchedConceptSubClassesIds(ArrayList<String> matchedConceptSubClassesIds) {
		this.matchedConceptSubClassesIds = matchedConceptSubClassesIds;
	}

}
