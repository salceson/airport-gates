package pl.edu.agh.bo.airportgates.gapsolver;

import com.google.common.base.Function;
import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import pl.edu.agh.bo.airportgates.model.Flight;
import pl.edu.agh.bo.airportgates.model.Gate;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.util.Pair;

import java.util.*;

/**
 * @author Michal Janczykowski
 */
public class ABCGAPSolverUtils {

    public static Collection<Constraint> getXZ01Constraints(GateAssignmentProblem gap) {
        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();

        final List<Constraint> constraints = new ArrayList<>();

        {
            int i = 0;
            for (Flight iFlight : flights) {
                int j = 0;
                for (Flight jFlight : flights) {
                    if (i == j || iFlight.isAtGateAfter(jFlight)) {
                        j++;
                        continue;
                    }

                    for (int k = 0; k < gates.size(); k++) {
                        // z {0,1} constraints creation (7)
                        final String zVar = String.format("z_%d_%d_%d", i, j, k);
                        constraints.add(ILPGAPSolverUtils.createConstraint7811lower(zVar));
                        constraints.add(ILPGAPSolverUtils.createConstraint7811upper(zVar));
                    }
                    j++;
                }
                i++;
            }
        }

        for (int i = 0; i < flights.size(); i++) {
            for (int k = 0; k < gates.size(); k++) {
                // x {0,1} constraints creation (6)
                final String xVar = String.format("x_%d_%d", i, k);
                constraints.add(ILPGAPSolverUtils.createConstraint7811lower(xVar));
                constraints.add(ILPGAPSolverUtils.createConstraint7811upper(xVar));
            }
        }

        return constraints;
    }


    public static Collection<String> getAllVariables(GateAssignmentProblem gap) {
        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();

        final Set<String> variables = new HashSet<>();

//        for (int i = 0; i < flights.size(); i++) {
//            for (int j = 0; j < flights.size(); j++) {
        {
            int i = 0;
            for (Flight iFlight : flights) {
                for (int k = 0; k < gates.size(); k++) {
                    final String xikVar = String.format("x_%d_%d", i, k);
                    variables.add(xikVar);
                }
                i++;
            }
        }

        {
            int i = 0;
            for (Flight iFlight : flights) {
                int j = 0;
                for (Flight jFlight : flights) {
                    if (i == j || iFlight.isAtGateAfter(jFlight)) {
                        j++;
                        continue;
                    }
                    for (int k = 0; k < gates.size(); k++) {
                        final String zijkVar = String.format("z_%d_%d_%d", i, j, k);
                        variables.add(zijkVar);
                    }
                    j++;
                }
                i++;
            }
        }


        return variables;
    }

    public static Linear getObjectiveForProblem(GateAssignmentProblem gap) {
        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();
        final Function<Pair<Flight, Flight>, Integer> flowFunction = gap.getFlightsFlow();
        final Function<Pair<Gate, Gate>, Integer> distanceFunction = gap.getGatesDistances();

        final Linear objective = new Linear();

        int i = 0;
        for (Flight iFlight : flights) {
            int j = 0;
            for (Flight jFlight : flights) {
                if (i == j || iFlight.isAtGateAfter(jFlight)) {
                    j++;
                    continue;
                }
                final long ijFlow = flowFunction.apply(new Pair<>(iFlight, jFlight));
                int k = 0;
                for (Gate kGate : gates) {
                    int l = 0;
                    for (Gate lGate : gates) {
                        final long klDistance = distanceFunction.apply(new Pair<>(kGate, lGate));
                        final long coef = ijFlow * klDistance;
//                        final String var = String.format("y_%d_%d_%d_%d", i, j, k, l);
                        final String xVar1 = String.format("x_%d_%d", i, k);
                        final String xVar2 = String.format("x_%d_%d", j, l);
                        objective.add(coef, new Pair<String, String>(xVar1, xVar2));
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
