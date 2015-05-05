package pl.edu.agh.bo.airportgates.gapsolver;

import com.google.common.base.Function;
import net.sf.javailp.*;
import pl.edu.agh.bo.airportgates.model.Flight;
import pl.edu.agh.bo.airportgates.model.Gate;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.model.GateAssignmentResult;
import pl.edu.agh.bo.airportgates.util.Pair;

import java.util.List;

/**
 * Created by Michal Janczykowski on 2015-05-05.
 */
public class ILPGateAssignmentSolverImpl implements GateAssignmentSolver {
    @Override
    public GateAssignmentResult solve(GateAssignmentProblem gap) {
        final SolverFactory factory = new SolverFactoryLpSolve();
        factory.setParameter(Solver.VERBOSE, 0);
        factory.setParameter(Solver.TIMEOUT, 100);
        final Problem ilpProblem = new Problem();

        final Linear objective = getObjectiveForProblem(gap);

        return new GateAssignmentResult(null);
    }

    public Linear getObjectiveForProblem(GateAssignmentProblem gap) {
        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();
        final Function<Pair<Flight, Flight>, Integer> flowFunction = gap.getFlightsFlow();
        final Function<Pair<Gate, Gate>, Integer> distanceFunction = gap.getGatesDistances();

        final Linear objective = new Linear();

        int i = 0;
        for(Flight iFlight: flights) {
            int j = 0;
            for(Flight jFlight: flights) {
                final long ijFlow = flowFunction.apply(new Pair<>(iFlight, jFlight));
                int k = 0;
                for(Gate kGate: gates) {
                    int l = 0;
                    for(Gate lGate: gates) {
                        final long klDistance = distanceFunction.apply(new Pair<>(kGate, lGate));
                        final long coef = ijFlow * klDistance;
                        final String var = String.format("y_%d_%d_%d_%d", i, j, k, l);
                        objective.add(coef, var);
                        l++;
                    }
                    k++;
                }
                j++;
            }
            i++;
        }

        return objective;
    }
}
