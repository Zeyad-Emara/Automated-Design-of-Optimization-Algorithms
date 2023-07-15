import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * 
 * This class is responsible for converting a MWCP benchmark instance (in the format of DIMACS-W(ASCII Undirected)) to an adjacency matrix.
 *
 */
public class AdjacencyMatrixLoader 
{
	private static int[][] adjacencyMatrix;
    private static int numVertices;
    /**
     * 
     * @param filename
     * @return Returns an adjacency matrix representation of a DIMACS-W(ASCII Undirected) graph(vertex weighted undirected graph)
     * @throws FileNotFoundException
     */
    public static int[][] loadAdjacencyMatrix(String filename) throws FileNotFoundException
    {
    	File file = new File(filename);
        Scanner scanner = new Scanner(file);

        //read the number of vertices and edges according to the standard DIMACS graph format
        String line = scanner.nextLine();
        String[] tokens = line.split(" ");
        numVertices = Integer.parseInt(tokens[2]);
        int numEdges = Integer.parseInt(tokens[3]);


       
        adjacencyMatrix = new int[numVertices][numVertices];//initialize adjacency matrix with the number of vertices

        
        for (int i = 0; i < numVertices; i++)//read the weights for each vertex 
        {
            line = scanner.nextLine();
            tokens = line.split(" ");
            int vertex = Integer.parseInt(tokens[1]) - 1;
            int weight = Integer.parseInt(tokens[2]);
            // Store the weight of each vertex in the adjacency matrix
            adjacencyMatrix[vertex][vertex] = weight;
        }
        
        for (int i = 0; i < numEdges; i++)//read the edges from each vertex 
        {
            line = scanner.nextLine();
            tokens = line.split(" ");
            int x = Integer.parseInt(tokens[1]) - 1;
            int y = Integer.parseInt(tokens[2]) - 1;
            adjacencyMatrix[x][y] = 1;
            adjacencyMatrix[y][x] = 1;
        }

        scanner.close();
        
		return adjacencyMatrix;//return adjacency matrix
       
        
    }
}
