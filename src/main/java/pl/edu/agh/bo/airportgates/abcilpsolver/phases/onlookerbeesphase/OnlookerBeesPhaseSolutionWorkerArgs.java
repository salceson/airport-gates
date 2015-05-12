package pl.edu.agh.bo.airportgates.abcilpsolver.phases.onlookerbeesphase;

import lombok.Builder;
import lombok.Data;
import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;

/**
 * @author Michał Ciołczyk
 */
@Data
@Builder
public class OnlookerBeesPhaseSolutionWorkerArgs {
    private final int beesCount;
    private final int startBee;
    private final int endBee;
    private final double modificationRate;
    private final Solution[] currentSolutions;
    private final Solution[] newSolutions;
    private final double[] prob;
    private final Problem problem;
    private final int lowerBound;
    private final int upperBound;
    private final int dimension;
    private final double searchRange;
}
