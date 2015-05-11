package pl.edu.agh.bo.airportgates.abcilpsolver;

import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michał Ciołczyk
 */
public class ABCILPSolver implements Solver {
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
    public Result solve(Problem problem) {
        return null;
    }
}
