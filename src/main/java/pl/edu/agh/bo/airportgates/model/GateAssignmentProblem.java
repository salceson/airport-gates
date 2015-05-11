package pl.edu.agh.bo.airportgates.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import lombok.Value;
import pl.edu.agh.bo.airportgates.util.Pair;

/**
 * @author Michał Ciołczyk
 */
@Value
public class GateAssignmentProblem {
    private ImmutableList<Flight> flights;
    private ImmutableList<Gate> gates;
    private Function<Pair<Flight, Flight>, Integer> flightsFlow;
    private Function<Pair<Gate, Gate>, Integer> gatesDistances;
}
