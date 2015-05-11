package pl.edu.agh.bo.airportgates.gapsolver;

import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.model.GateAssignmentResult;

/**
 * @author Michał Janczykowski
 */
public interface GateAssignmentSolver {
    GateAssignmentResult solve(GateAssignmentProblem gap);
}
