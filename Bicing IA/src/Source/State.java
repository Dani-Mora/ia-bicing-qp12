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
       
    public Integer[] stations;
    public Integer[] vans;

    State(Integer[] numStations, Integer[] vansPosition) {
        this.stations = numStations;
        this.vans = vansPosition;
    }
  
}
