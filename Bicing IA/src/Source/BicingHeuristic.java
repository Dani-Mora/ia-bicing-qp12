/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import IA.Bicing.Bicing;
import aima.search.framework.HeuristicFunction;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Dani
 */
public class BicingHeuristic implements HeuristicFunction {

    private final Integer heuristicUsed = 0;
    private Bicing context;
    
    BicingHeuristic(Bicing context) {
        this.context = context;
    }
    
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
        List<Transport> movements = st.getMovements();
        Double result = 0.0;
        for (int i = 0; i < movements.size(); ++i) {
            Transport transp = movements.get(i);
            Integer origin, dest, numB;
            Integer demandA, demandB, demandC;
            Integer amountA, amountB, amountC;
            Integer unusedA, unusedB, unusedC;
            
            origin = transp.getOrigin();
            dest = transp.getPreferredDestination();
            numB = transp.getBicyclesAmount();
            demandA = this.context.getDemandNextHour(origin);
            demandB = this.context.getDemandNextHour(dest);
            amountA = this.context.getStationNextState(origin);
            amountB = this.context.getStationNextState(dest);
            unusedA = this.context.getStationDoNotMove(origin);
            unusedB = this.context.getStationDoNotMove(dest);
            
            Integer lostInA = demandA - st.getNumBicycles(origin);
            // 0, +, -ç
            // si 0, ganancia/perdua 0, OK
            // Si +
            
            if (transp.HasTwoDestinations()) {
                
            }

            
            
            
            
            
            
            
            Integer numBicOrigin = stState[movements.get(i).];
        }
        
        
        return 0;
    }
    
}
