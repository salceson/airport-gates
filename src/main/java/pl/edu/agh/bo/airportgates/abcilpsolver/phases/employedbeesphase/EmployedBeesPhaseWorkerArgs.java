package pl.edu.agh.bo.airportgates.abcilpsolver.phases.employedbeesphase;

import lombok.Builder;
import lombok.Data;
import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;

/**
 * Arguments for Employed Bees' Phase Workers.
 *
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
    private final double modificationRate;
    private final double searchRange;
    private final Problem problem;
    private final int lowerBound;
    private final int upperBound;
}
