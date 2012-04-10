/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import aima.search.framework.GoalTest;

/**
 *
 * @author Dani
 */
public class FinalCondition implements GoalTest {

    @Override
    public boolean isGoalState(Object o) {
        // Condició en què trobem una solució que ens va bé
        // De moment, fem que no en trobi cap fins que gasti tots les furgonetes
        // més endavant, poder seria convenient veure si podem acabar
        BicingState state = (BicingState) o;
        Simulation.finalState = state;
        return false;
    }
    
}
