package pl.edu.agh.bo.airportgates.util;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import pl.edu.agh.bo.airportgates.model.Flight;

import java.util.Map;

/**
 * @author Michal Janczykowski
 */
public class MapBasedPaxFlowFunction implements Function<Pair<Flight, Flight>, Integer> {

    private final ImmutableMap<Pair<Flight, Flight>, Integer> paxFlows;

    public MapBasedPaxFlowFunction(Map<Pair<Flight, Flight>, Integer> paxFlows) {
        this.paxFlows = ImmutableMap.copyOf(paxFlows);
    }

    @Override
    public Integer apply(Pair<Flight, Flight> flightsPair) {
        if (!paxFlows.containsKey(flightsPair)) {
            return 0;
        }
        return paxFlows.get(flightsPair);
    }
}
