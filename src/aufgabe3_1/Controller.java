/**
 * 
 */
package aufgabe3_1;

import java.util.List;

/**
 * @version 0.6
 * @since 25.05.2013
 * Anforderungen an der Controller 
 *
 */
public interface Controller {
	/**
	 * Lässt die Simulation um X Schritte vorranschreiten.
	 * Da die Simulation open end ist bestimmt man somit die 
	 * Simulationsdauer bzw. Genauigkeit durch die Anzahl der 
	 * Systemdurchläufe.
	 * @param steps Anzahl der Schritte um die das Programm vorranschreiten soll
	 * @return die Bisher gemachten Schritte in der Simulation
	 */
	public int doSteps(int steps);
	
	/**
	 * Lässt die Simulation laufen bis der nächste "Xte" Pfad gefunden ist
	 * @param pathNR der nächste anzuzeigende pfad
	 * @return die Bisher gemachten Schritte in der Simulation
	 */
	public int doStepsTillPath(int pathNR);
	
	
	/**
	 * Gibt einen der bereits gefundenen Pade zurück	 
	 * Es werden die letzten 10 archiviert. 
	 * @param pathNR Nummer des Pfades 0 der aktuelle , max 10 der pfad vor 10 optimierungsschritten
	 * @return zusammensetzung des Pfades , oder Leerer Pfad falls keiner exsistiert oder bis jetzt gefunden 
	 */
	public List<Integer> foundPaths(int pathNR);
	
	/**
	 * Gibt die aktuelle Knotenliste des implementierenden Objektes  des Controllerinterfaces zurück.
	 * 
	 * Die Returnliste ist eine Kopie der orginalen Nodelist (+final nodes) 
	 * um Kapselung und funktionale hier immutable Programmierung zu erhalten
	 * 
	 * @param realy Wenn sie diese Information nicht wollen false -> generiert dann leere Knotenliste
	 * @return Knotenliste der Simulation
	 */
	public List<Node> giveNodelist(boolean really);

	/**
	 * Gibt die aktuelle Liste der Connections  zurueck
	 * @param really
	 * @return
	 */
	public List<Connection> giveConnections(boolean really);
	/**
	 * Gibt die aktuelle Liste der benutzten Connections zurueck
	 * @param really
	 * @return
	 */
}
