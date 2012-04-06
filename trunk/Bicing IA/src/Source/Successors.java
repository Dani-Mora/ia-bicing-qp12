/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import IA.Bicing.Bicing;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dani
 */
public class Successors implements SuccessorFunction {

    private final static Integer OPERATORS = 0;
    
    private Bicing context;
    private Integer numVans;
    
    public Successors(Bicing bicing, Integer numV)
    {
        this.context = bicing;
        this.numVans = numV;
    }
    
    @Override
    public List getSuccessors(Object o) {
        BicingState state = (BicingState) o;
        
        if (OPERATORS == 0) {
            return this.getSuccessorsA(state);
        }
        else {
            return null; //return this.getSuccessorsB(state);
        }
    }
    
    private List getSuccessorsA(BicingState state) {
        ArrayList successors = new ArrayList();
        Integer numStations = this.context.getNumStations();                   
        
        // Simple movements
        for (int i = 0; i < numStations; ++i) {
            // afegir goal condition que ens diu que acabem si hem mogut totes les furgones i/o ja no queden moviments
            //if (TallarArbre si sobren bicis) {
                for (int j = 0; j < numStations; ++j) {
                        if (i != j) {
                            for (int z = 0; z <= 30; ++z) {
                                BicingState newState = new BicingState(state.getBicycleDisposition(), state.getMovements());
                                newState.simpleMoveBicycles(i, j, z);
                                successors.add(new Successor(i + " - " + z + " - > " + j, newState));                
                            }
                        }
                    }   
        }
        
        // Simple movements
        for (int i = 0; i < numStations; ++i) {
                for (int j = 0; j < numStations; ++j) {
                    for (int k = 0; k < numStations; ++k)
                    for (int z = 0; z <= 30; ++z) {
                        BicingState newState = new BicingState(state.getBicycleDisposition(), state.getMovements());
                        newState.simpleMoveBicycles(i, j, z);
                        successors.add(new Successor(i + " - " + z + " - > " + j, newState));                
                    }
                }       
        }
        
        return successors;
    } 
}
