package BioOntologiesRepo;

import java.util.ArrayList;

public class BioClass {
	private String id;
	private String name;
	private ArrayList<String> synonyms;
	private ArrayList<String> definations;
	private ArrayList<String> subclasses;
	private ArrayList<String> subclassesId;
	
	public BioClass(String id, String name, ArrayList<String> synonyms, ArrayList<String> definations,
			ArrayList<String> subclasses, ArrayList<String> subclassesId) {
		super();
		this.id = id;
		this.name = name;
		this.synonyms = synonyms;
		this.definations = definations;
		this.subclasses = subclasses;
		this.subclassesId = subclassesId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(ArrayList<String> synonyms) {
		this.synonyms = synonyms;
	}

	public ArrayList<String> getDefinations() {
		return definations;
	}

	public void setDefinations(ArrayList<String> definations) {
		this.definations = definations;
	}

	public ArrayList<String> getSubclasses() {
		return subclasses;
	}

	public void setSubclasses(ArrayList<String> subclasses) {
		this.subclasses = subclasses;
	}

	public ArrayList<String> getSubclassesId() {
		return subclassesId;
	}

	public void setSubclassesId(ArrayList<String> subclassesId) {
		this.subclassesId = subclassesId;
	}
	
}
