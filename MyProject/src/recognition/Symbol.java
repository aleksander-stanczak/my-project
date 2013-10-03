package recognition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.Direction;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;

public class Symbol {
	
	Hmm<ObservationDiscrete<Direction>> model;
	public String name;
	
	
	
	public Symbol(Hmm<ObservationDiscrete<Direction>> model, String name) {
		super();
		this.model = model;
		this.name = name;
	}


	public double findProbability(List<ObservationDiscrete<Direction>> featureVector){
				
		return model.probability(featureVector);
		
	}

	@Override
	public String toString() {
		
		return "{"+name+"}";
	}
	
	

}
