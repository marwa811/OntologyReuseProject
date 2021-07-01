package OntologyMatchingPackage;

//A class for AML Mappings//

public class AMLMapping {
	private int mappingId;
	private String sourceURI;
	private String sourceName;
	private String targetURI;
	private String targetName;
	private double similarityScore;
	
	public AMLMapping() 
	{}
	
	public AMLMapping(int mappingId, String sourceURI, String sourceName, String targetURI, 
			String targetName, double similarityScore) {
		this.mappingId=mappingId;
		this.sourceURI=sourceURI;
		this.sourceName=sourceName;
		this.targetURI=targetURI;
		this.targetName=targetName;
		this.similarityScore=similarityScore;
	}

	public int getMappingId() {
		return mappingId;
	}
	

	public void setMappingId(int mappingId) {
		this.mappingId = mappingId;
	}

	public String getSourceURI() {
		return sourceURI;
	}

	public void setSourceURI(String sourceURI) {
		this.sourceURI = sourceURI;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getTargetURI() {
		return targetURI;
	}

	public void setTargetURI(String targetURI) {
		this.targetURI = targetURI;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public double getSimilarityScore() {
		return similarityScore;
	}

	public void setSimilarityScore(String similarityScoreString) {
		this.similarityScore=Double.parseDouble(similarityScoreString);
	}
	
	public void dispalyMApping(AMLMapping mapping) {
		System.out.println("This mapping: "+mapping.getMappingId()+"  "+mapping.getSourceName()+"  "+
				mapping.getSourceURI()+"  "+mapping.getTargetName()+"  "+
				mapping.getTargetURI()+"  "+mapping.getSimilarityScore());
	}
}
