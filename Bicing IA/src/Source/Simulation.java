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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Dani
 */
public class Simulation {
        
    private static final Integer NUM_VANS = 6;
    private static final Integer NUM_BIC = 1000;
    private static final Integer NUM_EST = 20;
    private static final Integer DEMAND = 0;
    
    /* Print functions */
    
    private static void printState(BicingState state) {
        Integer bicycles[] = state.getBicycleDisposition();
        ArrayList<Transport> movements = (ArrayList<Transport>) state.getMovements();
        
        for (int i = 0; i < bicycles.length; ++i) {
            System.out.println("Estació " + i + " : " + bicycles[i] + " bicycles");
        }
        
        for (int i = 0; i < movements.size(); ++i) {
            System.out.println("Movement " + i + " : " + movements.get(i).getOrigin() + " - " + movements.get(i).getBicyclesAmount() + "-> " + movements.get(i).getPreferredDestination() + "," + movements.get(i).getSecondDestination() + "(" + movements.get(i).getBicyclesToSecondDest() + ")");
        }
    }
    
    public static void main(String[] args){
        Bicing bicing = new Bicing(NUM_EST,NUM_BIC, DEMAND);
        BicingState initialState = new BicingState(NUM_EST);
        initialState.calculateInitialState(bicing, NUM_BIC);
        printState(initialState);
        
        
        //ExecuteHillClimbing(bicing, initialState);
        //ExecuteSimulatedAnnealing(bicing, initialState);
    }
    
    private static void ExecuteHillClimbing(Bicing bicing, BicingState initSt) {
        System.out.println("\n HillClimbing Solution  -->");
        try {
            Problem problem = new Problem(initSt, new Successors(bicing, NUM_VANS), new FinalCondition(), new BicingHeuristic());
            Search search =  new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem,search);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void ExecuteSimulatedAnnealing(Bicing bicing, BicingState initSt) {
        System.out.println("\nSimulated Annealing Solution -->");
        try {
            Problem problem = new Problem(initSt, new SuccessorsSA(), new FinalCondition(), new BicingHeuristic());
            Search search =  new SimulatedAnnealingSearch();
            SearchAgent agent = new SearchAgent(problem,search);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CODI JAUME
    
        
    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }
        
    }
    
    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }
    
}
