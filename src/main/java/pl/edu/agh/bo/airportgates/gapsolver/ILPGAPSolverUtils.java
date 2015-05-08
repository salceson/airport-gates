package pl.edu.agh.bo.airportgates.gapsolver;

import com.google.common.base.Function;
import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import pl.edu.agh.bo.airportgates.model.Flight;
import pl.edu.agh.bo.airportgates.model.FlightType;
import pl.edu.agh.bo.airportgates.model.Gate;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.util.Pair;

import java.util.*;

/**
 * Created by Michal Janczykowski on 2015-05-05.
 */
class ILPGAPSolverUtils {

    private static final long M = 2048;

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
                final long ijFlow = flowFunction.apply(new Pair<>(iFlight, jFlight));
                int k = 0;
                for (Gate kGate : gates) {
                    int l = 0;
                    for (Gate lGate : gates) {
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

    // 1. each flight is assigned to exactly one gate
    // for each i in A: sum of x_i_k is 1
    public static Collection<Constraint> getXlimitConstraints(GateAssignmentProblem gap) {
        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();

        final List<Constraint> constraints = new ArrayList<>();

        for (int i = 0; i < flights.size(); i++) {
            final Linear linear = new Linear();
            for (int k = 0; k < gates.size(); k++) {
                final String xikVar = String.format("x_%d_%d", i, k);
                linear.add(1, xikVar);
            }
            final Constraint xLimitConstraint = new Constraint(linear, "=", 1);
            constraints.add(xLimitConstraint);
        }

        return constraints;
    }

    // constraints 2 & 3 - set z to 1 only for two subsequent flights assigned to a single gate
    public static Collection<Constraint> getSubsequentFlightsOnGateConstraints(GateAssignmentProblem gap) {
        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();

        final List<Constraint> constraints = new ArrayList<>();

        for (int i = 0; i < flights.size(); i++) {
            for (int k = 0; k < gates.size(); k++) {
                final Linear linear = new Linear();
                final String xikVar = String.format("x_%d_%d", i, k);
                linear.add(1, xikVar);
                for (int j = 0; j < flights.size(); j++) {
                    final String zijkVar = String.format("z_%d_%d_%d", i, j, k);
                    linear.add(-1, zijkVar);
                }
                final Constraint constraint2 = new Constraint(linear, ">=", 0);
                constraints.add(constraint2);
            }
        }

        for (int j = 0; j < flights.size(); j++) {
            for (int k = 0; k < gates.size(); k++) {
                final Linear linear = new Linear();
                final String xjkVar = String.format("x_%d_%d", j, k);
                linear.add(1, xjkVar);
                for (int i = 0; i < flights.size(); i++) {
                    final String zijkVar = String.format("z_%d_%d_%d", i, j, k);
                    linear.add(-1, zijkVar);
                }
                final Constraint constraint3 = new Constraint(linear, ">=", 0);
                constraints.add(constraint3);
            }
        }

        return constraints;
    }

    // constraint 4
    public static Collection<Constraint> getZXRelationConstraints(GateAssignmentProblem gap) {
        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();

        final List<Constraint> constraints = new ArrayList<>();

        for (int k = 0; k < gates.size(); k++) {
            final Linear linear = new Linear();
            for (int l = 0; l < flights.size(); l++) {
                final String xlkVar = String.format("x_%d_%d", l, k);
                linear.add(1, xlkVar);
            }

            for (int i = 0; i < flights.size(); i++) {
                for (int j = 0; j < flights.size(); j++) {
                    final String zijkVar = String.format("z_%d_%d_%d", i, j, k);
                    linear.add(-1, zijkVar);
                }
            }

            final Constraint constraint4 = new Constraint(linear, "<=", 1);
            constraints.add(constraint4);
        }

        return constraints;
    }

    //constraint 5a, 5b, 5c, 5d
    public static Collection<Constraint> getSubsequentFlightsAtGateTimingConstraints(GateAssignmentProblem gap) {
        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();

        final List<Constraint> constraints = new ArrayList<>();

        int i = 0;
        for (Flight iFlight : flights) {
            final long iOperTime = iFlight.getOperationTime();
            final long iGateTime = iFlight.getAircraftType().getTimeAtGate();

            int j = 0;
            for (Flight jFlight : flights) {
                final long jOperTime = jFlight.getOperationTime();
                final long jGateTime = jFlight.getAircraftType().getTimeAtGate();

                for (int k = 0; k < gates.size(); k++) {
                    final String zijkVar = String.format("z_%d_%d_%d", i, j, k);

                    if (iFlight.getFlightType() == FlightType.ARRIVAL) {
                        if (jFlight.getFlightType() == FlightType.ARRIVAL) {
                            constraints.add(createConstraint5a(zijkVar, iOperTime, jOperTime, iGateTime, jGateTime));
                        } else {
                            constraints.add(createConstraint5b(zijkVar, iOperTime, jOperTime, iGateTime, jGateTime));
                        }
                    } else {
                        if (jFlight.getFlightType() == FlightType.ARRIVAL) {
                            constraints.add(createConstraint5c(zijkVar, iOperTime, jOperTime, iGateTime, jGateTime));
                        } else {
                            constraints.add(createConstraint5d(zijkVar, iOperTime, jOperTime, iGateTime, jGateTime));
                        }

                    }
                }
                j++;
            }
            i++;
        }

        return constraints;
    }

    private static Constraint createConstraint5a(String zijkVar, long iOperTime, long jOperTime, long iGateTime, long jGateTime) {
        final Linear linear = new Linear();
        linear.add(M, zijkVar);

        return new Constraint(linear, "<=", jOperTime + M - (iOperTime + iGateTime));
    }

    private static Constraint createConstraint5b(String zijkVar, long iOperTime, long jOperTime, long iGateTime, long jGateTime) {
        final Linear linear = new Linear();
        linear.add(M, zijkVar);

        return new Constraint(linear, "<=", jOperTime - jGateTime + M - (iOperTime + iGateTime));
    }

    private static Constraint createConstraint5c(String zijkVar, long iOperTime, long jOperTime, long iGateTime, long jGateTime) {
        final Linear linear = new Linear();
        linear.add(M, zijkVar);

        return new Constraint(linear, "<=", jOperTime + M - iOperTime);
    }

    private static Constraint createConstraint5d(String zijkVar, long iOperTime, long jOperTime, long iGateTime, long jGateTime) {
        final Linear linear = new Linear();
        linear.add(M, zijkVar);

        return new Constraint(linear, "<=", jOperTime - jGateTime + M - iOperTime);
    }

    public static Collection<Constraint> getXYConstraints(GateAssignmentProblem gap) {
        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();

        final List<Constraint> constraints = new ArrayList<>();

        for (int i = 0; i < flights.size(); i++) {
            for (int j = 0; j < flights.size(); j++) {
                for (int k = 0; k < gates.size(); k++) {
                    final String xikVar = String.format("x_%d_%d", i, k);

                    for (int l = 0; l < gates.size(); l++) {
                        final String xjlVar = String.format("x_%d_%d", j, l);
                        final String yVar = String.format("y_%d_%d_%d_%d", i, j, k, l);

                        // xy constraints creation
                        constraints.add(createConstraint89(yVar, xikVar));
                        constraints.add(createConstraint89(yVar, xjlVar));
                        constraints.add(createConstraint10(yVar, xikVar, xjlVar));
//                        constraints.add(createConstraint7811upper(yVar));
//                        constraints.add(createConstraint7811lower(yVar));
                    }
                }
            }
        }

        return constraints;
    }

    public static Collection<Constraint> getXYZ01Constraints(GateAssignmentProblem gap) {
        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();

        final List<Constraint> constraints = new ArrayList<>();
        for (int i = 0; i < flights.size(); i++) {
            for (int j = 0; j < flights.size(); j++) {
                for (int k = 0; k < gates.size(); k++) {
                    // z {0,1} constraints creation (7)
                    final String zVar = String.format("z_%d_%d_%d", i, j, k);
                    constraints.add(createConstraint7811lower(zVar));
                    constraints.add(createConstraint7811upper(zVar));
                }
            }
        }

        for (int i = 0; i < flights.size(); i++) {
            for (int j = 0; j < flights.size(); j++) {
                for (int k = 0; k < gates.size(); k++) {
                    for (int l = 0; l < gates.size(); l++) {
                        final String yVar = String.format("y_%d_%d_%d_%d", i, j, k, l);
                        // y {0,1} constraints creation (11)
                        constraints.add(createConstraint7811upper(yVar));
                        constraints.add(createConstraint7811lower(yVar));
                    }
                }
            }
        }

        for (int i = 0; i < flights.size(); i++) {
            for (int k = 0; k < gates.size(); k++) {
                // x {0,1} constraints creation (6)
                final String xVar = String.format("x_%d_%d", i, k);
                constraints.add(createConstraint7811lower(xVar));
                constraints.add(createConstraint7811upper(xVar));
            }
        }

        return constraints;
    }

    private static Constraint createConstraint7811upper(String var) {
        final Linear linear = new Linear();
        linear.add(1, var);
        return new Constraint(linear, "<=", 1);
    }

    private static Constraint createConstraint7811lower(String var) {
        final Linear linear = new Linear();
        linear.add(1, var);
        return new Constraint(linear, ">=", 0);
    }

    private static Constraint createConstraint10(String yVar, String xikVar, String xjlVar) {
        final Linear linear = new Linear();
        linear.add(1, xikVar);
        linear.add(1, xjlVar);
        linear.add(-1, yVar);
        return new Constraint(linear, "<=", 1);
    }

    private static Constraint createConstraint89(String yVar, String xVar) {
        final Linear linear = new Linear();
        linear.add(1, yVar);
        linear.add(-1, xVar);
        return new Constraint(linear, "<=", 0);
    }

    //returns collection of all variables in GAP
    public static Collection<String> getAllVariables(GateAssignmentProblem gap) {
        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();

        final Set<String> variables = new HashSet<>();

        for (int i = 0; i < flights.size(); i++) {
            for (int j = 0; j < flights.size(); j++) {
                for (int k = 0; k < gates.size(); k++) {
                    final String xikVar = String.format("x_%d_%d", i, k);
                    variables.add(xikVar);

                    final String zijkVar = String.format("z_%d_%d_%d", i, j, k);
                    variables.add(zijkVar);

                    for (int l = 0; l < gates.size(); l++) {
                        final String yVar = String.format("y_%d_%d_%d_%d", i, j, k, l);
                        variables.add(yVar);
                    }
                }
            }
        }

        return variables;
    }

    //returns collection of all variables in GAP
    public static Collection<String> getXVariables(GateAssignmentProblem gap) {
        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();

        final Set<String> variables = new HashSet<>();

        for (int i = 0; i < flights.size(); i++) {
            for (int k = 0; k < gates.size(); k++) {
                final String xikVar = String.format("x_%d_%d", i, k);
                variables.add(xikVar);
            }
        }

        return variables;
    }
}
