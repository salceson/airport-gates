package pl.edu.agh.bo.airportgates.main;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.sf.javailp.SolverFactoryGurobi;
import pl.edu.agh.bo.airportgates.gapsolver.GateAssignmentSolver;
import pl.edu.agh.bo.airportgates.gapsolver.ILPGateAssignmentSolverImpl;
import pl.edu.agh.bo.airportgates.model.*;
import pl.edu.agh.bo.airportgates.util.MapBasedPaxFlowFunction;
import pl.edu.agh.bo.airportgates.util.Pair;
import pl.edu.agh.bo.airportgates.util.SimpleGateDistancesFunction;

import java.util.Map;

/**
 * @author Michał Ciołczyk
 * @author Michał Janczykowski
 */
public class Main {
    public static void main(String[] args) {
        final GateAssignmentProblem gateAssignmentProblem = createProblem();

        long startTime = System.currentTimeMillis();

        final GateAssignmentSolver solver = new ILPGateAssignmentSolverImpl(new SolverFactoryGurobi());
//        final GateAssignmentSolver solver = new ILPGateAssignmentSolverImpl(new SolverFactoryLpSolve());
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

        final ImmutableList<Flight> flight = ImmutableList.of(loFlight1, klFlight1, loFlight2, klFlight2, loFlight3, klFlight3,
                loFlight4, klFlight4, loFlight5, klFlight5, loFlight6, klFlight6);

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

        final ImmutableList<Gate> gates = ImmutableList.of(
                gate1, gate2, gate3, gate4, gate5, gate6, gate7, gate8, gate9, gate10);

        return new GateAssignmentProblem(flight, gates, new MapBasedPaxFlowFunction(paxFlows), new SimpleGateDistancesFunction());
    }
}
