package aufgabe3_1;

import java.util.List;
import java.util.Map;

public interface ACO {

  //route berechnen
    public int length(List<Integer> list, List<Connection> connectionList);
    
    //erzeugung einer bestimmten anzahl von ameisen an einer bestimmten Node
    public List<Ant> createAnts(int antCount, Node startPosition);
    
    //alle nodes von der uebergebene ameise werden zurueckgesetz "fange neu an"
    public Ant clear(Ant ant);

    //ist die uebergebene ameise alle nodes durch gegangen
    public boolean tourFinished(Ant ant, int id);

    public Connection updatePheromones(Connection connection, double q);

    public List<Connection> evaporate(List<Connection> list, double rho);

    public Map<Connection, Double> findPath(Ant ant, Node node, int cities, double alpha, double beta);

    public Node randomPathChoice(Ant a,List<Node> nodes, Map<Connection, Double> map, Node startNode);
    
    public List<Integer> visitedStreets(List<Integer> visitedNodes,List<Connection> connectionList);

}
