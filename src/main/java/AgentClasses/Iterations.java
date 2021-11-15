package AgentClasses;

import java.util.ArrayList;

public class Iterations {
	private ArrayList<IterationClass> iterations;

	public Iterations() {
		super();
	}

	public Iterations(ArrayList<IterationClass> iterations) {
		super();
		this.iterations=iterations;
	}

	public ArrayList<IterationClass> getIterations() {
		return iterations;
	}

	public void setIterations(ArrayList<IterationClass> iterations) {
		this.iterations = iterations;
	}

}
