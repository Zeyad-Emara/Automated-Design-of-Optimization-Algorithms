import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * Class which evaluates a CMCS configuration on a set of DIMACS-W benchmark instances.
 *
 */
public class EvaluateCMCS 
{
	/**
     * Runs the given CMCS configuration on a set of instances and returns the results.
     * @param heuristics The list of heuristic components used in the CMCS configuration.
     * @param instances The set of instances to run the CMCS configuration on.
     * @param terminateInSeconds The termination time the CMCS configuration to run on each instance.
     * @param msucc The success transition matrix.
     * @param mfail The failure transition matrix.
     * @return A 2D array containing the objective value and cardinality for each instance.
     * @throws FileNotFoundException
     */
	public static int[][] runConfig(List<Heuristic> heuristics, int[][][] instances, long terminateInSeconds,double[][] msucc, double[][] mfail) throws FileNotFoundException
	{
		//Initialize an array which holds the results for 1 run
	    int instanceCount = instances.length;
	    int[][] results = new int[instanceCount][2];
	    int i = 0;
	    long terminate;
	    
	    //Instantiate the MWCP objective function and the CMCS framework
	    ObjectiveFunctionMWCP evaluator = new ObjectiveFunctionMWCP();
	    CMCS cmcs = new CMCS();
	    
	    //Iterate through the adjacency matrices/instances
	    for (int[][] adjacencyMatrix : instances) 
	    {
	    	terminate = System.currentTimeMillis() + 1000 * terminateInSeconds;//set the CMCS termination time
	       

	        int[] solution = cmcs.Optimize(heuristics, msucc, mfail, terminate, adjacencyMatrix);//Run the CMCS algorithm
	        int objectiveValue = evaluator.Evaluate(solution, adjacencyMatrix);//Evaluate solution
	        
	        //Calculate solution cardinality
	        int cardinality = 0;
	        for (int value : solution) 
	        {
                if (value == 1) 
                {
                    cardinality++;
                }
            }
	        
	        results[i][0] = objectiveValue;
            results[i][1] = cardinality;
	        
	       
	        i++;
	    }
	    
	    
	    return results;//Return results matrix containing objective value and cardinality of solution for each instance
	
	}
	
	/**
     * Controls the evaluation and CMCS parameters then runs evaluation and writes results to a file.
     * Results are written every run to ensure the data is saved if the evaluation is interrupted
     * @param heuristics The list of heuristic components used in the CMCS configuration.
     * @throws FileNotFoundException
     * @throws IOException
     */
	public static void runEvaluation(List<Heuristic> heuristics) throws FileNotFoundException, IOException 
	{
		
        Scanner scanner = new Scanner(System.in);
        //Initialize CMCS configuration, transition matrices and configuration filename 
        double[][] msucc;
        double[][] mfail;
        String transitionMatrix;
        
        //Prompt user to enter the filename for the transition matrices/CMCS configuration
		System.out.println("\nEnter the filename for the transition matrices(please include file extension):");
		transitionMatrix = scanner.next();
		//Load configuration using loadTransitionMatrices method
		double[][][] transitionMatrices = TransitionMatrixLoader.loadTransitionMatrices(transitionMatrix);
		msucc = transitionMatrices[0];
		mfail = transitionMatrices[1];
    	
		//Display loaded configuration
        System.out.println("\nTransition Matrix Success: ");
        Main.print2DArray(msucc);
        System.out.println("\nTransition Matrix Fail: ");
    	Main.print2DArray(mfail);
    	
    	//Prompt user to enter the termination time for CMCS in seconds
    	System.out.println("\nPlease enter the termination time for CMCS in seconds:");
		long terminateInSeconds = scanner.nextLong();
		
		//Prompt user to enter number of runs to be performed on each instance
        System.out.println("\nPlease Enter Number of Runs: ");
        int numRuns = scanner.nextInt();
        System.out.println("\n");

        //Prompt user to enter the algorithm name to be displayed when writing to file
        System.out.println("\nPlease Enter Configuration Name for Use in Results Header: ");
        String configName = scanner.next();

    	//Load instances using loadInstances method
        Object[] instancesInfo = InstancesLoader.loadInstances();
        int[][][] adjacencyMatrices = (int[][][]) instancesInfo[0];
        String[] instanceNames = (String[]) instancesInfo[1];
        int numInstances = (int) instancesInfo[2];

        //Initialize arrays to store results
        int[][] instanceResults = new int[numInstances][2];
        int[] sumObjectiveValues = new int[numInstances];
        int[] bestObjectiveValues = new int[numInstances];
        int[] highestObjectiveCount = new int[numInstances];
        int[] bestCardinality = new int[numInstances];

        //Initialize the arrays with default values
        Arrays.fill(bestObjectiveValues, Integer.MIN_VALUE);
        Arrays.fill(highestObjectiveCount, 0);
        Arrays.fill(bestCardinality, 0);


        //Write results to .txt file. results are written each run to avoid loss of data.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Result.txt", true))) 
        {
        	
        	//Write header information.
        	String algorithmName = "Configuration Name: " + configName;
            writer.write(algorithmName);
            writer.newLine();
            
            String runTime = "Run Time: " + terminateInSeconds + "(s)";
            writer.write(runTime);
            writer.newLine();
            
            String runNum = "Runs: " + numRuns;
            writer.write(runNum);
            writer.newLine();
            
            String header = String.format("%-20s | %-30s | %-30s | %-30s | %-30s", "Instance Name", "Instance Best Objective Value", "Instance Average Objective Value", "Highest Objective Value Count", "|C|");
            writer.write(header);
            writer.newLine();
            
            for (int x = 0; x < numRuns; x++)//For each run, run CMCS on instances and get results, update the total results 
            {
                instanceResults = runConfig(heuristics, adjacencyMatrices, terminateInSeconds, msucc, mfail);

                for (int i = 0; i < numInstances; i++)//Check the new run for better results and update the average solution objective value so far 
                {
                	int currentValue = instanceResults[i][0];
                    int currentCardinality = instanceResults[i][1];
                    sumObjectiveValues[i] += currentValue;

                    if (currentValue > bestObjectiveValues[i]) 
                    {
                        bestObjectiveValues[i] = currentValue;
                        bestCardinality[i] = currentCardinality;
                        highestObjectiveCount[i] = 1;
                    } 
                    else if (currentValue == bestObjectiveValues[i]) 
                    {
                        highestObjectiveCount[i]++;
                    }
                }

                //Write the results to the file after each run
                writer.write("Run: " + (x + 1));
                writer.newLine();
                for (int i = 0; i < numInstances; i++) 
                {
                    double averageObjectiveValue = sumObjectiveValues[i] / (x + 1);
                    String outputLine = String.format("%-20s | %-30s | %-32.2f | %-30s | %-30s", instanceNames[i], bestObjectiveValues[i], averageObjectiveValue, highestObjectiveCount[i], bestCardinality[i]);
                    writer.write(outputLine);
                    writer.newLine();
                }
                writer.newLine();//Add an extra newline to separate each run
                writer.flush();//Flush the writer to ensure the content is written to the file after each run
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

}
