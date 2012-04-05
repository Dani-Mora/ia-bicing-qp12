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
    private Integer secondDestination;
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
    
}
