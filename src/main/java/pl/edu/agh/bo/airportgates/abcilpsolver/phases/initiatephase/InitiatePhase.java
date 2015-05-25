package pl.edu.agh.bo.airportgates.abcilpsolver.phases.initiatephase;

import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;
import pl.edu.agh.bo.airportgates.abcilpsolver.phases.utils.PhasesUtils;

/**
 * @author Michał Ciołczyk
 */
public class InitiatePhase {
    public static void run(InitiatePhaseArgs args) {
        int beesCount = args.getBeesCount();
        int[] tries = args.getTries();
        Problem problem = args.getProblem();
        int lowerBound = args.getLowerBound();
        int upperBound = args.getUpperBound();
        int dimension = args.getDimension();
        Solution[] currentSolutions = args.getCurrentSolutions();

        for (int i = 0; i < beesCount; i++) {
            tries[i] = 0;
            currentSolutions[i] = PhasesUtils.createSolution(problem, lowerBound, upperBound, dimension);
        }
    }
}
