/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import IA.Bicing.Bicing;
import IA.Bicing.test;
import java.util.Random;

/**
 *
 * @author Dani
 */
public class IA {
    
    private Bicing context;
    private static final Integer NUM_VANS = 6;
    
    private Random myRandom;
    
    public IA() {
        this.context = new Bicing(20,120, 0);    
    }
    
    
    // Pre: assuming ther're enough bicycles to cover the expected amount of bycycles
    public BicingState GetInitialState() {
        Integer[] numBic = new Integer[this.context.getNumStations()];        
        //Integer[] sortedStations = new Integer[this.context.getNumStations()];
        Integer[] vansStation = new Integer[NUM_VANS]; 
        
        int numStations = this.context.getNumStations();
        for (int i = 0; i < numStations; ++i) {
            // Stations
            numBic[i] = this.context.getStationNextState((i));
            vansStation[i] = this.myRandom.nextInt(numStations);
            // Vans
        }
     
        return new BicingState(numBic, vansStation);       
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         test a = new test();       
    }
}
