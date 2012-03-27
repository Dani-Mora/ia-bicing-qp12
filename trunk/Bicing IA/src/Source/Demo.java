/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import IA.Bicing.Bicing;
import java.util.Random;

/**
 *
 * @author Dani
 */
public class Demo {
    
    private Random myRandom = new Random();
    
    private static final Integer NUM_VANS = 6;
    private static final Integer NUM_BICIS = 120;
    private static final Integer NUM_EST = 20;
    private static final Integer DEMANDA = 0;
    
    public static void main(String[] args){
        Bicing bicing = new Bicing(NUM_EST,NUM_BICIS, DEMANDA);
        ExecuteHillClimbing(bicing);
        ExecuteSimulatedAnnealing(bicing);
    }
    
    private static void ExecuteHillClimbing(Bicing bicing) {
        System.out.println("\n HillClimbing Solution  -->");
        try {
            Problem problem =  new Problem(TSPB,new ProbTSPSuccessorFunction(), new ProbTSPGoalTest(),new ProbTSPHeuristicFunction());
            Search search =  new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem,search);
            
            System.out.println();
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void ExecuteSimulatedAnnealing(Bicing bicing) {
        System.out.println("\nSimulated Annealing Solution -->");
        try {
            Problem problem =  new Problem(TSPB,new ProbTSPSuccessorFunctionSA(), new ProbTSPGoalTest(),new ProbTSPHeuristicFunction());
            SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(2000,100,5,0.001);
            //search.traceOn();
            SearchAgent agent = new SearchAgent(problem,search);
            
            System.out.println();
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
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
