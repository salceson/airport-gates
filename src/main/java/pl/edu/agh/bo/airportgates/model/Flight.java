package pl.edu.agh.bo.airportgates.model;

import lombok.Data;

/**
 * @author Michał Ciołczyk
 */
@Data
public class Flight {
    private final FlightType flightType;
    private final AircraftType aircraftType;
    private final String number;
    private final String otherAirport;
}
