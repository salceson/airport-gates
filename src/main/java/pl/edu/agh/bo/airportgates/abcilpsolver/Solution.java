package pl.edu.agh.bo.airportgates.abcilpsolver;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.sf.javailp.Constraint;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;

import java.util.List;
import java.util.Map;

/**
 * @author Michał Ciołczyk
 */
@Data
@RequiredArgsConstructor
public class Solution implements Comparable<Solution> {
    public static final int NOT_SATISFIED_CONSTRAINT_PENALTY = 10000;

    private final int dimension;
    private final Map<Object, Long> variables;
    private final Problem problem;
    private Long objectiveValue = null;
    private Boolean valid;

    public boolean isValid() {
        if (valid == null) {
            calculateObjectiveAndCheckIfValid();
        }
        return valid;
    }

    @Override
    public int compareTo(Solution o) {
        switch (problem.getOptType()) {
            case MAX:
                return Long.compare(getObjectiveValue(), o.getObjectiveValue());
            case MIN:
                return -Long.compare(getObjectiveValue(), o.getObjectiveValue());
        }
        return 0;
    }

    public long getObjectiveValue() {
        if (objectiveValue == null) {
            calculateObjectiveAndCheckIfValid();
        }

        return objectiveValue;
    }

    private void calculateObjectiveAndCheckIfValid() {
        List<Number> objectiveCoefficients = problem.getObjective().getCoefficients();

        long objective = 0;

        int i = 0;
        for (Object variable : problem.getObjective().getVariables()) {
            objective += variables.get(variable) * (long) objectiveCoefficients.get(i++);
        }

        long penaltyCoefficient = (problem.getOptType() == OptType.MAX) ? 1 : -1;

        for (Constraint constraint : problem.getConstraints()) {
            objective = penaltyObjectiveIfConstraintNotValid(
                    objective, penaltyCoefficient, constraint);
        }

        objectiveValue = objective;

        if (valid == null) {
            valid = true;
        }
    }

    private long penaltyObjectiveIfConstraintNotValid(long objective, long penaltyCoefficient, Constraint constraint) {
        List<Number> constraintCoefficients = constraint.getLhs().getCoefficients();
        long constraintValue = 0;

        int i = 0;
        for (Object variable : constraint.getLhs().getVariables()) {
            constraintValue += variables.get(variable) * (long) constraintCoefficients.get(i++);
        }

        long constraintRightSide = (long) constraint.getRhs();

        switch (constraint.getOperator()) {
            case EQ:
                if (constraintValue != constraintRightSide) {
                    objective += penaltyCoefficient * NOT_SATISFIED_CONSTRAINT_PENALTY;
                    valid = false;
                }
                break;
            case GE:
                if (constraintValue < constraintRightSide) {
                    objective += penaltyCoefficient * NOT_SATISFIED_CONSTRAINT_PENALTY;
                    valid = false;
                }
                break;
            case LE:
                if (constraintValue > constraintRightSide) {
                    objective += penaltyCoefficient * NOT_SATISFIED_CONSTRAINT_PENALTY;
                    valid = false;
                }
        }

        return objective;
    }
}
