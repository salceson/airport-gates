package pl.edu.agh.bo.airportgates.abcilpsolver;

import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.ResultImpl;
import net.sf.javailp.Solver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.bo.airportgates.abcilpsolver.exceptions.ABCILPSolverException;
import pl.edu.agh.bo.airportgates.abcilpsolver.phases.employedbeesphase.EmployedBeesPhase;
import pl.edu.agh.bo.airportgates.abcilpsolver.phases.employedbeesphase.EmployedBeesPhaseArgs;
import pl.edu.agh.bo.airportgates.abcilpsolver.phases.initiatephase.InitiatePhase;
import pl.edu.agh.bo.airportgates.abcilpsolver.phases.initiatephase.InitiatePhaseArgs;
import pl.edu.agh.bo.airportgates.abcilpsolver.phases.onlookerbeesphase.OnlookerBeesPhase;
import pl.edu.agh.bo.airportgates.abcilpsolver.phases.onlookerbeesphase.OnlookerBeesPhaseArgs;
import pl.edu.agh.bo.airportgates.abcilpsolver.phases.scoutbeesphase.ScoutBeesPhase;
import pl.edu.agh.bo.airportgates.abcilpsolver.phases.scoutbeesphase.ScoutBeesPhaseArgs;
import pl.edu.agh.bo.airportgates.gui.GUI;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * The Artificial Bees' Colony Integer Linear Programming Solver Algorithm.
 *
 * @author Michał Ciołczyk
 */
public class ABCILPSolver implements Solver {
    /**
     * Parameter that determines thread pool size used by algorithm.
     * <p/>
     * Expected type: int.
     */
    public static final int THREAD_POOL_SIZE_PARAMETER = 4000;
    /**
     * Parameter that determines bees population size.
     * <p/>
     * Expected type: int.
     */
    public static final int BEES_COUNT_PARAMETER = 4001;
    /**
     * Parameter that determines how many variables are modified during calculating of new solutions.
     * <p/>
     * Expected type: double.
     */
    public static final int MODIFICATION_RATE_PARAMETER = 4002;
    /**
     * Parameter that determines size of the neighbourhood that is looked at during calculating of new solutions.
     * <p/>
     * Expected type: double.
     */
    public static final int SEARCH_RANGE_PARAMETER = 4003;
    /**
     * Parameter that determines how many iterations of algorithm will be run.
     * <p/>
     * Expected type: int.
     */
    public static final int ITERATIONS_PARAMETER = 4004;
    /**
     * Parameter that determines how many tries bees has to improve their solution before recalculating
     * the solution as random.
     * <p/>
     * Expected type: int.
     */
    public static final int ABANDONMENT_LIMIT_PARAMETER = 4005;
    /**
     * Parameter that determines the number of scout bees.
     * <p/>
     * Expected type: int.
     */
    public static final int SCOUT_BEES_NUMBER_PARAMETER = 4006;
    /**
     * Parameter that determines the lower bound of the universe.
     * <p/>
     * Expected type: int.
     */
    public static final int LOWER_BOUND_PARAMETER = 4007;
    /**
     * Parameter that determines the upper bound of the universe.
     * <p/>
     * Expected type: int.
     */
    public static final int UPPER_BOUND_PARAMETER = 4008;

    private final Map<Object, Object> parameters = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger("ABCILP");

    /**
     * Sets the parameter o to value o1.
     *
     * @param o  the parameter to set
     * @param o1 the value to set
     */
    @Override
    public void setParameter(Object o, Object o1) {
        parameters.put(o, o1);
    }

    /**
     * Gets the parameters map.
     *
     * @return the map containing parameters
     */
    @Override
    public Map<Object, Object> getParameters() {
        return parameters;
    }

    /**
     * Solves ILP Problem. If no solution can be found, null is returned.
     *
     * @param problem problem to solve
     * @return solution if found, null otherwise
     * @throws ABCILPSolverException
     */
    @Override
    public Result solve(Problem problem) throws ABCILPSolverException {
        checkParameters();
        int poolSize, beesCount, iterations, lowerBound, upperBound, abandonmentLimit, scoutBeesNumber;
        double modificationRate, searchRange;
        try {
            poolSize = (int) parameters.get(THREAD_POOL_SIZE_PARAMETER);
            beesCount = (int) parameters.get(BEES_COUNT_PARAMETER);
            modificationRate = (double) parameters.get(MODIFICATION_RATE_PARAMETER);
            searchRange = (double) parameters.get(SEARCH_RANGE_PARAMETER);
            iterations = (int) parameters.get(ITERATIONS_PARAMETER);
            lowerBound = (int) parameters.get(LOWER_BOUND_PARAMETER);
            upperBound = (int) parameters.get(UPPER_BOUND_PARAMETER);
            abandonmentLimit = (int) parameters.get(ABANDONMENT_LIMIT_PARAMETER);
            scoutBeesNumber = (int) parameters.get(SCOUT_BEES_NUMBER_PARAMETER);
        } catch (ClassCastException e) {
            throw new ABCILPSolverException("Please provide parameters in correct types.", e);
        }

        boolean verbose = false;
        try {
            verbose = parameters.containsKey(Solver.VERBOSE) && (int) parameters.get(Solver.VERBOSE) > 0;
        } catch (ClassCastException ignored) {
        }

        int dimension = problem.getVariablesCount();

        Solution[] currentSolutions = new Solution[beesCount];
        Solution[] newSolutions = new Solution[beesCount];
        int[] tries = new int[beesCount];

        if (verbose) {
            logger.debug("Initiating solutions...");
        }

        //Initiate solutions
        InitiatePhase.run(
                InitiatePhaseArgs.builder()
                        .beesCount(beesCount)
                        .dimension(dimension)
                        .lowerBound(lowerBound)
                        .upperBound(upperBound)
                        .problem(problem)
                        .tries(tries)
                        .currentSolutions(currentSolutions)
                        .build()
        );

        int beesPerThread = beesCount / poolSize;

        List<Solution> initialSolutions = newArrayList(currentSolutions);
        Collections.sort(initialSolutions);
        Solution totalBestSolution = initialSolutions.get(beesCount - 1);

        long bestSolutionIteration = 0;

        //Main loop
        for (int cycle = 1; cycle <= iterations; cycle++) {
            if (verbose) {
                logger.debug("Cycle (iteration): " + cycle);
                logger.debug("\tEmployed Bees' Phase...");
            }

            EmployedBeesPhase.run(
                    EmployedBeesPhaseArgs.builder()
                            .problem(problem)
                            .poolSize(poolSize)
                            .beesCount(beesCount)
                            .modificationRate(modificationRate)
                            .searchRange(searchRange)
                            .dimension(dimension)
                            .currentSolutions(currentSolutions)
                            .newSolutions(newSolutions)
                            .beesPerThread(beesPerThread)
                            .lowerBound(lowerBound)
                            .upperBound(upperBound)
                            .tries(tries)
                            .build()
            );

            if (verbose) {
                logger.debug("\tOnlooker Bees' Phase...");
            }

            OnlookerBeesPhase.run(
                    OnlookerBeesPhaseArgs.builder()
                            .beesCount(beesCount)
                            .beesPerThread(beesPerThread)
                            .poolSize(poolSize)
                            .currentSolutions(currentSolutions)
                            .newSolutions(newSolutions)
                            .dimension(dimension)
                            .lowerBound(lowerBound)
                            .upperBound(upperBound)
                            .modificationRate(modificationRate)
                            .searchRange(searchRange)
                            .problem(problem)
                            .tries(tries)
                            .build()
            );

            if (verbose) {
                logger.debug("\tOnlooker Bees' Phase...");
            }

            ScoutBeesPhase.run(
                    ScoutBeesPhaseArgs.builder()
                            .beesCount(beesCount)
                            .tries(tries)
                            .dimension(dimension)
                            .currentSolutions(currentSolutions)
                            .upperBound(upperBound)
                            .lowerBound(lowerBound)
                            .abandonmentLimit(abandonmentLimit)
                            .problem(problem)
                            .scoutBeesNumber(scoutBeesNumber)
                            .build()
            );

            List<Solution> solutions = Arrays.asList(currentSolutions);
            Collections.sort(solutions);
            Solution bestSolution = solutions.get(beesCount - 1);
            Solution worstSolution = solutions.get(0);

            if (bestSolution.compareTo(totalBestSolution) > 0) {
                totalBestSolution = bestSolution;
                bestSolutionIteration = cycle;
            }

            if (cycle % 5 == 0) {
                long iterBest = bestSolution.getObjectiveValue();
                long iterWorst = worstSolution.getObjectiveValue();
                long totalBest = totalBestSolution.getObjectiveValue();
                System.out.println(
                        "Iteration " + cycle + ": Best (iteration): " + iterBest
                                + ", Worst (iteration): " + iterWorst
                                + ", Best (TOTAL): " + totalBest
                );

                GUI.addCostsToChart(iterBest, iterWorst, totalBest);
            }
        }

        System.out.println(totalBestSolution.isValid(true));

//        if (totalBestSolution.isValid()) {
            Result result = new ResultImpl(totalBestSolution.getObjectiveValue());

            for (Map.Entry<Object, Long> variableEntry : totalBestSolution.getVariables().entrySet()) {
                result.put(variableEntry.getKey(), variableEntry.getValue());
            }

        result.put("bestSolutionIteration", bestSolutionIteration);

            if (verbose) {
                logger.debug("Found valid solution: " + result);
            }

            return result;
//        }
//
//        if (verbose) {
//            logger.debug("Solution not found.");
//        }
//
//        return null;
    }

    private void checkParameters() {
        if (!parameters.containsKey(THREAD_POOL_SIZE_PARAMETER)) {
            throw new ABCILPSolverException("You need to specify the thread pool" +
                    " by setting (example):\n"
                    + "solverFactory.setParameter(ABCILPSolver.THREAD_POOL_SIZE_PARAMETER, 4);\n"
                    + "Expected type: int.\n"
                    + "Please correct your solver configuration.");
        }

        if (!parameters.containsKey(BEES_COUNT_PARAMETER)) {
            throw new ABCILPSolverException("You need to specify the bees count" +
                    " by setting (example):\n"
                    + "solverFactory.setParameter(ABCILPSolver.BEES_COUNT_PARAMETER, 20);\n"
                    + "Expected type: int.\n"
                    + "Please correct your solver configuration.");
        }

        if (!parameters.containsKey(ITERATIONS_PARAMETER)) {
            throw new ABCILPSolverException("You need to specify the iterations number" +
                    " by setting (example):\n"
                    + "solverFactory.setParameter(ABCILPSolver.ITERATIONS_PARAMETER, 1000);\n"
                    + "Expected type: int.\n"
                    + "Please correct your solver configuration.");
        }

        if (!parameters.containsKey(MODIFICATION_RATE_PARAMETER)) {
            throw new ABCILPSolverException("You need to specify the modification rate" +
                    " by setting (example):\n"
                    + "solverFactory.setParameter(ABCILPSolver.MODIFICATION_RATE_PARAMETER, 0.3);\n"
                    + "Expected type: double.\n"
                    + "Please correct your solver configuration.");
        }

        if (!parameters.containsKey(SEARCH_RANGE_PARAMETER)) {
            throw new ABCILPSolverException("You need to specify the search range" +
                    " by setting (example):\n"
                    + "solverFactory.setParameter(ABCILPSolver.SEARCH_RANGE_PARAMETER, 1.0);\n"
                    + "Expected type: double.\n"
                    + "Please correct your solver configuration.");
        }

        if (!parameters.containsKey(ABANDONMENT_LIMIT_PARAMETER)) {
            throw new ABCILPSolverException("You need to specify the abandonment limit" +
                    " by setting (example):\n"
                    + "solverFactory.setParameter(ABCILPSolver.ABANDONMENT_LIMIT_PARAMETER, 20);\n"
                    + "Expected type: int.\n"
                    + "Please correct your solver configuration.");
        }

        if (!parameters.containsKey(SCOUT_BEES_NUMBER_PARAMETER)) {
            throw new ABCILPSolverException("You need to specify the scout bees' number" +
                    " by setting (example):\n"
                    + "solverFactory.setParameter(ABCILPSolver.SCOUT_BEES_NUMBER_PARAMETER, 20);\n"
                    + "Expected type: int.\n"
                    + "Please correct your solver configuration.");
        }

        if (!parameters.containsKey(LOWER_BOUND_PARAMETER)) {
            throw new ABCILPSolverException("You need to specify the lower bound for variables" +
                    " by setting (example):\n"
                    + "solverFactory.setParameter(ABCILPSolver.LOWER_BOUND_PARAMETER, 0;\n"
                    + "Expected type: int.\n"
                    + "Please correct your solver configuration.");
        }

        if (!parameters.containsKey(UPPER_BOUND_PARAMETER)) {
            throw new ABCILPSolverException("You need to specify the upper bound for variables" +
                    " by setting (example):\n"
                    + "solverFactory.setParameter(ABCILPSolver.UPPER_BOUND_PARAMETER, 1);\n"
                    + "Expected type: int.\n"
                    + "Please correct your solver configuration.");
        }
    }
}
