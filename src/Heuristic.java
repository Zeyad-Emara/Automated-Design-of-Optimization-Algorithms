/**
 * 
 * Heuristic interface which is implemented by any heuristic component.
 * This interface facilitates treating the heuristic components as black box algorithms through abstraction.
 *
 */
public interface Heuristic 
{
	int[] run(int[] solution, int[][] adjMatrix, ObjectiveFunctionMWCP evaluator);//method for executing a heuristic component on a given solution 

}
