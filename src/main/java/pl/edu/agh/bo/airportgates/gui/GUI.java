package pl.edu.agh.bo.airportgates.gui;

import pl.edu.agh.bo.airportgates.dataloader.InvalidFileFormatException;
import pl.edu.agh.bo.airportgates.dataloader.ProblemDataLoader;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


public class GUI extends JPanel implements ActionListener {
    static private final String newline = "\n";
    private JButton openButton, startButton;
    private JTextArea logTextArea;
    private JTextField timeoutTextField;
    private JLabel timeoutLabel;
    private JFileChooser fc;
    private static JFrame frame;
    private SolverRunner solverRunner = new SolverRunner();

    public GUI() {
        super(new BorderLayout());

        logTextArea = new JTextArea(5,20);
        logTextArea.setMargin(new Insets(5, 5, 5, 5));
        logTextArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logTextArea);

        timeoutLabel = new JLabel("Timeout: ");
        timeoutTextField = new JTextField("100");

        Path cwd = Paths.get(System.getProperty("user.dir"));
        Path dataPath = cwd.resolve("data");
        fc = new JFileChooser(dataPath.toString());

        openButton = new JButton("Load a problem file");
        openButton.addActionListener(this);
        startButton = new JButton("Start solving!");
        startButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);

        JPanel paramsPanel = new JPanel();
        paramsPanel.add(timeoutLabel, 0);
        paramsPanel.add(timeoutTextField, 1);
        paramsPanel.add(startButton, 2);

        add(buttonPanel, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
        add(paramsPanel, BorderLayout.PAGE_END);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            int retVal = fc.showOpenDialog(GUI.this);

            if (retVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                logTextArea.append("Opening: " + file.getName() + "." + newline);
                GateAssignmentProblem problem = null;
                try {
                    solverRunner.gateAssignmentProblem = ProblemDataLoader.loadProblemFromFile(file);
                } catch (IOException ex) {
                    logTextArea.append("Problem while loading file.");
                    JOptionPane.showMessageDialog(frame, "Problem while loading file.");
                    return;
                } catch (InvalidFileFormatException e1) {
                    logTextArea.append("Ivalid file format.");
                    JOptionPane.showMessageDialog(frame, "Invalid file format.");
                    return;
                }
            } else {
                logTextArea.append("Open command cancelled by user." + newline);
            }

            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        } else if (e.getSource() == startButton) {
            if (solverRunner.gateAssignmentProblem == null) {
                logTextArea.append("Data file not chosen.");
                JOptionPane.showMessageDialog(frame, "You should choose a data file first.");
                return;
            }

            int timeout = 100;
            try {
                timeout = Integer.parseInt(timeoutTextField.getText());
                System.out.println("Timeout is: " + timeout);
            }
            catch (NumberFormatException ex) {
                logTextArea.append("Malformed timeout.");
                JOptionPane.showMessageDialog(frame, "Timeout has to be a number.");
                return;
            }

            logTextArea.append("Problem loaded, starting to solve.");
            solverRunner.runSolver();  // TODO do sth with the solution
        }
    }

    public static void createAndShowGUI() {
        frame = new JFrame("Airport Gates Problem Solver");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.add(new GUI());
        frame.setVisible(true);
    }
}
