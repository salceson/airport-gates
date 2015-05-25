package pl.edu.agh.bo.airportgates.gui;

import com.google.common.base.Optional;
import net.sf.javailp.SolverFactory;
import pl.edu.agh.bo.airportgates.abcilpsolver.ABCILPSolver;
import pl.edu.agh.bo.airportgates.abcilpsolver.ABCILPSolverFactory;
import pl.edu.agh.bo.airportgates.gapsolver.ABCGateAssignmentSolverImpl;
import pl.edu.agh.bo.airportgates.gapsolver.GateAssignmentSolver;
import pl.edu.agh.bo.airportgates.gapsolver.GateAssignmentSolverParams;
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

        SolverFactory solverFactory = new ABCILPSolverFactory();
        solverFactory.setParameter(ABCILPSolver.ITERATIONS_PARAMETER, params.iterations);
        solverFactory.setParameter(ABCILPSolver.LOWER_BOUND_PARAMETER, 0);
        solverFactory.setParameter(ABCILPSolver.UPPER_BOUND_PARAMETER, 1);
        solverFactory.setParameter(ABCILPSolver.MODIFICATION_RATE_PARAMETER, params.modificationRate);
        solverFactory.setParameter(ABCILPSolver.THREAD_POOL_SIZE_PARAMETER, params.threadPoolSize);
        solverFactory.setParameter(ABCILPSolver.BEES_COUNT_PARAMETER, params.beesCount);
        solverFactory.setParameter(ABCILPSolver.SEARCH_RANGE_PARAMETER, 2.0);
        solverFactory.setParameter(ABCILPSolver.ABANDONMENT_LIMIT_PARAMETER, params.abandomentLimit);
        solverFactory.setParameter(ABCILPSolver.SCOUT_BEES_NUMBER_PARAMETER, params.scoutBeesNumber);

        final GateAssignmentSolver solver = new ABCGateAssignmentSolverImpl(solverFactory, params);
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
