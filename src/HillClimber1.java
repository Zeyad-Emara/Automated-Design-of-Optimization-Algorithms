import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class implements the "HillClimber1" heuristic component. It implements the Heuristic interface for abstraction.
 * This heuristic improves the solution stochastically, adding a vertex randomly from the candidate set of vertices.
 *
 */
public class HillClimber1 implements Heuristic
{
	
    @Override
    public int[] run(int[] solution, int[][] adjMatrix, ObjectiveFunctionMWCP evaluator)//implement Heuristic interface 
    {
        return Hc1(solution, adjMatrix, evaluator);
    }
    
    /**
     * 
     * @param currentSolution The solution which is to be improved
     * @param adjMatrix The problem instance/graph
     * @param evaluator The objective function
     * @return
     */
    public int[] Hc1(int[] currentSolution, int[][] adjMatrix, ObjectiveFunctionMWCP evaluator) 
    {
    	
    	int numVertices = adjMatrix.length;

    	// Initialize the best solution to the current solution
     	int[] bestSolution = currentSolution.clone();

     	//Create a set to store the vertices that are not currently in the solution
        Set<Integer> candidateSet = new HashSet<>();

        //Iterate through all the vertices and add the ones not in the current solution to the candidate set
        for (int i = 0; i < numVertices; i++) 
        {
            if (currentSolution[i] == 0) 
            {
                candidateSet.add(i);
            }
        }
        

        //Convert the candidate set to a list and shuffle it to create a random order for processing
        List<Integer> candidateList = new ArrayList<>(candidateSet);
        Collections.shuffle(candidateList);


        //Iterate through the shuffled candidate list
        for (int i : candidateList) 
        {
        	//Assume the current vertex can be added to the clique
    	   	boolean validClique = true;
            for (int j = 0; j < numVertices; j++)//Check if adding the current vertex maintains a valid clique 
            {
            	if (currentSolution[j] == 1 && adjMatrix[i][j] == 0)//If the current vertex is not connected to an existing vertex in the solution, it cannot form a valid clique 
                {
                    validClique = false;
                    break;
                }
            }

           if (validClique)//If the current vertex maintains a valid clique, add it to the current solution and update the best solution 
            {
                currentSolution[i] = 1;
                bestSolution = currentSolution.clone();
            }
       	}

 	    return bestSolution;
 	}
    
    
	
}
