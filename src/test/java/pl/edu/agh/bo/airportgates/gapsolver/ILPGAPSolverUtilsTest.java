package pl.edu.agh.bo.airportgates.gapsolver;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import net.sf.javailp.Linear;
import org.junit.Test;
import pl.edu.agh.bo.airportgates.model.*;
import pl.edu.agh.bo.airportgates.util.Pair;
import pl.edu.agh.bo.airportgates.util.SimpleGateDistancesFunction;

import static org.assertj.core.api.Assertions.assertThat;


public class ILPGAPSolverUtilsTest {

    @Test
    public void testGetObjectiveForProblem() throws Exception {
        //given
        final Flight flight1 = new Flight(FlightType.INBOUND, AircraftType.SMALL, "LO1", "CDG");
        final Flight flight2 = new Flight(FlightType.INBOUND, AircraftType.SMALL, "LO2", "CDG");
        final Flight flight3 = new Flight(FlightType.INBOUND, AircraftType.SMALL, "LO3", "CDG");

        final ImmutableList<Flight> flight = ImmutableList.of(flight1, flight2, flight3);

        final Gate gate1 = new Gate();
        gate1.setNumber(1);
        final Gate gate2 = new Gate();
        gate2.setNumber(3);

        final ImmutableList<Gate> gates = ImmutableList.of(gate1, gate2);

        final GateAssignmentProblem gap = new GateAssignmentProblem(flight, gates, new MockPAXFlowFunction(), new SimpleGateDistancesFunction());

        //when
        final Linear result = ILPGAPSolverUtils.getObjectiveForProblem(gap);

        //then
        System.out.println(result.getCoefficients());

        for (Object var : result.getVariables()) {
            System.out.println(var);
        }

        assertThat(result.getVariables()).hasSize(36).doesNotHaveDuplicates();
        assertThat(result.getCoefficients()).hasSize(36).containsOnly(0L, 20L);
    }

    static class MockPAXFlowFunction implements Function<Pair<Flight, Flight>, Integer> {

        @Override
        public Integer apply(Pair<Flight, Flight> flightFlightPair) {
            return 10;
        }
    }
}