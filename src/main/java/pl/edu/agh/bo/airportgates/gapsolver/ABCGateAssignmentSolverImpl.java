package pl.edu.agh.bo.airportgates.gapsolver;

import com.google.common.base.Optional;
import net.sf.javailp.*;
import pl.edu.agh.bo.airportgates.abcilpsolver.ABCILPSolverFactory;
import pl.edu.agh.bo.airportgates.model.Flight;
import pl.edu.agh.bo.airportgates.model.Gate;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.model.GateAssignmentResult;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Michal Janczykowski
 */
public class ABCGateAssignmentSolverImpl implements GateAssignmentSolver {

    private final SolverFactory solverFactory;

    public ABCGateAssignmentSolverImpl(SolverFactory solverFactory) {
        this.solverFactory = solverFactory;
    }

    @Override
    public Optional<GateAssignmentResult> solve(GateAssignmentProblem gap) {
        solverFactory.setParameter(Solver.VERBOSE, 0);
        solverFactory.setParameter(Solver.TIMEOUT, 100);
        final Problem ilpProblem = new Problem();

        final Linear objective = ABCGAPSolverUtils.getObjectiveForProblem(gap);
        ilpProblem.setObjective(objective, OptType.MIN);

        final Collection<Constraint> constraints = new ArrayList<>();
        constraints.addAll(ILPGAPSolverUtils.getXlimitConstraints(gap)); //constraint 1
        constraints.addAll(ILPGAPSolverUtils.getSubsequentFlightsOnGateConstraints(gap)); //constraints 2 & 3
        constraints.addAll(ILPGAPSolverUtils.getZXRelationConstraints(gap)); //constraint 4
        constraints.addAll(ILPGAPSolverUtils.getSubsequentFlightsAtGateTimingConstraints(gap)); //constraints 5a, 5b, 5c, 5d
//        constraints.addAll(ILPGAPSolverUtils.getXYZ01Constraints(gap)); //constraints 6, 7, 11
//        constraints.addAll(ABCGAPSolverUtils.getXZ01Constraints(gap)); //constraints 6, 7
//        constraints.addAll(ILPGAPSolverUtils.getXYConstraints(gap)); //constraints 8, 9, 10

        try {
            FileWriter fw = new FileWriter("constraints.txt");
            for (Constraint constraint : constraints) {
                fw.write(constraint.getName() + ": ");
                fw.write(constraint.toString());
                fw.write("\n");
                ilpProblem.add(constraint);
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Collection<String> variables = ABCGAPSolverUtils.getAllVariables(gap);
        try {
            FileWriter fw = new FileWriter("vars.txt");
            for (String var : variables) {
                fw.write(var);
                fw.write("\n");
                ilpProblem.setVarType(var, Integer.class);
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Solver solver = solverFactory.get();
        final Result ilpResult = solver.solve(ilpProblem);

        return createGAPResultFromILPResult(gap, ilpResult);
    }

    private Optional<GateAssignmentResult> createGAPResultFromILPResult(GateAssignmentProblem gap, Result ilpResult) {
        if (ilpResult == null) {
            return Optional.absent();
        }

        final List<Flight> flights = gap.getFlights();
        final List<Gate> gates = gap.getGates();

        final long cost = ilpResult.getObjective().longValue();

        final GateAssignmentResult.Builder gapResultBuilder = new GateAssignmentResult.Builder(cost);

        int i = 0;
        for (Flight flight : flights) {
            int k = 0;
            for (Gate gate : gates) {
                final String xikVar = String.format("x_%d_%d", i, k);
                if (ilpResult.get(xikVar).intValue() == 1) {
                    gapResultBuilder.putGateAssignment(gate, flight);
                }
                k++;
            }
            i++;
        }

        return Optional.of(gapResultBuilder.build());
    }
}
