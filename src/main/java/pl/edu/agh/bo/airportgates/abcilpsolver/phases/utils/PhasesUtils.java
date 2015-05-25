package pl.edu.agh.bo.airportgates.abcilpsolver.phases.utils;

import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Michał Ciołczyk
 */
public class PhasesUtils {
    private static Random random = new Random();

    /**
     * Returns random number from range [-searchRate, searchRate] (phi_i_j).
     *
     * @param searchRate determines search range
     * @return number from range [-searchRate, searchRate]
     */
    public static double getPhi(double searchRate) {
        return random.nextDouble() * 2 * searchRate - searchRate;
    }

    /**
     * Returns random bee's index that is not the current bee.
     *
     * @param bee       index of current bee
     * @param beesCount the number of bees population
     * @return index that is not bee
     */
    public static int getK(int bee, int beesCount) {
        int k;

        do {
            k = random.nextInt(beesCount);
        } while (k == bee);

        return k;
    }

    /**
     * Creates random solution for problem.
     *
     * @param problem    ILP problem being solved
     * @param lowerBound lower bound of the universe
     * @param upperBound upper bound of the universe
     * @param dimension  universe's dimension
     * @return solution in universe
     */
    public static Solution createSolution(Problem problem, int lowerBound, int upperBound, int dimension) {
        Map<Object, Long> solutionMap = new HashMap<>();
        for (Object variable : problem.getVariables()) {
            long varNum = random.nextInt(upperBound - lowerBound + 1) - lowerBound;
            solutionMap.put(variable, varNum);
        }
        return new Solution(dimension, solutionMap, problem);
    }
}
