package pl.edu.agh.bo.airportgates.dataloader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Cleanup;
import org.apache.commons.lang3.StringUtils;
import pl.edu.agh.bo.airportgates.model.*;
import pl.edu.agh.bo.airportgates.util.MapBasedPaxFlowFunction;
import pl.edu.agh.bo.airportgates.util.Pair;
import pl.edu.agh.bo.airportgates.util.SimpleGateDistancesFunction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Michal Janczykowski
 */
public class ProblemDataLoader {
    public static GateAssignmentProblem loadProblemFromFile(String filepath) throws IOException, InvalidFileFormatException {
        return loadProblemFromFileAux(new FileReader(filepath));
    }

    public static GateAssignmentProblem loadProblemFromFile(File file) throws IOException, InvalidFileFormatException {
        return loadProblemFromFileAux(new FileReader(file));
    }

    public static GateAssignmentProblem loadProblemFromFileAux(FileReader fr) throws IOException, InvalidFileFormatException {
        @Cleanup final FileReader fileReader = fr;
        @Cleanup final BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;
        if ((line = bufferedReader.readLine()) == null) {
            throw new InvalidFileFormatException();
        }

        //read base airport code
        final String baseAirport = StringUtils.split(line)[0];

        //read number of gates
        if ((line = bufferedReader.readLine()) == null) {
            throw new InvalidFileFormatException();
        }

        final int gatesCount = Integer.parseInt(StringUtils.split(line)[0]);

        //read number of flights
        if ((line = bufferedReader.readLine()) == null) {
            throw new InvalidFileFormatException();
        }

        final int flightsCount = Integer.parseInt(StringUtils.split(line)[0]);

        //read flights
        final List<Flight> flights = Lists.newArrayList();
        for (int i = 0; i < flightsCount; i++) {
            if ((line = bufferedReader.readLine()) == null) {
                throw new InvalidFileFormatException("Cannot read flight");
            }

            final String[] flightData = StringUtils.split(line);
            final int operationTime = Integer.parseInt(flightData[0]);
            final String airline = flightData[1];
            final String flightNum = flightData[2];
            final String depAirport = flightData[3];
            final String arrAirport = flightData[4];
            final AircraftType acftType = AircraftType.valueOf(flightData[5]);
            final FlightType flightType;
            if (baseAirport.equals(depAirport)) {
                flightType = FlightType.DEPARTURE;
            } else if (baseAirport.equals(arrAirport)) {
                flightType = FlightType.ARRIVAL;
            } else {
                throw new InvalidFileFormatException("Invalid flight");
            }

            final Flight flight = new Flight(operationTime, flightType, acftType, airline, flightNum, depAirport, arrAirport);
            flights.add(flight);
        }

        //create gates
        final List<Gate> gates = Lists.newArrayList();

        for (int i = 0; i < gatesCount; i++) {
            gates.add(new Gate(i));
        }

        //read pax flows
        if ((line = bufferedReader.readLine()) == null) {
            throw new InvalidFileFormatException();
        }

        final Map<Pair<Flight, Flight>, Integer> paxFlows = Maps.newHashMap();

        final int paxFlowExprs = Integer.parseInt(StringUtils.split(line)[0]);
        for (int i = 0; i < paxFlowExprs; i++) {
            if ((line = bufferedReader.readLine()) == null) {
                throw new InvalidFileFormatException();
            }

            final String[] paxFlowsData = StringUtils.split(line);
            final int flight1Ind = Integer.parseInt(paxFlowsData[0]);
            final int flight2Ind = Integer.parseInt(paxFlowsData[1]);
            final int flow = Integer.parseInt(paxFlowsData[2]);

            final Flight flight1 = flights.get(flight1Ind);
            final Flight flight2 = flights.get(flight2Ind);

            paxFlows.put(new Pair<>(flight1, flight2), flow);
        }

        return new GateAssignmentProblem.Builder()
                .withFlights(flights)
                .withGateDistancesFunction(new SimpleGateDistancesFunction())
                .withGates(gates)
                .withFlightsFlowFunction(new MapBasedPaxFlowFunction(paxFlows))
                .build();
    }
}

