package pl.edu.agh.bo.airportgates.gapsolver;

import pl.edu.agh.bo.airportgates.model.GateAssignmentResult;

/**
 * @author Michał Janczykowski
 */
public interface GateAssignmentValidator {
    boolean isValid(GateAssignmentResult gapResult);
}
