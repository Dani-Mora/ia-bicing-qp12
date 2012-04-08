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
        
    private static final Integer NUM_VANS = 5;
    private static final Integer NUM_BIC = 1250;
    private static final Integer NUM_EST = 25;
    private static final Integer DEMAND = 0;
    
    /* Print functions */
    
    private static void printInfo(Bicing context, Integer i) {
        System.out.println("No usades: " + context.getStationDoNotMove(i));
        System.out.println("Hi haura: " + context.getStationNextState(i));
        System.out.println("Demanda: " + context.getDemandNextHour(i));
    }
    
    private static void printState(BicingState state, Bicing context) {
        //mph
//        Integer bicycles[] = state.getBicycleDisposition();
//        ArrayList<Transport> movements = (ArrayList<Transport>) state.getMovements();
//        Integer totalDemand = 0, totalAssignedBicycles = 0;
//        for (int i = 0; i < bicycles.length; ++i) {
//            System.out.println("Estació " + i + " : " + bicycles[i] + " bicycles");
//            totalDemand += context.getDemandNextHour(i);
//            totalAssignedBicycles += bicycles[i];
//            printInfo(context, i);
//        }
//        System.out.println("Total Demand: " + totalDemand);
//        System.out.println("Total Assigned Bicycles: " + totalAssignedBicycles);
//        System.out.println("Theoretical Bicycles = " + NUM_BIC);
//        for (int i = 0; i < movements.size(); ++i) {
//            System.out.println("Movement " + i + " : " + movements.get(i).getOrigin() + " - " + movements.get(i).getBicyclesAmount() + "-> " + movements.get(i).getPreferredDestination() + "," + movements.get(i).getSecondDestination() + "(" + movements.get(i).getBicyclesToSecondDest() + ")");
//        }
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
        
        
    }
    
    public static void main(String[] args){
        Random rand = new Random();
        Bicing bicing = new Bicing(NUM_EST,NUM_BIC, DEMAND, rand.nextInt());
        BicingState initialState = new BicingState(NUM_EST);
        System.out.println("****************SIMPLE******************");
        //initialState.calculateInitialState(bicing, NUM_BIC);
        initialState.calculateInitialState(bicing, NUM_BIC, NUM_VANS);
        printState(initialState, bicing);
        //System.out.println("****************COMPLEX******************");
        //initialState.setInitialState(0);
        //initialState.calculateInitialState(bicing, NUM_BIC);
        //printState(initialState, bicing);
        
        
        ExecuteHillClimbing(bicing, initialState);
        printState(initialState, bicing);
        //ExecuteSimulatedAnnealing(bicing, initialState);
    }
    
    private static void ExecuteHillClimbing(Bicing bicing, BicingState initSt) {
        System.out.println("\n HillClimbing Solution  -->");
        try {
            Problem problem = new Problem(initSt, new Successors(bicing, NUM_VANS), new FinalCondition(), new BicingHeuristic(bicing));
            Search search =  new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem,search);
            
            System.out.println();
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void ExecuteSimulatedAnnealing(Bicing bicing, BicingState initSt) {
        System.out.println("\nSimulated Annealing Solution -->");
        try {
            Problem problem = new Problem(initSt, new SuccessorsSA(), new FinalCondition(), new BicingHeuristic(bicing));
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
