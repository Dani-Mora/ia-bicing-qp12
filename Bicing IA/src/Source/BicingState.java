/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Dani
 */
public class BicingState {
       
    private Integer[] bicBalanceOnStation;
    private List<Transport> movements;
    
    private Integer initialState = 1;

    public void setInitialState(Integer initialState) {
        this.initialState = initialState;
    }

    BicingState(Integer numStations) {
        this.movements = new ArrayList<Transport>();
        this.bicBalanceOnStation = new Integer[numStations];   
    }
    
    BicingState(List<Transport> movements) {
        this.movements = movements;
    }
    
    BicingState(Integer nm, List<Transport> mov) {
        this.movements = new ArrayList<Transport>(nm);
        for (int i = 0; i < mov.size(); ++i) {
            this.movements.add(mov.get(i));
        }
    } 

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
        //
    }
    
    private int calculateBicycleSurplus(int doNotMove, int nextHour, int demand) {
        return Math.min(nextHour - demand, doNotMove);
    }
    

    private void calculateSimpleInitialState() {
        //mph
        System.out.println("SITUATION ANALYSIS STARTED");
        
        //1. WE SPLIT THE STATIONS INTO 2 GROUPS, THOSE IN NEED OF BICYCLES
        //  AND THOSE WITH EXTRA BICYCLES
        //2. LISTS ARE  NOT SORTED (se podria hacer)
        //3. ITERATION FROM 0 TO F (OR THE LENGTH OF THE EXTRA LIST, MOVEMENTS ARE ASSIGNED
        // PROGRESSIVELY UP UNTIL EITHER LIST IS FINISHED >>OR<< THE F MOVEMENTS HAVE BEEN
        // ALREADY DECIDED
        
        ArrayList<Integer> stationsInNeed = new ArrayList<Integer>(), stationsToSpare = new ArrayList<Integer>();
        
        for (int i = 0; i < Simulation.bicing.getNumStations(); ++i) {
            int donotmove, nexthour, demand, bicyclesToSpare;
            donotmove = Simulation.bicing.getStationDoNotMove(i);
            nexthour = Simulation.bicing.getStationNextState(i);
            demand = Simulation.bicing.getDemandNextHour(i);
            System.out.println("Station " + i + " DO NOT MOVE " + donotmove + " NEXT HOUR " + nexthour + " DEMAND " + demand);
            //using these 3 values, we calculate the max amount of bicycles
            //we could move from station "i"
            int bicycleSurplus = calculateBicycleSurplus(donotmove, nexthour, demand);
            this.bicBalanceOnStation[i] = bicycleSurplus; //aqui es donde iremos sumando o restando a partir de los movimientos que hagamos
            if (bicycleSurplus > 0) {
                // afegim estacio que te bicis sobrants
                System.out.println("BICYCLES WE CAN TAKE: " + bicycleSurplus);
                stationsToSpare.add(i);
            }
            else if (bicycleSurplus < 0) {
                // estacio que necessita bicis
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
            while (!stationsToSpare.isEmpty() && !stationsInNeed.isEmpty() && movementCount < Simulation.NUM_VANS) {
                int indexOrigin = stationsToSpare.get(0), indexDest;
                if (!stationsInNeed.isEmpty()) {
                    indexDest = stationsInNeed.get(0); //first element on this list
                    int bicToTransport, balanceOrigin, balanceDest;
                    balanceOrigin = bicBalanceOnStation[indexOrigin];
                    balanceDest = bicBalanceOnStation[indexDest];
                    // Agafo el mínim del que necessiten, del que hi ha a orige i de 30
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
    }

    public List<Transport> getMovements() {
        return movements;
    }

    public void setMovements(List<Transport> movements) {
        this.movements = movements;
    }   
    
    public Integer getBalancedIndex(Integer station) {
        System.out.println("Looking for index of: " + station);
        for (int i = 0; i < this.bicBalanceOnStation.length; ++i) System.out.println(this.bicBalanceOnStation[i]);
        return this.bicBalanceOnStation[station];
    }
    
    // No sé si la usaremos porque ya tenemos el balanced, pero la he hecho por si acaso
    public Integer getReceivedBicycles(Integer station) {
        Integer result = 0;
        Iterator iterator = this.movements.iterator();
        while(iterator.hasNext()) {
            Transport transp = (Transport) iterator.next();
            if (transp.getPreferredDestination() == station) {          
                if (transp.HasTwoDestinations()) {
                    result += transp.getBicyclesAmount() - transp.getBicyclesToSecondDest();
                }
                else {
                    result += transp.getBicyclesAmount();
                }
            }
            else if (transp.getSecondDestination() == station) {
                result += transp.getBicyclesToSecondDest();
            }
        }
        return result;
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
       
    public void removeMovement(Integer index) {
        this.movements.remove(index);
    }
    
    public void MoveBicycles(Integer origin, Integer destination, Integer amount) {
        this.bicBalanceOnStation[origin] -= amount;
        this.bicBalanceOnStation[destination] += amount;
    }
    
    public void MoveBicyclesTwoDestinations(Integer origin, Integer dest1, Integer dest2, Integer amount, Integer secondDestinationAmount) {
        this.MoveBicycles(origin, dest1, amount - secondDestinationAmount);
        this.MoveBicycles(origin, dest2, amount - secondDestinationAmount);
    }
    
    // ** TODO **/

}
