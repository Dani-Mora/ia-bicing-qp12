/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import IA.Bicing.Bicing;
import java.util.Random;

/**
 *
 * @author Dani
 */
public class BicingState {
       
    public Integer[] numBic;   
    public Integer[] vanPositions;

    BicingState(Integer[] numBic, Integer[] vansPosition) {
        this.numBic = numBic;
        this.vanPositions = vansPosition;
    }
  
    // Pre: assuming ther're enough bicycles to cover the expected amount of bycycles
    public static BicingState GetInitialState(Bicing context, Integer numVans) {
        Random rand = new Random();
        Integer[] numBic = new Integer[context.getNumStations()];        
        //Integer[] sortedStations = new Integer[this.context.getNumStations()];
        Integer[] vansStation = new Integer[numVans]; 

        int numStations = context.getNumStations();
        for (int i = 0; i < numStations; ++i) {
            // Stations
            numBic[i] = context.getStationNextState((i));
            vansStation[i] = rand.nextInt(numStations);
            // Vans
        }
        return new BicingState(numBic, vansStation);       
    }
    
}
