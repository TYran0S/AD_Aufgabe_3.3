package aufgabe3_1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Immutable Klasse des Simulationsablaufs
 * @version 0.8
 * 
 * TODO: mehr TESTEN !!!!!
 *
 */
public class Simulation {
	//final public static int BESTSOLUTION = 1770;            // Beste bislang bekannte Loesung
	
	final public int CITIES;                    			// Anzahl der Staedte
	final public String TESTDATA;  							// TSP Datei    
    final public static int ANTS = 5;                      // Anzahl der Ameisen
    final public static int STARTNODE = 1;                  // Startpunkt der Ameise
    final public static double ALPHA = 1.0;                 // Einfluss der Pheromonene
    final public static double BETA = 5.0;                  // Einfluss der Weglaenge
    final public static double RHO = 0.5;                   // Evaporationskonstante
    final public static double Q = 1.0;                     // Pheromon-Menge eines Connection-Updates
	final public static int MAXCYCLES=500000;				//Maximale schritte der Simulation 
    public static final int MAXHISTORY = 20;				//maximum der History der gefundenen Wege
    
	
    final public List<Connection> CONNECTIONS;
    final public List<Ant> ANTLIST;
    final public List<Node> NODES;
    final private ACO COLONY;
    final public List<List<Integer>> BESTROUTE;
    final public int STEPS;
    
    
    
    public Simulation(int cities , String path ){
    	CITIES=cities;
    	TESTDATA=path;
        COLONY = new ACOImpl();       
        List<Connection> connections = Parser.initConnections(Parser.parseTestFile(TESTDATA));// Connections parsen aus der Testdatei
        
        // Connections durch evaporate-Methode mit positivem Pheromon-Wert vorinitialisieren, um bei Wahrscheinlichkeitsberechnung nicht durch 0 zu dividieren
        connections = COLONY.evaporate(connections, Q);        
        NODES = Parser.initNodes(connections);// Geupdatete Connections in die Liste der Nodes erzeugen
        
        CONNECTIONS = connections;       
        ANTLIST = COLONY.createAnts(ANTS, NODES.get(STARTNODE - 1));// Kolonie von Ameisen erstellen
        BESTROUTE = new LinkedList<List<Integer>>();         	
        STEPS=0;
    }
    
    private Simulation(int cities , String path , List<Connection> cons , List<Ant> antlist , List<Node> nodes, List<List<Integer>> bestRoute , int steps){
    	CITIES=cities;
    	TESTDATA=path;
        COLONY = new ACOImpl();
        List<Connection> connections = cons;
        connections = COLONY.evaporate(connections, Q);
        NODES = nodes;        
        CONNECTIONS = connections;
        ANTLIST = antlist; 
        BESTROUTE = bestRoute;
        STEPS=steps;
    }
    
    /**
     * L�sst dieses Simulationsobjekt die n�chsten schritte errechnen und gibt dann die neue
     * Situation als Simulations Objekt aus
     * @param steps Angabe wieviele schritte die Simulation vorranschreiten soll
     * @return neue vorrangeschrittene Simulation
     */
    public Simulation doSteps(int steps){    	
    	//reale kopie vom inhalt n�tig!!!//nein da inhalte immutable also reicht kopie der liste
    	List<Connection>connections = new ArrayList<Connection>(this.CONNECTIONS);
    		//List<Connection>connections = new ArrayList<Connection>();
    		//for(Connection i: this.CONNECTIONS)
    		//	connections.add((Connection)i.clone());
    		
    	List<Ant> antList=new ArrayList<Ant>(this.ANTLIST);
    		/*
    		List<Ant> antList=new ArrayList<Ant>();
    		for(Ant i: this.antList)
    			antList.add((Ant)i.clone());*/
    		
    	    List<Node> nodes=this.NODES;    		
    	    LinkedList<List<Integer>> bestRoute=new LinkedList<>(this.BESTROUTE);    	    
    		
    	   
    	    int bestLength = 0;
    	    if(!BESTROUTE.isEmpty()){
    	    	bestLength= COLONY.length(BESTROUTE.get(0), connections);
	    	    /*for(int subLenght :BESTROUTE.get(0)){
	    	    	//bestLength+=subLenght;
	    	    	
	    	    }*/   
    	    }

        	
        	
        	
	        int i = 1; // Laufvariable
	        while ( (i <= steps /*|| (BESTSOLUTION != bestLength )*/ ) && steps < MAXCYCLES) {
	
	            for(int a = 0; a < antList.size(); a++) {
	            	//wenn der letzte konten nicht gleich der erste knoten ist den eine Ameise besucht hat
	            	while(antList.get(a).visitedNodes.get(antList.get(a).visitedNodes.size() - 1) != (antList.get(a).visitedNodes.get(0))){
           			 
           			   // Start-Node erzeugen			wie viele nodes die ant besucht hat
                        Node startNode = nodes.get(antList.get(a).visitedNodes.get(antList.get(a).visitedNodes.size() - 1) - 1);    //vorherige knoten auf dem die Ameise war
                        
                        // Fuer die Connections vom Start-Node die Probabilities erzeugen
                        Map<Connection, Double> probabilities = COLONY.findPath(antList.get(a), startNode,startNode.trails.size(), ALPHA, BETA);
                                                     
                        
                        // Nach Zufallsmuster anhand der Probabilities den naechsten Node ermitteln
                        Node nextNode = COLONY.randomPathChoice(antList.get(a),nodes, probabilities,startNode);

                        // Ameise zum nextNode bewegen, dabei wird eine neue Ameise mit dem nextNode in ihrer visitedNodes Liste erzeugt
                        Ant movedAnt = COLONY.move(antList.get(a), nextNode);

                        // Alte Ameise in der Liste durch neue Ameise mit aktualisierter visitedNodes Liste ersetzen
                        antList.set(a, movedAnt);

                        // Benutzte Connection suchen und in tempConnection ablegen
                        Connection tempConnection = null;
                        for(Connection element : connections) {
                            if(element.cities.containsAll(antList.get(a).visitedNodes.subList(antList.get(a).visitedNodes.size() - 2, antList.get(a).visitedNodes.size() - 1))) {
                                tempConnection = element;
                            } // if
                        } // for

                        
                        // Pheromon bei benutzter Connection updaten und an gleicher Stelle wieder in der Connetions Liste abspeichern
                        connections.set((tempConnection.ID - 1), COLONY.updatePheromones(tempConnection, Q));

                        // Nodes mit aktualisierten Connections erzeugen
                        nodes = Parser.initNodes(connections);		 
           		 
           		 	}//end while
	            	
	            	//bist du fertig ?
	               	 if(COLONY.tourFinished(antList.get(a), CITIES)) 	{
	               		
	               		 
	                        
	               		int tempLength = COLONY.length(antList.get(a).visitedNodes, connections); // Laenge berechnen
	               		 
	               		 // Bei kuerzerer Laenge die Laenge in bestLength abspeichern und die verwendete Route in bestRoute
	                        if(((bestLength - tempLength) > 0) || bestLength == 0) {
	                            bestLength = tempLength; // Neue kuerzeste Laenge abspeichern
	                            bestRoute.addFirst(antList.get(a).visitedNodes); // Neue kuerzeste Route abspeichern
	                            if(bestRoute.size()>MAXHISTORY){
		                        	bestRoute.removeLast();
		                        }
	                        } // if
	                        
	                        // Besuchte Nodes der Ameise resetten
	                        antList.set(a, COLONY.clear(antList.get(a)));
	                        
	            
	               	 }else{
	        				
	               		 
	               		 
	               		 
	    					//setze letzte besuchte stadt als start node, f�r die actuelle amese
	    							//hole von der actuellen ameise die letzte node
	    											 //hole die letzte besuchte stadt
	               // Start-Node erzeugen								   wie viele nodes die ant besucht hat
	               Node startNode = nodes.get(antList.get(a).visitedNodes.get(antList.get(a).visitedNodes.size() - 1) - 1);
	
	        
	        		// Fuer die Connections vom Start-Node die Probabilities erzeugen
	               Map<Connection, Double> probabilities = COLONY.findPath(antList.get(a), startNode,startNode.trails.size(), ALPHA, BETA);
	        		// Nach Zufallsmuster anhand der Probabilities den naechsten Node ermitteln
	        		Node nextNode = COLONY.randomPathChoice(antList.get(a),nodes, probabilities,startNode);
	        		// Ameise zum nextNode bewegen, dabei wird eine neue Ameise mit dem nextNode in ihrer visitedNodes Liste erzeugt
	        		Ant movedAnt = COLONY.move(antList.get(a), nextNode);
	
	        		// Alte Ameise in der Liste durch neue Ameise mit aktualisierter visitedNodes Liste ersetzen
	        		antList.set(a, movedAnt);
	
	        		// Benutzte Connection suchen und in tempConnection ablegen
	        		Connection tempConnection = null;
	        		for(Connection element : connections) {
	        				if(element.cities.containsAll(antList.get(a).visitedNodes.subList(antList.get(a).visitedNodes.size() - 2, antList.get(a).visitedNodes.size() - 1))) {
	        					tempConnection = element;
	        					} // if
	        			} // for
	
	        
	        			// Pheromon bei benutzter Connection updaten und an gleicher Stelle wieder in der Connetions Liste abspeichern
	        			connections.set((tempConnection.ID - 1), COLONY.updatePheromones(tempConnection, Q));
	
	        			// Nodes mit aktualisierten Connections erzeugen
	        			nodes = Parser.initNodes(connections);
	
	        			// Aktualisierung fuer die Konsolenausgabe resetten
	   
	               	 }	
	            	
	            } // for
	
	            // Evaporation der Pheromone an den Connections durchfuehren
	            connections = COLONY.evaporate(connections, RHO);
	
	            // Nodes mit aktualisierten Connections erzeugen
	            nodes = Parser.initNodes(connections);
	            
	
	            i++; // Takt hochzaehlen
	        } // while	
	        return new Simulation(CITIES, TESTDATA, connections, antList, nodes, bestRoute,this.STEPS+steps);
	} // methode 
    
}