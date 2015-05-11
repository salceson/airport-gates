package pl.edu.agh.bo.airportgates.abcilpsolver.phases.employedbeesphase;

import lombok.RequiredArgsConstructor;
import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Worker for Employed Bees' Phase.
 *
 * @author Michał Ciołczyk
 */
@RequiredArgsConstructor
public class EmployedBeesPhaseWorker implements Runnable {
    private final EmployedBeesPhaseWorkerArgs args;
    private final Random random = new Random();
    private Boolean finished = false;

    @Override
    public void run() {
        int startBee = args.getStartBee();
        int endBee = args.getEndBee();
        int dimension = args.getDimension();
        double modificationRate = args.getModificationRate();
        double a = args.getSearchRange();
        Problem problem = args.getProblem();
        List<Object> variables = newArrayList(problem.getVariables());
        Solution[] currentSolutions = args.getSolutions();
        Solution[] newSolutions = args.getNewSolutions();
        int lowerBound = args.getLowerBound();
        int upperBound = args.getUpperBound();

        for (int i = startBee; i < endBee; i++) {
            Map<Object, Long> newSolutionVariables = new HashMap<>();
            for (Object variable : variables) {
                long variableVal = currentSolutions[i].getVariables().get(variable);

                int k = getK(i);
                long variableKVal = currentSolutions[k].getVariables().get(variable);

                if (random.nextDouble() < modificationRate) {
                    long newVal = variableVal + Math.round(getPhi(a) * (variableVal - variableKVal));

                    if (newVal > upperBound) {
                        newVal = upperBound;
                    }
                    if (newVal < lowerBound) {
                        newVal = lowerBound;
                    }

                    newSolutionVariables.put(variable, newVal);
                } else {
                    newSolutionVariables.put(variable, variableVal);
                }
                newSolutions[i] = new Solution(dimension, newSolutionVariables, problem);
            }
        }

        synchronized (finished) {
            finished = true;
        }
    }

    /**
     * Returns random number from range [-a, a] (phi_i_j).
     *
     * @param a determines search range
     * @return number from range [-a, a]
     */
    private double getPhi(double a) {
        return random.nextDouble() * 2 * a - a;
    }

    /**
     * Returns random bee's index that is not the current bee.
     *
     * @param bee index of current bee
     * @return index that is not bee
     */
    private int getK(int bee) {
        int beesCount = args.getBeesCount();
        int k;

        do {
            k = random.nextInt(beesCount);
        } while (k == bee);

        return k;
    }

    /**
     * Waits for completion of the worker.
     *
     * @throws InterruptedException
     */
    public void waitForFinish() throws InterruptedException {
        synchronized (finished) {
            while (!finished) {
                finished.wait();
            }
        }
    }
}
