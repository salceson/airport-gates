package pl.edu.agh.bo.airportgates.util;

import com.google.common.base.Function;
import pl.edu.agh.bo.airportgates.model.Gate;

/**
 * @author Michał Ciołczyk
 */
public class SimpleGateDistancesFunction implements Function<Pair<Gate, Gate>, Integer> {
    @Override
    public Integer apply(Pair<Gate, Gate> gateGatePair) {
        int firstGateNumber = gateGatePair.getFirst().getNumber();
        int secondGateNumber = gateGatePair.getSecond().getNumber();
        return Math.abs(firstGateNumber - secondGateNumber);
    }
}
