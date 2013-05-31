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
    final public static int capacity = 2;
    final public static int ANTS = 50; // Anzahl der Ameisen
    final public static int STARTNODE = 1; // Startpunkt der Ameise
    final public static double ALPHA = 1; // Einfluss der Pheromonene
    final public static double BETA = 5.0; // Einfluss der Weglaenge
    final public static double RHO = 0.5; // Evaporationskonstante
    final public static double Q = 1.0; // Pheromon-Menge eines
                                        // Connection-Updates
    final public static int MAXCYCLES = 500000; // Maximale schritte der
                                                // Simulation
    public static final int MAXHISTORY = 20; // maximum der History der
                                             // gefundenen Wege

    final public List<Connection> CONNECTIONS;
    final public List<Ant> ANTLIST;
    final public List<Node> NODES;
    final private ACO COLONY;
    final public List<List<Integer>> BESTROUTE;
    final public int STEPS;

    public Simulation(int cities, String path) {
        List<List<Integer>> t = new ArrayList<List<Integer>>();
        t.add(new ArrayList<Integer>());
        t.add(new ArrayList<Integer>());
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

        ACOImpl.ACOImplInit(t);
        
        CITIES = cities;
        TESTDATA = path;
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

    private Simulation(int cities, String path, List<Connection> cons, List<Ant> antlist, List<Node> nodes, List<List<Integer>> bestRoute, int steps) {
        List<List<Integer>> t = new ArrayList<List<Integer>>();
        t.add(new ArrayList<Integer>());
        t.add(new ArrayList<Integer>());
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
                nodes = Parser.initNodes(connections);
                
                // Entladen der Packete wenn moeglich
                ant.unload(nextNode.ID);
                ant.load(nextNode.ID);
                

                // bist du fertig ?
                if (COLONY.tourFinished(ant, nextNode.ID)) {
                    int tempLength = COLONY.length(ant.visitedNodes, connections); // Laenge
                                                                                              // berechnen

                    // Bei kuerzerer Laenge die Laenge in bestLength abspeichern
                    // und die verwendete Route in bestRoute
                    if (((bestLength - tempLength) > 0) || bestLength == 0) {
                        bestLength = tempLength; // Neue kuerzeste Laenge
                                                 // abspeichern
                        bestRoute.addFirst(ant.visitedNodes); // Neue
                                                                         // kuerzeste
                                                                         // Route
                                                                         // abspeichern
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
            nodes = Parser.initNodes(connections);
        }
        
        return new Simulation(CITIES, TESTDATA, connections, antList, nodes, bestRoute, this.STEPS + steps);
    } // methode

}