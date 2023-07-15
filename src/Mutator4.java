import java.util.Random;

/**
 * 
 * This class implements the "Mutator4" heuristic. It implements the Heuristic interface for abstraction.
 * "Mutation4" flips all the vertices to 0 and initializes a new solution by choosing a random vertex and flipping it to 1.
 *
 */
public class Mutator4 implements Heuristic
{
	
	@Override
    public int[] run(int[] solution, int[][] adjMatrix, ObjectiveFunctionMWCP evaluator) 
    {
        return mutate(solution, adjMatrix, evaluator);
    }
	
	/**
	 * 
	 * @param solution The solution which is to be mutated
     * @param adjMatrix The problem instance/graph
     * @param evaluator The objective function
	 * @return
	 */
	public int[] mutate(int[] solution, int[][] adjMatrix, ObjectiveFunctionMWCP evaluator) 
	{
		
		Random random = new Random();//Initialize a random number generator
		
		int numVertices = adjMatrix.length;//Get the number of vertices in the graph
		
        int[] mutatedSolution = solution.clone();//Create a copy of the input solution to avoid modifying the original

        for (int i = 0; i < numVertices; i++)//Flip all vertices in solution to 0 
        {
            if (solution[i] == 1) 
            {
            	mutatedSolution[i] = 0;
            }
        }
        
        //Initialize a new solution, flip a random vertex to 1
		int randomVertex = random.nextInt(mutatedSolution.length); 
		mutatedSolution[randomVertex] = 1;

        
        return mutatedSolution;//Return the mutated solution
  
    }
	
	

}
