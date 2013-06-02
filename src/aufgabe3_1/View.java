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
	 * Wenn ein neu Gefundener Pfad Auftritt wird er �ber diese Schnittstelle
	 * in Form einer Knotenliste �bertragen.
	 * Realisierungsidee Anmerkung:
	 * Die Connection zum n�chsten (falls ben�tigt) Knoten kann z.B 
 	 * �ber die gemeinsame Verbindung beider Knoten abgefragt werden.  
	 * @param path
	 * @return �bertragung erfolgreich
	 */
	public boolean newPath(List<List<Integer>> path, List<Connection> c);
}
