package pl.edu.agh.bo.airportgates.abcilpsolver;

import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michał Ciołczyk
 */
public class ABCILPSolverFactory implements SolverFactory {
    private final Map<Object, Object> parameters = new HashMap<>();

    @Override
    public void setParameter(Object o, Object o1) {
        parameters.put(o, o1);
    }

    @Override
    public Map<Object, Object> getParameters() {
        return parameters;
    }

    @Override
    public Solver get() {
        Solver solver = new ABCILPSolver();
        for (Map.Entry<Object, Object> paramsEntry : parameters.entrySet()) {
            solver.setParameter(paramsEntry.getKey(), paramsEntry.getValue());
        }
        return solver;
    }
}
