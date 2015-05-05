package pl.edu.agh.bo.airportgates.gapsolver;

import net.sf.javailp.*;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.model.GateAssignmentResult;

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

        final Linear objective = ILPGAPSolverUtils.getObjectiveForProblem(gap);
        ilpProblem.setObjective(objective);

        return new GateAssignmentResult(null);
    }


}
