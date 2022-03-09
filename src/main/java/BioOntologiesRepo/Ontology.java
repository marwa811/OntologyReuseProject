package BioOntologiesRepo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Ontology {
	
	private String name;
	private String id;
	private String acronym;
	private String description;
	private String[] categories;
	private int veiws;
	private int projects;
	private int objPropertyNo;
	private int classNo;
	private ArrayList<BioClass> classes;
	
	//ontology class constructor
	public Ontology(String name, String id, String acronym, String description ,String[] categories, int veiws, int projects, 
			int objPropertyNo, int classNo) {
		super();
		this.name = name;
		this.id = id;
		this.acronym = acronym;
		this.description= description;
		this.categories = categories;
		this.veiws = veiws;
		this.projects=projects;
		this.objPropertyNo = objPropertyNo;
		this.classNo = classNo;
		
	}
	
	//ontology class constructor
		public Ontology(String name, String id, String acronym, String description ,String[] categories, int veiws, int projects, 
				int objPropertyNo, int classNo, ArrayList<BioClass> classes) {
			super();
			this.name = name;
			this.id = id;
			this.acronym = acronym;
			this.description= description;
			this.categories = categories;
			this.veiws = veiws;
			this.projects=projects;
			this.objPropertyNo = objPropertyNo;
			this.classNo = classNo;
			this.classes=classes;
		}
	
	public Ontology() 
	{}
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAcronym() {
		return acronym;
	}
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}
	
	public String[] getCategories() {
		return categories;
	}
	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	public int getVeiws() {
		return veiws;
	}
	public void setVeiws(int veiws) {
		this.veiws = veiws;
	}
	public int getProjects() {
		return projects;
	}
	public void setProjects(int projects) {
		this.projects = projects;
	}
	public int getObjPropertyNo() {
		return objPropertyNo;
	}
	public void setObjPropertyNo(int objPropertyNo) {
		this.objPropertyNo = objPropertyNo;
	}
	public int getClassNo() {
		return classNo;
	}
	public void setClassNo(int classNo) {
		this.classNo = classNo;
	}
	public ArrayList<BioClass> getClasses() {
		return classes;
	}

	public void setClasses(ArrayList<BioClass> classes) {
		this.classes = classes;
	}

}
