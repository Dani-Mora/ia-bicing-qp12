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

    private final Integer heuristicUsed = 1;
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
    
    protected Double calculateDistance(Integer ix, Integer iy, Integer jx, Integer jy) {
        return (Math.abs(ix.doubleValue() - jx.doubleValue()) 
                + Math.abs(iy.doubleValue() - jy.doubleValue())) / 10;
    }
    
    protected Double calculateCost(Transport t) {
        int[] Or = new int[2], Dt = new int[2];
        Integer indexOr, indexDt, nb;
        indexOr = t.getOrigin();
        indexDt = t.getPreferredDestination();
        Or = context.getStationCoord(indexOr);
        Dt = context.getStationCoord(indexDt);
        Double dist = calculateDistance(Or[0], Or[1], Dt[0], Dt[1]);
        nb = t.getBicyclesAmount();
        return ((nb.doubleValue() % 10) + 1) * dist;
    }
    
    protected Double calculateOriginOutcome(Transport t) {
        Integer dNM, nS, dem, indexOrigin;
        indexOrigin = t.getOrigin(); System.out.println("indexOrigin = " + indexOrigin);
        dNM = context.getStationDoNotMove(indexOrigin);
        nS = context.getStationNextState(indexOrigin);
        dem = context.getDemandNextHour(indexOrigin);
        Integer aux = nS - t.getBicyclesAmount();
        if (aux >= dem) return 0.0;
        
        else {
            //deixem l'estacio per baix de la demanda
            return dem.doubleValue() - aux.doubleValue();
            
        }
    }
    
    protected Double calculateDestinationIncome(Transport t) {
        Integer dNM, nS, dem, indexDest;
        indexDest = t.getPreferredDestination();
        dNM = context.getStationDoNotMove(indexDest);
        nS = context.getStationNextState(indexDest);
        dem = context.getDemandNextHour(indexDest);
        
        Integer newAmount = nS + t.getBicyclesAmount();
        if (newAmount <= dem) return t.getBicyclesAmount().doubleValue();
        else return dem.doubleValue() - nS.doubleValue();
    }
    
    protected Double calculateIncome(Transport t) {

        Double originBalance, destinationBalance; 
        originBalance = calculateOriginOutcome(t);
        destinationBalance = calculateDestinationIncome(t);System.out.println("originBalance = " + originBalance + " destinationBalance = " + destinationBalance);
        return destinationBalance - originBalance;

       // return t.getBicyclesAmount().doubleValue();
    }
    
    protected double getComplexHeuristic(BicingState st) {
        List<Transport> movements = st.getMovements();
        Double totalInc = 0.0;
        for (int i = 0; i < movements.size(); ++i) {
            Transport transp = movements.get(i);
            Double income = calculateIncome(transp);
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
            Double income = calculateIncome(transp);
            
            totalInc += income;
        }
        System.out.println("SIMPLE HEURISTIC, totalInc = " + totalInc);
        return totalInc;

//        List<Transport> movements = st.getMovements();
//        Double result = 0.0;
//        for (int i = 0; i < movements.size(); ++i) {
//            Transport transp = movements.get(i);
//            Integer origin, dest, numB;
//            Integer demandA, demandB, demandC;
//            Integer amountA, amountB, amountC;
//            Integer unusedA, unusedB, unusedC;
//            
//            origin = transp.getOrigin();
//            dest = transp.getPreferredDestination();
//            numB = transp.getBicyclesAmount();
//            demandA = this.context.getDemandNextHour(origin);
//            demandB = this.context.getDemandNextHour(dest);
//            amountA = this.context.getStationNextState(origin);
//            amountB = this.context.getStationNextState(dest);
//            unusedA = this.context.getStationDoNotMove(origin);
//            unusedB = this.context.getStationDoNotMove(dest);
//            
//            Integer lostInA = demandA - st.getNumBicycles(origin);
//            // 0, +, -ç
//            // si 0, ganancia/perdua 0, OK
//            // Si +
//            
//            if (transp.HasTwoDestinations()) {
//                
//            }
//
//            
//            
//            
//            
//            
//            
//            
//           // Integer numBicOrigin = stState[movements.get(i).];
//        }
//        
//        
//        return 0;
    }
    
}
