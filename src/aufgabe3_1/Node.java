package aufgabe3_1;

import java.util.*;

public class Node {
    final public int ID;                    // ID des Nodes
    final public List<Connection> trails;   // Liste aller Connections die vom Node wegfuehren
    public int amountOfPackets;
    
    public Node(int ID, List<Connection> trails,int amountOfPackets) {
        this.ID = ID;
        this.trails = trails;
        this.amountOfPackets = amountOfPackets;
    }
    
    public void setAmountOfPackets( int amount){
    	this.amountOfPackets = amount;
    }

    @Override
    public String toString() {
        return "Node-ID: " + ID + "\nTrails:\n" + trails + "\nPakete:\n" + amountOfPackets + " \n" ;
    }

}
