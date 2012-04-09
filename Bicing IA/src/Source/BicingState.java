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
    
    private Integer initialState = 0;

    public void setInitialState(Integer initialState) {
        this.initialState = initialState;
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
        if (initialState == 0) {
            this.calculateComplexInitialState();            
        }
        else
        {
            this.calculateSimpleInitialState();     
        }
    }
    
    private void calculateComplexInitialState() {
        System.out.println("SITUATION ANALYSIS STARTED");
        
        Integer balancedIndex[] = new Integer[Simulation.bicing.getNumStations()];
        ArrayList<Integer> stationsInNeed = new ArrayList<Integer>(), stationsToSpare = new ArrayList<Integer>();
        
        // Diferenciem d'estacions que necessiten bicis i estacions que li sobren
        for (int i = 0; i < Simulation.bicing.getNumStations(); ++i) {                      
            Integer bicycleSurplus = calculateBicycleSurplus(i);
            balancedIndex[i] = bicycleSurplus;
            if (bicycleSurplus > 0) {
                System.out.println("BICYCLES WE CAN TAKE: " + bicycleSurplus);
                stationsToSpare.add(i);
            }
            else if (bicycleSurplus < 0) {
                System.out.println("BICYCLES NEEDED: " + bicycleSurplus);
                stationsInNeed.add(i);
            }
            else {
                System.out.println("NO BICYCLES AVAILABLE");
            }
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

                System.out.println("MOVEMENT " + movementCount + " SELECTED ");
                System.out.println("ORIGIN ST: " + indexOrigin);
                System.out.println("DEST ST: " + indexDest);
                System.out.println("BICYCLE AMOUNT: " + bicToTransport);
                System.out.println("BALANCE BEFORE AND AFTER (OR) " + balanceOrigin + "   " + balancedIndex[indexOrigin]);
                System.out.println("BALANCE BEFORE AND AFTER (DEST) " + balanceDest + "   " + balancedIndex[indexDest]);
            }
            else System.out.println("ERROR: stationsInNeed is Empty - This cannot happen");
        }
        
        System.out.println("NUMBER OF MOVEMENTS DECIDED: " + movementCount);     
        
        System.out.println("SITUATION ANALYSIS FINISHED");
    }
    
    // We calculate the max amount of bicycles we could move from station "i"
    private int calculateBicycleSurplus(Integer station) {
        int doNotMove, nextHour, demand;
        doNotMove = Simulation.bicing.getStationDoNotMove(station);
        nextHour = Simulation.bicing.getStationNextState(station);
        demand = Simulation.bicing.getDemandNextHour(station);
        System.out.println("Station " + station + " DO NOT MOVE " + doNotMove + " NEXT HOUR " + nextHour + " DEMAND " + demand);
        return Math.min(nextHour - demand, doNotMove);
    }   

    private void calculateSimpleInitialState() {       
        for (int i = 0; i < Simulation.NUM_VANS; ++i) {
            this.AddRandomMovement(Boolean.FALSE);
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
    
    public void editDestination(Integer indexMovement, Integer newDestination, Integer newAmount) {
        Transport t = this.movements.get(indexMovement);
        Integer oldAmount = t.getBicyclesAmount(), oldDest = t.getPreferredDestination();
        Integer origin = t.getOrigin();
        
        this.estimatedBicyclesNextHour[oldDest] -= oldAmount;
        this.estimatedBicyclesNextHour[origin] += oldAmount;
        
        this.estimatedBicyclesNextHour[newDestination] += newAmount;
        this.estimatedBicyclesNextHour[origin] -= newAmount;
        
        t.setBicyclesAmount(newAmount);
        t.setPreferredDestination(newDestination);
        
        this.movements.set(indexMovement, t);
    }
    
    // problema: pot decidir agafar 0 bicis
    // TODO improve
    // intelligentOrigin: agafa bicis només d'on sobrin i fa numStations intents. Sinó, agafa una qualssevol
    public void AddRandomMovement(Boolean intelligentOrigin) {
        Random rand = new Random();
        Integer origin, dest, amount, numStations = Simulation.bicing.getNumStations();
        
        Integer counter = 0;
        origin = rand.nextInt(numStations);
        Boolean improvement = this.getBicyclesNextHour(origin) < Simulation.bicing.getDemandNextHour(origin);
        while (!this.stationAlreadyOrigin(origin) && Simulation.bicing.getStationDoNotMove(origin) == 0 && (!intelligentOrigin || intelligentOrigin && improvement)) {
            origin = rand.nextInt(numStations);
            improvement = this.getBicyclesNextHour(origin) < Simulation.bicing.getDemandNextHour(origin);
            if (++counter >= numStations) break;
        }
        
        if (counter >= numStations) {
            origin = rand.nextInt(numStations);
        }
        
        dest = rand.nextInt(numStations);
        amount = Math.min(30,rand.nextInt(Simulation.bicing.getStationDoNotMove(origin)));
        while (dest == origin) {
            dest = rand.nextInt(numStations);
            amount = Math.min(30,rand.nextInt(Simulation.bicing.getStationDoNotMove(origin)));
        }
        this.addMovement(new Transport(origin, dest, amount));
    }
    
    public void EraseRandomMovement() {
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
    
    public void exchangeOrigin(Integer indexA, Integer indexB) {
        Transport tA, tB;
        int valueA, valueB;
        
        tA = this.movements.get(indexA); 
        valueA = tA.getOrigin();
        tB = this.movements.get(indexB); 
        valueB = tB.getOrigin();
        
        this.estimatedBicyclesNextHour[indexA] += tB.getBicyclesAmount() - tA.getBicyclesAmount();
        this.estimatedBicyclesNextHour[indexB] += tA.getBicyclesAmount() - tB.getBicyclesAmount();
        
        tA.setOrigin(valueB);
        tB.setOrigin(valueA);
        
        this.movements.set(indexA, tA); // caldria fer aixo?
        this.movements.set(indexB, tB); // caldria fer aixo?
    }
    
    // TODO arreglar
    public void editBicycleAmount(Integer index, Integer newAmount) {
        Transport t = this.movements.get(index);
        t.setBicyclesAmount(newAmount);
        this.movements.set(index, t);
    }
    
    // ** TODO **/

}
