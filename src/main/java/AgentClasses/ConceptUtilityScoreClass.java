package AgentClasses;

public class ConceptUtilityScoreClass{
	private String matchedConceptName;
	private double conceptContextMatchingScore;
	private double conceptSemanticRichnessScore;
	private double conceptUtilityScore;
	
	public ConceptUtilityScoreClass() {
		super();
		this.matchedConceptName="";
		this.conceptContextMatchingScore = 0;
		this.conceptSemanticRichnessScore = 0;
		this.conceptUtilityScore = 0;
	}
	
	public ConceptUtilityScoreClass(String matchedConceptName, double conceptContextMatchingScore, double conceptSemanticRichnessScore,
			double conceptUtilityScore) {
		super();
		this.matchedConceptName= matchedConceptName;
		this.conceptContextMatchingScore = conceptContextMatchingScore;
		this.conceptSemanticRichnessScore = conceptSemanticRichnessScore;
		this.conceptUtilityScore = conceptUtilityScore;
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
}
