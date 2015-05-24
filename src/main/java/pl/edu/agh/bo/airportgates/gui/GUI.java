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


public class GUI extends JPanel implements ActionListener {
    static private final String newline = "\n";
    private JButton openButton;
    private JTextArea textArea;
    private JFileChooser fc;
    private static JFrame frame;

    public GUI() {
        super(new BorderLayout());

        textArea = new JTextArea(5,20);
        textArea.setMargin(new Insets(5, 5, 5, 5));
        textArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(textArea);

        Path cwd = Paths.get(System.getProperty("user.dir"));
        Path dataDir = Paths.get("src/main/java/pl/edu/agh/bo/airportgates/data");
        Path dataPath = cwd.resolve(dataDir);
        fc = new JFileChooser(dataPath.toString());

        openButton = new JButton("Load a problem file");
        openButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        add(buttonPanel, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            int retVal = fc.showOpenDialog(GUI.this);

            if (retVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                textArea.append("Opening: " + file.getName() + "." + newline);
                GateAssignmentProblem problem = null;
                try {
                    problem = ProblemDataLoader.loadProblemFromFile(file);
                } catch (IOException ex) {
                    textArea.append("Problem while loading file.");
                    JOptionPane.showMessageDialog(frame, "Problem while loading file.");
                } catch (InvalidFileFormatException e1) {
                    textArea.append("Ivalid file format.");
                    JOptionPane.showMessageDialog(frame, "Invalid file format.");
                }

                if (problem != null) {
                    textArea.append("Problem loaded, starting to solve.");
                    SolverRunner.runSolver(problem);
                }

            } else {
                textArea.append("Open command cancelled by user." + newline);
            }

            textArea.setCaretPosition(textArea.getDocument().getLength());
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
