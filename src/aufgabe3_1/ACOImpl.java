package aufgabe3_1;

import java.util.*;

/**
 * 
 * @version 0.8
 * @since 25.05.2013
 * 
 */
public class ACOImpl implements ACO {
    public static int total = 0;
    public static HashMap<Integer, Integer> packages = new HashMap<Integer, Integer>();

    public static void ACOImplInit(List<List<Integer>> l) {
        if (total == 0) {
            for (int i = 0; i < l.get(0).size(); i++) {
                if(l.get(1).get(i) != 0){
                    total += l.get(1).get(i);
                    packages.put(l.get(0).get(i), l.get(1).get(i));
                }
            }
        }
    }

    /**
     * Berechnet die Laenge des Weges, den die Ameise gegangen ist
     * 
     * @param visitedNodes
     *            Liste der besuchten Staedte als Integer
     * @param connectionList
     *            Liste aller Connections
     * @return Laenge des Weges der diese Staedte miteinander verbindet
     */
    // FIXME: Fuer die Laengenberechnung werden die zwei Cities, die an einer
    // Connection haengen, in der ArrayList direkt via Index abgefragt.
    // Haengen in der Implementierung MEHR als zwei Cities an einer
    // Connection, dann muss die if-Abfrage angepasst werden.

    public int length(List<Integer> visitedNodes, List<Connection> connectionList) {
        int length = 0;

        // Durchlaufe alle Nodes
        for (int i = 1; i < visitedNodes.size(); i++) {

            // Durchlaufe fuer jeweiligen Node alle Connections
            for (int j = 0; j < connectionList.size(); j++) {

                // Die Connection suchen, die zwei besuchte Nodes miteinander
                // verbindet
                if ((visitedNodes.get(i) == connectionList.get(j).cities.get(0) && visitedNodes.get(i - 1) == connectionList.get(j).cities.get(1))
                        || (visitedNodes.get(i) == connectionList.get(j).cities.get(1) && visitedNodes.get(i - 1) == connectionList.get(j).cities
                                .get(0))) {

                    length += connectionList.get(j).length;
                    // System.out.println("Visited" + connectionList.get(j).ID);
                } // if
            } // for
        } // for

        return length;
    }

    public List<Integer> visitedStreets(List<Integer> visitedNodes, List<Connection> connectionList) {
        List<Integer> visitedConnections = new ArrayList<Integer>();

        // Durchlaufe alle Nodes
        for (int i = 1; i < visitedNodes.size(); i++) {

            // Durchlaufe fuer jeweiligen Node alle Connections
            for (int j = 0; j < connectionList.size(); j++) {

                // Die Connection suchen, die zwei besuchte Nodes miteinander
                // verbindet
                if ((visitedNodes.get(i) == connectionList.get(j).cities.get(0) && visitedNodes.get(i - 1) == connectionList.get(j).cities.get(1))
                        || (visitedNodes.get(i) == connectionList.get(j).cities.get(1) && visitedNodes.get(i - 1) == connectionList.get(j).cities
                                .get(0))) {
                    visitedConnections.add(connectionList.get(j).ID);
                    // System.out.println("Visited" + connectionList.get(j).ID);
                } // if
            } // for
        } // for

        return visitedConnections;
    }

    /**
     * Erzeugt eine ArrayList von Ants
     * 
     * @param antCount
     *            Anzahl der Ameisen
     * @param startPosition
     *            Startpunkt der Ameisen
     * @return ArrayList mit Ameisen
     */
    public List<Ant> createAnts(int antCount, Node startPosition) {
        List<Ant> ants = new ArrayList<Ant>();

        for (int i = 0; i < antCount; i++) {
            ants.add(new Ant(packages, total));
        }

        return ants;
    }

    /**
     * Loescht die Liste der besuchten Nodes aus der Ameise
     * 
     * @param ant
     *            Ameise, bei der die Liste geloescht werden soll
     * @return Neue, saubere Ameise
     */
    public Ant clear(Ant ant) {
        List<Integer> nodeList = new ArrayList<Integer>();
        nodeList.add(ant.visitedNodes.get(0));

        return new Ant(packages, total);
    }

    /**
     * Prueft, ob die Ameise ihre Tour beenden und den Rueckweg antreten muss
     * 
     * @param ant
     *            Ameise, die geprueft werden soll
     * @param numCities
     *            Anzahl der Staedte
     * @return Status der Tour
     */
    public boolean tourFinished(Ant ant, int id) {
        return Ant.start == id && ant.packagesToDeliver.isEmpty();
    }

    /**
     * Updatet den Pheromonwert einer Connection (erfordert Update der Nodes)
     * 
     * @param connection
     *            Connection, die geupdatet werden soll
     * @param alpha
     *            Wert um den Pheromone veraendert werden sollen
     * @return Geupdatete Connection
     */
    public Connection updatePheromones(Connection oldConnection, double q) {
        double newPheromon = 0;

        newPheromon = (oldConnection.pheromon + q);

        if (newPheromon < 0) {
            newPheromon = 0; // Negative Pheromon-Werte verhindern
        }

        return new Connection(oldConnection.ID, oldConnection.length, newPheromon, oldConnection.cities);
    }

    /**
     * Verringert die Pheromonwerte aller Connections (erfordert Update der
     * Nodes)
     * 
     * @param oldList
     *            Liste aller aktueller Connections
     * @param rho
     *            Evaporations-Koeffizient
     * @return Neue Liste aller Connections mit reduziertem Pheromonwert
     */
    public List<Connection> evaporate(List<Connection> oldList, double rho) {
        List<Connection> newPheromones = new ArrayList<Connection>();

        for (int i = 0; i < oldList.size(); i++) {
            newPheromones.add(updatePheromones(oldList.get(i), rho));
        } // for

        return newPheromones;
    }

    public Map<Connection, Double> findPath(Ant ant, Node aktNode, int numCities, double alpha, double beta) {

        Map<Connection, Double> probabilities = new HashMap<Connection, Double>();
        double nenner = 0.0;
        int ConnectionAnzahl = aktNode.trails.size();
        List<Connection> tmp = new ArrayList<Connection>();

        for (int i = 0; i < aktNode.trails.size(); i++) {

            Connection currentConnection = aktNode.trails.get(i); // Aktuelle
                                                                  // Connections
            tmp.add(currentConnection);
            List<Integer> currentCities = currentConnection.cities; // Aktuelle
                                                                    // Cities
                                                                    // der
                                                                    // Connections

            // Wenn die Staedte in currentCities noch nicht besucht worden sind,
            // Nenner berechnen
            if (!(ant.visitedNodes.containsAll(currentCities))) {

                nenner += Math.pow(aktNode.trails.get(i).pheromon, alpha) * Math.pow(1.0 / aktNode.trails.get(i).length, beta);

                probabilities.put(currentConnection, 1.0); // probabilities
                                                           // Index zur
                                                           // spaeteren
                                                           // Berechnung mit
                                                           // 1.0 taggen
            } else {
                ConnectionAnzahl--;
            }
        }

        if (ConnectionAnzahl == 0) {
            for (int i = 0; i < tmp.size(); i++) {
                nenner += Math.pow(tmp.get(i).pheromon, alpha) * Math.pow(1.0 / tmp.get(i).length, beta);
                double zaehler = Math.pow(tmp.get(i).pheromon, alpha) * Math.pow(1.0 / tmp.get(i).length, beta);
                probabilities.put(tmp.get(i), zaehler / nenner);
            }
        }

        // Fuer getaggte Probabilities den Zaehler und das Endergebnis berechnen
        for (Map.Entry<Connection, Double> e : probabilities.entrySet()) {
            Connection c = e.getKey();
            Double d = e.getValue();
            if (d == 1.0) {
                double zaehler = Math.pow(c.pheromon, alpha) * Math.pow(1.0 / c.length, beta);
                probabilities.put(c, zaehler / nenner);
            }
        }
        return probabilities;
    }

    public Node randomPathChoice(Ant a, List<Node> nodes, Map<Connection, Double> probMap, Node startNode) {

        double random = (new Random().nextDouble()); // Neuer Random double Wert
        double sumProbabilities = 0.0; // Aufsummierte Probabilities
        int resultIndex = -1; // Index von foundProbability im Original Array
        Connection c = null;
        // SORTIEREN
        List<Map.Entry<Connection, Double>> entries = sortByValue(probMap);

        for (Map.Entry<Connection, Double> e : entries) {
            c = e.getKey();
            Double d = e.getValue();
            sumProbabilities += d.doubleValue();
            if (sumProbabilities > random) {
                // vermeide stehen bleiben
                resultIndex = c.cities.get(0);
                if (resultIndex == startNode.ID) {
                    resultIndex = c.cities.get(1);
                }
                break;
            }
        }

       
        return nodes.get(resultIndex - 1);
    }

    @SuppressWarnings("unchecked")
    public static <K, V extends Comparable> List<Map.Entry<K, V>> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(map.size());
        entries.addAll(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                return e1.getValue().compareTo(e2.getValue());
            }
        });

        return entries;
    }

}