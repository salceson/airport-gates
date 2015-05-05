package pl.edu.agh.bo.airportgates.algorithm;

import net.sf.javailp.*;
import pl.edu.agh.bo.airportgates.model.GateAssignmentProblem;

/**
 * @author Michał Ciołczyk
 */
public class AlgorithmLpSolveImpl implements Algorithm {
    @Override
    public void run(GateAssignmentProblem gateAssignmentProblem, int maxIterations) {
            SolverFactory factory = new SolverFactoryLpSolve();
            factory.setParameter(Solver.VERBOSE, 0);
            factory.setParameter(Solver.TIMEOUT, 100);
            Problem problem = new Problem();

            Linear linear = new Linear();
            linear.add(143, "x");
            linear.add(60, "y");

            problem.setObjective(linear, OptType.MAX);

            linear = new Linear();
            linear.add(120, "x");
            linear.add(210, "y");

            problem.add(linear, "<=", 15000);

            linear = new Linear();
            linear.add(110, "x");
            linear.add(30, "y");

            problem.add(linear, "<=", 4000);

            linear = new Linear();
            linear.add(1, "x");
            linear.add(1, "y");

            problem.add(linear, "<=", 75);

            problem.setVarType("x", Integer.class);
            problem.setVarType("y", Integer.class);

            Solver solver = factory.get(); // you should use this solver only once for one problem
            Result result = solver.solve(problem);

            System.out.println(result);

    }
}
