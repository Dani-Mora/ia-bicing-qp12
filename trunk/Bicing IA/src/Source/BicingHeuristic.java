/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import aima.search.framework.HeuristicFunction;
import java.util.List;

/**
 *
 * @author Dani
 */
public class BicingHeuristic implements HeuristicFunction {

    private final Integer heuristicUsed = 1;
    
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
    
    protected Double calculateCost(Transport t) {
        Double dist = Simulation.bicing.getStationsDistance(t.getOrigin(), t.getPreferredDestination());
        Integer nb = t.getBicyclesAmount();
        return ((nb.doubleValue() / 10) + 1) * dist; // corregido, es / i no %, aviso del raco
    }
    
    protected Double calculateOriginOutcome(Transport t, BicingState state) {
        Integer demanded, indexOrigin, current;
        indexOrigin = t.getOrigin(); System.out.println("Heuristic. indexOrigin = " + indexOrigin);
        current = state.getNextStatePlusMovements(indexOrigin);
        demanded = Simulation.bicing.getDemandNextHour(indexOrigin);
        Integer aux = current - t.getBicyclesAmount();
        if (aux >= demanded) return 0.0;       
        else {
            //deixem l'estacio per baix de la demanda
            return demanded.doubleValue() - aux.doubleValue();      
        }
    }
    
    protected Double calculateDestinationIncome(Transport t, BicingState state) {
        Integer demand, indexDest, current;
        indexDest = t.getPreferredDestination();
        current = state.getNextStatePlusMovements(indexDest);
        demand = Simulation.bicing.getDemandNextHour(indexDest);
        
        Integer newAmount = current + t.getBicyclesAmount();
        if (newAmount <= demand) return t.getBicyclesAmount().doubleValue();
        else return demand.doubleValue() - current.doubleValue();
    }
    
    protected Double calculateIncome(Transport t, BicingState state) {
        Double originBalance, destinationBalance; 
        originBalance = calculateOriginOutcome(t, state);
        destinationBalance = calculateDestinationIncome(t, state);System.out.println("originBalance = " + originBalance + " destinationBalance = " + destinationBalance);
        return destinationBalance - originBalance;
    }
    
    protected double getComplexHeuristic(BicingState st) {
        List<Transport> movements = st.getMovements();
        Double totalInc = 0.0;
        for (int i = 0; i < movements.size(); ++i) {
            Transport transp = movements.get(i);
            Double income = calculateIncome(transp, st);
            Double cost = calculateCost(transp);
            totalInc += income - cost;
        }
        System.out.println("COMPLEX HEURISTIC, totalInc = " + totalInc);
        return totalInc;
    }
    
    public double getSimpleHeuristic(BicingState st) {
        /*
        Random rnd = new Random();
        Double r = rnd.nextDouble();System.out.println("SIMPLE HEURISTIC, = " + r);
        return r;
        */
        List<Transport> movements = st.getMovements();
        Double totalInc = 0.0;
        for (int i = 0; i < movements.size(); ++i) {
            Transport transp = movements.get(i);
            Double income = calculateIncome(transp, st);           
            totalInc += income;
        }
        System.out.println("SIMPLE HEURISTIC, totalInc = " + totalInc);
        return totalInc;
    }
    
}
