package aufgabe3_1;

import java.io.*;
import java.util.*;

public class Parser {

    // Startwert der Pheromone pro Connection
    final public static int pheromon = 0;
    //Liste für die Anzahl der Pakete pro Stadt
    public static ArrayList<Integer> packetList = new ArrayList<Integer>();

    /**
     * Liest eine (symmetrische) TSP Datei ein und uebertraegt die linke
     * untere Dreiecksmatrix in ein int Array.
     * Erfordert Nullen als Trennzeichen (siehe Aufbau der TSP Dateien).
     * 
     * @param filePath  String des Dateinamens / Dateipfades
     * @return int      Array mit den Laengen der TSP Datei
     */
    public static int[] parseTestFile(String filePath) {

        int[] resultArray = null;
        String line; // Zu verarbeitende Zeile
        String splitedLine[]; // Werte ohne Trennzeichen
        

        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            splitedLine = line.split(";");
         
         
            resultArray = new int[splitedLine.length];
            for(int i = 0; i < resultArray.length; i++) {
                resultArray[i] = Integer.parseInt(splitedLine[i]);
            } // for
                
    
            br.close(); // BufferedReader Stream schliessen

        } catch (Exception e) {
            System.err.println(e);
            System.err.println("csv Datei konnte nicht eingelesen werden!");
            System.exit(0);
        } // catch
        Simulation.capacity = resultArray[0];
//        System.out.println(Simulation.capacity);
        return resultArray;
    }

    /**
     * Erzeugt eine ArrayList von Connections
     * 
     * @param array Array mit den Entfernungen zwischen den Nodes
     * @return      Eine ArrayList von Connections
     */
    public static List<Connection> initConnections(int[] array) {

        ArrayList<Connection> connectionList = new ArrayList<Connection>();
        int currentCity = 1;
        int neighborCity = 0;
        int connectionID = 1;

        for (int i = 1; i < array.length-1; i++) {
            
            if (array[i] == 0) {
                
                currentCity++;
                if(array[i+1] == -1){
                	packetList.add(0);
                } else {
                packetList.add(array[i+1]);
                }
                neighborCity = array[i+2];
                List<Integer> cities = new ArrayList<Integer>();
                cities.add(currentCity);
                cities.add(neighborCity);
                connectionList.add(new Connection(connectionID, array[i + 3],
                        pheromon, cities));
                connectionID++;
                i = i+3;            
            } else {
                 List<Integer> cities = new ArrayList<Integer>();
                neighborCity = array[i];
                cities.add(currentCity);
                cities.add(neighborCity);
                connectionList.add(new Connection(connectionID, array[i+1], pheromon, cities));
                connectionID++;
                i = i+1;
            } // else
        } // for        
        //Checken ob Kapazität und Bestellungen harmonieren(Kapazität muss >= Max Anzahl Paket für kunde sein)
        ArrayList<Integer> sortlist = (ArrayList<Integer>) packetList.clone();
        Collections.sort(sortlist);
        if(Simulation.capacity < sortlist.get(sortlist.size()-1)){
        	System.err.println("Kapazität kleiner als Max Anzahl Pakete für Kunden");
        	System.exit(0);
        }
        return connectionList;
    }

    public static  List<List<Integer>> initPackages(int[] array) {
    	
    	List<List<Integer>> packageMap = new ArrayList<List<Integer>>();
    	packageMap.add(new ArrayList<Integer>());
        packageMap.add(new ArrayList<Integer>());
    	int currentCity = 1;
          
    	for (int i = 1; i < array.length-1; i++) {
            
            if (array[i] == 0) {
                
                currentCity++;
                packageMap.get(0).add(currentCity);
                if(array[i+1] == -1){
                	packageMap.get(1).add(0);
                } else {
                packageMap.get(1).add(array[i+1]);
                }

                i = i+3;            
            }else{
            	i++;
            }
    	}
    	return packageMap;
    	
    }
    
    
    /**
     * Erzeugt eine ArrayListe aus Nodes
     * 
     * @param connections   ArrayListe von Connections
     * @return              ArrayList von Nodes
     * @throws InterruptedException 
     */
    public static List<Node> initNodes(List<Connection> connections) {

        List<Node> nodeList = new ArrayList<Node>();
        Set<Integer> connectionSet = new HashSet<Integer>();

        for (int i = 0; i < connections.size(); i++) {
            connectionSet.addAll(connections.get(i).cities);
        } // for

        for (int i = 0; i < connectionSet.size(); i++) {

            List<Connection> connectionList = new ArrayList<Connection>();

            for (int j = 0; j < connections.size(); j++) {

                if (connections.get(j).cities.contains(i + 1)) {
                    connectionList.add(connections.get(j));
                } // if
            } // for
            //Der Node die Anzahl der Packets uebergeben(packetList.get(Index connectionSet)
            if(i != 0) {
                nodeList.add(new Node(i + 1, connectionList,packetList.get(i-1)));
            } else {
                nodeList.add(new Node(i + 1, connectionList,0));
           }
        } // for
       
        return nodeList;
    }
    
    public static void writeCity(int packetamount,int ... connections) throws IOException{
        String input = 0 + ";" + packetamount;
        for(int x : connections){
            input+= x + ";";
        }
         FileWriter fr = new FileWriter("GRSelfMade.csv");
         BufferedWriter br = new BufferedWriter(fr);
         br.append(input);
         br.close();
    }

}
