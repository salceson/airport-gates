package pl.edu.agh.bo.airportgates.main;

import com.google.common.collect.ImmutableList;
import net.sf.javailp.SolverFactoryLpSolve;
import pl.edu.agh.bo.airportgates.gapsolver.GateAssignmentSolver;
import pl.edu.agh.bo.airportgates.gapsolver.ILPGateAssignmentSolverImpl;
import pl.edu.agh.bo.airportgates.model.Flight;
import pl.edu.agh.bo.airportgates.model.Gate;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.util.SimpleFlightFlowFunction;
import pl.edu.agh.bo.airportgates.util.SimpleGateDistancesFunction;

/**
 * @author Michał Ciołczyk
 */
public class Main {
    public static void main(String[] args) {
        final GateAssignmentProblem gateAssignmentProblem = createProblem();

        final GateAssignmentSolver solver = new ILPGateAssignmentSolverImpl(new SolverFactoryLpSolve());
        solver.solve(gateAssignmentProblem);
    }

    private static GateAssignmentProblem createProblem() {
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

        return new GateAssignmentProblem(flight, gates, new SimpleFlightFlowFunction(), new SimpleGateDistancesFunction());
    }
}
