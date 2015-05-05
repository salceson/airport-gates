package pl.edu.agh.bo.airportgates.algorithm;

import lpsolve.LpSolveException;
import pl.edu.agh.bo.airportgates.model.AlgorithmData;

/**
 * @author Michał Ciołczyk
 */
public interface Algorithm {
    void run(AlgorithmData algorithmData, int maxIterations) throws LpSolveException;
}
