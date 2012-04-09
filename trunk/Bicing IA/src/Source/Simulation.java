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
import java.util.Random;

/**
 *
 * @author Dani
 */
public class Simulation {
    
    public static final Integer NUM_VANS = 20;
    public static final Integer NUM_BIC = 5000;
    private static final Integer NUM_EST = 100;
    public static final Integer DEMAND = 0;
    public static Bicing bicing = new Bicing(NUM_EST,NUM_BIC, DEMAND, new Random().nextInt());
    
    /* Print functions */
    
    private static void printInfo(Integer i) {
        System.out.println("No usades: " + bicing.getStationDoNotMove(i));
        System.out.println("Hi haura: " + bicing.getStationNextState(i));
        System.out.println("Demanda: " + bicing.getDemandNextHour(i));
    }
    
    private static void printState(BicingState state) {
        ArrayList<Transport> movements = (ArrayList<Transport>) state.getMovements();
        System.out.println("*****printState*****");
        System.out.println("Number of selected movements:" + movements.size());
        System.out.println("MOVEMENT DESCRIPTION LIST");
        for (int i = 0; i < movements.size(); ++i) {
            System.out.println(i+1);
            Transport t = movements.get(i);
            System.out.println(" ORIGIN ST: " + t.getOrigin());
            System.out.println(" DESTINATION ST #1: " + t.getPreferredDestination());
            System.out.println(" BICYCLE AMOUNT TO #1: " + t.getBicyclesAmount());
            if (t.HasTwoDestinations()) {
                System.out.println(" DESTINATION ST #2: " + t.getSecondDestination());
                System.out.println(" BICYCLE AMOUNT TO #2: " + t.getBicyclesToSecondDest());
            }
        }
        
        Integer aux[] = state.getAllBicyclesNextHour();
        for (int i = 0; i < aux.length; ++i) {
            System.out.println("Previsió estació : " + i + " " + aux[i]);
        }
        
        BicingHeuristic heuristic = new BicingHeuristic();
        System.out.println(" ****************************************** dsfsfaf");
        System.out.println("HEURISTIC ESTAT: " + heuristic.getSimpleHeuristic(state));
        
    }
    
    public static void main(String[] args){
        BicingState initialState = new BicingState();
        System.out.println("****************SIMPLE******************");
        initialState.calculateInitialState();
        printState(initialState);
        //System.out.println("****************COMPLEX******************");
        //initialState.setInitialState(0);
        //initialState.calculateInitialState(bicing, NUM_BIC);
        //printState(initialState, bicing);
           
        ExecuteHillClimbing(initialState);
        //printState(initialState);
        //ExecuteSimulatedAnnealing(bicing, initialState);
    }
    
    private static void ExecuteHillClimbing(BicingState initSt) {
        System.out.println("\n HillClimbing Solution  -->");
        try {
            Problem problem = new Problem(initSt, new Successors(), new FinalCondition(), new BicingHeuristic());
            Search search =  new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem,search);
            
            System.out.println();
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void ExecuteSimulatedAnnealing(BicingState initSt) {
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
        System.out.println(actions.size());
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }
    
}
