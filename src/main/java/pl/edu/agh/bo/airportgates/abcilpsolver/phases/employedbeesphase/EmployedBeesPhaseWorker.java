package pl.edu.agh.bo.airportgates.abcilpsolver.phases.employedbeesphase;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;

import java.util.*;

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
    @Getter
    private Boolean finished = false;

    @Override
    public void run() {
        int startBee = args.getStartBee();
        int endBee = args.getEndBee();
        int dimension = args.getDimension();
        int modificationRate = args.getModificationRate();
        Problem problem = args.getProblem();
        List<Object> variables = newArrayList(problem.getVariables());
        Solution[] currentSolutions = args.getSolutions();
        Solution[] newSolutions = args.getNewSolutions();

        for (int i = startBee; i < endBee; i++) {
            Map<Object, Long> newSolutionVariables = new HashMap<>();
            List<Object> variablesCopy = newArrayList(variables);
            Collections.shuffle(variablesCopy);
            List<Object> variablesToChange = variablesCopy.subList(0, modificationRate);
            for (Object variable : variables) {
                long variableVal = currentSolutions[i].getVariables().get(variable);
                int k = getK(i);
                long variableKVal = currentSolutions[k].getVariables().get(variable);
                if (variablesToChange.contains(variable)) {
                    newSolutionVariables.put(variable, variableVal
                            + getPhi() * (variableVal - variableKVal));
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
     * Returns random integer from range [-1, 1] (phi_i_j).
     *
     * @return int from range [-1, 1]
     */
    private long getPhi() {
        return random.nextInt(3) - 1;
    }

    /**
     * Returns random index that is not bee.
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
