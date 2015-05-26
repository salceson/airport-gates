package pl.edu.agh.bo.airportgates.gui;

import pl.edu.agh.bo.airportgates.model.Gate;
import pl.edu.agh.bo.airportgates.model.GateAssignmentResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Michał Ciołczyk
 */
public class AfterSolvingGUI extends JPanel {
    private static JDialog dialog;

    public AfterSolvingGUI(GateAssignmentResult result, double timeElapsed) {
        super(new BorderLayout());

        JPanel statisticsPanel = new JPanel(new GridLayout(3, 1));
        statisticsPanel.setSize(400, 200);

        JLabel timeLabel = new JLabel("Time elapsed: " + timeElapsed + " s.");
        statisticsPanel.add(timeLabel);

        JLabel objectiveLabel = new JLabel("Objective: " + result.getCost());
        statisticsPanel.add(objectiveLabel);

        JLabel bestSolutionIterationLabel = new JLabel("Best solution found at iteration: "
                + result.getBestSolutionIteration());
        statisticsPanel.add(bestSolutionIterationLabel);

        JTextArea resultsTextArea = new JTextArea();
        for (Gate gate : result.getGateAssignments().keySet()) {
            String line = gate.getNumber() + ": " + result.getGateAssignments().get(gate) + "\n";
            resultsTextArea.append(line);
        }

        JScrollPane resultsScrollPane = new JScrollPane(resultsTextArea);
        resultsScrollPane.setSize(400, 300);

        JButton button = new JButton("OK");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
                dialog = null;
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setSize(400, 100);
        buttonPanel.add(button);

        add(statisticsPanel, BorderLayout.PAGE_START);
        add(resultsScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_END);
    }

    public static void createAndShow(GateAssignmentResult result, double timeElapsed, JFrame owner) {
        dialog = new JDialog(owner, "Solver run summary");
        dialog.add(new AfterSolvingGUI(result, timeElapsed));
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 600);
        dialog.pack();
        dialog.setVisible(true);
    }
}
