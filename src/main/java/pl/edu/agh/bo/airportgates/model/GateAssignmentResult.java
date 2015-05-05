package pl.edu.agh.bo.airportgates.model;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Created by Michal Janczykowski on 2015-05-05.
 */
@RequiredArgsConstructor
public class GateAssignmentResult {
    @Getter
    private final ImmutableMap<Gate, List<Flight>> gateAssignments;

    public int getCost() {
        //TODO
        return 0;
    }
}
