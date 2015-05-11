package pl.edu.agh.bo.airportgates.model;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import pl.edu.agh.bo.airportgates.util.Pair;

import java.util.Collections;
import java.util.List;

/**
 * @author Michał Ciołczyk
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GateAssignmentProblem {
    private final ImmutableList<Flight> flights;
    private final ImmutableList<Gate> gates;
    private final Function<Pair<Flight, Flight>, Integer> flightsFlow;
    private final Function<Pair<Gate, Gate>, Integer> gatesDistances;

    @NoArgsConstructor
    public static class Builder {
        private ImmutableList<Flight> flights;
        private ImmutableList<Gate> gates;
        private Function<Pair<Flight, Flight>, Integer> flightsFlow;
        private Function<Pair<Gate, Gate>, Integer> gatesDistances;

        public Builder withFlights(List<Flight> flights) {
            Collections.sort(flights);
            this.flights = ImmutableList.copyOf(flights);
            return this;
        }

        public Builder withGates(List<Gate> gates) {
            this.gates = ImmutableList.copyOf(gates);
            return this;
        }

        public Builder withFlightsFlowFunction(Function<Pair<Flight, Flight>, Integer> flightsFlow) {
            this.flightsFlow = flightsFlow;
            return this;
        }

        public Builder withGateDistancesFunction(Function<Pair<Gate, Gate>, Integer> gatesDistances) {
            this.gatesDistances = gatesDistances;
            return this;
        }

        public GateAssignmentProblem build() {
            Preconditions.checkState(flights != null);
            Preconditions.checkState(gates != null);
            Preconditions.checkState(gatesDistances != null);
            Preconditions.checkState(flightsFlow != null);

            return new GateAssignmentProblem(flights, gates, flightsFlow, gatesDistances);
        }
    }
}
