/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Dani
 */
public class Successors implements SuccessorFunction {

    
    @Override
    public List getSuccessors(Object o) {
        BicingState state = (BicingState) o;
        Simulation.finalState = state;
        ArrayList successors = new ArrayList();   
        if (state.getMovements().size() < Simulation.NUM_VANS) {
         List hola = this.getAllTransports(state, false);
         System.out.println("Tamany de agregar transports: " + hola.size());
         successors.addAll(hola);
        }
        List hola1 = this.EraseTransports(state);
        List hola2 = this.UnifyTransports(state);
        List hola3 = this.getDestinationAndAmountChanges(state);
        List hola4 = this.getOriginChanges(state);
        List hola5 = this.getExtraDestinations(state, false);
        
        System.out.println("Tamany d'eliminar transports: " + hola1.size());
        System.out.println("Tamany d'unificar transports: " + hola2.size());
        System.out.println("Tamany de canviar DESTINACIONS: " + hola3.size());
        System.out.println("Tamany de canviar ORIGENS: " + hola4.size());
        System.out.println("Tamany de AFEGIR DESTINACIO: " + hola5.size());
        
        //successors.addAll(hola5);
        //successors.addAll(hola3);
        //successors.addAll(hola4);
        successors.addAll(hola1);       
        //successors.addAll(hola2);
        
        
        System.out.println("Generació de successors"); 
        Simulation.numSuc += successors.size();
        return successors;
       
    }
    
    private int calculateBicycleAmount(int indexOrigin, int indexDestination) {
        int surplusOr, surplusDt;
        surplusOr = BicingState.calculateBicycleSurplus(indexOrigin);
        surplusDt = BicingState.calculateBicycleSurplus(indexDestination);        
        return Math.min(surplusOr, Math.abs(surplusDt));
    }
    
    /*private List getOriginSwap(BicingState state) {
        ArrayList successors = new ArrayList();      
        for (int i = 0; i < state.getMovements().size(); ++i) {
            for (int j = i+1; j < state.getMovements().size(); ++j) {
                
                BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());
                
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
        return successors;
    }*/
   
    // TODO: en comptes de fer de 1 a k podem precalcular-ho
    private List getExtraDestinations(BicingState state, boolean dock) {             
        ArrayList successors = new ArrayList();            
        List<Integer> stationsInNeed = state.getStationsinNeed();
        for (int i = 0; i < state.getMovements().size(); ++i) {
            Transport transp = state.getMovements().get(i);
            Integer movementOrigin = transp.getOrigin();
            Integer bicyclesLeft = Simulation.bicing.getStationDoNotMove(movementOrigin) - transp.getBicyclesAmount(); 
            if (!transp.HasTwoDestinations() && bicyclesLeft > 0) {
                Integer amountToFirst = transp.getBicyclesAmount();
                Integer movementFirstDest = transp.getPreferredDestination();
                for (int j = 0; j < stationsInNeed.size(); ++j) {
                    Integer movementNewSecondDest = stationsInNeed.get(j);               
                    if (!movementNewSecondDest.equals(movementOrigin) && !movementFirstDest.equals(movementNewSecondDest)) {                       
                            Integer maxAmount = Math.min(Simulation.bicing.getStationDoNotMove(movementOrigin) - amountToFirst, 30);
                            for (int k = 1; k <= maxAmount; ++k) {
                                BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());
                                Transport newTransport = new Transport(movementOrigin, movementFirstDest, movementNewSecondDest, amountToFirst + k, k);
                                if (!dock || dock && newTransport.worthyTransport()) {
                                    newState.eraseMovement(transp);
                                    newState.addMovement(newTransport);
                                    successors.add(new Successor("ADD destination, origin" + movementOrigin + " goes ALSO to-> " + movementNewSecondDest + "amountFIRST|SECOND " + transp.getBicyclesAmount() + "|" + k, newState));             
                                }

                            }     
                        }
                    }
                }
        }   
        return successors;
    }
    
    // WTF hace?
    private List getDestinationAndAmountChanges(BicingState state) {   
        ArrayList successors = new ArrayList();          
        List<Integer> stationsInNeed = state.getStationsinNeed();
        
        for (int i = 0; i < state.getMovements().size(); ++i) {
            for (int j = 0; j < stationsInNeed.size(); ++j) {
                Integer movementDest = stationsInNeed.get(j);
                Integer movementOrigin = state.getMovements().get(i).getOrigin();
                if (!movementDest.equals(movementOrigin)) {
                    BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());
                    int newAmount = calculateBicycleAmount(movementOrigin, movementDest);               
                    newAmount = Math.min(newAmount, 30);
                    newState.eraseMovement(state.getMovements().get(i));
                    newState.addMovement(new Transport(movementOrigin,movementDest,newAmount));
                    successors.add(new Successor("Edit destination, origin" + movementOrigin + " goes now to-> " + movementDest + " ", newState));
                }
            }
        }   
        return successors;
    }   
    
    private List getOriginChanges(BicingState state) {
        
        ArrayList successors = new ArrayList();     
        Integer numStations = Simulation.bicing.getNumStations(); 
        BicingHeuristic bicingHF = new BicingHeuristic();       
        List<Integer> stationsToSpare = BicingState.getInitialStationsToSpare();

        for (int i = 0; i < state.getMovements().size(); ++i) {
            int indexOrig = state.getMovements().get(i).getOrigin();
            boolean found = false;
            for (int j = 0; j < stationsToSpare.size() && !found; ++j) {
                if (stationsToSpare.get(j).equals(indexOrig)) {
                    stationsToSpare.remove(j);
                    found = true;
                }
            }
        }
        for (int i = 0; i < state.getMovements().size(); ++i) {
            Integer movementDest = state.getMovements().get(i).getPreferredDestination();
            for (int j = 0; j < stationsToSpare.size(); ++j) {
                //int movementDest = state.getMovements().get(i).getPreferredDestination();
                //esta linea no tiene sentido. La estacion de destino es la stationsInNeed[j]
                Integer movementOrig = stationsToSpare.get(j);
                
                if (!movementOrig.equals(movementDest)) {
                    BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());
                    int newAmount = calculateBicycleAmount(movementOrig, movementDest);  
                    if (newAmount > 0) {
                        newAmount = Math.min(newAmount, 30);
                        //System.out.println("getOriginChanges I, J, movDEST->movORIG, newAmount "
                        //        + i + " " + j + " " + movementDest+ " "  + movementOrig + " " + newAmount);
                        //newState.editDestination(i, movementDest, newAmount);
                        newState.eraseMovement(state.getMovements().get(i));
                        newState.addMovement(new Transport(movementOrig, movementDest,newAmount));
                        //newState.editDestination(i, movementDest); //aqui cambiaremos el valor de estimatedBicyclesNextHour del viejo y del nuevo destino
                        //newState.editBicycleAmount(i, newAmount);
                            //System.out.println("b");

                       // double d = bicingHF.getHeuristicValue(newState);
                       // System.out.println("Successors newState heuristic = " + d);

                        successors.add(new Successor("Edit ORIGIN, destination is: " + movementDest + " receives now from-> " + movementOrig + " ", newState));
                    }
                }
            }
        }   
        return successors;
    }
    
    private List getAllTransports(BicingState state, Boolean transportCost) {        
        ArrayList successors = new ArrayList();
        List<Integer> origins = state.getStationsToSpare();
        List<Integer> dests = state.getStationsinNeed();
        for (int i = 0; i < origins.size(); ++i) {
            Integer currentOrigin = origins.get(i);
            for (int j = 0; j < dests.size(); ++j) {
                Integer currentDest = dests.get(j);
                if (currentOrigin != dests.get(j)) {
                    Integer maxB = Math.min(BicingState.calculateBicycleSurplus(i), 30);
                    for (int z = 1; z <= maxB; ++z) {
                        BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());
                        Transport newTransport = new Transport(currentOrigin,currentDest,z);
                        if (!transportCost || transportCost && newTransport.worthyTransport()) {
                                BicingHeuristic heuristic = new BicingHeuristic();
                                newState.addMovement(newTransport);
                                //System.out.println("Moviment " + currentOrigin " -> " + currentDest + " (" + z + ")" + "Heuristic: " + heuristic.getComplexHeuristic(newState));
                                successors.add(new Successor("Added movement: " + i + " -> " + j + "(" + z + ")", newState));
                        }
                        
                    }
                }
            }           
        }
        return successors;
    }
    
    private List EraseTransports(BicingState state) {
        ArrayList successors = new ArrayList();       
        for (int i = 0; i < state.getMovements().size(); ++i) {
            BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());   
            newState.eraseMovement(i);
            successors.add(new Successor("Erased movement " + i, newState));  
        }  
        return successors;
    }
    
    private List UnifyTransports(BicingState state) {
        ArrayList successors = new ArrayList();
        Iterator iterator =  state.getMovements().iterator();  
        while(iterator.hasNext()) {
            Transport transp1 = (Transport) iterator.next();
            if (!transp1.HasTwoDestinations()) {          
                Iterator subIterator = state.getMovements().iterator();
                while (subIterator.hasNext()) {
                    Transport transp2 = (Transport) subIterator.next();
                    if (!transp2.HasTwoDestinations() && !transp1.equals(transp2)) {
                        Integer origin1= transp1.getOrigin();
                        Integer origin2= transp2.getOrigin();
                        Integer dest1 = transp1.getPreferredDestination();
                        Integer dest2 = transp2.getPreferredDestination();
                        Integer amount1 = transp1.getBicyclesAmount();
                        Integer amount2 = transp2.getBicyclesAmount();
                        Integer total = amount1 + amount2;
                        // Let's see if we can unify and that the origin has enough bicycles
                        if (dest1 != dest2 && Simulation.bicing.getStationDoNotMove(origin1) >= total) {
                            BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());                       
                            Transport unifiedTransport = new Transport(origin1, dest1, dest2, total, amount2);
                            newState.eraseMovement(transp1);
                            newState.eraseMovement(transp2);
                            newState.addMovement(unifiedTransport);
                            successors.add(new Successor("Unified movement", newState));
                    }
                }
                }
            }
        }
        return successors;
    }
}
