package pl.edu.agh.bo.airportgates.abcilpsolver.phases.onlookerbeesphase;

import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;
import pl.edu.agh.bo.airportgates.abcilpsolver.exceptions.ABCILPSolverException;

import java.util.ArrayList;
import java.util.List;

/**
 * Onlooker Bees' Phase of ABC Algorithm.
 *
 * @author Michał Ciołczyk
 */
public class OnlookerBeesPhase {
    public static void run(OnlookerBeesPhaseArgs args) throws ABCILPSolverException {
        int beesCount = args.getBeesCount();
        int beesPerThread = args.getBeesPerThread();
        int poolSize = args.getPoolSize();
        Solution[] currentSolutions = args.getCurrentSolutions();
        Solution[] newSolutions = args.getNewSolutions();
        int lowerBound = args.getLowerBound();
        int upperBound = args.getUpperBound();
        double modificationRate = args.getModificationRate();
        double searchRange = args.getSearchRange();
        Problem problem = args.getProblem();
        int dimension = args.getDimension();

        double[] prob = new double[beesCount];

        List<OnlookerBeesPhaseFitnessWorker> fitnessWorkers = new ArrayList<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            OnlookerBeesPhaseFitnessWorker worker = new OnlookerBeesPhaseFitnessWorker(
                    OnlookerBeesPhaseFitnessWorkerArgs.builder()
                            .currentSolutions(currentSolutions)
                            .startBee(i * beesPerThread)
                            .endBee(i * beesPerThread + beesPerThread)
                            .fitness(prob)
                            .build()
            );
            worker.run();
            fitnessWorkers.add(worker);
        }

        //Last bees in our thread
        new OnlookerBeesPhaseFitnessWorker(
                OnlookerBeesPhaseFitnessWorkerArgs.builder()
                        .currentSolutions(currentSolutions)
                        .startBee(poolSize * beesPerThread)
                        .endBee(beesCount)
                        .fitness(prob)
                        .build()
        ).run();

        for (OnlookerBeesPhaseFitnessWorker worker : fitnessWorkers) {
            try {
                worker.waitForFinish();
            } catch (InterruptedException e) {
                throw new ABCILPSolverException("Onlooker bees phase: threads not finished.", e);
            }
        }

        double sum = 0;
        for (int i = 0; i < beesCount; i++) {
            sum += prob[i];
        }

        for (int i = 0; i < beesCount; i++) {
            prob[i] /= sum;
        }

        List<OnlookerBeesPhaseSolutionWorker> workers = new ArrayList<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            OnlookerBeesPhaseSolutionWorker worker = new OnlookerBeesPhaseSolutionWorker(
                    OnlookerBeesPhaseSolutionWorkerArgs.builder()
                            .prob(prob)
                            .startBee(i * beesPerThread)
                            .endBee(i * beesPerThread + beesPerThread)
                            .problem(problem)
                            .modificationRate(modificationRate)
                            .beesCount(beesCount)
                            .currentSolutions(currentSolutions)
                            .newSolutions(newSolutions)
                            .dimension(dimension)
                            .searchRange(searchRange)
                            .modificationRate(modificationRate)
                            .lowerBound(lowerBound)
                            .upperBound(upperBound)
                            .build()
            );
            new Thread(worker).start();
            workers.add(worker);
        }

        //Last bees in our thread
        new OnlookerBeesPhaseSolutionWorker(
                OnlookerBeesPhaseSolutionWorkerArgs.builder()
                        .prob(prob)
                        .startBee(poolSize * beesPerThread)
                        .endBee(beesCount)
                        .problem(problem)
                        .modificationRate(modificationRate)
                        .beesCount(beesCount)
                        .currentSolutions(currentSolutions)
                        .newSolutions(newSolutions)
                        .dimension(dimension)
                        .searchRange(searchRange)
                        .modificationRate(modificationRate)
                        .lowerBound(lowerBound)
                        .upperBound(upperBound)
                        .build()
        ).run();

        for (OnlookerBeesPhaseSolutionWorker worker : workers) {
            try {
                worker.waitForFinish();
            } catch (InterruptedException e) {
                throw new ABCILPSolverException("Onlooker bees phase: threads not finished.", e);
            }
        }

        for (int i = 0; i < currentSolutions.length; i++) {
            if (currentSolutions[i].compareTo(newSolutions[i]) < 0) {
                currentSolutions[i] = newSolutions[i];
            }
        }
    }
}
