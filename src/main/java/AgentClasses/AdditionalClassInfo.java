package AgentClasses;

import java.util.ArrayList;

public class AdditionalClassInfo {
	private String conceptName;
	private String conceptLabel;
	private ArrayList<String> conceptSynonyms;
	private ArrayList<String> conceptDef;
	private ArrayList<String> conceptSubClasses;
	private ArrayList<String> conceptSubClassesIds;
	private boolean exists;
	
	public AdditionalClassInfo(String conceptName, String conceptLabel, ArrayList<String> conceptSynonyms,
			ArrayList<String> conceptDef, ArrayList<String> conceptSubClasses,
			ArrayList<String> conceptSubClassesIds ,boolean exists) {
		super();
		this.conceptName = conceptName;
		this.conceptLabel = conceptLabel;
		this.conceptSynonyms = conceptSynonyms;
		this.conceptDef = conceptDef;
		this.conceptSubClasses = conceptSubClasses;
		this.conceptSubClassesIds=conceptSubClassesIds;
		this.exists = exists;
	}
	
	public AdditionalClassInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getConceptName() {
		return conceptName;
	}

	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}

	public String getConceptLabel() {
		return conceptLabel;
	}

	public void setConceptLabel(String conceptLabel) {
		this.conceptLabel = conceptLabel;
	}

	public ArrayList<String> getConceptSynonyms() {
		return conceptSynonyms;
	}

	public void setConceptSynonyms(ArrayList<String> conceptSynonyms) {
		this.conceptSynonyms = conceptSynonyms;
	}

	public ArrayList<String> getConceptDef() {
		return conceptDef;
	}

	public void setConceptDef(ArrayList<String> conceptDef) {
		this.conceptDef = conceptDef;
	}

	public ArrayList<String> getConceptSubClasses() {
		return conceptSubClasses;
	}

	public void setConceptSubClasses(ArrayList<String> conceptSubClasses) {
		this.conceptSubClasses = conceptSubClasses;
	}
	
	public ArrayList<String> getConceptSubClassesIds() {
		return conceptSubClassesIds;
	}

	public void setConceptSubClassesIds(ArrayList<String> conceptSubClassesIds) {
		this.conceptSubClassesIds = conceptSubClassesIds;
	}

	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}	
}
