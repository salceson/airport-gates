package pl.edu.agh.bo.airportgates.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FlightTest {

    @Test
    public void test_compareTo_shouldReturnNegativeNumberIfFirstFlightIfEarlier() throws Exception {
        //given
        final Flight flight1 = new Flight(100, FlightType.ARRIVAL, AircraftType.SMALL, "LO", "7", "KRK", "WAW");
        final Flight flight2 = new Flight(200, FlightType.ARRIVAL, AircraftType.SMALL, "LO", "7", "KRK", "WAW");

        //when
        final long result = flight1.compareTo(flight2);

        //then
        assertThat(result).isNegative();
    }

    @Test
    public void test_compareTo_shouldReturnNegativeNumberIfFirstFlightIfDeparture() throws Exception {
        //given
        final Flight flight1 = new Flight(100, FlightType.DEPARTURE, AircraftType.SMALL, "LO", "7", "KRK", "WAW");
        final Flight flight2 = new Flight(100, FlightType.ARRIVAL, AircraftType.SMALL, "LO", "7", "KRK", "WAW");

        //when
        final long result = flight1.compareTo(flight2);

        //then
        assertThat(result).isNegative();
    }

    @Test
    public void test_compareTo_shouldReturnPositiveNumberIfFirstFlightIfLater() throws Exception {
        //given
        final Flight flight1 = new Flight(300, FlightType.ARRIVAL, AircraftType.SMALL, "LO", "7", "KRK", "WAW");
        final Flight flight2 = new Flight(200, FlightType.ARRIVAL, AircraftType.SMALL, "LO", "7", "KRK", "WAW");

        //when
        final long result = flight1.compareTo(flight2);

        //then
        assertThat(result).isPositive();
    }

    @Test
    public void test_compareTo_shouldReturnPositiveNumberIfFirstFlightIfArrival() throws Exception {
        //given
        final Flight flight1 = new Flight(100, FlightType.ARRIVAL, AircraftType.SMALL, "LO", "7", "KRK", "WAW");
        final Flight flight2 = new Flight(100, FlightType.DEPARTURE, AircraftType.SMALL, "LO", "7", "KRK", "WAW");

        //when
        final long result = flight1.compareTo(flight2);

        //then
        assertThat(result).isPositive();
    }

    @Test
    public void test_compareTo_shouldReturnZeroIfTimesAreEqual() throws Exception {
        //given
        final Flight flight1 = new Flight(100, FlightType.ARRIVAL, AircraftType.SMALL, "LO", "7", "KRK", "WAW");
        final Flight flight2 = new Flight(100, FlightType.ARRIVAL, AircraftType.SMALL, "LO", "7", "KRK", "WAW");

        //when
        final long result = flight1.compareTo(flight2);

        //then
        assertThat(result).isZero();
    }

    @Test
    public void test_compareTo_shouldReturnZeroIfFirstTimesAtGatesAreEqual() throws Exception {
        //given
        final Flight flight1 = new Flight(100 + AircraftType.SMALL.getGateOccupationTime(), FlightType.DEPARTURE, AircraftType.SMALL, "LO", "7", "KRK", "WAW");
        final Flight flight2 = new Flight(100, FlightType.ARRIVAL, AircraftType.SMALL, "LO", "7", "KRK", "WAW");

        //when
        final long result = flight1.compareTo(flight2);

        //then
        assertThat(result).isZero();
    }
}