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
        ArrayList successors = new ArrayList();   
//        if (state.getMovements().size() < Simulation.NUM_VANS) {
//         List hola = this.getAllTransports(state, false);
//         System.out.println("Tamany de agregar transports: " + hola.size());
//         successors.addAll(hola);
//        }
     //   List hola1 = this.EraseTransports(state);
        List hola2 = this.UnifyTransports(state);
       List hola3 = this.getTransportChanges(state);
       List hola4 = this.getOriginChanges(state);
        
       // System.out.println("Tamany d'eliminar transports: " + hola1.size());
        System.out.println("Tamany d'unificar transports: " + hola2.size());
        System.out.println("Tamany de canviar DESTINACIONS: " + hola3.size());
        System.out.println("Tamany de canviar ORIGENS: " + hola4.size());
        
        successors.addAll(hola3);
        successors.addAll(hola4);
//        successors.addAll(hola1);
//        
        successors.addAll(hola2);
        
        System.out.println("Generació de successors"); 
        return successors;
       
    }
    
    private int calculateBicycleSurplus(int doNotMove, int nextHour, int demand) {
        return Math.min(nextHour - demand, doNotMove);
    }
    
    private int calculateBicycleSurplus(int indexStation) {
        int dNM, nS, dem;
        dNM = Simulation.bicing.getStationDoNotMove(indexStation);
        nS = Simulation.bicing.getStationNextState(indexStation);
        dem = Simulation.bicing.getDemandNextHour(indexStation);
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
    
    private List getOriginSwap(BicingState state) {
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
    }
   
        private List getTransportChanges(BicingState state) {
        
        ArrayList successors = new ArrayList();     
        Integer numStations = Simulation.bicing.getNumStations(); 
        BicingHeuristic bicingHF = new BicingHeuristic();       
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
                //int movementDest = state.getMovements().get(i).getPreferredDestination();
                //esta linea no tiene sentido. La estacion de destino es la stationsInNeed[j]
                Integer movementDest = stationsInNeed.get(j);
                Integer movementOrigin = state.getMovements().get(i).getOrigin();
                if (!movementDest.equals(movementOrigin)) {
                    BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());
                    int newAmount = calculateBicycleAmount(movementOrigin, movementDest);               
                    newAmount = Math.min(newAmount, 30);
                    //System.out.println("getTransportChanges I, J, movOrigin, movDest, newAmount "
                    //        + i + " " + j + " " + movementOrigin+ " "  + movementDest + " " + newAmount);
                    //newState.editDestination(i, movementDest, newAmount);
                    newState.eraseMovement(state.getMovements().get(i));
                    newState.addMovement(new Transport(movementOrigin,movementDest,newAmount));
                    //newState.editDestination(i, movementDest); //aqui cambiaremos el valor de estimatedBicyclesNextHour del viejo y del nuevo destino
                    //newState.editBicycleAmount(i, newAmount);
                        //System.out.println("b");

                    //double d = bicingHF.getComplexHeuristic(newState);
                    //System.out.println("Successors newState heuristic = " + d);

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
        ArrayList<Integer> stationsToSpare = new ArrayList<Integer>();
        for (int i = 0; i < numStations; ++i) {
            int balance = calculateBicycleSurplus(i);
            if (balance > 0) stationsToSpare.add(i);
        }
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
                        //System.out.println("getTransportChanges I, J, movDEST->movORIG, newAmount "
                        //        + i + " " + j + " " + movementDest+ " "  + movementOrig + " " + newAmount);
                        //newState.editDestination(i, movementDest, newAmount);
                        newState.eraseMovement(state.getMovements().get(i));
                        newState.addMovement(new Transport(movementOrig, movementDest,newAmount));
                        //newState.editDestination(i, movementDest); //aqui cambiaremos el valor de estimatedBicyclesNextHour del viejo y del nuevo destino
                        //newState.editBicycleAmount(i, newAmount);
                            //System.out.println("b");

                        //double d = bicingHF.getComplexHeuristic(newState);
                        //System.out.println("Successors newState heuristic = " + d);

                        successors.add(new Successor("Edit ORIGIN, destination is: " + movementDest + " receives now from-> " + movementOrig + " ", newState));
                    }
                }
            }
        }   
        return successors;
    }
    
    /*
    private List getTransportChanges(BicingState state) {
        
        ArrayList successors = new ArrayList();     
        Integer numStations = Simulation.bicing.getNumStations(); 
        BicingHeuristic bicingHF = new BicingHeuristic();       
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
                int movementDest = state.getMovements().get(i).getPreferredDestination();
                int movementOrigin = state.getMovements().get(i).getOrigin();
                if (DestinationOK(movementDest, stationsInNeed)) {
                   BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAvailableBicyclesNextHour());
                    newState.editDestination(i, movementDest);
                    int newAmount = calculateBicycleAmount(movementOrigin, movementDest);
                    newAmount = Math.min(newAmount, 30);
                    newState.editBicycleAmount(i, newAmount);
                     //System.out.println("b");
                    
                    double d = bicingHF.getSimpleHeuristic(newState);
                    System.out.println("Successors newState heuristic = " + d);
                    
                    successors.add(new Successor("Edit destination, transport" + i + " goes now to->" + movementDest, newState));
                }
            }
        }   
        return successors;
    }*/
    
    // dock -> indicates wether we want to avoid generating innecessary successors
    // possible millora, afegir nomes les bicis de diferencia
    private List getAllTransports(BicingState state, Boolean dock) {
        ArrayList successors = new ArrayList();
        for (int i = 0; i < Simulation.bicing.getNumStations(); ++i) {
            Boolean alreadyOrigin = state.stationAlreadyOrigin(i);
            Boolean canBeOrigin = (state.getBicyclesNextHour(i) - Simulation.bicing.getDemandNextHour(i)) > 0;
            if (alreadyOrigin || (dock && !canBeOrigin) );
            else {
                for (int j = 0; j < Simulation.bicing.getNumStations(); ++j) {
                    Boolean shouldBeDestination = Simulation.bicing.getDemandNextHour(j) > state.getBicyclesNextHour(i);
                    if (!dock || (dock  && shouldBeDestination) ) {
                        Integer maxB = Simulation.bicing.getStationDoNotMove(i);
                        for (int z = 1; z < 30 && z <= maxB; ++z) {
                             BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());
                             newState.addMovement(new Transport(i,j,z));
                             successors.add(new Successor("Added movement: " + i + " -> " + j + "(" + z + ")", newState));
                        }
                    }
                }
            }           
        }
        return successors;
    }
    
    // creo estats redundants, TODO FIX
    private List EraseTransports(BicingState state) {
        ArrayList successors = new ArrayList();
        Iterator iterator =  state.getMovements().iterator();
        while(iterator.hasNext()) {
            BicingState newState = new BicingState(state.getMovements().size(), state.getMovements(), state.getAllBicyclesNextHour());                           
            newState.eraseMovement((Transport) iterator.next());
            successors.add(new Successor("Erased movement", newState));          
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
                        if (Simulation.bicing.getStationDoNotMove(origin1) >= total) {
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
