package AgentClasses;

public class OntologyUtilityScoreClass {
	private double ontologyDomainScore;
	private double ontologyPopularityScore;
	private double ontologyCoverageScore;
	private double prefOntologyScore;
	private double ontologyTypeScore;
	private double ontologyTotalUtilityScore;
	
	public OntologyUtilityScoreClass() {
		super();
		this.ontologyDomainScore = 0;
		this.ontologyPopularityScore = 0;
		this.ontologyCoverageScore = 0;
		this.prefOntologyScore = 0;
		this.ontologyTypeScore = 0;
		this.ontologyTotalUtilityScore = 0;
	}
	
	public OntologyUtilityScoreClass(double ontologyDomainScore, double ontologyPopularityScore,
			double ontologyCoverageScore, double prefOntologyScore, double ontologyTypeScore,
			double ontologyUtilityScore) {
		super();
		this.ontologyDomainScore = ontologyDomainScore;
		this.ontologyPopularityScore = ontologyPopularityScore;
		this.ontologyCoverageScore = ontologyCoverageScore;
		this.prefOntologyScore = prefOntologyScore;
		this.ontologyTypeScore = ontologyTypeScore;
		this.ontologyTotalUtilityScore = ontologyUtilityScore;
	}

	public double getOntologyDomainScore() {
		return ontologyDomainScore;
	}

	public void setOntologyDomainScore(double ontologyDomainScore) {
		this.ontologyDomainScore = ontologyDomainScore;
	}

	public double getOntologyPopularityScore() {
		return ontologyPopularityScore;
	}

	public void setOntologyPopularityScore(double ontologyPopularityScore) {
		this.ontologyPopularityScore = ontologyPopularityScore;
	}

	public double getOntologyCoverageScore() {
		return ontologyCoverageScore;
	}

	public void setOntologyCoverageScore(double ontologyCoverageScore) {
		this.ontologyCoverageScore = ontologyCoverageScore;
	}

	public double getPrefOntologyScore() {
		return prefOntologyScore;
	}

	public void setPrefOntologyScore(double prefOntologyScore) {
		this.prefOntologyScore = prefOntologyScore;
	}

	public double getOntologyTypeScore() {
		return ontologyTypeScore;
	}

	public void setOntologyTypeScore(double ontologyTypeScore) {
		this.ontologyTypeScore = ontologyTypeScore;
	}

	public double getOntologyTotalUtilityScore() {
		return ontologyTotalUtilityScore;
	}

	public void setOntologyTotalUtilityScore(double ontologyUtilityScore) {
		this.ontologyTotalUtilityScore = ontologyUtilityScore;
	}
}
