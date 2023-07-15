import java.util.Arrays;
import java.util.List;

/**
 * This class implements an objective function for Simulated Annealing(SA) for optimizing CMCS configurations.
 * The normalized scores for each instance are calculated, then calculate the average normalized score across all instances. 
 * This is done by dividing the sum of normalized scores by the number of instances.
 * The resulting average normalized score is used as the objective value for a configuration to be used in the Simulated annealing class.
 * 
 *
 */
public class ObjectiveFunctionSA 
{
	/**
	 * 
	 * @param heuristics The list of heuristic components used in the CMCS configuration.
     * @param instances The set of instances to run the CMCS configuration on.
	 * @param msucc
	 * @param mfail
	 * @param terminateInSeconds The termination time the CMCS configuration to run on each instance.
	 * @return
	 */
	public static double EvaluateConfig(List<Heuristic> heuristics, int[][][] instances, double[][] msucc, double[][] mfail, long terminateInSeconds)
	{
		//Initialize variables
		long terminate;
		double totalNormalizedScore = 0.0;
	    int instanceCount = instances.length;
	    int[] objectiveValues = new int[instanceCount];
	    int i = 0;
	    
	    //Instantiate the MWCP objective function and the CMCS framework
	    ObjectiveFunctionMWCP evaluator = new ObjectiveFunctionMWCP();
	    CMCS cmcs = new CMCS();
	    
	    //Iterate through the adjacency matrices/instances
	    for (int[][] adjacencyMatrix : instances) 
	    {
	    	terminate = System.currentTimeMillis() + 1000 * terminateInSeconds;//set the CMCS termination time
	        int totalWeight = computeTotalWeight(adjacencyMatrix);//compute total graph weight of the instance
	       
	        
	        int[] solution = cmcs.Optimize(heuristics, msucc, mfail, terminate, adjacencyMatrix);//Run the CMCS algorithm
	        int objectiveValue = evaluator.Evaluate(solution, adjacencyMatrix);//Evaluate solution

	        //Normalize the score
	        double normalizedScore = (double) objectiveValue / totalWeight;
	        totalNormalizedScore += normalizedScore;
	        
	        objectiveValues[i] = objectiveValue;//Storing every instance's solution objective value 
	        
	        //Display the solution objective values for each instance 
	        System.out.println("\nInstance " + (i+1) + " Objective Value: " + objectiveValue);
	       
	        i++;
	    }
	    //Display the array of objective values 
	    System.out.println("\nObjective Values: " + Arrays.toString(objectiveValues));
	    
	    //Compute the average normalized score
	    double averageNormalizedScore = (totalNormalizedScore / instanceCount);
	    
	    //Display the average normalized score for this configuration
	    System.out.println("\nScore: " + averageNormalizedScore);
	    
	    return averageNormalizedScore;//Return the average normalized score
	
	}
	
	/**
	 * This method calculates the total sum all vertex weight in the graph
	 * This method is used in calculating the normalized score of a given solution
	 * @param adjacencyMatrix An adjacency matrix representation of a vertex weighted undirected graph
	 * @return
	 */
	public static int computeTotalWeight(int[][] adjacencyMatrix) 
	{
	    int totalWeight = 0;
	    for (int i = 0; i < adjacencyMatrix.length; i++)//Iterate through all vertices in the graph, adding the weight of each vertex to the total weight 
	    {
	        totalWeight += adjacencyMatrix[i][i];
	    }
	    return totalWeight;
	}

	
}
