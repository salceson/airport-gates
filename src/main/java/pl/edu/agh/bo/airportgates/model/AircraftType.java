package pl.edu.agh.bo.airportgates.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Michal Janczykowski
 */
@RequiredArgsConstructor
public enum AircraftType {
    SMALL(40), LARGE(60);

    @Getter
    private final int gateOccupationTime;
}
