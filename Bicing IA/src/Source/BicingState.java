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
    
    private static final Integer initialState = 0;

    BicingState(Integer[] numBic) {
        this.bicOnStation = numBic;
        this.movements = new ArrayList<Transport>();
    }
    
    BicingState(Integer numStations) {
        this.bicOnStation = new Integer[numStations];
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
        
        while (bicLeft > 0) {         
            Integer currentDemand = context.getDemandNextHour(currentStation);
            Integer currentBicycles = this.bicOnStation[currentStation];
            Integer addedBicycles = rand.nextInt(currentDemand - currentBicycles);
            if (bicLeft < addedBicycles) {
                addedBicycles = bicLeft;
            }
            bicLeft -= addedBicycles;
            this.bicOnStation[currentStation] += addedBicycles;
            currentStation = (currentStation++ % this.bicOnStation.length);
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
