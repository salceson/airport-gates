package pl.edu.agh.bo.airportgates.abcilpsolver.phases.employedbeesphase;

import lombok.Builder;
import lombok.Data;
import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;

/**
 * @author Michał Ciołczyk
 */
@Data
@Builder
public class EmployedBeesPhaseWorkerArgs {
    private final Solution[] solutions;
    private final Solution[] newSolutions;
    private final int startBee;
    private final int endBee;
    private final int beesCount;
    private final int dimension;
    private final int modificationRate;
    private final Problem problem;
}
