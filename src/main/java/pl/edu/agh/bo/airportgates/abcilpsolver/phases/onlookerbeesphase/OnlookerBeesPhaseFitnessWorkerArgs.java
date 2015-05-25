package pl.edu.agh.bo.airportgates.abcilpsolver.phases.onlookerbeesphase;

import lombok.Builder;
import lombok.Data;
import pl.edu.agh.bo.airportgates.abcilpsolver.Solution;

/**
 * Arguments for Onlooker Bees' Phase Fitness Worker.
 *
 * @author Michał Ciołczyk
 */
@Data
@Builder
public class OnlookerBeesPhaseFitnessWorkerArgs {
    private final int startBee;
    private final int endBee;
    private final Solution[] currentSolutions;
    private final double[] fitness;
}
