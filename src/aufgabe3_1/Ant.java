package aufgabe3_1;

import java.util.*;

import com.sun.javafx.binding.StringFormatter;

public class Ant {
    public final static int start = Simulation.STARTNODE;
    
    final public int ID;                        // ID der Ameise
    final public List<Integer> visitedNodes = new ArrayList<Integer>();    // von der Ameise besuchte Nodes
    private static int count = 0;
    
    final public static int capacity = Simulation.capacity;
    public final int numberOfPackages;
    public HashMap<Integer,Integer> packagesToDeliver;
    public int deliverdPackagesInARound = 0;
    public int deliverdPackagesTotal = 0;
    public boolean tourFin = false;
    public int lkwID = 1;
	    public int loadCount = 0;
	    public static String output = "";
    public int lastNode = -1;
    
    @SuppressWarnings("unchecked")
    public Ant(HashMap<Integer,Integer> h, int t) {
        this.ID = count++;
        visitedNodes.add(Simulation.STARTNODE);
        
        packagesToDeliver =  (HashMap<Integer, Integer>) h.clone();
        numberOfPackages = t;
    }

    /**
     * Prueft ob die Ameise an dieser Stelle abladen kann wenn sie es kann gibt die Methode
     * true zurueck sonst false
     * @param id
     * @return
     */
    public boolean unload(Integer id){
        if (packagesToDeliver.containsKey(id) && capacity - deliverdPackagesInARound >= packagesToDeliver.get(id)){
            
            if(tourFin)
            {
                output += String.format("LKW %d entlädt %d Paket/e bei Kunde %d\n", lkwID, packagesToDeliver.get(id), id);
            }
        	System.out.printf("Ant %d unload %d packages at %d , %d/%d/%d\n", ID, packagesToDeliver.get(id), id, deliverdPackagesInARound, capacity, deliverdPackagesInARound + deliverdPackagesTotal);
            deliverdPackagesInARound += packagesToDeliver.get(id);
            packagesToDeliver.remove(id);

            lastNode = -1;
            return true;
        }
        return false;
    }

    /**
     * Prueft ob die Ameise an dieser Stelle beladen werden kann wenn sie es kann gibt die Methode
     * true zurueck sonst false
     * @param pos
     * @return
     */
    public boolean load(Integer pos){

        if(pos == start && deliverdPackagesInARound > 0
         && (int)((numberOfPackages - deliverdPackagesTotal + capacity - 1) / capacity) == 
         (int)((numberOfPackages - deliverdPackagesTotal + capacity - 1 - deliverdPackagesInARound) / capacity + 1)){
            
            deliverdPackagesTotal += deliverdPackagesInARound;
            deliverdPackagesInARound = 0;
            loadCount++;
            System.out.printf("Ant %d load %d packages\n",ID ,deliverdPackagesInARound);
            lastNode = -1;
            return true;
        }else if(pos == start && deliverdPackagesInARound > 0 && !packagesToDeliver.isEmpty()){
            Integer[] g =  packagesToDeliver.values().toArray(new Integer[1]);
            for(int i = 0; i < g.length; i++){
                if(g[i] + deliverdPackagesInARound + 1 < capacity){
                    return false;
                }
            }
            deliverdPackagesTotal += deliverdPackagesInARound;
            deliverdPackagesInARound = 0;
            loadCount++;
            System.out.printf("Ant %d load %d packages\n",ID , deliverdPackagesInARound);
            lastNode = -1;
            return true;
        }
        return false;
    }
    
    /**
     * Bewegt eine Ameise zu einem neuen Node
     * 
     * @param ant
     *            Ameise die bewegt werden soll
     * @param node
     *            Node zu dem sich die Ameise bewegen soll
     * @return Ameise an der neuen Position
     */
    public void move(Node node) {
        visitedNodes.add(node.ID);
    }
    
    
    @Override
    public String toString() {
        return "Ant-ID: " + ID;
    }

}
