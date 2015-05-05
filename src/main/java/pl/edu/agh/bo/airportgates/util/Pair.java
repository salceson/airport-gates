package pl.edu.agh.bo.airportgates.util;

import lombok.Value;

/**
 * @author Michał Ciołczyk
 */
@Value
public class Pair<A, B> {
    private A first;
    private B second;
}
