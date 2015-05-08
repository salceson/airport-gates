package pl.edu.agh.bo.airportgates.gapsolver;

import net.sf.javailp.*;
import pl.edu.agh.bo.airportgates.model.Flight;
import pl.edu.agh.bo.airportgates.model.Gate;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.model.GateAssignmentResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Michal Janczykowski on 2015-05-05.
 */
public class ILPGateAssignmentSolverImpl implements GateAssignmentSolver {

    private final SolverFactory solverFactory;

    public ILPGateAssignmentSolverImpl(SolverFactory solverFactory) {
        this.solverFactory = solverFactory;
    }

    @Override
    public GateAssignmentResult solve(GateAssignmentProblem gap) {
        solverFactory.setParameter(Solver.VERBOSE, 0);
        solverFactory.setParameter(Solver.TIMEOUT, 100);
        final Problem ilpProblem = new Problem();

        final Linear objective = ILPGAPSolverUtils.getObjectiveForProblem(gap);
        ilpProblem.setObjective(objective, OptType.MIN);

        final Collection<Constraint> constraints = new ArrayList<>();
        constraints.addAll(ILPGAPSolverUtils.getXlimitConstraints(gap)); //constraint 1
        constraints.addAll(ILPGAPSolverUtils.getSubsequentFlightsOnGateConstraints(gap)); //constraints 2 & 3
        constraints.addAll(ILPGAPSolverUtils.getZXRelationConstraints(gap)); //constraint 4
        constraints.addAll(ILPGAPSolverUtils.getSubsequentFlightsAtGateTimingConstraints(gap)); //constraints 5a, 5b, 5c, 5d
        constraints.addAll(ILPGAPSolverUtils.getXYZ01Constraints(gap)); //constraints 6, 7, 11
        constraints.addAll(ILPGAPSolverUtils.getXYConstraints(gap)); //constraints 8, 9, 10

        for (Constraint constraint : constraints) {
            System.out.println(constraint);
            ilpProblem.add(constraint);
        }

        final Collection<String> variables = ILPGAPSolverUtils.getAllVariables(gap);
        for (String var : variables) {
            ilpProblem.setVarType(var, Integer.class);
        }

        final Solver solver = solverFactory.get();
        final Result ilpResult = solver.solve(ilpProblem);

        System.out.println(ilpResult);
        return createGAPResultFromILPResult(gap, ilpResult);
    }

    private GateAssignmentResult createGAPResultFromILPResult(GateAssignmentProblem gap, Result ilpResult) {
        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();

        final long cost = ilpResult.getObjective().longValue();

        final GateAssignmentResult.Builder gapResultBuilder = new GateAssignmentResult.Builder(cost);

        int i = 0;
        for (Flight flight : flights) {
            int k = 0;
            for (Gate gate : gates) {
                final String xikVar = String.format("x_%d_%d", i, k);
                if(ilpResult.get(xikVar).intValue() == 1) {
                    gapResultBuilder.putGateAssignment(gate, flight);
                }
                k++;
            }
            i++;
        }

        return gapResultBuilder.build();
    }


}
