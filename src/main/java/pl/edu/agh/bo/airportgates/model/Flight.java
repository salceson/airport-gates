package pl.edu.agh.bo.airportgates.model;

import lombok.Data;

/**
 * @author Michał Ciołczyk
 */
@Data
public class Flight {
    private FlightType flightType;
    private String number;
    private String otherAirport;
}
