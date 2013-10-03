package recognition;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import util.Direction;
import app.Config;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Observation;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscreteFactory;
import be.ac.ulg.montefiore.run.jahmm.draw.GenericHmmDrawerDot;
import be.ac.ulg.montefiore.run.jahmm.io.FileFormatException;
import be.ac.ulg.montefiore.run.jahmm.io.OpdfReader;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;

public class HMM {

	public String getModel_name() {
		return model_name;
	}


	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}


	// attributes
	public Hmm<ObservationDiscrete<Direction>> model;
	List<List<ObservationDiscrete<Direction>>> sequences = new ArrayList<List<ObservationDiscrete<Direction>>>();
	private String model_name;
	
	public HMM(String model_name){
		this.model_name = model_name;
	}

	
	public void addLearningSequence(List<ObservationDiscrete<Direction>> learningSequence) 
	throws java.io.IOException
	{	
		
		sequences.add(learningSequence);
	}

	public void learnModel(){
		
		/* Baum-Welch learning */
		
		BaumWelchLearner bwl = new BaumWelchLearner();
		
		
		// This object measures the distance between two HMMs
		KullbackLeiblerDistanceCalculator klc = 
			new KullbackLeiblerDistanceCalculator();
		for (int i = 0; i < 10; i++) {
			model = bwl.iterate(model, sequences);
		}
		
		System.out.println("Resulting HMM:\n" + model);
		
		/* Computing the probability of a sequence */
		
		
		/* Write the final result to a 'dot' (graphviz) file. */
		
		try {
			(new GenericHmmDrawerDot()).write(model, "learntHmm.dot");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public double checkSequence(List<ObservationDiscrete<Direction>> testSequence) 
	throws java.io.IOException
	{	
		double probability=model.probability(testSequence);
		//System.out.println("Probablitiy to model "+model_name+": "+probability);
		return probability;
	}
	
	/* The HMM this example is based on */
	
/*	static Hmm<ObservationDiscrete<Direction>> buildHmm()
	{	
		Hmm<ObservationDiscrete<Direction>> hmm = 
			new Hmm<ObservationDiscrete<Direction>>(2,
					new OpdfDiscreteFactory<Direction>(Direction.class));
		
		hmm.setPi(0, 1);
		hmm.setPi(1, 0);
		
		hmm.setOpdf(0, new OpdfDiscrete<Direction>(Direction.class, 
				new double[] { 0.95, 0.05 }));
		hmm.setOpdf(1, new OpdfDiscrete<Direction>(Direction.class,
				new double[] { 0.20, 0.80 }));
		
		hmm.setAij(0, 1, 0.05);
		hmm.setAij(0, 0, 0.95);
		hmm.setAij(1, 0, 0.10);
		hmm.setAij(1, 1, 0.90);
		
		return hmm;
	}*/
	
/*	public static Hmm<ObservationDiscrete<Direction>> buildHmm3(String sign)
	{	
		Hmm<ObservationDiscrete<Direction>> hmm = 
			new Hmm<ObservationDiscrete<Direction>>(3,
					new OpdfDiscreteFactory<Direction>(Direction.class));
		
		hmm.setPi(0, 1);
		hmm.setPi(1, 0);
		hmm.setPi(2, 0);
		
		// legend: O,N,NE,E,SE,S,SW,W,NW;
		
		if( sign.equalsIgnoreCase("N") ){
		
		hmm.setOpdf(0, new OpdfDiscrete<Direction>(Direction.class,
				new double[] { 0.04, 0.44 , 0.16 , 0.04 , 0.04 , 0.04 , 0.04 , 0.04, 0.16}));
		hmm.setOpdf(1, new OpdfDiscrete<Direction>(Direction.class,
				new double[] { 0.04, 0.04 , 0.16 , 0.44 , 0.16 , 0.04 , 0.04 , 0.04, 0.04 }));
		hmm.setOpdf(2, new OpdfDiscrete<Direction>(Direction.class,
				new double[] { 0.04, 0.44 , 0.16 , 0.04 , 0.04 , 0.04 , 0.04 , 0.04, 0.16}));
		} else if( sign.equalsIgnoreCase("L") ){
		
			hmm.setOpdf(0, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { 0.04, 0.04 , 0.04 , 0.04 , 0.16 , 0.44 , 0.16 , 0.04, 0.04}));
			hmm.setOpdf(1, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { 0.04, 0.04 , 0.16 , 0.44 , 0.16 , 0.04 , 0.04 , 0.04, 0.04 }));
		}
		else if( sign.equalsIgnoreCase("V") ){
			
			hmm.setOpdf(0, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { 0.04, 0.04 , 0.04 , 0.16 , 0.44 , 0.16 , 0.04 , 0.04, 0.04}));
			hmm.setOpdf(1, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { 0.04, 0.16 , 0.44 , 0.16 , 0.04 , 0.04 , 0.04 , 0.04, 0.04 }));
		}
		hmm.setAij(0, 2, 0.0);
		hmm.setAij(0, 1, 0.05);
		hmm.setAij(0, 0, 0.95);
		hmm.setAij(1, 0, 0.0);
		hmm.setAij(1, 1, 0.95);
		hmm.setAij(1, 2, 0.05);
		hmm.setAij(2, 0, 0.0);
		hmm.setAij(2, 1, 0.0);
		hmm.setAij(2, 2, 1.0);
		
		return hmm;
	}*/
	
	public static Hmm<ObservationDiscrete<Direction>> build3DHmm(String sign)
	{	
		Hmm<ObservationDiscrete<Direction>> hmm = 
			new Hmm<ObservationDiscrete<Direction>>(2,
					new OpdfDiscreteFactory<Direction>(Direction.class));
		
		hmm.setPi(0, 1);
		hmm.setPi(1, 0);
		
		final double D = 0.20; // x 1
		final double S = 0.08; // x 8
		final double W = 0.009;// x 17
		final double O = 0.009;// x 1
		
		// O,N,NE,E,SE,S,SW,W,NW,CN,CNE,CE,CSE,CS,CSW,CW,CNW,FN,FNE,FE,FSE,FS,FSW,FW,FNW,C,F;
		
		if( sign.equalsIgnoreCase("back_L") ){
		
		hmm.setOpdf(0, new OpdfDiscrete<Direction>(Direction.class,
				new double[] { O, // O
								//N,NE, E, SE, S,  SW,  W, NW
								W , W , W , S , D , S , W, W,
								//CN,CNE,CE,CSE,CS,CSW,CW,CNW
								W , W , W , S , S , S , W, W,
								//FN,FNE,FE,FSE,FS,FSW,FW,FNW
								W , W , W , S , S , S , W, W,  
								W , W // C,F
								}));
		hmm.setOpdf(1, new OpdfDiscrete<Direction>(Direction.class,
				new double[] { 1, // O
								//N,NE, E, SE, S,  SW,  W, NW
								W , W , W , W , W , S , D, S,
								//CN,CNE,CE,CSE,CS,CSW,CW,CNW
								W , W , W , W , W , S , S, S,
								//FN,FNE,FE,FSE,FS,FSW,FW,FNW
								W , W , W , W , W , S , S, S,  
								W , W // C,F
								}));
		
		} else if( sign.equalsIgnoreCase("<") ){
		
			hmm.setOpdf(0, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { O, // O
									//N,NE, E, SE, S,  SW,  W, NW
									W , W , W , W , S , D , S, W,
									//CN,CNE,CE,CSE,CS,CSW,CW,CNW
									W , W , W , W , S , S , S, W,
									//FN,FNE,FE,FSE,FS,FSW,FW,FNW
									W , W , W , W , S , S , S, W,  
									W , W // C,F
									}));
			hmm.setOpdf(1, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { 1, // O
									//N,NE, E, SE, S,  SW,  W, NW
									W , W , S , D , S , W , W, W,
									//CN,CNE,CE,CSE,CS,CSW,CW,CNW
									W , W , S , S , S , W , W, W,
									//FN,FNE,FE,FSE,FS,FSW,FW,FNW
									W , W , S , S , S , W , W, W,  
									W , W // C,F
									}));
		}
		else if( sign.equalsIgnoreCase("V") ){
			
			hmm.setOpdf(0, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { 0.04, 0.04 , 0.04 , 0.16 , 0.44 , 0.16 , 0.04 , 0.04, 0.04}));
			hmm.setOpdf(1, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { 0.04, 0.16 , 0.44 , 0.16 , 0.04 , 0.04 , 0.04 , 0.04, 0.04 }));
		}
		else if( sign.equalsIgnoreCase("backF") ){
			
			hmm.setOpdf(0, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { 0.04, 0.44 , 0.16 , 0.04 , 0.04 , 0.04 , 0.04 , 0.04, 0.16}));
			hmm.setOpdf(1, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { 0.04, 0.04 , 0.04 , 0.04 , 0.04 , 0.04 , 0.16 , 0.44, 0.16 }));
		}
		else if( sign.equalsIgnoreCase("backC") ){
						// legend: O,N,NE,E,SE,S,SW,W,NW;
			hmm.setOpdf(0, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { 0.04, 0.16 , 0.44 , 0.16 , 0.04 , 0.04 , 0.04 , 0.04, 0.04 }));
			hmm.setOpdf(1, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { 0.04, 0.16 , 0.04 ,0.04 , 0.04, 0.04,  0.04 , 0.16 , 0.44  }));
		}
		else if( sign.equalsIgnoreCase("distr") ){
			// legend: O,N,NE,E,SE,S,SW,W,NW; NE-W - back F distractor
			hmm.setOpdf(0, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { 0.04, 0.16 , 0.44 , 0.16 , 0.04 , 0.04 , 0.04 , 0.04, 0.04 }));
			hmm.setOpdf(1, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { 0.04, 0.04 , 0.04 ,0.04 , 0.04, 0.04,  0.16 , 0.44 , 0.16  }));
		}
		hmm.setAij(0, 1, 0.2);
		hmm.setAij(0, 0, 0.8);
		hmm.setAij(1, 0, 0.0);
		hmm.setAij(1, 1, 1);
		
		return hmm;
	}
	
	/* Initial guess for the Baum-Welch algorithm */
	public Hmm<ObservationDiscrete<Direction>> buildInitHmm(int states)
	{	
		
		Hmm<ObservationDiscrete<Direction>> hmm = 
			new Hmm<ObservationDiscrete<Direction>>(states,
					new OpdfDiscreteFactory<Direction>(Direction.class));
		
		
		// set 100% probability of starting in 1st state and 0% in others
		hmm.setPi(0, 1);
		for (int i = 1; i < states; i++) {
			hmm.setPi(i, 0);
			
		}
		
		// set minimal probability for less probable features
		/*double M = Config.MIN_FEATURE_PROB;
		for (int i = 0; i < states; i++) {
			hmm.setOpdf(i, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { M, // O
						//N,NE, E, SE, S,  SW,  W, NW
						M , M , M , M , M , M , M, M,
						//CN,CNE,CE,CSE,CS,CSW,CW,CNW
						M , M , M , M , M , M , M, M,
						//FN,FNE,FE,FSE,FS,FSW,FW,FNW
						M , M , M , M , M , M , M, M,  
						M , M // C,F
						}));
		}*/
		
		/*hmm.setPi(0, 1);
		hmm.setPi(1, 0);
		hmm.setOpdf(0, new OpdfDiscrete<Direction>(Direction.class,
				new double[] { 0.04, 0.12 , 0.12 , 0.12 , 0.12 , 0.12 , 0.12 , 0.12, 0.12}));
		hmm.setOpdf(1, new OpdfDiscrete<Direction>(Direction.class,
				new double[] { 0.04, 0.12 , 0.12 , 0.12 , 0.12 , 0.12 , 0.12 , 0.12, 0.12}));
		
		hmm.setAij(0, 1, 0.2);
		hmm.setAij(0, 0, 0.8);
		hmm.setAij(1, 0, 0);
		hmm.setAij(1, 1, 1);*/
		
		
		
		
		
		//System.out.println(opdf.probability(new ObservationDiscrete<Direction>(Direction.values()[3])));
		
		
		//System.out.println(Direction.values()[3]);
		//System.out.println(hmm);
		return hmm;
	}
	
	/*void tuneHmm(Hmm<ObservationDiscrete<Direction>> hmm){
		
		// set minimal probability for less probable features
		double M = Config.MIN_FEATURE_PROB;
		for (int i = 0; i < states; i++) {
			
			
			hmm.setOpdf(i, new OpdfDiscrete<Direction>(Direction.class,
					new double[] { M, // O
						//N,NE, E, SE, S,  SW,  W, NW
						M , M , M , M , M , M , M, M,
						//CN,CNE,CE,CSE,CS,CSW,CW,CNW
						M , M , M , M , M , M , M, M,
						//FN,FNE,FE,FSE,FS,FSW,FW,FNW
						M , M , M , M , M , M , M, M,  
						M , M // C,F
						}));
			
			Opdf<ObservationDiscrete<Direction>> stateOpdf = hmm.getOpdf(i);

			
		}
	}*/
	
	static void setMinimalOPDF(Hmm<ObservationDiscrete<Direction>> hmm, int states){
		
		
		Opdf<ObservationDiscrete<Direction>> opdf;// = hmm.getOpdf(0);
		Opdf<ObservationDiscrete<Direction>> newOpdf;
		double[] opdfValues = new double[Direction.values().length];
		
		// set minimal probability of every emission
		for (int i = 0; i < states; i++) {
			
			opdf = hmm.getOpdf(i);
					
			for (int j = 0; j < Direction.values().length; j++) {
				opdfValues[j] = opdf.probability(new ObservationDiscrete<Direction>(Direction.values()[j]))
									+ Config.MINIMAL_OPDF_VALUE;
			}
		
			newOpdf = new OpdfDiscrete<Direction>(Direction.class,
					opdfValues);
			hmm.setOpdf(i, newOpdf);
		}
	}
	
}
