package pl.edu.agh.bo.airportgates.main;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import net.sf.javailp.SolverFactory;
import pl.edu.agh.bo.airportgates.abcilpsolver.ABCILPSolver;
import pl.edu.agh.bo.airportgates.abcilpsolver.ABCILPSolverFactory;
import pl.edu.agh.bo.airportgates.gapsolver.ABCGateAssignmentSolverImpl;
import pl.edu.agh.bo.airportgates.gapsolver.GateAssignmentSolver;
import pl.edu.agh.bo.airportgates.model.*;
import pl.edu.agh.bo.airportgates.util.MapBasedPaxFlowFunction;
import pl.edu.agh.bo.airportgates.util.Pair;
import pl.edu.agh.bo.airportgates.util.SimpleGateDistancesFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Michał Ciołczyk
 * @author Michał Janczykowski
 */
public class Main {
    public static void main(String[] args) {
        final GateAssignmentProblem gateAssignmentProblem = createProblem();

        long startTime = System.currentTimeMillis();

//        final GateAssignmentSolver solver = new ILPGateAssignmentSolverImpl(new SolverFactoryGurobi());
//        final GateAssignmentSolver solver = new ILPGateAssignmentSolverImpl(new SolverFactoryLpSolve());
        SolverFactory solverFactory = new ABCILPSolverFactory();
        setABCParams(solverFactory);
//        final GateAssignmentSolver solver = new ILPGateAssignmentSolverImpl(solverFactory);
        final GateAssignmentSolver solver = new ABCGateAssignmentSolverImpl(solverFactory);
        final Optional<GateAssignmentResult> gapResultOptional = solver.solve(gateAssignmentProblem);

        long endTime = System.currentTimeMillis();

        System.out.println("Time elapsed: " + (endTime - startTime) / 1000.0 + " s");

        if (!gapResultOptional.isPresent()) {
            System.out.println("No suitable assignment found.");
            return;
        }

        final GateAssignmentResult gapResult = gapResultOptional.get();

        System.out.println(gapResult.getCost());

        for (Gate gate : gapResult.getGateAssignments().keySet()) {
            System.out.println(gate.getNumber() + ": " + gapResult.getGateAssignments().get(gate));
        }
    }

    private static GateAssignmentProblem createProblem() {
        final Flight loFlight1 = new Flight(100, FlightType.ARRIVAL, AircraftType.SMALL, "LO", "10", "WAW", "MUC");
        final Flight loFlight2 = new Flight(180, FlightType.DEPARTURE, AircraftType.SMALL, "LO", "11", "MUC", "WAW");
        final Flight loFlight3 = new Flight(140, FlightType.ARRIVAL, AircraftType.SMALL, "LO", "12", "KRK", "MUC");
        final Flight loFlight4 = new Flight(220, FlightType.DEPARTURE, AircraftType.SMALL, "LO", "13", "MUC", "KRK");
        final Flight loFlight5 = new Flight(200, FlightType.DEPARTURE, AircraftType.LARGE, "LO", "7", "MUC", "ORD");
        final Flight loFlight6 = new Flight(100, FlightType.ARRIVAL, AircraftType.LARGE, "LO", "6", "ORD", "MUC");

        final Flight klFlight1 = new Flight(80, FlightType.ARRIVAL, AircraftType.LARGE, "KL", "1", "SXM", "MUC");
        final Flight klFlight2 = new Flight(205, FlightType.DEPARTURE, AircraftType.LARGE, "KL", "2", "MUC", "SXM");
        final Flight klFlight3 = new Flight(110, FlightType.ARRIVAL, AircraftType.SMALL, "KL", "3", "AMS", "MUC");
        final Flight klFlight4 = new Flight(190, FlightType.DEPARTURE, AircraftType.SMALL, "KL", "4", "MUC", "AMS");
        final Flight klFlight5 = new Flight(200, FlightType.DEPARTURE, AircraftType.SMALL, "KL", "5", "MUC", "KRK");
        final Flight klFlight6 = new Flight(120, FlightType.ARRIVAL, AircraftType.SMALL, "KL", "6", "KRK", "MUC");

        final List<Flight> flights = Arrays.asList(loFlight1, klFlight1, loFlight2, klFlight2, loFlight3, klFlight3,
                loFlight4, klFlight4, loFlight5, klFlight5, loFlight6, klFlight6);
//
//        final List<Flight> flights = Arrays.asList(loFlight1, loFlight2, loFlight3, loFlight4, loFlight5, loFlight6);

        final Map<Pair<Flight, Flight>, Integer> paxFlows = Maps.newHashMap();
        paxFlows.put(new Pair<>(loFlight1, loFlight5), 100);
        paxFlows.put(new Pair<>(loFlight3, loFlight5), 60);
        paxFlows.put(new Pair<>(loFlight6, loFlight2), 80);
        paxFlows.put(new Pair<>(loFlight6, loFlight4), 90);
        paxFlows.put(new Pair<>(klFlight1, klFlight4), 140);
        paxFlows.put(new Pair<>(klFlight1, klFlight5), 50);
        paxFlows.put(new Pair<>(klFlight3, klFlight2), 120);
        paxFlows.put(new Pair<>(klFlight6, klFlight2), 40);

        final Gate gate1 = new Gate(1);
        final Gate gate2 = new Gate(2);
        final Gate gate3 = new Gate(3);
        final Gate gate4 = new Gate(4);
        final Gate gate5 = new Gate(5);
        final Gate gate6 = new Gate(6);
        final Gate gate7 = new Gate(7);
        final Gate gate8 = new Gate(8);
        final Gate gate9 = new Gate(9);
        final Gate gate10 = new Gate(10);

        final List<Gate> gates = Arrays.asList(gate1, gate2, gate3, gate4, gate5, gate6, gate7, gate8, gate9, gate10);

        return new GateAssignmentProblem.Builder()
                .withFlights(flights)
                .withGates(gates)
                .withFlightsFlowFunction(new MapBasedPaxFlowFunction(paxFlows))
                .withGateDistancesFunction(new SimpleGateDistancesFunction())
                .build();
    }

    private static void setABCParams(SolverFactory solverFactory) {
        solverFactory.setParameter(ABCILPSolver.ITERATIONS_PARAMETER, 1000);
        solverFactory.setParameter(ABCILPSolver.LOWER_BOUND_PARAMETER, 0);
        solverFactory.setParameter(ABCILPSolver.UPPER_BOUND_PARAMETER, 1);
        solverFactory.setParameter(ABCILPSolver.MODIFICATION_RATE_PARAMETER, 0.7);
        solverFactory.setParameter(ABCILPSolver.THREAD_POOL_SIZE_PARAMETER, 8);
        solverFactory.setParameter(ABCILPSolver.BEES_COUNT_PARAMETER, 50);
        solverFactory.setParameter(ABCILPSolver.SEARCH_RANGE_PARAMETER, 2.0);
        solverFactory.setParameter(ABCILPSolver.ABANDONMENT_LIMIT_PARAMETER, 50);
        solverFactory.setParameter(ABCILPSolver.SCOUT_BEES_NUMBER_PARAMETER, 20);
    }
}
