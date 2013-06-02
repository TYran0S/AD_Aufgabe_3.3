/**
 * 
 */
package aufgabe3_1;

import java.util.ArrayList;
import java.util.List;
//import GUI.*;

/**
 * 
 *
 */
public class ControlUnit implements Controller {

    private Simulation SimAnt;
    final private View VIEW;
    
    
	public ControlUnit(View gui, int anzahlCities, String file){
		//uebergabe der GUI!
		VIEW = gui;
		SimAnt=new Simulation(anzahlCities, file);
	}


	public ControlUnit(View gui, int anzahlCities, List<Connection> con, List<Node> nod, List<List<Integer>> nodePackage){
		//uebergabe der GUI!
		VIEW = gui;
		SimAnt=new Simulation(anzahlCities,con, nod, nodePackage);
	}

	/* (non-Javadoc)
	 * @see aufgabe3_1.Controller#doSteps(int)
	 */
	@Override
	public int doSteps(int steps) {
		SimAnt=SimAnt.doSteps(steps);
		if(!Simulation.bestTours.isEmpty()){
	                this.VIEW.newPath(Simulation.bestTours, SimAnt.CONNECTIONS);
		}else if(!SimAnt.BESTROUTE.isEmpty()){

			/*
			 * Die Eins muss dann hochgezählt werden für verschiedene Colors
			 * 
			 * */

			//VIEW.newPath(SimAnt.BESTROUTE.get(0), new ACOImpl().length(this.SimAnt.BESTROUTE.get(0), SimAnt.CONNECTIONS), new ACOImpl().visitedStreets(this.SimAnt.BESTROUTE.get(0), SimAnt.CONNECTIONS ),1);
		}
		return this.SimAnt.STEPS;
	}

	/* (non-Javadoc)
	 * @see aufgabe3_1.Controller#doStepsTillPath(int)
	 */
	@Override
	public int doStepsTillPath(int pathNR) {
		Simulation nextPathSim=this.SimAnt;
		Simulation prePathSim;	
		for(int i=0; i>= pathNR;i++ ){
			do{
				prePathSim=nextPathSim;
				nextPathSim=nextPathSim.doSteps(1);
			}while(prePathSim.BESTROUTE.containsAll(nextPathSim.BESTROUTE));	//m�gliche probleme wegen objektkopien und vergleichen		
			//manueller garbage collector start optional
		}
		this.SimAnt=nextPathSim;
		if(!SimAnt.BESTROUTE.isEmpty()){
			/*
			 * Die Eins muss dann hochgezählt werden für verschiedene Colors
			 * 
			 * */


			//this.VIEW.newPath(this.SimAnt.BESTROUTE.get(0), new ACOImpl().length(this.SimAnt.BESTROUTE.get(0), SimAnt.CONNECTIONS),new ACOImpl().visitedStreets(this.SimAnt.BESTROUTE.get(0), SimAnt.CONNECTIONS), 1);
		}
		return this.SimAnt.STEPS;
	}

	/* (non-Javadoc)
	 * @see aufgabe3_1.Controller#foundPaths(int)
	 */
	@Override
	public List<Integer> foundPaths(int pathNR) {
		return this.SimAnt.BESTROUTE.get(pathNR);
	}

//	/** Einstigspunkt der Simulation
//	 * @param args nicht verwendet
//	 */
//	public static void main(String[] args) {
//		new ControlUnit();
//	}

	@Override
	public List<Node> giveNodelist(boolean really) {
		List<Node> nodes;
		if(really){
			nodes=new ArrayList<Node>(SimAnt.NODES);
		}else{
			nodes=new ArrayList<Node>();
		}
		return nodes;
	}

	@Override
	public List<Connection> giveConnections(boolean really) {
		List<Connection> connections;
		if(really){
			connections=new ArrayList<Connection>(SimAnt.CONNECTIONS);
		}else{
			connections=new ArrayList<Connection>();
		}
		return connections;
	}
}