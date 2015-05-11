package pl.edu.agh.bo.airportgates.abcilpsolver.exceptions;

/**
 * @author Michał Ciołczyk
 */
public class ABCILPSolverException extends RuntimeException {
    private static String MSG = "Solver has been terminated due to internal error.";

    public ABCILPSolverException() {
        super(MSG);
    }

    public ABCILPSolverException(String message) {
        super(MSG + "\n" + message);
    }

    public ABCILPSolverException(String message, Throwable cause) {
        super(MSG + "\n" + message, cause);
    }

    public ABCILPSolverException(Throwable cause) {
        super(cause);
    }

    public ABCILPSolverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(MSG + "\n" + message, cause, enableSuppression, writableStackTrace);
    }
}
