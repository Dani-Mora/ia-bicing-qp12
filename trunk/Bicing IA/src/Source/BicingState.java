/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import IA.Bicing.Bicing;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Dani
 */
public class BicingState {
       
    private Integer[] bicOnStation; 
    private Integer[] bicBalanceOnStation;
    private List<Transport> movements;
    
    private Integer initialState = 1;

    public void setInitialState(Integer initialState) {
        this.initialState = initialState;
    }

    BicingState(Integer numStations) {
        this.movements = new ArrayList<Transport>();
        this.bicOnStation = new Integer[numStations];
        this.bicBalanceOnStation = new Integer[numStations];
        for (int i = 0; i < this.bicOnStation.length; ++i) {
            this.bicOnStation[i] = 0;
        }    
    }
    
    BicingState(Integer[] bicOnStations, List<Transport> movements) {
        this.bicOnStation = bicOnStations;
        this.movements = movements;
    }
    
    BicingState(Integer nm, List<Transport> mov) {
        this.movements = new ArrayList<Transport>(nm);
        for (int i = 0; i < mov.size(); ++i) {
            this.movements.add(mov.get(i));
        }
    }
   

    public void calculateInitialState(Bicing context, Integer numBic, Integer numVans) {
        if (initialState == 0) {
            this.calculateComplexInitialState(context, numBic, numVans);            
        }
        else
        {
            this.calculateSimpleInitialState(context, numBic, numVans);     
        }
    }
    
    private Integer calculateDemandAverage(Bicing context) {
        Integer average = 0;
        for (int i = 0; i < this.bicOnStation.length; ++i) {
            average += context.getDemandNextHour(i);
        }
        average /= this.bicOnStation.length;
        return average;
    }
    
    private Integer calculateTotalAverage(Integer numBic) {
        Integer result = numBic/this.bicOnStation.length;
        return result;
    } 
    
    private void calculateComplexInitialState(Bicing context, Integer numBic, Integer numVans) {
        Integer demandBicycleAverage = this.calculateDemandAverage(context);
        Integer totalBicycleAverage =  this.calculateTotalAverage(numBic);
        Integer bicLeft = numBic;
        Integer belowDemandCounter = 0;
        
        // We assign the total average, that could be below the demand
        for (int i = 0; i < this.bicOnStation.length; ++i) {
            Integer demand = context.getDemandNextHour(i);
            Integer assignedBicycles = demand;
            if (demand <= totalBicycleAverage) {
                assignedBicycles = demand;
            }
            else {
                assignedBicycles = totalBicycleAverage;
                ++belowDemandCounter;
            }
            bicLeft -= assignedBicycles;
            if (bicLeft < 0) {
                System.out.println("ERROR: no bicycles left");
            }
            this.bicOnStation[i] = assignedBicycles;
        }
        
        // bicLeft >= 0
        int iter = 0;
        while (bicLeft > 0) {
            Integer assignedBicycles = 0;
            Integer currentBicycles = this.bicOnStation[iter];
            Integer neededBicycles = context.getDemandNextHour(iter) - currentBicycles;
            if (neededBicycles < 0 && neededBicycles <= bicLeft) {
                assignedBicycles = neededBicycles; 
            }
            else {
                assignedBicycles = bicLeft;
            }
            bicLeft -= assignedBicycles;
            this.bicOnStation[iter] += assignedBicycles;
            iter = (iter++) % numBic;
        }   
    }
    
    private int calculateBicycleSurplus(int doNotMove, int nextHour, int demand) {
        return Math.min(nextHour - demand, doNotMove);
    }
    

    private void calculateSimpleInitialState(Bicing context, Integer numBic, Integer numVans) {
        //mph
        System.out.println("SITUATION ANALYSIS STARTED");
        
        //1. WE SPLIT THE STATIONS INTO 2 GROUPS, THOSE IN NEED OF BICYCLES
        //  AND THOSE WITH EXTRA BICYCLES
        //2. LISTS ARE  NOT SORTED (se podria hacer)
        //3. ITERATION FROM 0 TO F (OR THE LENGTH OF THE EXTRA LIST, MOVEMENTS ARE ASSIGNED
        // PROGRESSIVELY UP UNTIL EITHER LIST IS FINISHED >>OR<< THE F MOVEMENTS HAVE BEEN
        // ALREADY DECIDED
        
        ArrayList<Integer> stationsInNeed = new ArrayList<Integer>(), stationsToSpare = new ArrayList<Integer>();
        
        for (int i = 0; i < context.getNumStations(); ++i) {
            int donotmove, nexthour, demand, bicyclesToSpare;
            donotmove = context.getStationDoNotMove(i);
            nexthour = context.getStationNextState(i);
            demand = context.getDemandNextHour(i);
            System.out.println("Station " + i + " DO NOT MOVE " + donotmove + " NEXT HOUR " + nexthour + " DEMAND " + demand);
            //using these 3 values, we calculate the max amount of bicycles
            //we could move from station "i"
            int bicycleSurplus = calculateBicycleSurplus(donotmove, nexthour, demand);
            bicBalanceOnStation[i] = bicycleSurplus; //aqui es donde iremos sumando o restando a partir de los movimientos que hagamos
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
            //ahora generamos los movimientos, iteraremos por las dos listas, y se
            // irán asignando movimientos de estaciones de una lista a otra
            // una vez asignado, se descontarán y sumarán esas bicis en el bicAvailableOnStation
            // por ultimo, eliminamos de la lista la estación de origen y si la de destino ya tiene
//              suficientes bicis, se quitará de la lista también. Si se termina la lista de InNeed (que no debe pasar)
//               o bien la lista de ToSpare (que se supone que ha de pasar), paramos, y los movimientos resultantes son
//               nuestra solucion inicial.
//                No tenemos en cuenta nada mas, por el momento
        
        //primera version: meto bicis de la primera posicion ToSpare a la primera InNeed, tal cual
            int movementCount = 0;
            while (!stationsToSpare.isEmpty() && !stationsInNeed.isEmpty() && movementCount < numVans) {
                int indexOrigin = stationsToSpare.get(0), indexDest;
                if (!stationsInNeed.isEmpty()) {
                    indexDest = stationsInNeed.get(0); //first element on this list
                    int bicToTransport, balanceOrigin, balanceDest;
                    balanceOrigin = bicBalanceOnStation[indexOrigin];
                    balanceDest = bicBalanceOnStation[indexDest];
                    bicToTransport = Math.min(balanceOrigin, Math.abs(balanceDest));
                    bicToTransport = Math.min(bicToTransport, 30);
                    ++movementCount;
                    bicBalanceOnStation[indexOrigin] -= bicToTransport;
                    bicBalanceOnStation[indexDest] += bicToTransport;
                    stationsToSpare.remove(0); //la estacion de origen es inmediatamente eliminada de la lista
                    if (bicBalanceOnStation[indexDest] >= 0) {
                        stationsInNeed.remove(0); //si la de destino está ya cubierta, la quitamos
                    }
                    Transport t = new Transport(indexOrigin, indexDest, bicToTransport);
                    boolean add = movements.add(t);
                    if (!add) System.out.println("ERROR WHEN ADDING A MOVEMENT TO THE LIST");
                    
                    System.out.println("MOVEMENT " + movementCount + " SELECTED ");
                    System.out.println("ORIGIN ST: " + indexOrigin);
                    System.out.println("DEST ST: " + indexDest);
                    System.out.println("BICYCLE AMOUNT: " + bicToTransport);
                    System.out.println("BALANCE BEFORE AND AFTER (OR) " + balanceOrigin + "   " + bicBalanceOnStation[indexOrigin]);
                    System.out.println("BALANCE BEFORE AND AFTER (DEST) " + balanceDest + "   " + bicBalanceOnStation[indexDest]);
                }
                else System.out.println("ERROR: stationsInNeed is Empty - This cannot happen");
            }
        
        System.out.println("NUMBER OF MOVEMENTS DECIDED: " + movementCount);

        
        

        
        
        System.out.println("SITUATION ANALYSIS FINISHED");
        
//        Random rand = new Random();
//     
//        Integer bicLeft = numBic;
//        Integer demandBicycleAverage = this.calculateDemandAverage(context);
//        Integer currentStation = 0;
//        int iterWh = 0;
//        while (bicLeft > 0) {
//
//            System.out.println("Bic: " + bicLeft);
//            Integer currentDemand = context.getDemandNextHour(currentStation);System.out.println("currentDemand: " + currentDemand);
//            Integer currentBicycles = this.bicOnStation[currentStation];System.out.println("currentBicycles: " + currentBicycles);
//            Integer aux = currentDemand - currentBicycles;
//            Integer addedBicycles;
//            if (aux <= 0) {
//                addedBicycles = 0;System.out.println("IF");
//                ++iterWh;
//                if (iterWh == this.bicOnStation.length) {
//                    addedBicycles = bicLeft;
//                }
//            }
//            else {
//                iterWh = 0;
//                Integer rnd = rand.nextInt(aux+1); System.out.println("ELSE");
//                addedBicycles = rnd; System.out.println(rnd);
//            } //random value between current bicycles and current demand
//            // no bicycles added if current bicycles > current demand
//            System.out.println("addedBicycles: " + addedBicycles);
//            
//            if (bicLeft < addedBicycles) {
//                addedBicycles = bicLeft;
//            }
//            bicLeft -= addedBicycles;
//            this.bicOnStation[currentStation] += addedBicycles;
//            currentStation++;
//            currentStation %= this.bicOnStation.length;
//            if (bicLeft < 0) {
//                System.out.println("ERROR: no bicycles left");
//            }
//        }
    }
    
    public Integer[] getBicycleDisposition() {
        return this.bicOnStation;
    }
    
    public Integer getNumBicycles(Integer station) {
        return this.bicOnStation[station];
    }

    public List<Transport> getMovements() {
        return movements;
    }

    public void setBicOnStation(Integer[] bicOnStation) {
        this.bicOnStation = bicOnStation;
    }

    public void setMovements(List<Transport> movements) {
        this.movements = movements;
    }
    
    
    
    // OPERATORS //
    
    public void exchangeOrigin(Integer indexA, Integer indexB) {
        Transport tA, tB;
        int valueA, valueB;
        tA = this.movements.get(indexA); valueA = tA.getOrigin();
        tB = this.movements.get(indexB); valueB = tB.getOrigin();
        
        tA.setOrigin(valueB);
        tB.setOrigin(valueA);
        
        this.movements.set(indexA, tA);
        this.movements.set(indexB, tB);
    }
    
    public void editBicycleAmount(Integer index, Integer newAmount) {
        Transport t = this.movements.get(index);
        t.setBicyclesAmount(newAmount);
        this.movements.set(index, t);
    }
    
    public void editDestination(Integer indexMovement, Integer newDestination) {
        Transport t;
        t = this.movements.get(indexMovement);
        t.setPreferredDestination(newDestination);
        this.movements.set(indexMovement, t);
    }
    
    public void simpleMoveBicycles(Integer origen, Integer dest, Integer nBic) {
        this.movements.add(new Transport(origen, dest, nBic));
        this.bicOnStation[origen] -= nBic;
        this.bicOnStation[dest] += nBic;
    }
    
    public void doubleMoveBicycles(Integer origen, Integer dest1, Integer dest2, Integer nBic, Integer secondDestBic) {
        this.movements.add(new Transport(origen, dest1, dest2, nBic, secondDestBic));
        this.bicOnStation[origen] -= nBic;
        this.bicOnStation[dest1] += nBic - secondDestBic;
        this.bicOnStation[dest2] += secondDestBic;
    }
    
    /*
    public void removeMovement(int index) {
        this.movements.remove(index);
    }
     * */
    
    // ** TODO **/

}
