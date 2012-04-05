/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import aima.search.framework.HeuristicFunction;
import java.util.Random;

/**
 *
 * @author Dani
 */
public class BicingHeuristic implements HeuristicFunction {

    private final Integer heuristicUsed = 0;
    
    @Override
    public double getHeuristicValue(Object o) {
        BicingState st = (BicingState) o;      
        if (heuristicUsed == 0) {
            return this.getComplexHeuristic(st);    
        }
        else {
            return this.getSimpleHeuristic(st);
        }
    }
    
    protected double getComplexHeuristic(BicingState st) {
        return new Random().nextDouble();
    }
    
    protected double getSimpleHeuristic(BicingState st) {
        return 0;
    }
    
}
