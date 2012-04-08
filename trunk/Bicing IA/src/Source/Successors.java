/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import IA.Bicing.Bicing;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dani
 */
public class Successors implements SuccessorFunction {

    private final static Integer OPERATORS = 0;
    
    private Bicing context;
    private Integer numVans;
    
    public Successors(Bicing bicing, Integer numV)
    {
        this.context = bicing;
        this.numVans = numV;
    }
    
    @Override
    public List getSuccessors(Object o) {
        BicingState state = (BicingState) o;
        
        if (OPERATORS == 0) {
            return this.getSuccessorsA(state);
        }
        else {
            return null; //return this.getSuccessorsB(state);
        }
    }
    
    private int calculateBicycleSurplus(int doNotMove, int nextHour, int demand) {
        return Math.min(nextHour - demand, doNotMove);
    }
    
    private int calculateBicycleSurplus(int indexStation) {
        int dNM, nS, dem;
        dNM = context.getStationDoNotMove(indexStation);
        nS = context.getStationNextState(indexStation);
        dem = context.getDemandNextHour(indexStation);
        return calculateBicycleSurplus(dNM, nS, dem);
    }
    
    private int calculateBicycleAmount(int indexOrigin, int indexDestination) {
        int surplusOr, surplusDt;
        surplusOr = calculateBicycleSurplus(indexOrigin);
        surplusDt = calculateBicycleSurplus(indexDestination);
        
        return Math.min(surplusOr, Math.abs(surplusDt));
    }
    
    private Boolean DestinationOK (Integer movementDest, ArrayList<Integer> stationsInNeed) {
        for (int i = 0; i < stationsInNeed.size(); ++i) {
           if (movementDest.equals(stationsInNeed.get(i))) {
               //System.out.println("Destination NOT OK");
               return false;
           }
        }
        //System.out.println("Destination OK");
        return true;
    }
    
    private List getSuccessorsA(BicingState state) {
        ArrayList successors = new ArrayList();
        Integer numStations = this.context.getNumStations();                   
        // Simple movements
        System.out.println("Generació de successors");
        //Primer generem tots els intercanvis d'origen
        // es a dir. 
        
        for (int i = 0; i < state.getMovements().size(); ++i) {
            for (int j = i+1; j < state.getMovements().size(); ++j) {
                
                BicingState newState = new BicingState(state.getMovements());
                
                int idOrigin, idDest, newAmountA, newAmountB;
                idOrigin = state.getMovements().get(i).getOrigin();
                idDest = state.getMovements().get(j).getPreferredDestination();
                newAmountA = calculateBicycleAmount(idOrigin, idDest);
                newAmountA = Math.min(newAmountA, 30);
                
                idOrigin = state.getMovements().get(j).getOrigin();
                idDest = state.getMovements().get(i).getPreferredDestination();              
                newAmountB = calculateBicycleAmount(idOrigin, idDest);
                newAmountB = Math.min(newAmountB, 30);
  
                newState.editBicycleAmount(j, newAmountA);
                newState.editBicycleAmount(i, newAmountB);
                newState.exchangeOrigin(i, j);
                //System.out.println("a");
                successors.add(new Successor("Exchange, " + i + "<->" + j, newState));
            }
        }
        //ara generem canvis cap a altres estacions. Sempre cap a a estacions amb deficit
        //de bicicletes
        //
        ArrayList<Integer> stationsInNeed = new ArrayList<Integer>();
        for (int i = 0; i < numStations; ++i) {
            int balance = calculateBicycleSurplus(i);
            if (balance < 0) stationsInNeed.add(i);
        }
        for (int i = 0; i < state.getMovements().size(); ++i) {
            int indexDest = state.getMovements().get(i).getPreferredDestination();
            boolean found = false;
            for (int j = 0; j < stationsInNeed.size() && !found; ++j) {
                if (stationsInNeed.get(j).equals(indexDest)) {
                    stationsInNeed.remove(j);
                    found = true;
                }
            }
        }
        for (int i = 0; i < state.getMovements().size(); ++i) {
            for (int j = 0; j < stationsInNeed.size(); ++j) {
                //System.out.println(i+j);
                int movementDest = state.getMovements().get(i).getPreferredDestination();
                int movementOrigin = state.getMovements().get(i).getOrigin();
                if (DestinationOK(movementDest, stationsInNeed)) {
                    BicingState newState = new BicingState(state.getMovements());
                    newState.editDestination(i, movementDest);
                    int newAmount = calculateBicycleAmount(movementOrigin, movementDest);
                    newAmount = Math.min(newAmount, 30);
                    newState.editBicycleAmount(i, newAmount);
                     //System.out.println("b");
                    successors.add(new Successor("Edit destination, transport" + i + " goes now to->" + movementDest, newState));
                }
            }
        }
        
        
        return successors;
//        for (int i = 0; i < numStations; ++i) {
//            // afegir goal condition que ens diu que acabem si hem mogut totes les furgones i/o ja no queden moviments
//            //if (TallarArbre si sobren bicis) {
//                for (int j = 0; j < numStations; ++j) {
//                        if (i != j) {
//                            for (int z = 0; z <= 30; ++z) {
//                                BicingState newState = new BicingState(state.getBicycleDisposition(), state.getMovements());
//                                newState.simpleMoveBicycles(i, j, z);
//                                successors.add(new Successor(i + " - " + z + " - > " + j, newState));                
//                            }
//                        }
//                    }   
//        }
//        
//        // Simple movements
//        for (int i = 0; i < numStations; ++i) {
//                for (int j = 0; j < numStations; ++j) {
//                    for (int k = 0; k < numStations; ++k)
//                    for (int z = 0; z <= 30; ++z) {
//                        BicingState newState = new BicingState(state.getBicycleDisposition(), state.getMovements());
//                        newState.simpleMoveBicycles(i, j, z);
//                        successors.add(new Successor(i + " - " + z + " - > " + j, newState));                
//                    }
//                }       
//        }
//        
//        return successors;
    } 
}
