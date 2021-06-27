package UserPreferences;

import java.util.ArrayList;

public class UserPreferencesModel {
	
	private String inputFileName;
	private ArrayList<String> userPrefDomain;
	private int userPrefOntologyPopularity;
	private int userPrefOntologyCoverage;
	private ArrayList<String> userPrefOntologies;
	private int userPrefOntologyType;
	
	public UserPreferencesModel(String inputFileName, ArrayList<String> userPrefDomain, int userPrefOntologyPopularity,
			int userPrefOntologyCoverage, ArrayList<String> userPrefOntologies, int userPrefOntologyType) {
		super();
		this.inputFileName=inputFileName;
		this.userPrefDomain = userPrefDomain;
		this.userPrefOntologyPopularity = userPrefOntologyPopularity;
		this.userPrefOntologyCoverage = userPrefOntologyCoverage;
		this.userPrefOntologies = userPrefOntologies;
		this.userPrefOntologyType = userPrefOntologyType;
	}
	
	public UserPreferencesModel() {
		// TODO Auto-generated constructor stub
	}

	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}
	public ArrayList<String> getUserPrefDomain() {
		return userPrefDomain;
	}
	public void setUserPrefDomain(ArrayList<String> userPrefDomain) {
		this.userPrefDomain = userPrefDomain;
	}
	public int getUserPrefOntologyPopularity() {
		return userPrefOntologyPopularity;
	}
	public void setUserPrefOntologyPopularity(int userPrefOntologyPopularity) {
		this.userPrefOntologyPopularity = userPrefOntologyPopularity;
	}
	public int getUserPrefOntologyCoverage() {
		return userPrefOntologyCoverage;
	}
	public void setUserPrefOntologyCoverage(int userPrefOntologyCoverage) {
		this.userPrefOntologyCoverage = userPrefOntologyCoverage;
	}
	public ArrayList<String> getUserPrefOntologies() {
		return userPrefOntologies;
	}
	public void setUserPrefOntologies(ArrayList<String> userPrefOntologies) {
		this.userPrefOntologies = userPrefOntologies;
	}
	public int getUserPrefOntologyType() {
		return userPrefOntologyType;
	}
	public void setUserPrefOntologyType(int userPrefOntologyType) {
		this.userPrefOntologyType = userPrefOntologyType;
	}
}
