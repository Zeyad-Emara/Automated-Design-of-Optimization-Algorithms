import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class is responsible for loading a CMCS configuration from a text file.
 * The CMCS configuration must be stored in an exact format. The first line is a single integer n, representing the size of the square transition matrices (in this case, n = 6). 
 * The next n lines represent the first transition matrix, M_Succ, with n space separated floating-point values on each line.
 * The next n lines represent the second transition matrix, M_Fail, with n space separated floating-point values on each line.
 * 
 *
 */
public class TransitionMatrixLoader 
{
	/**
	 * 
	 * @param filename
	 * @return
	 * @throws FileNotFoundException
	 */
    public static double[][][] loadTransitionMatrices(String filename) throws FileNotFoundException 
    {
        Scanner scanner = new Scanner(new File(filename));

        int n = scanner.nextInt(); // Number of heuristics
        double[][] msucc = new double[n][n];
        double[][] mfail = new double[n][n];

        // Read m_succ matrix
        for (int i = 0; i < n; i++) 
        {
            for (int j = 0; j < n; j++) 
            {
                msucc[i][j] = scanner.nextDouble();
            }
        }

        // Read m_fail matrix
        for (int i = 0; i < n; i++) 
        {
            for (int j = 0; j < n; j++) 
            {
                mfail[i][j] = scanner.nextDouble();
            }
        }

        scanner.close();

        return new double[][][] {msucc, mfail};//Return a 3D array containing the 2 transition matrices 
    }
}
