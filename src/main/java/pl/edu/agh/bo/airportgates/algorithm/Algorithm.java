package pl.edu.agh.bo.airportgates.algorithm;

import lpsolve.LpSolveException;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;

/**
 * @author Michał Ciołczyk
 */
public interface Algorithm {
    void run(GateAssignmentProblem gateAssignmentProblem, int maxIterations) throws LpSolveException;
}
