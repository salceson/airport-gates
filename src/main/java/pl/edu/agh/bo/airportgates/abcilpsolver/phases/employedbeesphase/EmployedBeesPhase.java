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
    public static void run(Problem problem, int poolSize, int beesCount, double modificationRate, int dimension,
                           Solution[] currentSolutions, Solution[] newSolutions, int beesPerThread)
            throws ABCILPSolverException {
        List<EmployedBeesPhaseWorker> workers = new ArrayList<>();

        for (int threadNum = 0; threadNum < poolSize; threadNum++) {
            EmployedBeesPhaseWorker worker = new EmployedBeesPhaseWorker(
                    EmployedBeesPhaseWorkerArgs.builder()
                            .beesCount(beesCount)
                            .modificationRate(modificationRate)
                            .dimension(dimension)
                            .startBee(threadNum * beesPerThread)
                            .endBee(threadNum * beesPerThread + beesPerThread)
                            .problem(problem)
                            .solutions(currentSolutions)
                            .newSolutions(newSolutions)
                            .build()
            );

            Thread thread = new Thread(worker);
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new ABCILPSolverException("Employed bees phase: threads not finished.", e);
            }

            workers.add(worker);
        }

        // Last bees in our thread :)
        new EmployedBeesPhaseWorker(
                EmployedBeesPhaseWorkerArgs.builder()
                        .beesCount(beesCount)
                        .dimension(dimension)
                        .startBee(poolSize * beesPerThread)
                        .endBee(beesCount)
                        .problem(problem)
                        .solutions(currentSolutions)
                        .newSolutions(newSolutions)
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
            if (currentSolutions[i].compareTo(newSolutions[i]) < 0) {
                currentSolutions[i] = newSolutions[i];
            }
        }
    }
}
