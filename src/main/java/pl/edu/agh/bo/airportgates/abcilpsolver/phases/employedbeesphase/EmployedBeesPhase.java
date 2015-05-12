package pl.edu.agh.bo.airportgates.abcilpsolver.phases.employedbeesphase;

import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;
import pl.edu.agh.bo.airportgates.abcilpsolver.exceptions.ABCILPSolverException;

import java.util.ArrayList;
import java.util.List;

/**
 * Employed Bees' Phase of ABC Algorithm.
 *
 * @author Michał Ciołczyk
 */
public class EmployedBeesPhase {
    public static void run(EmployedBeesPhaseArgs args) throws ABCILPSolverException {
        Problem problem = args.getProblem();
        int poolSize = args.getPoolSize();
        int beesCount = args.getBeesCount();
        double modificationRate = args.getModificationRate();
        double searchRange = args.getSearchRange();
        int dimension = args.getDimension();
        Solution[] currentSolutions = args.getCurrentSolutions();
        Solution[] newSolutions = args.getNewSolutions();
        int beesPerThread = args.getBeesPerThread();
        int lowerBound = args.getLowerBound();
        int upperBound = args.getUpperBound();
        int[] tries = args.getTries();
        List<EmployedBeesPhaseWorker> workers = new ArrayList<>();

        for (int threadNum = 0; threadNum < poolSize; threadNum++) {
            EmployedBeesPhaseWorker worker = new EmployedBeesPhaseWorker(
                    EmployedBeesPhaseWorkerArgs.builder()
                            .beesCount(beesCount)
                            .modificationRate(modificationRate)
                            .searchRange(searchRange)
                            .dimension(dimension)
                            .startBee(threadNum * beesPerThread)
                            .endBee(threadNum * beesPerThread + beesPerThread)
                            .problem(problem)
                            .solutions(currentSolutions)
                            .newSolutions(newSolutions)
                            .lowerBound(lowerBound)
                            .upperBound(upperBound)
                            .build()
            );
            // We wait for the threads to finish below
            new Thread(worker).start();
            workers.add(worker);
        }

        // Last bees in our thread :)
        new EmployedBeesPhaseWorker(
                EmployedBeesPhaseWorkerArgs.builder()
                        .beesCount(beesCount)
                        .dimension(dimension)
                        .searchRange(searchRange)
                        .modificationRate(modificationRate)
                        .startBee(poolSize * beesPerThread)
                        .endBee(beesCount)
                        .problem(problem)
                        .solutions(currentSolutions)
                        .newSolutions(newSolutions)
                        .lowerBound(lowerBound)
                        .upperBound(upperBound)
                        .build()
        ).run();

        for (EmployedBeesPhaseWorker worker : workers) {
            try {
                worker.waitForFinish();
            } catch (InterruptedException e) {
                throw new ABCILPSolverException("Employed bees phase: threads not finished.", e);
            }
        }

        for (int i = 0; i < currentSolutions.length; i++) {
            if (wasSolutionImproved(currentSolutions[i], newSolutions[i])) {
                currentSolutions[i] = newSolutions[i];
            } else {
                tries[i]++;
            }
        }
    }

    private static boolean wasSolutionImproved(Solution currentSolution, Solution newSolution) {
        return currentSolution.compareTo(newSolution) < 0;
    }
}
