/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import aima.search.framework.HeuristicFunction;

/**
 *
 * @author Dani
 */
public class Heuristic implements HeuristicFunction {

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
        return 0;
    }
    
    protected double getSimpleHeuristic(BicingState st) {
        return 0;
    }
    
}
