package aufgabe3_1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Immutable Klasse des Simulationsablaufs
 * 
 * @version 0.8
 * 
 *          TODO: mehr TESTEN !!!!!
 * 
 */
public class Simulation {
    // final public static int BESTSOLUTION = 1770; // Beste bislang bekannte
    // Loesung

    final public int CITIES; // Anzahl der Staedte
    final public String TESTDATA; // TSP Datei
    public static int capacity;
    final public static int ANTS = 1; // Anzahl der Ameisen
    final public static int STARTNODE = 1; // Startpunkt der Ameise
    final public static double ALPHA = 1; // Einfluss der Pheromonene
    final public static double BETA = 5.0; // Einfluss der Weglaenge
    final public static double RHO = 0.5; // Evaporationskonstante
    final public static double Q = 1.0; // Pheromon-Menge eines
    // Connection-Updates
	public static List<List<Integer>> bestTours;
    final public static int MAXCYCLES = 500000; // Maximale schritte der
    // Simulation
    public static final int MAXHISTORY = 20; // maximum der History der
    // gefundenen Wege

    public static int BESTNUMBEROFDELIVERYVANS = 0x7FFFFFFF;//speichert die beste gefundene Anzahl von benoetigten Lieferwagen 

    final public List<Connection> CONNECTIONS;
    final public List<Ant> ANTLIST;
    final public List<Node> NODES;
    final private ACO COLONY;
    final public List<List<Integer>> BESTROUTE;
    final public int STEPS;

    public Simulation(int cities, String path) {
    	TESTDATA = path;
    	List<List<Integer>> packageMap = new ArrayList<List<Integer>>();
    	packageMap = Parser.initPackages(Parser.parseTestFile(TESTDATA));
        ACOImpl.ACOImplInit(packageMap);

        CITIES = cities;
        
        COLONY = new ACOImpl();
        List<Connection> connections = Parser.initConnections(Parser.parseTestFile(TESTDATA));// Connections
        // parsen
        // aus
        // der
        // Testdatei

        // Connections durch evaporate-Methode mit positivem Pheromon-Wert
        // vorinitialisieren, um bei Wahrscheinlichkeitsberechnung nicht durch 0
        // zu dividieren
        connections = COLONY.evaporate(connections, Q);
        NODES = Parser.initNodes(connections);// Geupdatete Connections in die
        // Liste der Nodes erzeugen

        CONNECTIONS = connections;
        ANTLIST = COLONY.createAnts(ANTS, NODES.get(STARTNODE - 1));// Kolonie
        // von
        // Ameisen
        // erstellen
        BESTROUTE = new LinkedList<List<Integer>>();
        STEPS = 0;
    }
    public Simulation(int cities, List<Connection> con, List<Node> nod, List<List<Integer>> t, int capa) {

            
            ACOImpl.ACOImplInit(t);
            this.capacity = capa;
            TESTDATA = "werd glücklich java";
            CITIES = cities;
            COLONY = new ACOImpl();
            // Connections durch evaporate-Methode mit positivem Pheromon-Wert
            // vorinitialisieren, um bei Wahrscheinlichkeitsberechnung nicht durch 0
            // zu dividieren
            con = COLONY.evaporate(con, Q);
            NODES = nod;// Geupdatete Connections in die
        // Liste der Nodes erzeugen
        
            CONNECTIONS = con;
            ANTLIST = COLONY.createAnts(ANTS, NODES.get(STARTNODE - 1));// Kolonie
        // von
        // Ameisen
        // erstellen
        BESTROUTE = new LinkedList<List<Integer>>();
            STEPS = 0;
    }

        private Simulation(int cities, String path, List<Connection> cons, List<Ant> antlist, List<Node> nodes, List<List<Integer>> bestRoute, int steps) {
            List<List<Integer>> t = new ArrayList<List<Integer>>();
            t.add(new ArrayList<Integer>());
            t.add(new ArrayList<Integer>());
            System.out.printf("init\n");
            t.get(0).add(5);
            t.get(0).add(2);
            t.get(0).add(3);
            t.get(0).add(4);
            t.get(0).add(6);
            t.get(1).add(1);
            t.get(1).add(1);
            t.get(1).add(1);
            t.get(1).add(1);
            t.get(1).add(1);
            capacity = 8;
            ACOImpl.ACOImplInit(t);

            CITIES = cities;
            TESTDATA = path;
            COLONY = new ACOImpl();
            List<Connection> connections = cons;
            connections = COLONY.evaporate(connections, Q);
            NODES = nodes;
            CONNECTIONS = connections;
            ANTLIST = antlist;
            BESTROUTE = bestRoute;
            STEPS = steps;
        }

    /**
     * Laesst dieses Simulationsobjekt die naechsten schritte errechnen und gibt
     * dann die neue Situation als Simulations Objekt aus
     * 
     * @param steps
     *            Angabe wieviele schritte die Simulation vorranschreiten soll
     * @return neue vorrangeschrittene Simulation
     */
    public Simulation doSteps(int steps) {
        // nein da inhalte immutable also
        // reicht kopie der liste
        List<Connection> connections = new ArrayList<Connection>(CONNECTIONS);

        List<Ant> antList = new ArrayList<Ant>(ANTLIST);

        List<Node> nodes = NODES;
        LinkedList<List<Integer>> bestRoute = new LinkedList<>(BESTROUTE);
        int bestLength = 0;
        if (!BESTROUTE.isEmpty()) {
            bestLength = COLONY.length(BESTROUTE.get(0), connections);
        }

        for (int i = 1; (i <= steps) && steps < MAXCYCLES;i++) {
            System.out.printf("Step = %d/%d\n", i,steps);
            for (int j = 0; j < antList.size(); j++) {
                Ant ant = antList.get(j);
                // Start-Node erzeugen wie viele Nodes die ant besucht hat
                // vorherige Knoten auf dem die Ameise war
                Node startNode = nodes.get(ant.visitedNodes.get(ant.visitedNodes.size() - 1) - 1);

                // Fuer die Connections vom Start-Node die Probabilities
                // erzeugen
                Map<Connection, Double> probabilities = COLONY.findPath(ant, startNode, startNode.trails.size(), ALPHA, BETA);

                // Nach Zufallsmuster anhand der Probabilities den naechsten
                // Node ermitteln
                Node nextNode = COLONY.randomPathChoice(ant, nodes, probabilities, startNode);
                while(nextNode.ID == ant.lastNode){
                    nextNode = COLONY.randomPathChoice(ant, nodes, probabilities, startNode);
                }
                ant.lastNode = startNode.ID;
                // Ameise zum nextNode bewegen, dabei wird mit dem nextNode in
                // ihrer visitedNodes Liste erzeugt
                ant.move(nextNode);

                // Benutzte Connection suchen und in tempConnection ablegen
                Connection tempConnection = null;
                for (Connection element : connections) {
                    if (element.cities.containsAll(ant.visitedNodes.subList(ant.visitedNodes.size() - 2,
                                    ant.visitedNodes.size() - 1))) {
                        tempConnection = element;
                    } // if
                } // for

                // Pheromon bei benutzter Connection updaten und an gleicher
                // Stelle wieder in der Connetions Liste abspeichern
                connections.set((tempConnection.ID - 1), COLONY.updatePheromones(tempConnection, Q));

                // Nodes mit aktualisierten Connections erzeugen
                //TODO nodes = Parser.initNodes(connections);

                // Entladen der Packete wenn moeglich
                ant.unload(nextNode.ID);
                ant.load(nextNode.ID);


                // bist du fertig ?
                if (COLONY.tourFinished(ant, nextNode.ID)) {
                    System.out.printf("Ant %d finish tour\n",ant.ID);
                    List<Integer> temp = ant.visitedNodes;
                    temp = optimizePath(ant.visitedNodes, connections);
                    int tempLength = COLONY.length(temp, connections); // Laenge berechnen

                    // Bei kuerzerer Laenge die Laenge in bestLength abspeichern
                    // und die verwendete Route in bestRoute
                    if (BESTNUMBEROFDELIVERYVANS > ant.loadCount || (BESTNUMBEROFDELIVERYVANS == ant.loadCount && ((bestLength - tempLength) > 0) || bestLength == 0)) {
                        BESTNUMBEROFDELIVERYVANS = ant.loadCount;
                        bestLength = tempLength; // Neue kuerzeste Laenge abspeichern
                        bestRoute.addFirst(temp); // Neue kuerzeste Route abspeichern
                        if (bestRoute.size() > MAXHISTORY) {
                            bestRoute.removeLast();
                        }
                    } // if

                    // Besuchte Nodes der Ameise resetten
                    antList.set(j, COLONY.clear(antList.get(j)));

                }

            } // for
            // Evaporation der Pheromone an den Connections durchfuehren
            connections = COLONY.evaporate(connections, RHO);

            // Nodes mit aktualisierten Connections erzeugen
            //TODO nodes = Parser.initNodes(connections);
        }
        //if(bestTours != null && !bestTours.isEmpty()){
            bestTours = calculateTours(bestRoute.getFirst());
        //}
        return new Simulation(CITIES, TESTDATA, connections, antList, nodes, bestRoute, this.STEPS + steps);
    } // methode

    /**
     * Optimiert den Weg den die Ameise nimmt
     * @param temp
     * @param connections
     * @return
     */
    List<Integer> optimizePath(List<Integer> temp, List<Connection> connections){
        List<Integer> path = temp.subList(0, temp.size());
        Ant ant = new Ant(ACOImpl.packages, ACOImpl.total);
		//Ueberprueft 3 aufeinander folgende eintraege in die Liste wenn der Erste gleich dem Dritten ist und am Zweiten weder beladen noch endladen wurde
		//wird der Zweite und der Dritte Eintrag aus der Liste entfernt
        for(int i = 0; i + 2 < path.size(); i++){
            if(!(ant.load(path.get(i + 1)) || ant.unload(path.get(i + 1))) && 
                    path.get(i) == path.get(i + 2)){
                path.remove(i + 1);
                path.remove(i + 1);
                i--;
                    }
        }

		//Bereinigung von vielleicht durch die Optimierung auftretende Einträge des gleichen Nodes in der Liste
        for(int i = 0; i + 1 < path.size(); i++){
            if(path.get(i) == path.get(i + 1)){
                path.remove(i + 1);
                i--;
            }
        }
        ant = new Ant(ACOImpl.packages, ACOImpl.total);
        int load = 0;
        int unload1 = 0;
        int unload2 = 0;
        List<Integer> l = new ArrayList<Integer>();
        l.add(path.get(0));
		
		
		//geht die Teilrouten durch und speichert das erste und letzte Mal entladen
		//Wenn die Länge der Route zwischen Start und letzten Entladen kleiner ist als der Rest der Route wird der Rest durch den Weg zwischen Start und letzten Entladen in umgekehrter Reihen folge ersetzt
		//Gleiches gilt fue zwischen Start und ersten Entladen und erstes Entladen Ende, dabei ersetzt gegebenenfalls die Route erstes Entladen Ende, die Route Start erstes Entladen
        for(int i = 1; i < path.size(); i++){
            if(ant.unload(path.get(i))){
                if(unload1 == 0){
                    unload1 = i;
                    unload2 = i;
                }else{
                    unload2 = i;
                }
            }
            if(ant.load(path.get(i)) || i == path.size() - 1){
                if(COLONY.length(path.subList(load, unload2 + 1), connections) < COLONY.length(path.subList(unload2, i + 1), connections)){
                    List<Integer> p = path.subList(load, unload2 + 1);
                    for(int j = 1; j < p.size(); j++){
                        l.add(p.get(j));
                    }
                    for(int j = p.size() - 2; j >= 0; j--){
                        l.add(p.get(j));
                    }
                }else if(COLONY.length(path.subList(load, unload1 + 1), connections) > COLONY.length(path.subList(unload1, i + 1), connections)){
                    List<Integer> p = path.subList(unload1, i + 1);
                    for(int j = p.size() - 2; j >= 0; j--){
                        l.add(p.get(j));
                    }
                    for(int j = 1; j < p.size(); j++){
                        l.add(p.get(j));
                    }
                }else{
                    List<Integer> p = path.subList(load + 1, i + 1);
                    l.addAll(p);
                }
                unload1 = 0;
                load = i;
            }
        }
        path = l;
		
		//Bereinigung von vielleicht durch die Optimierung auftretende Einträge des gleichen Nodes in der Liste
        for(int i = 0; i + 1 < path.size(); i++){
            if(path.get(i) == path.get(i + 1)){
                path.remove(i + 1);
                i--;
            }
        }
        return path;
    }
	
	/**
     * berrechnet die einzelnen Touren einer Ameise
     * @param path
     * @return
     */
    ArrayList<List<Integer>> calculateTours(List<Integer> path) {
        System.out.printf("calculateTours\n");
        
        ArrayList<List<Integer>> tours = new ArrayList<List<Integer>>();
        Ant ant = new Ant(ACOImpl.packages, ACOImpl.total);
        ant.tourFin = true;
        int j = 0;
        for (int i = 0; i < path.size(); i++) {
            ant.unload(path.get(i));
            if (ant.load(path.get(i)) || i == path.size() - 1) {
                tours.add(path.subList(j, i + 1));
                j = i;
                
            }
            ant.lkwID = tours.size()+1;
        }
        System.out.printf("Finish calulating " + tours.size());
        return tours;
    }
}
