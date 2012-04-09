/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import aima.search.framework.HeuristicFunction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
        /*Integer demand, indexDest, current;
        indexDest = t.getPreferredDestination();
        current = state.getNextStatePlusMovements(indexDest);
        demand = Simulation.bicing.getDemandNextHour(indexDest);
        
        Integer newAmount = current + t.getBicyclesAmount();
        if (newAmount <= demand) return t.getBicyclesAmount().doubleValue();
        else return demand.doubleValue() - current.doubleValue();*/
        Double income = 0.0;        
   
        // We evaluate the final disposition of all the movements
        Iterator destsIter = this.getDistinctDestinations(state).iterator();
        while(destsIter.hasNext()) {
            Integer destStation = (Integer) destsIter.next();
            Integer demand = Simulation.bicing.getDemandNextHour(destStation);
            Integer nextHourEstimate = state.getBicyclesNextHour(destStation);
            Integer previousAmount = nextHourEstimate - state.getReceivedBycicles(destStation);
            if (nextHourEstimate > demand) {
                nextHourEstimate = demand;
            }
            income = (Double) (nextHourEstimate.doubleValue() - previousAmount.doubleValue());
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
        Double result  = this.getSimpleHeuristic(st) + this.getAllTransportCosts(st);
        Double originLoses = this.calculateOriginLoses(st);
        Double destinationIncome = this.calculateDestinationIncome(st);
        System.out.println("COMPLEX HEURISTIC, totalInc = " + result);
        return result;
    }
    
    public double getSimpleHeuristic(BicingState st) { 

//        Random r = new Random();
//        Double d = r.nextDouble(); //System.out.println(d);
//        return d;
        /* HEURISTICO SENCILLO
        Double result = 0.0;
        Integer estimation[] = st.getAvailableBicyclesNextHour();
        for (int i = 0; i < estimation.length; ++i) {
            Integer currentStationDemand = Simulation.bicing.getDemandNextHour(i);
            if (estimation[i] < currentStationDemand) {
                result += currentStationDemand - estimation[i];
            }
        }
        
        return 1/result; // inversa, porque nos interesa que sea mínimo
        */
        

        Double totalInc = 0.0, aa = 1000000.0;
        Double originLoses = this.calculateOriginLoses(st);
        Double destinationIncome = this.calculateDestinationIncome(st);
        totalInc = destinationIncome - originLoses  ; // TODO
        System.out.println("Original loses|Dest Income|Heuristic: " + originLoses + "|" + destinationIncome + "|" + totalInc);
        //System.out.println("Destination Income: " + destinationIncome);
        //System.out.println("Heuristic: " + totalInc);
        return 1.0 / totalInc;      
       
    }
    
}
