package pl.edu.agh.bo.airportgates.gui;

import com.google.common.base.Optional;
import pl.edu.agh.bo.airportgates.abcilpsolver.ABCILPSolverFactory;
import pl.edu.agh.bo.airportgates.gapsolver.GateAssignmentSolver;
import pl.edu.agh.bo.airportgates.gapsolver.GateAssignmentSolverParams;
import pl.edu.agh.bo.airportgates.gapsolver.ILPGateAssignmentSolverImpl;
import pl.edu.agh.bo.airportgates.model.Gate;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.model.GateAssignmentResult;


public class SolverRunner {

    public SolverRunner() {
        params = GateAssignmentSolverParams.defaultParams();
    }

    // BUHAHAHAHAH.
    public GateAssignmentProblem gateAssignmentProblem;
    public GateAssignmentSolverParams params;

    public Optional<GateAssignmentResult> runSolver() {

        long startTime = System.currentTimeMillis();

//        final GateAssignmentSolver solver = new ILPGateAssignmentSolverImpl(new SolverFactoryGurobi());
//        final GateAssignmentSolver solver = new ILPGateAssignmentSolverImpl(new SolverFactoryLpSolve());
        final GateAssignmentSolver solver = new ILPGateAssignmentSolverImpl(new ABCILPSolverFactory(), params);
        final Optional<GateAssignmentResult> gapResultOptional = solver.solve(gateAssignmentProblem);

        long endTime = System.currentTimeMillis();

        System.out.println("Time elapsed: " + (endTime - startTime) / 1000.0 + " s");

        return gapResultOptional;

//        if (!gapResultOptional.isPresent()) {
//            System.out.println("No suitable assignment found.");
//            return Optional.absent();
//        }

//        final GateAssignmentResult gapResult = gapResultOptional.get();
//
//        System.out.println(gapResult.getCost());
//
//        for (Gate gate : gapResult.getGateAssignments().keySet()) {
//            System.out.println(gate.getNumber() + ": " + gapResult.getGateAssignments().get(gate));
//        }
//
//        return gapResult;
    }
}
