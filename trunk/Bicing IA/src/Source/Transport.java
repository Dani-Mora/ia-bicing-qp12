/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

/**
 *
 * @author Dani
 */
public class Transport {
    
    private Integer origin;
    private Integer preferredDestination;
    private Integer secondDestination = -1;
    private Integer bicyclesAmount;
    private Integer bicyclesToSecondDest;
    
    public Transport(Integer origin, Integer dest1, Integer numBic) {
        this.origin = origin;
        this.preferredDestination = dest1;
        this.bicyclesAmount = numBic;
    }
    
    public Transport(Integer origin, Integer dest1, Integer dest2, Integer numBic, Integer bicyclesToSecondDest) {
        this(origin, dest1, numBic);
        this.bicyclesToSecondDest = bicyclesToSecondDest;
        this.secondDestination = dest2;
    }
    
    public Integer getBicyclesAmount() {
        return bicyclesAmount;
    }

    public Integer getBicyclesToSecondDest() {
        return bicyclesToSecondDest;
    }

    public Integer getOrigin() {
        return origin;
    }

    public Integer getPreferredDestination() {
        return preferredDestination;
    }

    public Integer getSecondDestination() {
        return secondDestination;
    }
    
    public Boolean HasTwoDestinations() {
        return this.secondDestination != -1;
    }
        
    public void setBicyclesAmount(Integer bicyclesAmount) {
        this.bicyclesAmount = bicyclesAmount;
    }

    public void setBicyclesToSecondDest(Integer bicyclesToSecondDest) {
        this.bicyclesToSecondDest = bicyclesToSecondDest;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    public void setPreferredDestination(Integer preferredDestination) {
        this.preferredDestination = preferredDestination;
    }

    public void setSecondDestination(Integer secondDestination) {
        this.secondDestination = secondDestination;
    }
    
    private Double calculateJourney(Double dist, Integer amount) {
        return ((amount.doubleValue() / 10) + 1) * dist;
    }
    
    public Double calculateCost() {
        Double result = 0.0;
        Double dist = Simulation.bicing.getStationsDistance(this.getOrigin(), this.getPreferredDestination());
        if (this.HasTwoDestinations()) {
            // First journey
            Integer firstAmount = this.bicyclesAmount - this.bicyclesToSecondDest;
            result += this.calculateJourney(dist, firstAmount);
            // Second Journey
            Integer secondAmount = this.bicyclesToSecondDest;
            Double secondDist = Simulation.bicing.getStationsDistance(this.getPreferredDestination(), this.getSecondDestination());
            result += this.calculateJourney(secondDist, secondAmount);
        }
        else {
            result = this.calculateJourney(dist, this.getBicyclesAmount());
        }
        return result;
    }
}
