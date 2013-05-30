/**
 * 
 */
package aufgabe3_1;

import java.util.List;

/**
 * @version 0.3
 * @since 19.05.2013
 * Anforderungen an die View. 
 * Es muss noch genauer ausgehandelt werden ob dieses Format akzeptabel
 */
public interface View {
	
	/**
	 * Wenn ein neu Gefundener Pfad Auftritt wird er über diese Schnittstelle
	 * in Form einer Knotenliste übertragen.
	 * Realisierungsidee Anmerkung:
	 * Die Connection zum nächsten (falls benötigt) Knoten kann z.B 
 	 * über die gemeinsame Verbindung beider Knoten abgefragt werden.  
	 * @param path
	 * @return übertragung erfolgreich
	 */
	public boolean newPath(List<Integer> path, final int laenge, List<Integer> visited);
	
	/**
	 * Erhält eine Liste der Ameisen für die Grafische Verarbeitung dieser.
	 * Wird dies nicht unterstützt/benötigt wird false zurückgegeben.
	 * @param antList Liste mit den Ameisen in der Simulation
	 * @return false wenn diese Daten nicht gebraucht werden
	 */
	public boolean positionsOfAnts(List<Ant> antList);
	
	//direkt über Konstruktor oder 1 mal über initialisierung über interface ???
	//public boolean availableNodes(List<Node> nodeList);	
	//public boolean availableConnections(List<Connection> connectionList);
	
	
}
