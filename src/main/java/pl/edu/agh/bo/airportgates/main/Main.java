package pl.edu.agh.bo.airportgates.main;

import pl.edu.agh.bo.airportgates.gui.GUI;

/**
 * @author Michał Ciołczyk
 * @author Michał Janczykowski
 */
public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI.createAndShowGUI();
            }
        });
    }
}
