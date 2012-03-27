/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import IA.Bicing.Bicing;
import aima.search.framework.SuccessorFunction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dani
 */
public class Successors implements SuccessorFunction {

    private Bicing context;
    private Integer numVans;
    
    public Successors(Bicing bicing, Integer numVans)
    {
        this.context = bicing;
        this.numVans = numVans;
    }
    
    @Override
    public List getSuccessors(Object o) {
        ArrayList successors = new ArrayList();
        Integer numStations = this.context.getNumStations();
        
        for (int i = 0; i < numVans; ++i) {
            for (int j = 0; j < numStations; ++j)   {
                // TODO
            }         
        }
        
        return successors;
    }
    
}
