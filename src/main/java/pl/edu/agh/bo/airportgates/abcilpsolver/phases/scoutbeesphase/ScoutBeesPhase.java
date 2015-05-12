package pl.edu.agh.bo.airportgates.abcilpsolver.phases.scoutbeesphase;

import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;
import pl.edu.agh.bo.airportgates.abcilpsolver.phases.utils.PhasesUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Scout Bees' Phase of the ABC algorithm.
 *
 * @author Michał Ciołczyk
 */
public class ScoutBeesPhase {
    public static void run(ScoutBeesPhaseArgs args) {
        Problem problem = args.getProblem();
        int[] tries = args.getTries();
        int beesCount = args.getBeesCount();
        List<Integer> triesList = new ArrayList<>(beesCount);
        int abandonmentLimit = args.getAbandonmentLimit();
        Solution[] currentSolutions = args.getCurrentSolutions();
        int lowerBound = args.getLowerBound();
        int upperBound = args.getUpperBound();
        int dimension = args.getDimension();
        int scoutBeesNumber = args.getScoutBeesNumber();

        for (int bee = 0; bee < scoutBeesNumber; bee++) {
            for (int i = 0; i < beesCount; i++) {
                triesList.add(i, tries[i]);
            }

            int max = Collections.max(triesList);

            for (int i = 0; i < beesCount; i++) {
                if (tries[i] == max) {
                    if (tries[i] > abandonmentLimit) {
                        currentSolutions[i] = PhasesUtils.createSolution(problem, lowerBound, upperBound, dimension);
                        currentSolutions[i].getFitness();
                        tries[i] = 0;
                    }
                    break;
                }
            }
        }
    }
}
