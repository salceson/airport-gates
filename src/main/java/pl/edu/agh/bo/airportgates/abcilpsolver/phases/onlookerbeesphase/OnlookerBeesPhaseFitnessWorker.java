package pl.edu.agh.bo.airportgates.abcilpsolver.phases.onlookerbeesphase;

import lombok.RequiredArgsConstructor;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;

/**
 * Onlooker Bees' Phase Fitness Worker.
 * <p/>
 * Represents thread that calculate bees' fitness.
 *
 * @author Michał Ciołczyk
 */
@RequiredArgsConstructor
public class OnlookerBeesPhaseFitnessWorker implements Runnable {
    private final OnlookerBeesPhaseFitnessWorkerArgs args;
    private Boolean finished = false;

    @Override
    public void run() {
        int startBee = args.getStartBee();
        int endBee = args.getEndBee();
        Solution[] currentSolutions = args.getCurrentSolutions();
        double[] fitness = args.getFitness();

        for (int i = startBee; i < endBee; i++) {
            fitness[i] = currentSolutions[i].getFitness();
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
