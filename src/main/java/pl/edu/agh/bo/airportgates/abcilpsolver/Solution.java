package pl.edu.agh.bo.airportgates.abcilpsolver;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.sf.javailp.Constraint;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import pl.edu.agh.bo.airportgates.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author Michał Ciołczyk
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public class Solution implements Comparable<Solution> {
    public static final int NOT_SATISFIED_CONSTRAINT_PENALTY = 10000;

    private final int dimension;
    @Getter
    private final Map<Object, Long> variables;
    private final Problem problem;
    private Long objectiveValue = null;
    private Boolean valid;

    public boolean isValid(boolean print) {
        if (valid == null || print) {
            calculateObjectiveAndCheckIfValid(print);
        }
        return valid;
    }

    @Override
    public int compareTo(Solution o) {
        int comparison = Double.compare(getObjectiveValue(), o.getObjectiveValue());
        switch (problem.getOptType()) {
            case MIN:
                return -comparison;
            case MAX:
                return comparison;
        }
        return 0;
    }

    public long getObjectiveValue() {
        if (objectiveValue == null) {
            calculateObjectiveAndCheckIfValid(false);
        }

        return objectiveValue;
    }

    public double getFitness() {
//        if (objectiveValue == null) {
//            calculateObjectiveAndCheckIfValid();
//        }
//
//        switch (problem.getOptType()) {
//            case MIN:
//                if (objectiveValue >= 0) {
//                    return 1.0 / (1.0 + objectiveValue);
//                }
//
//                return 1.0 - objectiveValue;
//            case MAX:
//                if (objectiveValue <= 0) {
//                    return 1.0 / (1.0 - objectiveValue);
//                }
//
//                return 1.0 + objectiveValue;
//        }
//
//        return 0.0;
        return getObjectiveValue();
    }

    private void calculateObjectiveAndCheckIfValid(boolean print) {
        List<Number> objectiveCoefficients = problem.getObjective().getCoefficients();

        long objective = 0;

        int i = 0;
        for (Object variable : problem.getObjective().getVariables()) {
            if (variable instanceof Pair) {
                Pair variablesPair = (Pair) variable;
                objective += variables.get(variablesPair.getFirst())
                        * variables.get(variablesPair.getSecond())
                        * (long) objectiveCoefficients.get(i++);
            } else {
                objective += variables.get(variable) * (long) objectiveCoefficients.get(i++);
            }
        }

        long penaltyCoefficient = (problem.getOptType() == OptType.MAX) ? -1 : 1;

        for (Constraint constraint : problem.getConstraints()) {
            objective = penaltyObjectiveIfConstraintNotValid(objective, penaltyCoefficient, constraint, print);
        }

        objectiveValue = objective;

        if (valid == null) {
            valid = true;
        }
    }

    private long penaltyObjectiveIfConstraintNotValid(long objective, long penaltyCoefficient,
                                                      Constraint constraint, boolean print) {
        List<Number> constraintCoefficients = constraint.getLhs().getCoefficients();
        long constraintValue = 0;

        int i = 0;
        for (Object variable : constraint.getLhs().getVariables()) {
            long coeff;
            try {
                coeff = (long) constraintCoefficients.get(i);
            } catch (ClassCastException e) {
                coeff = (int) constraintCoefficients.get(i);
            }
            i++;
            constraintValue += variables.get(variable) * coeff;
        }

        long constraintRightSide;
        try {
            constraintRightSide = (long) constraint.getRhs();
        } catch (ClassCastException e) {
            constraintRightSide = (int) constraint.getRhs();
        }

        long delta = Math.abs(constraintValue - constraintRightSide);

        switch (constraint.getOperator()) {
            case EQ:
                if (constraintValue != constraintRightSide) {
                    objective += penaltyCoefficient * NOT_SATISFIED_CONSTRAINT_PENALTY * delta;
                    if (print) {
                        System.out.println(constraint);
                    }
                    valid = false;
                }
                break;
            case GE:
                if (constraintValue < constraintRightSide) {
                    objective += penaltyCoefficient * NOT_SATISFIED_CONSTRAINT_PENALTY * delta;
                    if (print) {
                        System.out.println(constraint);
                    }
                    valid = false;
                }
                break;
            case LE:
                if (constraintValue > constraintRightSide) {
                    objective += penaltyCoefficient * NOT_SATISFIED_CONSTRAINT_PENALTY * delta;
                    if (print) {
                        System.out.println(constraint);
                    }
                    valid = false;
                }
        }

        return objective;
    }
}
