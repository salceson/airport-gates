package pl.edu.agh.bo.airportgates.abcilpsolver.phases.onlookerbeesphase;

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
 * Onlooker Bees' Phase Solution Worker.
 * <p/>
 * Represents threads that calculate new solutions in Onlooker Bees' Phase.
 *
 * @author Michał Ciołczyk
 */
@RequiredArgsConstructor
public class OnlookerBeesPhaseSolutionWorker implements Runnable {
    private final OnlookerBeesPhaseSolutionWorkerArgs args;
    private Boolean finished = false;
    private Random random = new Random();

    @Override
    public void run() {
        int startBee = args.getStartBee();
        int endBee = args.getEndBee();
        int dimension = args.getDimension();
        double modificationRate = args.getModificationRate();
        double a = args.getSearchRange();
        double[] prob = args.getProb();
        Problem problem = args.getProblem();
        List<Object> variables = newArrayList(problem.getVariables());
        Solution[] currentSolutions = args.getCurrentSolutions();
        Solution[] newSolutions = args.getNewSolutions();
        int lowerBound = args.getLowerBound();
        int upperBound = args.getUpperBound();
        int beesCount = args.getBeesCount();

        for (int i = startBee; i < endBee; i++) {
            if (random.nextDouble() < prob[i]) {
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
                }

                //Invoke lazy evaluation of fitness - it'll be done quicker in multi threads
                newSolutions[i].getFitness();
            }
        }

        synchronized (this) {
            finished = true;
            notifyAll();
        }
    }

    public void waitForFinish() throws InterruptedException {
        synchronized (this) {
            while (!finished) {
                wait();
            }
        }
    }
}
