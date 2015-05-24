package pl.edu.agh.bo.airportgates.main;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.sf.javailp.SolverFactoryGurobi;
import net.sf.javailp.SolverFactoryLpSolve;
import pl.edu.agh.bo.airportgates.abcilpsolver.ABCILPSolverFactory;
import pl.edu.agh.bo.airportgates.dataloader.InvalidFileFormatException;
import pl.edu.agh.bo.airportgates.dataloader.ProblemDataLoader;
import pl.edu.agh.bo.airportgates.gapsolver.GateAssignmentSolver;
import pl.edu.agh.bo.airportgates.gapsolver.ILPGateAssignmentSolverImpl;
import pl.edu.agh.bo.airportgates.model.*;
import pl.edu.agh.bo.airportgates.util.MapBasedPaxFlowFunction;
import pl.edu.agh.bo.airportgates.util.Pair;
import pl.edu.agh.bo.airportgates.util.SimpleGateDistancesFunction;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        final GateAssignmentSolver solver = new ILPGateAssignmentSolverImpl(new ABCILPSolverFactory());
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
}
