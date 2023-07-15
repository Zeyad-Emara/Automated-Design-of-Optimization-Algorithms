import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 * This program is designed to solve the Maximum Weighted Clique Problem (MWCP) using the Conditional Markov Chain Search (CMCS) framework and Simulated Annealing (SA) algorithm for optimizing CMCS configurations.
 * @author Zeyad Hesham Emara
 * @version 1.00, 24-04-2023
 *
 */
public class Main 
{

	/**
	 * Main method, responsible for program control.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		Scanner scanner = new Scanner(System.in);//Scanner object instantiation, is used to take input from the standard input 
	
		/* Instantiating heuristic components
		 * Any heuristic component must be instantiated here
		 */
		HillClimber1 hillClimber1 = new HillClimber1();
		HillClimber2 hillClimber2 = new HillClimber2();
		Mutator1 mutator1 = new Mutator1();
		Mutator2 mutator2 = new Mutator2();
		Mutator3 mutator3 = new Mutator3();
		Mutator4 mutator4 = new Mutator4();

		/* Adding the heuristic components to a list of type "Heuristic"
		 * Any heuristic component must be added to this list to be usable in CMCS
		 * The order of this list is significant as it translate to the index of each component in CMCS transition matrices
		 */
		List<Heuristic> heuristics = new ArrayList<>();
	    heuristics.add(hillClimber1);
	    heuristics.add(hillClimber2);
	    heuristics.add(mutator1);
	    heuristics.add(mutator2);
	    heuristics.add(mutator3);
	    heuristics.add(mutator4);

	    //Prompting user to choose weather to evaluate an existing CMCS configuration or generate a new configuration
	    System.out.println("Please choose an option:");
        System.out.println("1. Evaluate an existing CMCS configuration");
        System.out.println("2. Train new configuration");
		
		int choice = scanner.nextInt();		
        if (choice == 1) //If the user enters "1" evaluate CMCS configuration
        {
        	EvaluateCMCS.runEvaluation(heuristics);//Pass the list of Heuristic components to the EvaluateCMCS class          
        } 
        else //If the user enters "2" generate/train a new configuration
        {
        	
        	SimulatedAnnealing sa = new SimulatedAnnealing();//Instantiating a SimulatedAnnealing class object          
        	       	   
            Object[] instancesInfo = InstancesLoader.loadInstances();//Load training instances using InstancesLoader class
            int[][][] adjacencyMatrices = (int[][][]) instancesInfo[0];//Extract the adjacency matrices from the loaded instances           
                                    
            sa.optimize(heuristics, adjacencyMatrices);//Generate a new CMCS configuration using the SimulatedAnnealing class optimize method             
             
        }
     
        scanner.close();
	}
	
	/**
	 * Prints a 2D array to console.
	 * Used for displaying a CMCS configuration. For printing a CMCS configuration, M_Succ and M_Fail matrices have to be printed separately as this method only print one 2D array. 
	 * @param array A 2D double array. 
	 */
	public static void print2DArray(double[][] array)
	{
		System.out.println("\n");
		for (int i = 0; i < array.length; i++) 
    	{
            for (int j = 0; j < array[i].length; j++) 
            {
                System.out.printf("%.2f ", array[i][j]);
            }
            System.out.println();
        }
		System.out.println("\n");
	}

	
	
}
