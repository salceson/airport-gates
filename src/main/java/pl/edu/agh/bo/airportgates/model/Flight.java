package pl.edu.agh.bo.airportgates.model;

import lombok.Data;

/**
 * @author Michał Ciołczyk
 */
@Data
public class Flight {
    private final long operationTime;
    private final FlightType flightType;
    private final AircraftType aircraftType;
    private final String airline;
    private final String flightNumber;
    private final String departureAirport;
    private final String arrivalAirport;

    @Override
    public String toString() {
        return String.format("%d\t%s%s\t%s-%s\t%s", operationTime, airline, flightNumber, departureAirport, arrivalAirport, aircraftType.name());
    }
}
