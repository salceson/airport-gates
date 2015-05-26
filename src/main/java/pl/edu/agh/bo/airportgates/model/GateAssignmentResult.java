package pl.edu.agh.bo.airportgates.model;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Michal Janczykowski
 */
@RequiredArgsConstructor
public class GateAssignmentResult {

    @Getter
    private final long cost;

    @Getter
    private final ImmutableMultimap<Gate, Flight> gateAssignments;

    @Getter
    private final long bestSolutionIteration;

    @RequiredArgsConstructor
    public static class Builder {
        private final long cost;
        private final ImmutableMultimap.Builder<Gate, Flight> gateAssignmentsBuilder = new ImmutableListMultimap.Builder<>();
        private final long bestSolutionIteration;

        public Builder putGateAssignment(Gate gate, Flight flight) {
            gateAssignmentsBuilder.put(gate, flight);
            return this;
        }

        public GateAssignmentResult build() {
            return new GateAssignmentResult(cost, gateAssignmentsBuilder.build(), bestSolutionIteration);
        }
    }
}
