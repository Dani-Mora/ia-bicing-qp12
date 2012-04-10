/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import aima.search.framework.HeuristicFunction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Dani
 */
public class BicingHeuristic implements HeuristicFunction {

    private final Integer heuristicUsed = 0;
    
    @Override
    public double getHeuristicValue(Object o) {
        BicingState st = (BicingState) o;      
        Double heuristic = 0.0;
        if (heuristicUsed == 0) {
            heuristic = this.getComplexHeuristic(st);    
        }
        else {
            heuristic = this.getSimpleHeuristic(st);
        }
        return heuristic;
    }
    
    protected Double calculateCost(Transport t) {
        return t.calculateCost();
    }
    
    private List getDistinctDestinations(BicingState st) {
        List<Integer> dests = new ArrayList<Integer>();
        Iterator transpIter = st.getMovements().iterator();
        while(transpIter.hasNext()) {
            Transport transp = (Transport)transpIter.next();
            Integer currentDesp = (Integer) transp.getPreferredDestination(); 
            if (!dests.contains(currentDesp)) {
                dests.add((Integer) transp.getPreferredDestination());
            }
            if (transp.HasTwoDestinations() && !dests.contains(transp.getSecondDestination())) {
                dests.add((Integer) transp.getSecondDestination());
            }
        }
        return dests;
    }
    
    protected Double calculateDestinationIncome(BicingState state) { 
        Double income = 0.0;       
        Iterator destsIter = this.getDistinctDestinations(state).iterator();
        while(destsIter.hasNext()) {
            Integer destStation = (Integer) destsIter.next();
            Integer demand = Simulation.bicing.getDemandNextHour(destStation);
            Integer nextHourEstimate = state.getBicyclesNextHour(destStation);
            Integer previousAmount = nextHourEstimate - state.getReceivedBycicles(destStation);
            if (nextHourEstimate > demand) {
                nextHourEstimate = demand;
            }
            income += (Double) (nextHourEstimate.doubleValue() - previousAmount.doubleValue());
        }
        return income;
    }
    
    private Double calculateOriginLoses(BicingState st) { 
        Double loses = 0.0;
        List<Transport> transp = st.getMovements();
        Iterator transpIter = transp.iterator();
        while(transpIter.hasNext()) {
            Transport current = (Transport) transpIter.next();
            Integer demand = Simulation.bicing.getDemandNextHour(current.getOrigin());
            Integer nextHourEstimate = st.getBicyclesNextHour(current.getOrigin());
            if (nextHourEstimate < demand) {        
                loses += current.getBicyclesAmount();
            }
        } 
        return loses;
    }
    
    private Double getAllTransportCosts(BicingState st) {
        Double cost = 0.0;
        List<Transport> transp = st.getMovements();
        Iterator transpIter = transp.iterator();
        while(transpIter.hasNext()) {
            cost += this.calculateCost((Transport) transpIter.next());
        } 
        return cost;
    }
    
    protected Double getComplexHeuristic(BicingState st) {  
        Double costs = this.getAllTransportCosts(st);
        Double result  = this.getSimpleHeuristicRAW(st) - costs;
        return (1000000000.0 - result);
        //return this.getSimpleHeuristic(st) + this.getAllTransportCosts(st);
    }
    
    public double getSimpleHeuristicRAW(BicingState st) {
        Double totalInc = 0.0;
        Double originLoses = this.calculateOriginLoses(st);
        Double destinationIncome = this.calculateDestinationIncome(st);
        totalInc = destinationIncome - originLoses  ;
        return totalInc;      
    }
    
    //pre: there is no station as origin and destination of 2 different transports
    public double getSimpleHeuristic(BicingState st) { 
        return 1000000000.0 - getSimpleHeuristicRAW(st);
        /*Double result = 0.0;
        Integer[] stations = st.getAllBicyclesNextHour();
        for (int i = 0; i < stations.length; ++i) {
            if (stations[i] < Simulation.bicing.getDemandNextHour(i)) {
                result += Simulation.bicing.getDemandNextHour(i) - stations[i];
            }
        }
        return result;*/
    }
    
}
