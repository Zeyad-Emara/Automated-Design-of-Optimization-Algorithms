import java.util.List;
import java.util.Random;
/**
 * This class implements the Conditional Markov Chain Search framework.
 * This class treats the heuristic components as black box algorithms, thus it is compatible with other sets of heuristics.
 *
 */
public class CMCS 
{   
	/**
	 *  Method implementing the CMCS framework.
	 * @param heuristics A list of heuristic components.
	 * @param msucc M_Succ transition matrix, includes transitions if the last component execution was successful(improved the solution).
	 * @param mfail M_Fail transition matrix, includes transitions if the last component execution was unsuccessful(did not improve the solution).
	 * @param terminationTime Time in seconds after which CMCS terminates.
	 * @param adjMatrix Adjacency matrix representing an undirected weighted vertex graph. This is the MWCP instance represented as an adjacency matrix.
	 * @return The best solution found during the search process. The solution is a binary encoded array (0 or 1), if bestSolution[i] = 1, it means vertex i in the graph is included in the clique. 
	 */
    public int[] Optimize(List<Heuristic> heuristics, double[][] msucc, double[][] mfail, long terminationTime, int[][] adjMatrix) 
    {
    	
    	ObjectiveFunctionMWCP evaluator = new ObjectiveFunctionMWCP();//Create an instance of the objective function class to evaluate the solutions
    	
    	Random random = new Random();
    	
    	// Create an initial solution by selecting 1 random vertex in an empty solution
    	int[] initialSolution = new int[adjMatrix.length];
		int randomVertex = random.nextInt(initialSolution.length); 
	    initialSolution[randomVertex] = 1;
	    
	    //Set the current and best solution as the initial solution
	    int[] solution = initialSolution.clone();
        int[] bestSolution = solution.clone();
        int heuristicIndex = 0;//Initialize heuristicIndex, which stores the current component in the chain
      
        while (System.currentTimeMillis() < terminationTime)//While the termination time has not elapsed, continue the search process 
        {
        	
            int oldObjectiveValue = evaluator.Evaluate(solution, adjMatrix);//Evaluate the current solution
            solution = heuristics.get(heuristicIndex).run(solution, adjMatrix, evaluator);//Apply heuristic to current solution
            int newObjectiveValue = evaluator.Evaluate(solution, adjMatrix);//Evaluate the new solution

            if (newObjectiveValue > oldObjectiveValue)//If the new solution does improves the old solution, select the next component in the chain based on M_Succ transition matrix 
            {
                heuristicIndex = rouletteWheel(msucc[heuristicIndex]);//Use the roulette wheel method to determine the next component 
                if (newObjectiveValue > evaluator.Evaluate(bestSolution, adjMatrix))//If new solution is better that the best solution, update the best solution 
                {
                    bestSolution = solution.clone();
                }
            } 
            
            else//If the new solution does not improve the old solution, select the next component in the chain based on M_Fail transition matrix
            {
                heuristicIndex = rouletteWheel(mfail[heuristicIndex]);//Use the roulette wheel method to determine the next component
            }
        }

        return bestSolution;//return the best solution found during the search process
    }
    
    /**
     * The rouletteWheel method simulates a roulette wheel, where transition represents a section of the wheel with a size proportional to its probability. The method then spins the wheel by generating a random value and selecting the corresponding index based on the cumulative sum of probabilities. 
     * @param probabilities This parameter is a double array which contains the transition probabilities from a specific component(It is row i in the transition matrix, where heuristic index = i).
     * @return The method returns an index i in the probabilities array corresponding to the transition to the next component, if no index is found, it returns the last index of the array as a fallback.
     */
    private int rouletteWheel(double[] probabilities) 
    {
        double total = 0.0;
        for (double probability : probabilities)// Calculate the total sum of probabilities 
        {
            total += probability;
        }

        double randomValue = new Random().nextDouble() * total;// Generate a random value between 0 and the total sum of probabilities
        double cumulative = 0.0;

        for (int i = 0; i < probabilities.length; i++)//Iterate through the probabilities array 
        {
            cumulative += probabilities[i];//Add the current probability to the cumulative sum
            if (randomValue <= cumulative)//If the random value is less than or equal to the cumulative sum, return the current index 
            {
                return i;
            }
        }
        return probabilities.length - 1;// If no index was selected, return the last index of the array
    }
    

}
