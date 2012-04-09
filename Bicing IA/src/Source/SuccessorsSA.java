/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

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
        
        successors.addAll(this.addRandomMovement(state));
        
        return successors;
    }
    
    private List addRandomMovement(BicingState state)
    {
        ArrayList successors = new ArrayList();
        Random rand = new Random();
        Integer origin, dest, amount, numStations = Simulation.bicing.getNumStations();
        
        origin = rand.nextInt(numStations);
        while (state.getBicyclesNextHour(origin) < Simulation.bicing.getDemandNextHour(origin)) {
            origin = rand.nextInt();
        }
        
        dest = rand.nextInt();
        amount = rand.nextInt(Simulation.bicing.getStationDoNotMove(origin));
        while (dest != origin) {
            dest = rand.nextInt();
            amount = rand.nextInt(Simulation.bicing.getStationDoNotMove(origin));
        }
                
        BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());
        newState.addMovement(new Transport(origin, dest, amount));
        return successors;
    }

    // .. TODO AFEGIR ALGU MES.. //
    
}
