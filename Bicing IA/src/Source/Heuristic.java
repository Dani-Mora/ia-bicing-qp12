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
    
    /* Constants */
    private static final Integer HEURISTIC_SOL1 = 1;
    private static final Integer HEURISTIC_SOL2 = 2;
    // ...

    @Override
    public double getHeuristicValue(Object o) {
        BicingState st = (BicingState) o;
        return 0;
    }
    
}
