package pl.edu.agh.bo.airportgates.gapsolver;

import com.google.common.base.Optional;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.model.GateAssignmentResult;

/**
 * @author Michał Janczykowski
 */
public interface GateAssignmentSolver {
    Optional<GateAssignmentResult> solve(GateAssignmentProblem gap);
}
