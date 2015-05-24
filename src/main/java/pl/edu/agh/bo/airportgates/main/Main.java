package pl.edu.agh.bo.airportgates.main;

import com.google.common.base.Optional;
import pl.edu.agh.bo.airportgates.abcilpsolver.ABCILPSolverFactory;
import pl.edu.agh.bo.airportgates.dataloader.InvalidFileFormatException;
import pl.edu.agh.bo.airportgates.dataloader.ProblemDataLoader;
import pl.edu.agh.bo.airportgates.gapsolver.GateAssignmentSolver;
import pl.edu.agh.bo.airportgates.gapsolver.ILPGateAssignmentSolverImpl;
import pl.edu.agh.bo.airportgates.gui.GUI;
import pl.edu.agh.bo.airportgates.model.Gate;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;
import pl.edu.agh.bo.airportgates.model.GateAssignmentResult;

import javax.swing.*;
import java.io.IOException;

/**
 * @author Michał Ciołczyk
 * @author Michał Janczykowski
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //UIManager.put("swing.boldMetal", Boolean.FALSE);
                GUI.createAndShowGUI();
            }
        });
    }

    // keeping that one because... well because :)
    public static void main_old(String[] args) {
        final GateAssignmentProblem gateAssignmentProblem;
        try {
            gateAssignmentProblem = createProblem();
        } catch (IOException | InvalidFileFormatException e) {
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
