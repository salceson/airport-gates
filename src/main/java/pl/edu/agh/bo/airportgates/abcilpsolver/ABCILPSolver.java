package pl.edu.agh.bo.airportgates.abcilpsolver;

import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.ResultImpl;
import net.sf.javailp.Solver;
import pl.edu.agh.bo.airportgates.abcilpsolver.exceptions.ABCILPSolverException;
import pl.edu.agh.bo.airportgates.abcilpsolver.phases.employedbeesphase.EmployedBeesPhase;
import pl.edu.agh.bo.airportgates.abcilpsolver.phases.employedbeesphase.EmployedBeesPhaseArgs;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Michał Ciołczyk
 */
public class ABCILPSolver implements Solver {
    public static final int THREAD_POOL_SIZE_PARAMETER = 4000;
    public static final int BEES_COUNT_PARAMETER = 4001;
    public static final int MODIFICATION_RATE_PARAMETER = 4002;
    public static final int SEARCH_RANGE_PARAMETER = 4003;
    public static final int ITERATIONS_PARAMETER = 4004;
    public static final int LOWER_BOUND_PARAMETER = 4005;
    public static final int UPPER_BOUND_PARAMETER = 4006;

    private final Random random = new Random();
    private final Map<Object, Object> parameters = new HashMap<>();

    @Override
    public void setParameter(Object o, Object o1) {
        parameters.put(o, o1);
    }

    @Override
    public Map<Object, Object> getParameters() {
        return parameters;
    }

    @Override
    public Result solve(Problem problem) throws ABCILPSolverException {
        checkParameters();
        int poolSize, beesCount, iterations, lowerBound, upperBound;
        double modificationRate, searchRange;
        try {
            poolSize = (int) parameters.get(THREAD_POOL_SIZE_PARAMETER);
            beesCount = (int) parameters.get(BEES_COUNT_PARAMETER);
            modificationRate = (double) parameters.get(MODIFICATION_RATE_PARAMETER);
            searchRange = (double) parameters.get(SEARCH_RANGE_PARAMETER);
            iterations = (int) parameters.get(ITERATIONS_PARAMETER);
            lowerBound = (int) parameters.get(LOWER_BOUND_PARAMETER);
            upperBound = (int) parameters.get(UPPER_BOUND_PARAMETER);
        } catch (ClassCastException e) {
            throw new ABCILPSolverException("Please provide parameters as ints.", e);
        }

        List<Object> variables = newArrayList(problem.getVariables());

        int dimension = variables.size();

        Solution[] currentSolutions = new Solution[beesCount];
        Solution[] newSolutions = new Solution[beesCount];

        //Initiate solutions
        for (int i = 0; i < beesCount; i++) {
            Map<Object, Long> solutionMap = new HashMap<>();
            for (Object variable : variables) {
                long varNum = random.nextInt(upperBound - lowerBound + 1) - lowerBound;
                solutionMap.put(variable, varNum);
            }
            currentSolutions[i] = new Solution(dimension, solutionMap, problem);
        }

        int beesPerThread = beesCount / poolSize;

        //Main loop
        for (int cycle = 1; cycle <= iterations; cycle++) {
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
                            .build()
            );

            newSolutions = new Solution[beesCount];
        }

        List<Solution> solutions = Arrays.asList(currentSolutions);
        Collections.sort(solutions);
        Solution bestSolution = solutions.get(0);

        if (bestSolution.isValid()) {
            Result result = new ResultImpl(bestSolution.getObjectiveValue());

            for (Map.Entry<Object, Long> variableEntry : bestSolution.getVariables().entrySet()) {
                result.put(variableEntry.getKey(), variableEntry.getValue());
            }

            return result;
        }

        return null;
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

        if (!parameters.containsKey(LOWER_BOUND_PARAMETER)) {
            throw new ABCILPSolverException("You need to specify the lower bound for variables" +
                    " by setting (example):\n"
                    + "solverFactory.setParameter(ABCILPSolver.LOWER_BOUND_PARAMETER, lowerBoundariesArray);\n"
                    + "Expected type: int[].\n"
                    + "Please correct your solver configuration.");
        }

        if (!parameters.containsKey(UPPER_BOUND_PARAMETER)) {
            throw new ABCILPSolverException("You need to specify the upper bound for variables" +
                    " by setting (example):\n"
                    + "solverFactory.setParameter(ABCILPSolver.UPPER_BOUND_PARAMETER, upperBoundariesArray);\n"
                    + "Expected type: int[].\n"
                    + "Please correct your solver configuration.");
        }
    }
}
