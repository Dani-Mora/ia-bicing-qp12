/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import aima.util.Pair;
import IA.Bicing.Bicing;
import java.util.ArrayList;

/**
 *
 * @author Dani
 */
public class State {
       
    public ArrayList<Integer> stations;
    public Pair[] vans;
    
    public State() {
        //int numStations = Bicing.getNumStations();
        this.stations = new ArrayList<Integer>();
        
    }
  
}
