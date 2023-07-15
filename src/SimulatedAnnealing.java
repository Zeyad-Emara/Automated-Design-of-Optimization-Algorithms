import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A class implementing Simulated Annealing(SA) algorithm for optimizing CMCS configuration.
 * Requires a set of heuristic components and a set of benchmark instances for training.
 *
 */
public class SimulatedAnnealing 
{

	/**
	 * 
	 * @param heuristics The list of heuristic components used in the CMCS configuration.
     * @param instances The set of instances to run the CMCS configuration on.
	 * @return
	 * @throws IOException
	 */
    public double[][][] optimize(List<Heuristic> heuristics, int[][][] instances) throws IOException 
    {
    	//Initializing variables storing CMCS configuration
    	double[][] msucc;
        double[][] mfail;
        String transitionMatrix;//Configuration file name
        
        /*
         * Initializing component ID array which stores the type of heuristic(Hill Climber/Mutator) present at each index of the CMCS transition matrices
         * It uses a binary encoding (1 if a component is a hill climber, 0 if mutator)
         */
        int[] componentID;
        
    	Scanner scanner = new Scanner(System.in);
    	
    	//Prompt user to choose whether to optimize an existing CMCS configuration or initialize a new one 
    	System.out.println("\nPlease choose an option:");
        System.out.println("1. Read configuration from a file");
        System.out.println("2. Initialize new configuration\n");
        
        int choice = scanner.nextInt();//get user choice
        if(choice == 1)//User choose to optimize an existing CMCS configuration
        {
        	//Prompt user to enter configuration file path
			System.out.println("\nEnter the file path to the configuration file(please include file extension):");
			transitionMatrix = scanner.next();
			//Load configuration from file using the loadTransitionMatrices method
			double[][][] transitionMatrices = TransitionMatrixLoader.loadTransitionMatrices(transitionMatrix);
			msucc = transitionMatrices[0];
			mfail = transitionMatrices[1];
			
        	
        }
        else //User choose to initialize a new configuration
        {
        	//Prompt user to enter number components in that new configuration
        	System.out.println("\n");
    		System.out.println("\nPlease enter number of heuristics:");
    		int numHeuristics = scanner.nextInt();
    		
    		componentID = heuristicID(heuristics);//Identify the component types(Hill Climber/Mutator)
    		
    		//create a new configuration using the createRandomMatrix method
        	msucc = new double[numHeuristics][numHeuristics];
            mfail = new double[numHeuristics][numHeuristics];
        	msucc = createRandomMatrix(numHeuristics, componentID);
        	mfail = createRandomMatrix(numHeuristics, componentID);
        	
        }
        
        //Prompt user to enter termination time of CMCS
    	System.out.println("\nPlease enter the termination time for CMCS in seconds:");
		long terminateInSeconds = scanner.nextLong();
		
		//Prompting user to enter Simulated Annealing parameters
    	System.out.println("\nPlease enter initial temprature: ");//Initial temperature
        double initialTemperature = scanner.nextDouble();
        System.out.println("\nPlease enter stopping temprature: ");//Stopping temperature
        double stoppingTemperature = scanner.nextDouble();
        System.out.println("\nPlease enter cooling rate: ");//Cooling rate
        double coolingRate = scanner.nextDouble();
        
        double temperature = initialTemperature;//Initializing current temperature
       
    	int iterationNum = 0;//Initialize number of iterations, used to display to console
    	
    	//Printing a big header with the iteration number, useful because of long training times it indicates the current iteration
    	System.out.println("------------------------------------ITERATION " + (iterationNum + 1) + "------------------------------------");
    	
    	Random random = new Random();
    	//Initialize current solution(configuration) and current solution objective value
        double[][] bestMsucc = msucc.clone();
        double[][] bestMfail = mfail.clone();
        double bestScore = ObjectiveFunctionSA.EvaluateConfig(heuristics, instances, msucc, mfail, terminateInSeconds);
        
        int[] componentArray = heuristicID(heuristics);//Identify the component types(Hill Climber/Mutator)
        
        while (temperature > stoppingTemperature)//Run until current temperature reaches stopping temperature 
        {
        	iterationNum++;//Increment iteration number
        	
        	//Printing a big header with the iteration number, useful because of long training times it indicates the current iteration
        	System.out.println("------------------------------------ITERATION " + (iterationNum + 1) + "------------------------------------");
        	//Apply a mutation to the configuration
            double[][] newMsucc = mutate(msucc, componentArray, temperature, stoppingTemperature, initialTemperature);
            double[][] newMfail = mutate(mfail, componentArray, temperature, stoppingTemperature, initialTemperature);
            
            //Display the mutated solution(configuration)
            System.out.println("\nNew Transition Matrix Success: ");
            Main.print2DArray(newMsucc);
            System.out.println("\nNew Transition Matrix Fail: ");
        	Main.print2DArray(newMfail);
        	
        	//Evaluate the solution and return objective value
            double newScore = ObjectiveFunctionSA.EvaluateConfig(heuristics, instances, newMsucc, newMfail, terminateInSeconds);
            double delta = newScore - bestScore;
            
            //Display solution information 
            System.out.println("\nNew Score: " + newScore);
            System.out.println("\nDelta(New Score - Best Score): " + delta);
            
            if (delta > 0 || random.nextDouble() < Math.exp(delta / temperature))//If acceptance criteria is meet, accept new solution 
            {
            	System.out.println("\nMove Accepted");
                msucc = newMsucc;
                mfail = newMfail;
                
            	
                if (newScore > bestScore)//If new solution is better than the best solution the update best solution 
                {
                	//Notify the user that a new best score was reached
                	System.out.println("\nNew Best Score");
                	
                    bestMsucc = msucc.clone();
                    bestMfail = mfail.clone();
                    bestScore = newScore;
                    //Write the best solution to file in case the search terminates abruptly
                    String filename = "New_Config.txt";
                    writeToFile(filename, bestMsucc, bestMfail);
                    
                    //Display the best solution found so far
                    System.out.println("\nBest Score: " + bestScore);
                }
            }
            
            temperature *= coolingRate;//update the current temperature
            System.out.println("\nTemperature: " + temperature);//Display the current temperature
            System.out.println("\nBest Score: " + bestScore);//Display the best solution found so far
        }
        
        //When the search process is over Display the optimized configuration
        System.out.println("\nOptimized Transition Matrix Success: ");
        Main.print2DArray(bestMsucc);
        System.out.println("\nOptimized Transition Matrix Fail: ");
    	Main.print2DArray(bestMfail);
    	
    	//Write the best solution to file
        String filename = "New_Config.txt";
        writeToFile(filename, bestMsucc, bestMfail);
        
        return new double[][][] {bestMsucc, bestMfail};
    }

    /**
     * This method applies one mutation for one matrix either M_succ or M_fail. To determine which mutation to apply every iteration, each mutation has an associated probability, which are dynamically adjusted according a variable called normalized temperature.
     * Normalized temperature is used to determine the probabilities of selecting each mutation.
     * @param matrix A transition of the 2 CMCS transition matrices.
     * @param componentArray Array identifying the component types(Hill Climber/Mutator) at each index of the matrix.
     * @param temperature The current system temperature.
     * @param stoppingTemperature The system's stopping temperature.
     * @param initialTemperature The system's initial temperature.
     * @return returns a mutated matrix.
     */
    private double[][] mutate(double[][] matrix, int[] componentArray, double temperature, double stoppingTemperature, double initialTemperature) 
    {
    	//Initialize a new matrix to avoid altering original matrix
    	int n = matrix.length;
        double[][] newMatrix = new double[n][n];
        Random random = new Random();
        
        /*
         * Calculate the normalized temperature, a value between 0 and 1, 
         * representing the current temperature's position between the starting and stopping temperatures
         */
        double normalizedTemperature = (temperature - stoppingTemperature) / (initialTemperature - stoppingTemperature);

        //Calculate the probability of choosing each mutation
        double pRandomValues = (1/3) + (1/3) * (1 - normalizedTemperature);
        double pRandomRow = (1/3) - (1/6) * (1 - normalizedTemperature);

        //Generate a random double value between 0 and 1
        double randomValue = random.nextDouble();       
        
        if (randomValue < pRandomValues)//If the random value is less than the probability of using "randomValues" mutation 
        {	       	
            newMatrix = randomValues(matrix, componentArray);//Use "randomValues" mutation
        } 
        else if (randomValue < pRandomValues + pRandomRow)// If the random value is between the probability of using "randomValues" and the sum of probabilities 
        {        	
            newMatrix = randomRow(matrix, componentArray);
        } 
        else//If the random value is greater than the sum of probabilities of using "randomValues" and "randomRow" 
        {
            newMatrix = randomizeMatrix(matrix, componentArray);//use "randomizeMatrix"
        }

        return newMatrix;//return the mutated matrix
    }

    /**
     * This method saves a configuration to file.
     * @param filename The filename
     * @param msucc Success matrix of the configuration
     * @param mfail Fail matrix of the configuration
     * @throws IOException
     */
    public void writeToFile(String filename, double[][] msucc, double[][] mfail) throws IOException 
    {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) 
		{
			int n = msucc.length;

			// Write the number of heuristics
			writer.write(Integer.toString(n));
			writer.newLine();

			// Write the msucc matrix
			for (int i = 0; i < n; i++) 
			{
			    for (int j = 0; j < n; j++) 
			    {
			        writer.write(String.format("%.2f", msucc[i][j]));

			        if (j < n - 1) 
			        {
			            writer.write(" ");
			        }
			    }
			    writer.newLine();
			}

			// Write the mfail matrix
			for (int i = 0; i < n; i++) 
			{
			    for (int j = 0; j < n; j++) 
			    {
			        writer.write(String.format("%.2f", mfail[i][j]));

			        if (j < n - 1) 
			        {
			            writer.write(" ");
			        }
			    }
			    writer.newLine();
			}
		}
    }
    
    /**
     * 
     * @param n Number of heuristic components |H|
     * @param componentArray Array identifying the component types(Hill Climber/Mutator) at each index of the matrix.
     * @return returns a mutated matrix.
     */
	public static double[][] createRandomMatrix(int n, int[] componentArray) 
    {
	    Random random = new Random();
	    double[][] matrix = new double[n][n];
	
	    for (int i = 0; i < n; i++) 
	    {
	        //Choose the number of non-zero values for each row, between 1 and 3 inclusive
	        int nonZeroCount = random.nextInt(3) + 1;
	
	        //Initialize all elements in newRow to 0
	        double[] newRow = new double[n];
	        Arrays.fill(newRow, 0);
	
	        //Set nonZeroCount randomly chosen elements in newRow to non-zero values
	        for (int j = 0; j < nonZeroCount; j++) 
	        {
	            int randomIndex;
	            do 
	            {
	                //Choose a random index in newRow
	                randomIndex = random.nextInt(n);
	            } while (newRow[randomIndex] != 0 || (componentArray[i] == 1 && componentArray[randomIndex] == 1)); // Repeat until an index with a value of 0 is found
	
	            //Set a random non-zero value for the chosen index
	            newRow[randomIndex] = random.nextDouble();
	        }
	
	        //Normalize newRow so that the sum of its elements is 1
	        double sum = Arrays.stream(newRow).sum();
	        double normalizedSum = 0;
	        for (int j = 0; j < n; j++) 
	        {
	            newRow[j] /= sum;
	            normalizedSum += newRow[j];
	            if (j == n - 1 && normalizedSum < 1) 
	            {
	                newRow[j] += 1 - normalizedSum;
	            }
	        }
	
	        // Set the newRow to the matrix
	        matrix[i] = newRow;
	    }
	    
	    
	    return matrix;
	}
	
	/**
	 * This mutation completely randomizes the matrix, it could be thought of as applying "randomRow" mutation to all the rows of the matrix.
	 * @param matrix A transition of the 2 CMCS transition matrices.
	 * @param componentArray Array identifying the component types(Hill Climber/Mutator) at each index of the matrix.
	 * @return returns a mutated matrix.
	 */
	public static double[][] randomizeMatrix(double[][] matrix, int[] componentArray) 
	{
		
	    int n = matrix.length;
	    Random random = new Random();
	    
	    double[][] newMatrix = new double[n][n];
	    for (int i = 0; i < n; i++) 
	    {
	        newMatrix[i] = Arrays.copyOf(matrix[i], n);
	    }
	    
	    for (int i = 0; i < n; i++)//For each row in the matrix 
	    {
	        //Choose the number of non-zero values for each row, between 1 and 3 inclusive
	        int nonZeroCount = random.nextInt(3) + 1;

	        //Initialize all elements in newRow to 0
	        double[] newRow = new double[n];
	        Arrays.fill(newRow, 0);

	        //Set nonZeroCount randomly chosen elements in newRow to non-zero values
	        for (int j = 0; j < nonZeroCount; j++) 
	        {
	            int randomIndex;
	            do //Repeat until an index with a value of 0 is found
	            {
                //Choose a random index in newRow
	            	randomIndex = random.nextInt(n);
                } while (newRow[randomIndex] != 0 || (componentArray[i] == 1 && componentArray[randomIndex] == 1));//Transition between hill climbers are prohibited

	            //Set a random non-zero value for the chosen index
	            newRow[randomIndex] = random.nextDouble();
	        }

	        //Normalize newRow so that the sum of its elements is 1
	        double sum = Arrays.stream(newRow).sum();
	        double normalizedSum = 0;
	        for (int j = 0; j < n; j++) 
	        {
	            newRow[j] /= sum;
	            normalizedSum += newRow[j];
	            if (j == n - 1 && normalizedSum < 1) 
	            {
	                newRow[j] += 1 - normalizedSum;
	            }
	        }
	        
	        
	        // Set the newRow to the randomizedMatrix
	        newMatrix[i] = newRow;
	        
	    }
	    
	    
		return newMatrix;
	}
	
	/**
	 * This mutation chooses a random row in the matrix and randomizes it.
	 * @param matrix A transition of the 2 CMCS transition matrices.
	 * @param componentArray Array identifying the component types(Hill Climber/Mutator) at each index of the matrix.
	 * @return returns a mutated matrix.
	 */
	public static double[][] randomRow(double[][] matrix, int[] componentArray)
	{
		
		int n = matrix.length;
		
		double[][] newMatrix = new double[n][n];
	    for (int i = 0; i < n; i++) 
	    {
	        newMatrix[i] = Arrays.copyOf(matrix[i], n);
	    }

	    Random random = new Random();

	    //Choose a random row to modify
	    int randomRow = random.nextInt(n);

	    //Choose the number of non-zero values for the random row, between 1 and 3 inclusive
	    int nonZeroCount = random.nextInt(3) + 1;

	    //Initialize all elements in newRow to 0
	    double[] newRow = new double[n];
	    Arrays.fill(newRow, 0);

	    //Set nonZeroCount randomly chosen elements in newRow to non-zero values
	    for (int j = 0; j < nonZeroCount; j++) 
	    {
	        int randomIndex;
	        do//Repeat until an index with a value of 0 is found 
	        {
	            //Choose a random index in newRow
	            randomIndex = random.nextInt(n);
	            
	        } while (newRow[randomIndex] != 0 || (componentArray[randomRow] == 1 && componentArray[randomIndex] == 1));//Transition between hill climbers are prohibited
	        
	        newRow[randomIndex] = random.nextDouble();
	    }
	    
	    //Normalize row values so that the sum of its elements is 1
	    double sum = Arrays.stream(newRow).sum();
	    double normalizedSum = 0;
        for (int j = 0; j < n; j++) 
        {
            newRow[j] /= sum;
            normalizedSum += newRow[j];
            if (j == n - 1 && normalizedSum < 1) 
            {
                newRow[j] += 1 - normalizedSum;
            }
        }

	    //Replace the random row in the modifiedMatrix with newRow
	    newMatrix[randomRow] = newRow;

	    return newMatrix;
	}
	
	/**
	 * This mutation chooses a random row in the matrix and randomizes any non zero values in that row.
	 * @param matrix A transition of the 2 CMCS transition matrices.
	 * @param componentArray Array identifying the component types(Hill Climber/Mutator) at each index of the matrix.
	 * @return returns a mutated matrix.
	 */
	public static double[][] randomValues(double[][] matrix, int[] componentArray)
	{
		
		int n = matrix.length;
		
		double[][] newMatrix = new double[n][n];
	    for (int i = 0; i < n; i++) 
	    {
	        newMatrix[i] = Arrays.copyOf(matrix[i], n);
	    }
	    
		Random random = new Random();

	    //Choose a random row to modify
	    int randomRow = random.nextInt(n);
  
	    //Check if the row has any non-zero values
	    boolean hasNonZeroValues = false;
	    for (int j = 0; j < n; j++) 
	    {
	        if (newMatrix[randomRow][j] != 0) 
	        {
	            hasNonZeroValues = true;
	            break;
	        }
	    }

	    //If the row has no non-zero values, return the original matrix
	    if (!hasNonZeroValues) 
	    {
	        return matrix;
	    }
	    
	    for (int j = 0; j < n; j++) 
	    {
	        if (newMatrix[randomRow][j] != 0) 
	        {
	        	newMatrix[randomRow][j] = random.nextDouble();
	        }
	    }
	    
	    //Normalize row values so that the sum of its elements is 1
	    double sum = Arrays.stream(newMatrix[randomRow]).sum();
	    double normalizedSum = 0;
	    for (int j = 0; j < n; j++) 
	    {
	        newMatrix[randomRow][j] /= sum;
	        normalizedSum += newMatrix[randomRow][j];
	        if (j == n - 1 && normalizedSum < 1) 
	        {
	            newMatrix[randomRow][j] += 1 - normalizedSum;
	        }
	    }

	    
		return newMatrix;
	}
	
	/**
	 * This method assigns IDs to the input heuristics based on their class names. 
	 * If a heuristic's class name starts with "H", it is assigned 1, and if it starts with "M", it is assigned 0.
	 * @param heuristics
	 * @return Array with binary encoding (1 if a component is a hill climber, 0 if mutator)
	 */
	public int[] heuristicID(List<Heuristic> heuristics) 
	{
		//Create a new integer array with the same length as the input list of heuristic components
	    int[] componentID = new int[heuristics.size()];

	    //Loop through each heuristic in the list
	    for (int i = 0; i < heuristics.size(); i++) 
	    {
	    	//Get the simple class name of the heuristic object (e.g., HillClimber1 or Mutator1)
	        String componentName = heuristics.get(i).getClass().getSimpleName();
	        
	        if (componentName.startsWith("H"))//If a heuristic's class name starts with "H", assign the heuristic ID 1 to the corresponding element in the componentID array
	        {
	        	componentID[i] = 1;
	        } 
	        else if (componentName.startsWith("M"))//If a heuristic's class name starts with "M", assign the heuristic ID 0 to the corresponding element in the componentID array 
	        {
	        	componentID[i] = 0;
	        }
	    }

	    return componentID;//Return the array of heuristic IDs
	}
	
}