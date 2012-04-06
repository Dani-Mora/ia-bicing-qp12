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
    private List<Transport> movements;
    
    private Integer initialState = 1;

    public void setInitialState(Integer initialState) {
        this.initialState = initialState;
    }

    BicingState(Integer numStations) {
        this.movements = new ArrayList<Transport>();
        this.bicOnStation = new Integer[numStations];
        for (int i = 0; i < this.bicOnStation.length; ++i) {
            this.bicOnStation[i] = 0;
        }    
    }
    
    BicingState(Integer[] bicOnStations, List<Transport> movements) {
        this.bicOnStation = bicOnStations;
        this.movements = movements;
    }
   

    public void calculateInitialState(Bicing context, Integer numBic) {
        if (initialState == 0) {
            this.calculateComplexInitialState(context, numBic);            
        }
        else
        {
            this.calculateSimpleInitialState(context, numBic);     
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
    
    private void calculateComplexInitialState(Bicing context, Integer numBic) {
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
    
    private void calculateSimpleInitialState(Bicing context, Integer numBic) {
        Random rand = new Random();
     
        Integer bicLeft = numBic;
        Integer demandBicycleAverage = this.calculateDemandAverage(context);
        Integer currentStation = 0;
        int iterWh = 0;
        while (bicLeft > 0) {

            System.out.println("Bic: " + bicLeft);
            Integer currentDemand = context.getDemandNextHour(currentStation);System.out.println("currentDemand: " + currentDemand);
            Integer currentBicycles = this.bicOnStation[currentStation];System.out.println("currentBicycles: " + currentBicycles);
            Integer aux = currentDemand - currentBicycles;
            Integer addedBicycles;
            if (aux <= 0) {
                addedBicycles = 0;System.out.println("IF");
                ++iterWh;
                if (iterWh == this.bicOnStation.length) {
                    addedBicycles = bicLeft;
                }
            }
            else {
                iterWh = 0;
                Integer rnd = rand.nextInt(aux+1); System.out.println("ELSE");
                addedBicycles = rnd; System.out.println(rnd);
            } //random value between current bicycles and current demand
            // no bicycles added if current bicycles > current demand
            System.out.println("addedBicycles: " + addedBicycles);
            
            if (bicLeft < addedBicycles) {
                addedBicycles = bicLeft;
            }
            bicLeft -= addedBicycles;
            this.bicOnStation[currentStation] += addedBicycles;
            currentStation++;
            currentStation %= this.bicOnStation.length;
            if (bicLeft < 0) {
                System.out.println("ERROR: no bicycles left");
            }
        }
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
