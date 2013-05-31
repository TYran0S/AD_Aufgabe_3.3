package aufgabe3_1;

import java.util.*;

public class Node {
    final public int ID;                    // ID des Nodes
    final public List<Connection> trails;   // Liste aller Connections die vom Node wegfuehren
    final public int amountOfPackets;
    public Node(int ID, List<Connection> trails,int amountOfPackets) {
        this.ID = ID;
        this.trails = trails;
        this.amountOfPackets = amountOfPackets;
    }

    @Override
    public String toString() {
        return "Node-ID: " + ID + "\nTrails:\n" + trails + "\n";
    }

}
