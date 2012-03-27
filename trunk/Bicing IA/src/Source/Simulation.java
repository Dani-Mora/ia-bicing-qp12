/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;
import IA.Bicing.Bicing;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

/**
 *
 * @author Dani
 */
public class Simulation {
        
    private static final Integer NUM_VANS = 6;
    private static final Integer NUM_BICIS = 120;
    private static final Integer NUM_EST = 20;
    private static final Integer DEMANDA = 0;
    
    public static void main(String[] args){
        Bicing bicing = new Bicing(NUM_EST,NUM_BICIS, DEMANDA);
        BicingState initialState = BicingState.GetInitialState(bicing, NUM_VANS);
        ExecuteHillClimbing(bicing, initialState);
        ExecuteSimulatedAnnealing(bicing, initialState);
    }
    
    private static void ExecuteHillClimbing(Bicing bicing, BicingState initSt) {
        System.out.println("\n HillClimbing Solution  -->");
        try {
            Problem problem = new Problem(initSt, new Successors(), new FinalCondition(), new Heuristic());
            Search search =  new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem,search);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void ExecuteSimulatedAnnealing(Bicing bicing, BicingState initSt) {
        System.out.println("\nSimulated Annealing Solution -->");
        try {
            Problem problem = new Problem(initSt, new SuccessorsSA(), new FinalCondition(), new Heuristic());
            Search search =  new SimulatedAnnealingSearch();
            SearchAgent agent = new SearchAgent(problem,search);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}