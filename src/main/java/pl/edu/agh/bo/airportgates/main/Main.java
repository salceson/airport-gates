package pl.edu.agh.bo.airportgates.main;

import pl.edu.agh.bo.airportgates.algorithm.AlgorithmLpSolveImpl;

/**
 * @author Michał Ciołczyk
 */
public class Main {
    public static void main(String[] args) {
        new AlgorithmLpSolveImpl().run(null, 0);
    }
}
