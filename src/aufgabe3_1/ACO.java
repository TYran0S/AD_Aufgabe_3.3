package aufgabe3_1;

import java.util.List;
import java.util.Map;

public interface ACO {

  //route berechnen
    public int length(List<Integer> list, List<Connection> connectionList);
    
    //erzeugung einer bestimmten anzahl von ameisen an einer bestimmten Node
    public List<Ant> createAnts(int antCount, Node startPosition);
    
    //ameiise an den alten node zerstören und am ziel node neue ameise mit der selben ID erzeugen
    public Ant move(Ant ant, Node node);
    
    //alle nodes von der übergebene ameise werden zurückgesetz "fange neu an"
    public Ant clear(Ant ant);

    //ist die übergebene ameise alle nodes durch gegangen
    public boolean tourFinished(Ant ant, int cities);

    public Connection updatePheromones(Connection connection, double q);

    public List<Connection> evaporate(List<Connection> list, double rho);

    public Map<Connection, Double> findPath(Ant ant, Node node, int cities, double alpha, double beta);

    public Node randomPathChoice(Ant a,List<Node> nodes, Map<Connection, Double> map, Node startNode);
    
    public List<Integer> visitedStreets(List<Integer> visitedNodes,List<Connection> connectionList);

}
