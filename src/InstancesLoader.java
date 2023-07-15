import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * 
 * This class is responsible for loading a set of MWCP benchmark instances by passing every instance in a specific file path to the "AdjacencyMatrixLoader" to load one instance at a time.
 *
 */
public class InstancesLoader 
{
	/**
	 * 
	 * @return Returns an array object which contains a 3D array of instances(each instance is an adjacency matrix), a string array of instance names, and an int array of instance indices.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
    public static Object[] loadInstances() throws FileNotFoundException, IOException 
    {
    	//Prompt user to enter file path to file containing the instances 
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter the file path containing benchmark instances(Please ensure no blank spaces in the file path):");
        String directoryPath = scanner.next();
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        //count the number of instances that will be loaded
        int numInstances = 0;
        for (File file : files) 
        {
            if (file.isFile() && file.getName().endsWith(".clq")) 
            {
                numInstances++;
            }
        }

        //create arrays with size of number of instances to contain the adjacency matrices and instance names
        int[][][] adjacencyMatrices = new int[numInstances][][];
        String[] instanceNames = new String[numInstances];

        //For each instance in the file, load the instance with loadAdjacencyMatrix method and store the relevant information in the arrays
        int instanceIndex = 0;
        for (File file : files) 
        {
            if (file.isFile() && file.getName().endsWith(".clq")) 
            {
                System.out.println("Instance " + (instanceIndex + 1) + ": " + file.getName());
                adjacencyMatrices[instanceIndex] = AdjacencyMatrixLoader.loadAdjacencyMatrix(file.getPath());
                instanceNames[instanceIndex] = file.getName();
                instanceIndex++;
            }
        }
        //return array object containing a 3D array of instances(each instance is an adjacency matrix), a string array of instance names, and an int array of instance indices.
        return new Object[]{adjacencyMatrices, instanceNames, numInstances};
    }
}