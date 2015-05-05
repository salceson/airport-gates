package pl.edu.agh.bo.airportgates.gapsolver;

import com.google.common.collect.ImmutableList;
import net.sf.javailp.Linear;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.bo.airportgates.model.Flight;
import pl.edu.agh.bo.airportgates.model.Gate;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.util.SimpleFlightFlowFunction;
import pl.edu.agh.bo.airportgates.util.SimpleGateDistancesFunction;

public class ILPGateAssignmentSolverImplTest {

    ILPGateAssignmentSolverImpl instance;

    @Before
    public void setUp() throws Exception {
        instance = new ILPGateAssignmentSolverImpl();
    }

    @Test
    public void testGetObjectiveForProblem() throws Exception {
        //given
        final Flight flight1 = new Flight();
        flight1.setNumber("LO1");
        final Flight flight2 = new Flight();
        flight2.setNumber("LO2");
        final Flight flight3 = new Flight();
        flight3.setNumber("LO3");

        final ImmutableList<Flight> flight = ImmutableList.of(flight1, flight2, flight3);

        final Gate gate1 = new Gate();
        gate1.setNumber(1);
        final Gate gate2 = new Gate();
        gate2.setNumber(3);

        final ImmutableList<Gate> gates = ImmutableList.of(gate1, gate2);

        final GateAssignmentProblem gap = new GateAssignmentProblem(flight, gates, new SimpleFlightFlowFunction(), new SimpleGateDistancesFunction());

        //when
        final Linear result = instance.getObjectiveForProblem(gap);

        //then
        for (Number n : result.getCoefficients()) {
            System.out.println(n);
        }

        for (Object var : result.getVariables()) {
            System.out.println(var);
        }
    }
}