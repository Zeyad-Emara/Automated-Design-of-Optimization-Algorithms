/**
 * 
 * This class calculates the objective value of MWCP solutions.
 *
 */
public class ObjectiveFunctionMWCP 
{
	/**
	 * 
	 * @param solution Binary encoded array representing a solution.
	 * @param adjMatrix Adjacency matrix representing a MWCP instance with the diagonal of the matrix holding the weight value of each vertex.
	 * @return
	 */
	public int Evaluate(int[] solution, int[][] adjMatrix) 
    {
		int numVertices = adjMatrix.length;
		
        int objectiveValue = 0;
        
        for (int i = 0; i < numVertices; i++)//Calculate the sum of vertex weights in a solution 
        {
            if (solution[i] == 1) 
            {
                objectiveValue += adjMatrix[i][i]; 
            }
        }
        return objectiveValue;//return the objective value
    }
	
	

}
