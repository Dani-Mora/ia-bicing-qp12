/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Dani
 */
public class BicingState {
       
    private List<Transport> movements;
    private Integer[] estimatedBicyclesNextHour;
    
    public static Integer initialStateSimple;

    public void setInitialState(Integer initialState) {
        this.initialStateSimple = initialState;
    }

    private void initializeEstimations() {
        for (int i = 0; i < Simulation.bicing.getNumStations(); ++i) {
            this.estimatedBicyclesNextHour[i] = Simulation.bicing.getStationNextState(i);
        }
    }
    
    BicingState() {
        // Initializations
        this.movements = new ArrayList<Transport>();
        this.estimatedBicyclesNextHour = new Integer[Simulation.bicing.getNumStations()];
        this.initializeEstimations();
    }
    
    BicingState(Integer nm, List<Transport> mov, Integer[] availableB) {
        // Copy movements
        this.movements = new ArrayList<Transport>(nm);
        for (int i = 0; i < mov.size(); ++i) {
            this.movements.add(mov.get(i));
        }
        // Copy estimations
        this.estimatedBicyclesNextHour = new Integer[availableB.length];
        for (int i = 0; i < availableB.length; ++i) {
            this.estimatedBicyclesNextHour[i] = availableB[i];
        }
    } 

    // INITIAL STATE //
    
    public void calculateInitialState() {
        if (initialStateSimple == 0) {
            this.calculateComplexInitialState();            
        }
        else
        {
            this.calculateSimpleInitialState();     
        }
    }
    /*
    private void calculateComplexInitialState() {
        Integer balancedIndex[] = new Integer[Simulation.bicing.getNumStations()];
        ArrayList<Integer> stationsInNeed = new ArrayList<Integer>(), stationsToSpare = new ArrayList<Integer>();
        
        // Diferenciem d'estacions que necessiten bicis i estacions que li sobren
        for (int i = 0; i < Simulation.bicing.getNumStations(); ++i) {                      
            Integer bicycleSurplus = calculateBicycleSurplus(i);
            balancedIndex[i] = bicycleSurplus;
            if (bicycleSurplus > 0) stationsToSpare.add(i);
            else if (bicycleSurplus < 0) stationsInNeed.add(i);
        }
        
        // anem fent moviments de bicis de les toSpare a les InNeed.
        // Eliminem de la llista toSpare les que ja siguin origen (restricció) i si el festi
        // ja te suficients bicis la descartem de la InNeed
        // Anem assignant de la primera
        // Condició final: s'acabi alguna de les llistes i/o ja tinguem F moviments
        
        int movementCount = 0;
        while (!stationsToSpare.isEmpty() && !stationsInNeed.isEmpty() && movementCount < Simulation.NUM_VANS) {
            Integer indexOrigin = stationsToSpare.get(0), indexDest;
            if (!stationsInNeed.isEmpty()) {
                Integer bicToTransport, balanceOrigin, balanceDest;
                indexDest = stationsInNeed.get(0); //first element on this list           
                balanceOrigin = balancedIndex[indexOrigin];
                balanceDest = balancedIndex[indexDest];
                // Agafo el mínim del que necessiten, del que hi ha a orige i de 30
                bicToTransport = Math.min(balanceOrigin, Math.abs(balanceDest));
                bicToTransport = Math.min(bicToTransport, 30);

                balancedIndex[indexOrigin] -= bicToTransport;
                balancedIndex[indexDest] += bicToTransport;
                
                Transport t = new Transport(indexOrigin, indexDest, bicToTransport);
                this.addMovement(t);
                ++movementCount;
                
                if (balancedIndex[indexDest] >= 0) {
                    stationsInNeed.remove(0);
                }
                
                stationsToSpare.remove(0); //la estacion de origen es inmediatamente eliminada de la lista
            }
            else System.out.println("ERROR: stationsInNeed is Empty - This cannot happen");
        }   
    } */
    
    
    private void calculateComplexInitialState() {
        Integer balancedIndex[] = new Integer[Simulation.bicing.getNumStations()];
        ArrayList<Integer> stationsInNeed = new ArrayList<Integer>(), stationsToSpare = new ArrayList<Integer>();
        
        // Diferenciem d'estacions que necessiten bicis i estacions que li sobren
        for (int i = 0; i < Simulation.bicing.getNumStations(); ++i) {                      
            Integer bicycleSurplus = calculateBicycleSurplus(i);
            balancedIndex[i] = bicycleSurplus;
            if (bicycleSurplus > 0) stationsToSpare.add(i);
            else if (bicycleSurplus < 0) stationsInNeed.add(i);
        }
        
        // anem fent moviments de bicis de les toSpare a les InNeed.
        // Eliminem de la llista toSpare les que ja siguin origen (restricció) i si el festi
        // ja te suficients bicis la descartem de la InNeed
        // Anem assignant de la primera
        // Condició final: s'acabi alguna de les llistes i/o ja tinguem F moviments
        
        int movementCount = 0;
        while (!stationsToSpare.isEmpty() && !stationsInNeed.isEmpty() && movementCount < Simulation.NUM_VANS) {
            Integer indexOrigin = stationsToSpare.get(0), indexDest = -1;
            System.out.println("indexOrigin|indexDest " + indexOrigin + "|" + indexDest);
            boolean destinationFound = false;
            Integer balanceOrigin = balancedIndex[indexOrigin], bicToTransport, balanceDest, posDEST = 0;
            Integer indexDestMAX = -1, bicToTransportMAX = 0;
            for (int i = 0; i < stationsInNeed.size(); ++i) {
                indexDest = stationsInNeed.get(i);

                balanceDest = balancedIndex[indexDest];
                bicToTransport = Math.min(balanceOrigin, Math.abs(balanceDest));
                bicToTransport = Math.min(bicToTransport, 30);
                //System.out.println("FOR: i|indexDest|balanceDest|bicToTransport|indexDestMax" + i + "|" + indexDest + "|" + balanceDest + "|" + bicToTransport + "|" + indexDestMAX);
                if (bicToTransport > bicToTransportMAX) {
                    bicToTransportMAX = bicToTransport;
                    indexDestMAX = indexDest;
                    posDEST = i;
                    destinationFound = true;
                }
            }
            if (destinationFound) {
                //System.out.println("destinationFound");
                balanceDest = balancedIndex[indexDestMAX];
                bicToTransport = Math.min(balanceOrigin, Math.abs(balanceDest));
                bicToTransport = Math.min(bicToTransport, 30);

                balancedIndex[indexOrigin] -= bicToTransport;
                balancedIndex[indexDestMAX] += bicToTransport;

                Transport t = new Transport(indexOrigin, indexDestMAX, bicToTransport);
                this.addMovement(t);
                ++movementCount;

                if (balancedIndex[indexDestMAX] >= 0) {
                    //System.out.println("esborrem, posdest: " + posDEST);
                    //stationsInNeed.remove(posDEST);
                    boolean b = false;
                    for (int i = 0; i < stationsInNeed.size() && !b; ++i) {
                        if (stationsInNeed.get(i).equals(indexDestMAX)) {
                            stationsInNeed.remove(i);
                            b = true;
                        }
                    }
                    //System.out.println("stationsToSpare size : " + stationsToSpare.size());
                }
                //else break;
            }
            else System.out.println("ERROR: stationsInNeed is Empty - This cannot happen");
            stationsToSpare.remove(0);
            System.out.println("stationsToSpare size : " + stationsToSpare.size());
        }   
    }
    
    // We calculate the max amount of bicycles we could move from station "i"
    public static int calculateBicycleSurplus(Integer station) {
        int doNotMove, nextHour, demand;
        doNotMove = Simulation.bicing.getStationDoNotMove(station);
        nextHour = Simulation.bicing.getStationNextState(station);
        demand = Simulation.bicing.getDemandNextHour(station);
        return Math.min(nextHour - demand, doNotMove);
    }

    private void calculateSimpleInitialState() {       
        for (int i = 0; i < Simulation.NUM_VANS; ++i) {
            this.addRandomMovement(Boolean.FALSE);
        }
    }

    // MOVEMENTS //
    
    public List<Transport> getMovements() {
        return movements;
    }

    public void eraseMovement(Transport transp) {
        this.estimatedBicyclesNextHour[transp.getOrigin()] += transp.getBicyclesAmount();
        if (transp.HasTwoDestinations()) {
            this.estimatedBicyclesNextHour[transp.getPreferredDestination()] -= (transp.getBicyclesAmount() - transp.getBicyclesToSecondDest());
            this.estimatedBicyclesNextHour[transp.getSecondDestination()] -= transp.getBicyclesToSecondDest();
        }
        else {
            this.estimatedBicyclesNextHour[transp.getPreferredDestination()] -= transp.getBicyclesAmount();
        }
        this.movements.remove(transp);
    }
    
    public void eraseMovement(Integer index) {
        this.eraseMovement(this.movements.get(index));
    }
    
    // pre: Not all movements done yet
    public void addMovement(Transport transp) {
        this.estimatedBicyclesNextHour[transp.getOrigin()] -= transp.getBicyclesAmount();
        if (transp.HasTwoDestinations()) {
            this.estimatedBicyclesNextHour[transp.getPreferredDestination()] += transp.getBicyclesAmount() - transp.getBicyclesToSecondDest();
            this.estimatedBicyclesNextHour[transp.getSecondDestination()] += transp.getBicyclesToSecondDest();
        }
        else {
            this.estimatedBicyclesNextHour[transp.getPreferredDestination()] += transp.getBicyclesAmount();
        }
        //System.out.println("Afegit moviment: " + transp.getOrigin() + " -> " + transp.getPreferredDestination() + " (" + transp.getBicyclesAmount() + ")" );
        this.movements.add(transp);
    }
    
//    public void editDestination(Integer indexMovement, Integer newDestination, Integer newAmount) {
//        Transport t = this.movements.get(indexMovement);
//        Integer oldAmount = t.getBicyclesAmount(), oldDest = t.getPreferredDestination();
//        Integer origin = t.getOrigin();
//        
//        this.estimatedBicyclesNextHour[oldDest] -= oldAmount;
//        this.estimatedBicyclesNextHour[origin] += oldAmount;
//        
//        this.estimatedBicyclesNextHour[newDestination] += newAmount;
//        this.estimatedBicyclesNextHour[origin] -= newAmount;
//        
//        t.setBicyclesAmount(newAmount);
//        t.setPreferredDestination(newDestination);
//        
//        this.movements.set(indexMovement, t);
//    }
    
    public void addRandomMovement(Boolean intelligentOrigin) {
        Random rand = new Random();
        List<Integer> origins = this.getStationsToSpare();
        
        if (!origins.isEmpty()) {
            Integer origin = origins.get(rand.nextInt(origins.size())); // random origin
            List<Integer> stationsInNeed = this.getStationsinNeed(); 
            if (!stationsInNeed.isEmpty()) {
                Integer dest = rand.nextInt(stationsInNeed.size()); // random dest
                Integer amount = Math.min(30, rand.nextInt(Simulation.bicing.getStationDoNotMove(origin)));
                this.addMovement(new Transport(origin, dest, amount));
            }
        }
    }
    
    public void eraseRandomMovement() {
        List<Transport> transports = this.getMovements();     
        if (transports.size() > 0) {
            Collections.shuffle(transports);
            this.eraseMovement((Transport)transports.iterator().next());
        }    
    }
    
    public Integer getReceivedBycicles(Integer station) {
        Integer result = 0;
        Iterator transpIter = this.getMovements().iterator();
        while(transpIter.hasNext()) {
            Transport transp = (Transport)transpIter.next();
            if (transp.HasTwoDestinations()) {
                if (transp.getPreferredDestination() == station) {
                    result += transp.getBicyclesAmount() - transp.getBicyclesToSecondDest();
                }
                else if (transp.getSecondDestination() == station) {
                    result += transp.getBicyclesToSecondDest();
                }
            }
            else {
                if (transp.getPreferredDestination() == station) {
                    result += transp.getBicyclesAmount();
                }
            }
        }   
        return result;
    }
    
    public Boolean stationAlreadyOrigin(Integer station) {
        Iterator transpIter = this.getMovements().iterator();
        while(transpIter.hasNext()) {
            Transport transp = (Transport)transpIter.next();
            if (transp.getOrigin() == station) {
                return true;
            }
        }
        return false;
    }
    
    // AVAILABLE BICYCLES //
    
    public Integer getBicyclesNextHour(Integer station) {
        return this.estimatedBicyclesNextHour[station];
    }
    
    public void setBicyclesNextHour(Integer station, Integer newAmount) {
        this.estimatedBicyclesNextHour[station] = newAmount;
    }

    public Integer[] getAllBicyclesNextHour() {
        return estimatedBicyclesNextHour;
    }
    
    
    // OPERATORS //
    
    // Pre: both movements are simple
    /*public void swapOrigins(Integer index1, Integer index2) {
        Transport first = this.getMovements().get(index1);
        Transport second = this.getMovements().get(index2);
        
        int idOrigin1, idDest1, idOrigin2, idDest2, newAmount1, newAmount2;
        idOrigin1 = first.getOrigin();
        idDest2 = second.getPreferredDestination();
        idOrigin2 = second.getOrigin();
        idDest1= first.getPreferredDestination();

        newAmount1 = Simulation.bicing.getDemandNextHour(idDest1) - this.getBicyclesNextHour(idDest1); // les que necessito
        newAmount1 = Math.min(newAmount1, Math.min(Simulation.bicing.getStationDoNotMove(idOrigin2), 30));              

        newAmount2 = Simulation.bicing.getDemandNextHour(idDest2) - this.getBicyclesNextHour(idDest2); // les que necessito
        newAmount2 = Math.min(newAmount2, Math.min(Simulation.bicing.getStationDoNotMove(idOrigin1), 30)); 
        
        this.estimatedBicyclesNextHour[idOrigin1] += newAmount1 - first.getBicyclesAmount() ;
        this.estimatedBicyclesNextHour[idOrigin2] += newAmount2 - second.getBicyclesAmount();
        
        first.setOrigin(idOrigin2);
        second.setOrigin(idOrigin1);
        first.setBicyclesAmount(newAmount1);
        second.setBicyclesAmount(newAmount2);
    }
*/
    
    // AUX FUNCTIONS //
    
    public static List getInitialStationsToSpare() {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < Simulation.bicing.getNumStations(); ++i) {
            int balance = BicingState.calculateBicycleSurplus(i);
            if (balance > 0) result.add(i);
        }
        return result;
    }
    
    public static List getInitialStationsInNeed() {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < Simulation.bicing.getNumStations(); ++i) {
            int balance = BicingState.calculateBicycleSurplus(i);
            if (balance < 0) result.add(i);
        }
        return result;
    }
    
    public List getStationsinNeed() {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < Simulation.bicing.getNumStations(); ++i) {
            int balance = this.getBicyclesNextHour(i) - Simulation.bicing.getDemandNextHour(i);
            if (balance < 0) result.add(i);
        }
        return result;
    }
    
    public List getStationsToSpare() {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < Simulation.bicing.getNumStations(); ++i) {
            int balance = this.getBicyclesNextHour(i) - Simulation.bicing.getDemandNextHour(i);
            if (balance > 0 && !this.stationAlreadyOrigin(i) && Simulation.bicing.getStationDoNotMove(i) > 0) result.add(i);
        }
        return result;
    }   
}
