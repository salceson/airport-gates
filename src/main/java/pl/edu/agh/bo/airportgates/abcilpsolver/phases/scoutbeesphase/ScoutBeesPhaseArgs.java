package pl.edu.agh.bo.airportgates.abcilpsolver.phases.scoutbeesphase;

import lombok.Builder;
import lombok.Data;
import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;

/**
 * @author Michał Ciołczyk
 */
@Data
@Builder
public class ScoutBeesPhaseArgs {
    private final Solution[] currentSolutions;
    private final int dimension;
    private final int[] tries;
    private final int beesCount;
    private final int lowerBound;
    private final int upperBound;
    private final int abandonmentLimit;
    private final Problem problem;
    private final int scoutBeesNumber;
}
