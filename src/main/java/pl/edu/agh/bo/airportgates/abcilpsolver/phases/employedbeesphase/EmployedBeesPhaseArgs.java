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
public class EmployedBeesPhaseArgs {
    private final Problem problem;
    private final int poolSize;
    private final int beesCount;
    private final double modificationRate;
    private final double searchRange;
    private final int dimension;
    private final Solution[] currentSolutions;
    private final Solution[] newSolutions;
    private final int beesPerThread;
    private final int lowerBound;
    private final int upperBound;
}
