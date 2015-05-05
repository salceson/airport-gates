package pl.edu.agh.bo.airportgates.gapsolver;

import net.sf.javailp.*;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.model.GateAssignmentResult;

import java.util.ArrayList;
import java.util.Collection;

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
        constraints.addAll(ILPGAPSolverUtils.getXlimitConstraints(gap));
        constraints.addAll(ILPGAPSolverUtils.getXYConstraints(gap));

        for (Constraint constraint : constraints) {
            ilpProblem.add(constraint);
        }

        final Collection<String> variables = ILPGAPSolverUtils.getAllVariables(gap);
        for (String var : variables) {
            ilpProblem.setVarType(var, Integer.class);
        }

        Solver solver = solverFactory.get();
        Result result = solver.solve(ilpProblem);

        System.out.println(result);

        return new GateAssignmentResult(null);
    }


}
