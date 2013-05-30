package aufgabe3_1;

import java.util.*;

public class Ant {
    final public int ID;                        // ID der Ameise
    final public List<Integer> visitedNodes;    // von der Ameise besuchte Nodes
    final public List<Integer> lastTwoCon;

    public Ant(int ID, List<Integer> visitedNodes,List<Integer> last) {
        this.ID = ID;
        this.visitedNodes = visitedNodes;
        lastTwoCon = last;
    }

    @Override
    public String toString() {
        return "Ant-ID: " + ID;
    }

}