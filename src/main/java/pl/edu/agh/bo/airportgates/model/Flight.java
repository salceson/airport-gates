package pl.edu.agh.bo.airportgates.model;

import lombok.Data;

/**
 * @author Michał Ciołczyk
 */
@Data
public class Flight implements Comparable<Flight> {
    private final long operationTime;
    private final FlightType flightType;
    private final AircraftType aircraftType;
    private final String airline;
    private final String flightNumber;
    private final String departureAirport;
    private final String arrivalAirport;

    /**
     * @return operationTime - gateOccupationTime for DEPARTURE flights or operationTime + gateOccupationTime for ARRIVAL flights.
     */
    public long getTimeAtGate() {
        if (flightType == FlightType.DEPARTURE) {
            return operationTime - aircraftType.getGateOccupationTime();
        } else if (flightType == FlightType.ARRIVAL) {
            return operationTime;
        }

        throw new IllegalStateException("Flight is neither DEPARTURE nor ARRIVAL");
    }

    @Override
    public String toString() {
        return String.format("%d\t%s%s\t%s-%s\t%s", operationTime, airline, flightNumber, departureAirport, arrivalAirport, aircraftType.name());
    }

    @Override
    public int compareTo(Flight o) {
        return ((Long) getTimeAtGate()).compareTo((Long) o.getTimeAtGate());
    }

    public boolean isAtGateAfter(Flight o) {
        return this.compareTo(o) > 0;
    }
}
