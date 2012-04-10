/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Dani Successors for the Simulated Annealing search
 */
public class SuccessorsSA implements SuccessorFunction {

    @Override
    public List getSuccessors(Object o) {
        BicingState state = (BicingState) o;
        ArrayList successors = new ArrayList();
        Random rand = new Random();
        Integer operator = rand.nextInt(3);
        Successors suc = new Successors();
        return null;
    }
        /*
        if (operator == 0) {
            // Unify
            Transport first = state.getMovements().get(new Random().nextInt(state.getMovements().size()));
            Transport second = state.getMovements().get(new Random().nextInt(state.getMovements().size()));
            
            Integer counter = state.getMovements().size();
            while (first.HasTwoDestinations() && second.HasTwoDestinations() && (first.getOrigin() == second.getSecondDestination())) {
                 second = state.getMovements().get(new Random().nextInt(state.getMovements().size()));
                 ++counter;
                 if (counter > state.getMovements().size()) break;
            }
            
            if (counter > state.getMovements().size()) {
                
            }
            
            BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());                       
            Transport unifiedTransport = new Transport(origin1, dest1, dest2, total, amount2);
            newState.eraseMovement(transp1);
            newState.eraseMovement(transp2);
            newState.addMovement(unifiedTransport);
        }
        else if (operator == 1) {
            
        }
        else {
            
        }
        
        return successors;
    }
    
    public List unifyMovements(BicingState state) {

    }
*/
    // .. TODO AFEGIR ALGU MES.. //
    
}
