package pl.edu.agh.bo.airportgates.model;

import com.google.common.collect.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Michal Janczykowski
 */
@RequiredArgsConstructor
public class GateAssignmentResult {

    @Getter
    private final long cost;

    @Getter
    private final ImmutableMultimap<Gate, Flight> gateAssignments;

    @RequiredArgsConstructor
    public static class Builder {
        private final long cost;
        private final ImmutableMultimap.Builder<Gate, Flight> gateAssignmentsBuilder = new ImmutableListMultimap.Builder<>();

        public Builder putGateAssignment(Gate gate, Flight flight) {
            gateAssignmentsBuilder.put(gate, flight);
            return this;
        }

        public GateAssignmentResult build() {
            return new GateAssignmentResult(cost, gateAssignmentsBuilder.build());
        }
    }
}
