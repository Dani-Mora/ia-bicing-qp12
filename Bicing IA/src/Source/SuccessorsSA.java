/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dani Successors for the Simulated Annealing search
 */
public class SuccessorsSA implements SuccessorFunction {

    @Override
    public List getSuccessors(Object o) {
        BicingState state = (BicingState) o;
        ArrayList successors = new ArrayList();
        
        successors.addAll(this.addRandomMovement(state));
        successors.addAll(this.erasedMovement(state));
        return successors;
    }
    
    private List addRandomMovement(BicingState state) {   
        ArrayList successors = new ArrayList();
        BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());
        newState.AddRandomMovement(true);
        successors.add(new Successor("Added Random movement", newState));
        return successors;
    }
    
    private List erasedMovement(BicingState state) {
        ArrayList successors = new ArrayList();
        BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());
        newState.EraseRandomMovement();
        successors.add(new Successor("Erased random movement", newState));     
        return successors;
    }

    // .. TODO AFEGIR ALGU MES.. //
    
}
