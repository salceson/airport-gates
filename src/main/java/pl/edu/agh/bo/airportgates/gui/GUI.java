package pl.edu.agh.bo.airportgates.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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
    private JButton openButton, startButton, stopButton;
    //private JTextArea logTextArea;
    private JTextField timeoutTF, iterationsTF, modificationRateTF;
    private JTextField threadPoolSizeTF, beesCountTF, abandonmentLimitTF, scoutBeesTF;
    private JFileChooser fc;
    private static JFrame frame;
    private SolverRunner solverRunner = new SolverRunner();
    private static XYSeries totalBestSeries, iterBestSeries, iterWorstSeries;
    private static int currIter = 1;
    private static Thread runningThread = null;

    public GUI() {
        super(new BorderLayout());

        //logTextArea = new JTextArea(25,20);
        //logTextArea.setMargin(new Insets(5, 5, 5, 5));
        //logTextArea.setEditable(false);
        //JScrollPane logScrollPane = new JScrollPane(logTextArea);
        XYDataset ds = createDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("Iterations best", "Iteration", "Cost", ds, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel cp = new ChartPanel(chart);

        JLabel timeoutLabel = new JLabel("Timeout: ");
        timeoutTF = new JTextField("400");
        JLabel iterationsLabel = new JLabel("Iterations: ");
        iterationsTF = new JTextField("1000");
        JLabel modificationRateLabel = new JLabel("Modification rate: ");
        modificationRateTF = new JTextField("0.7");
        JLabel threadPoolSizeLabel = new JLabel("Thread pool size: ");
        threadPoolSizeTF = new JTextField("8");
        JLabel beesCountLabel = new JLabel("Bees count: ");
        beesCountTF = new JTextField("50");
        JLabel abandonmentLimiLabel = new JLabel("Abandonment limit: ");
        abandonmentLimitTF = new JTextField("50");
        JLabel scoutBeesLabel = new JLabel("Scout bees: ");
        scoutBeesTF = new JTextField("20");

        Path cwd = Paths.get(System.getProperty("user.dir"));
        Path dataPath = cwd.resolve("data");
        fc = new JFileChooser(dataPath.toString());

        openButton = new JButton("Load a problem file");
        openButton.addActionListener(this);
        startButton = new JButton("Start solving!");
        startButton.addActionListener(this);
        stopButton = new JButton("Stop solving!");
        stopButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);

        JPanel paramsPanel = new JPanel();
        paramsPanel.setSize(500, 300);
        paramsPanel.add(timeoutLabel, 0);
        paramsPanel.add(timeoutTF, 1);
        paramsPanel.add(iterationsLabel, 2);
        paramsPanel.add(iterationsTF, 3);
        paramsPanel.add(modificationRateLabel, 4);
        paramsPanel.add(modificationRateTF, 5);
        paramsPanel.add(threadPoolSizeLabel, 6);
        paramsPanel.add(threadPoolSizeTF, 7);
        paramsPanel.add(beesCountLabel, 8);
        paramsPanel.add(beesCountTF, 9);
        paramsPanel.add(abandonmentLimiLabel, 10);
        paramsPanel.add(abandonmentLimitTF, 11);
        paramsPanel.add(scoutBeesLabel, 12);
        paramsPanel.add(scoutBeesTF, 13);
        paramsPanel.add(startButton, 14);
        paramsPanel.add(stopButton, 15);

        add(buttonPanel, BorderLayout.PAGE_START);
        //add(logScrollPane, BorderLayout.CENTER);
        add(cp, BorderLayout.CENTER);
        add(paramsPanel, BorderLayout.PAGE_END); // TODO wtf happens here with the dimensions...
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            int retVal = fc.showOpenDialog(GUI.this);

            if (retVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
//                logTextArea.append("Opening: " + file.getName() + "." + newline);
                GateAssignmentProblem problem = null;
                try {
                    solverRunner.gateAssignmentProblem = ProblemDataLoader.loadProblemFromFile(file);
                } catch (IOException ex) {
//                    logTextArea.append("Problem while loading file.");
                    JOptionPane.showMessageDialog(frame, "Problem while loading file.");
                    return;
                } catch (InvalidFileFormatException e1) {
//                    logTextArea.append("Ivalid file format.");
                    JOptionPane.showMessageDialog(frame, "Invalid file format.");
                    return;
                }
            }

//            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());

        } else if (e.getSource() == startButton) {
            if (solverRunner.gateAssignmentProblem == null) {
//                logTextArea.append("Data file not chosen.");
                JOptionPane.showMessageDialog(frame, "You should choose a data file first.");
                return;
            }

            int timeout = 0;
            int iterations = 0;
            double modificationRate = 0;
            int threadPoolSize = 0;
            int beesCount = 0;
            int abandonmentLimit = 0;
            int scoutBeesNumber = 0;
            try {
                timeout = Integer.parseInt(timeoutTF.getText());
                iterations = Integer.parseInt(iterationsTF.getText());
                modificationRate = Double.parseDouble(modificationRateTF.getText());
                threadPoolSize = Integer.parseInt(threadPoolSizeTF.getText());
                beesCount = Integer.parseInt(beesCountTF.getText());
                abandonmentLimit = Integer.parseInt(abandonmentLimitTF.getText());
                scoutBeesNumber = Integer.parseInt(scoutBeesTF.getText());
            }
            catch (NumberFormatException ex) {
//                logTextArea.append("Malformed param.");
                JOptionPane.showMessageDialog(frame, "Params have to be numbers.");
                return;
            }

            //solverRunner.params = new GateAssignmentSolverParams();
            solverRunner.params.timeout = timeout;
            solverRunner.params.iterations = iterations;
            solverRunner.params.modificationRate = modificationRate;
            solverRunner.params.threadPoolSize = threadPoolSize;
            solverRunner.params.beesCount = beesCount;
            solverRunner.params.abandonmentLimit = abandonmentLimit;
            solverRunner.params.scoutBeesNumber = scoutBeesNumber;

//            logTextArea.append("Problem loaded, starting to solve.");
            runningThread = new Thread(new Runnable() {
                public void run() {
                    totalBestSeries.clear();
                    iterBestSeries.clear();
                    iterWorstSeries.clear();
                    currIter = 1;
                    solverRunner.runSolver();  // TODO do sth with the solution
                }
            });
            runningThread.start();
        } else if (e.getSource() == stopButton) {
            runningThread.stop();
            runningThread = null;
        }
    }

    private static XYDataset createDataset() {
        totalBestSeries = new XYSeries("Total Best");
        iterBestSeries = new XYSeries("Iteration Best");
        iterWorstSeries = new XYSeries("Iteration Worst");

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(totalBestSeries);
        dataset.addSeries(iterBestSeries);
        dataset.addSeries(iterWorstSeries);

        return dataset;
    }

    public static void addCostsToChart(long iterBest, long iterWorst, long totalBest) {
        iterBestSeries.add(currIter, iterBest);
        iterWorstSeries.add(currIter, iterWorst);
        totalBestSeries.add(currIter, totalBest);
        currIter += 5;
    }

    public static void createAndShowGUI() {
        frame = new JFrame("Airport Gates Problem Solver");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(700, 800);
        frame.add(new GUI());
        frame.pack();
        frame.setVisible(true);
    }
}
