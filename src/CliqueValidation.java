/**
 * 
 * This class is for testing purposes.
 * It Checks a MWCP solutions for validity.
 *  
 *
 */
public class CliqueValidation 
{
	/**
	 * 
	 * @param solution A MWCP solution, the solution is a binary encoded array (0 or 1).
	 * @param adjMatrix Adjacency matrix representing an undirected weighted vertex graph. This is the MWCP instance represented as an adjacency matrix.
	 * @return If a MWCP solution forms a valid clique return true, if invalid solution return false.
	 */
	public static boolean isClique(int[] solution,int[][] adjMatrix) 
	{
		int numVertices = adjMatrix.length;
        for (int i = 0; i < numVertices; i++)//Iterate through solution array 
        {
            if (solution[i] == 1)//If encountered a vertex "i" included in the solution 
            {
                for (int j = i + 1; j < numVertices; j++)//Check the encountered vertex "i" has an edge to every other vertex in the solution 
                {
                    if (solution[j] == 1 && adjMatrix[i][j] == 0)//If vertex "i" does not have an edge connecting it to another vertex  
                    {
                        return false;//return false, solution is not a valid clique
                    }
                }
            }
        }
        return true;//return true, solution is a valid clique
    }

}
