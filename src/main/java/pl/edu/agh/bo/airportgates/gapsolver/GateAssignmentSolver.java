package pl.edu.agh.bo.airportgates.gapsolver;

import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.model.GateAssignmentResult;

/**
 * Created by Michal Janczykowski on 2015-05-05.
 */
public interface GateAssignmentSolver {
    GateAssignmentResult solve(GateAssignmentProblem gap);
}
