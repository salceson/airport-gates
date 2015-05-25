package pl.edu.agh.bo.airportgates.main;

import com.google.common.base.Optional;
import net.sf.javailp.SolverFactory;
import pl.edu.agh.bo.airportgates.abcilpsolver.ABCILPSolver;
import pl.edu.agh.bo.airportgates.abcilpsolver.ABCILPSolverFactory;
import pl.edu.agh.bo.airportgates.dataloader.InvalidFileFormatException;
import pl.edu.agh.bo.airportgates.dataloader.ProblemDataLoader;
import pl.edu.agh.bo.airportgates.gapsolver.ABCGateAssignmentSolverImpl;
import pl.edu.agh.bo.airportgates.gapsolver.GateAssignmentSolver;
import pl.edu.agh.bo.airportgates.model.Gate;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.model.GateAssignmentResult;

import java.io.IOException;

/**
 * @author Michał Ciołczyk
 * @author Michał Janczykowski
 */
public class Main {
    public static void main(String[] args) {
        final GateAssignmentProblem gateAssignmentProblem;
        try {
            gateAssignmentProblem = createProblem();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InvalidFileFormatException e) {
            e.printStackTrace();
            return;
        }

        long startTime = System.currentTimeMillis();

//        final GateAssignmentSolver solver = new ILPGateAssignmentSolverImpl(new SolverFactoryGurobi());
//        final GateAssignmentSolver solver = new ILPGateAssignmentSolverImpl(new SolverFactoryLpSolve());
        SolverFactory solverFactory = new ABCILPSolverFactory();
        setABCParams(solverFactory);
//        final GateAssignmentSolver solver = new ILPGateAssignmentSolverImpl(solverFactory);
        final GateAssignmentSolver solver = new ABCGateAssignmentSolverImpl(solverFactory);
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

    private static GateAssignmentProblem createProblem() throws IOException, InvalidFileFormatException {
        return ProblemDataLoader.loadProblemFromFile("data/test2.txt");
    }

    private static void setABCParams(SolverFactory solverFactory) {
        solverFactory.setParameter(ABCILPSolver.ITERATIONS_PARAMETER, 1000);
        solverFactory.setParameter(ABCILPSolver.LOWER_BOUND_PARAMETER, 0);
        solverFactory.setParameter(ABCILPSolver.UPPER_BOUND_PARAMETER, 1);
        solverFactory.setParameter(ABCILPSolver.MODIFICATION_RATE_PARAMETER, 0.7);
        solverFactory.setParameter(ABCILPSolver.THREAD_POOL_SIZE_PARAMETER, 8);
        solverFactory.setParameter(ABCILPSolver.BEES_COUNT_PARAMETER, 50);
        solverFactory.setParameter(ABCILPSolver.SEARCH_RANGE_PARAMETER, 2.0);
        solverFactory.setParameter(ABCILPSolver.ABANDONMENT_LIMIT_PARAMETER, 50);
        solverFactory.setParameter(ABCILPSolver.SCOUT_BEES_NUMBER_PARAMETER, 20);
    }
}
