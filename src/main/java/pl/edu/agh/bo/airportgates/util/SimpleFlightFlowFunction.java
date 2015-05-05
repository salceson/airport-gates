package pl.edu.agh.bo.airportgates.util;

import com.google.common.base.Function;
import pl.edu.agh.bo.airportgates.model.Flight;

/**
 * @author Michał Ciołczyk
 */
public class SimpleFlightFlowFunction implements Function<Pair<Flight, Flight>, Integer> {
    @Override
    public Integer apply(Pair<Flight, Flight> flightFlightPair) {
        return 100;
    }
}
