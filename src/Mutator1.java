import java.util.Random;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This class implements the "Mutator1" heuristic. It implements the Heuristic interface for abstraction.
 * "Mutation1" flips 1/5 of the vertices included in the solution clique.
 *
 */
public class Mutator1 implements Heuristic
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

        
        List<Integer> verticesInClique = new ArrayList<>();//Create a list to store the vertices that are currently in the clique

        //Iterate through all the vertices and add the ones in the clique to the verticesInClique list 
        for (int i = 0; i < numVertices; i++)
        {
            if (solution[i] == 1) 
            {
                verticesInClique.add(i);
            }
        }
        
        //If the clique is empty, return the solution without any changes 
        if (verticesInClique.isEmpty())
        {
            return mutatedSolution;
        }
        
        //Calculate the number of vertices to remove from the clique (20% of the current clique size)
        int numVerticesToRemove = (int) Math.ceil(verticesInClique.size() / 5.0);
        
        
        if (numVerticesToRemove <= 0) 
        {
            return mutatedSolution;
        }
        
        //Randomly select a vertex from the vertices in the clique to be removed
        for (int i = 0; i < numVerticesToRemove; i++) 
        {
            int vertexToReplace = verticesInClique.get(random.nextInt(verticesInClique.size()));
            mutatedSolution[vertexToReplace] = 0;
            verticesInClique.remove(Integer.valueOf(vertexToReplace));
        }
        
        return mutatedSolution;//Return the mutated solution
    }
	
	
}
