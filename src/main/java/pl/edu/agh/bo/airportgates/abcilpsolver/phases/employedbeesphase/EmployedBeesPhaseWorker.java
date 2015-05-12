package pl.edu.agh.bo.airportgates.abcilpsolver.phases.employedbeesphase;

import lombok.RequiredArgsConstructor;
import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;
import pl.edu.agh.bo.airportgates.abcilpsolver.phases.utils.PhasesUtils;

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
        int beesCount = args.getBeesCount();

        for (int i = startBee; i < endBee; i++) {
            Map<Object, Long> newSolutionVariables = new HashMap<>();
            for (Object variable : variables) {
                long variableVal = currentSolutions[i].getVariables().get(variable);

                int k = PhasesUtils.getK(i, beesCount);
                long variableKVal = currentSolutions[k].getVariables().get(variable);

                if (random.nextDouble() < modificationRate) {
                    long newVal = variableVal + Math.round(PhasesUtils.getPhi(a) * (variableVal - variableKVal));

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

                //Invoke lazy evaluation of fitness - it'll be done quicker in multi threads
                newSolutions[i].getFitness();
            }
        }

        synchronized (finished) {
            finished = true;
            finished.notifyAll();
        }
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
