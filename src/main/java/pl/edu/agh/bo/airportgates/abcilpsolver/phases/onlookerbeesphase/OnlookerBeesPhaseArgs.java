package pl.edu.agh.bo.airportgates.abcilpsolver.phases.onlookerbeesphase;

import lombok.Builder;
import lombok.Data;
import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;

/**
 * Arguments for Onlooker Bees' Phase.
 *
 * @author Michał Ciołczyk
 */
@Data
@Builder
public class OnlookerBeesPhaseArgs {
    private final int beesPerThread;
    private final int poolSize;
    private final int beesCount;
    private final double modificationRate;
    private final Solution[] currentSolutions;
    private final Solution[] newSolutions;
    private final Problem problem;
    private final int lowerBound;
    private final int upperBound;
    private final int dimension;
    private final double searchRange;
    private final int[] tries;
}
