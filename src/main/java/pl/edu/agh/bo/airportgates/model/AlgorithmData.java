package pl.edu.agh.bo.airportgates.model;

import com.google.common.base.Function;
import lombok.Value;
import pl.edu.agh.bo.airportgates.util.Pair;

import java.util.List;

/**
 * @author Michał Ciołczyk
 */
@Value
public class AlgorithmData {
    private List<Flight> flights;
    private List<Gate> gates;
    private Function<Pair<Flight, Flight>, Integer> flightsFlow;
    private Function<Pair<Gate, Gate>, Integer> gatesDistances;
}
