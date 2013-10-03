package recognition;

import util.Direction;
import app.Config;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscreteFactory;

import java.io.IOException;
import java.util.*;

import be.ac.ulg.montefiore.run.jahmm.*;
import be.ac.ulg.montefiore.run.jahmm.draw.GenericHmmDrawerDot;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;


public class GestureRecognizer
{	
	
	// attributes
	Hmm<ObservationDiscrete<Direction>> model;
	List<List<ObservationDiscrete<Direction>>> sequences;
	ArrayList<Hmm<ObservationDiscrete<Direction>>> model_list = new ArrayList<Hmm<ObservationDiscrete<Direction>>>();
	ArrayList<String> model_list_names = new ArrayList<String>();
	
	ArrayList<Symbol> symbol_list = new ArrayList<Symbol>();
	

/*	static public void test() 
			throws java.io.IOException
		{	
			 Build a HMM and generate observation sequences using this HMM 
			
			Hmm<ObservationDiscrete<Direction>> hmm = buildHmm();
			
			List<List<ObservationDiscrete<Direction>>> sequences;
			sequences = generateSequences(hmm);
			
			 Baum-Welch learning 
			
			BaumWelchLearner bwl = new BaumWelchLearner();
			
			Hmm<ObservationDiscrete<Direction>> learntHmm = new HMM("Test").buildInitHmm();
			
			// This object measures the distance between two HMMs
			KullbackLeiblerDistanceCalculator klc = 
				new KullbackLeiblerDistanceCalculator();
			
			// Incrementally improve the solution
			for (int i = 0; i < 10; i++) {
				System.out.println("Distance at iteration " + i + ": " +
						klc.distance(learntHmm, hmm));
				learntHmm = bwl.iterate(learntHmm, sequences);
			}
			
			System.out.println("Resulting HMM:\n" + learntHmm);
			
			 Computing the probability of a sequence 
			
			ObservationDiscrete<Direction> packetOk = Direction.O.observation();
			ObservationDiscrete<Direction> packetLoss = Direction.SE.observation();
			
			List<ObservationDiscrete<Direction>> testSequence = 
				new ArrayList<ObservationDiscrete<Direction>>(); 
			testSequence.add(packetOk);
			testSequence.add(packetOk);
			testSequence.add(packetLoss);
			
			System.out.println("Sequence probability: " +
					learntHmm.probability(testSequence));
			
			 Write the final result to a 'dot' (graphviz) file. 
			
			(new GenericHmmDrawerDot()).write(learntHmm, "learntHmm.dot");
		}*/
	
	public GestureRecognizer() {
		
		// initialize build-in examples
		symbol_list.add(new Symbol(HMM.build3DHmm("<"),"<"));
		symbol_list.add(new Symbol(HMM.build3DHmm("back_L"),"back_L"));
		
		
	}
	
	public Symbol findMatchingSymbol(List<ObservationDiscrete<Direction>> featureVector){
		
		double maxProb = 0;
		Symbol recognizedSymbol  = null;
		
		for (Symbol symbol : symbol_list) {
			double prob = symbol.findProbability(featureVector);
			System.out.println(symbol.name);
			System.out.println(prob);
			if ( prob > maxProb && prob > Config.MIN_SYMBOL_PROB){
				recognizedSymbol = symbol;
				maxProb = prob;
			}
		}
		
		return recognizedSymbol;
	}
	
	public String writeFoundSymbol(List<ObservationDiscrete<Direction>> featureVector){
		
		double maxProb = 0;
		Symbol recognizedSymbol = null;
		
		for (Symbol symbol : symbol_list) {
			double prob = symbol.findProbability(featureVector);
			if ( prob > maxProb && prob > Config.MIN_SYMBOL_PROB){
				recognizedSymbol = symbol;
				maxProb = prob;
			}
		}
		
		if (recognizedSymbol != null)
			return recognizedSymbol+" with probability "+maxProb;
		else
			return "Symbol not fund";
	}
	
	public List<ObservationDiscrete<Direction>> generateTestSequence(){
		
		ObservationDiscrete<Direction> packetOk = Direction.W.observation();
		ObservationDiscrete<Direction> packetLoss = Direction.SE.observation();
		
		List<ObservationDiscrete<Direction>> testSequence = 
			new ArrayList<ObservationDiscrete<Direction>>(); 
		testSequence.add(Direction.N.observation());
		testSequence.add(Direction.E.observation());
		
		return testSequence;
	}
	
	private void initializeTestSequences(){
		
		sequences = 
				new ArrayList<List<ObservationDiscrete<Direction>>>(
						Arrays.asList(
								
				new ArrayList<ObservationDiscrete<Direction>>(
			    Arrays.asList(Direction.CSE.observation(), Direction.CSE.observation(), Direction.CSE.observation())),
			    
			    new ArrayList<ObservationDiscrete<Direction>>(
					    Arrays.asList(Direction.SE.observation(), Direction.CSE.observation(), Direction.SE.observation()))
			    ))
			    ;
	}
	
	public boolean newGesture(int states, List<List<ObservationDiscrete<Direction>>> sequences, String name){
		
		System.out.println(sequences);
		
		//Baum-Welch learning 
			
		BaumWelchLearner bwl = new BaumWelchLearner();
		
		Hmm<ObservationDiscrete<Direction>> learntHmm = new HMM("Test").buildInitHmm(states);
		
		// This object measures the distance between two HMMs
		KullbackLeiblerDistanceCalculator klc = 
			new KullbackLeiblerDistanceCalculator();
			
		// Incrementally improve the solution
		for (int i = 0; i < 10; i++) {
			/*System.out.println("Distance at iteration " + i + ": " +
					klc.distance(learntHmm, hmm));*/
			learntHmm = bwl.iterate(learntHmm, sequences);
		}
		
		// set minimal OPDF
		HMM.setMinimalOPDF(learntHmm, states);
		
		symbol_list.add(new Symbol(learntHmm, name));
		
		//System.out.println("Resulting HMM:\n" + learntHmm);
		
		
		
		return true;
	}
	
	public static void main(String[] args) {
		
		GestureRecognizer gr = new GestureRecognizer();
		gr.initializeTestSequences();
		System.out.println(gr.generateTestSequence());
		gr.sequences = new ArrayList<List<ObservationDiscrete<Direction>>>();
		gr.sequences.add(gr.generateTestSequence());
		gr.newGesture(2, gr.sequences, "test");
		
		//Symbol sym = gr.findMatchingSymbol(gr.generateTestSequence());
		//System.out.println(sym+" with probability "+sym.findProbability(gr.generateTestSequence()));
		
		//gr.newGesture(3, null);
		
		System.out.println("Finish");
		
	}
}

