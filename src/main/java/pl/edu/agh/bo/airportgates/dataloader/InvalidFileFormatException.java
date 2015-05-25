package pl.edu.agh.bo.airportgates.dataloader;

/**
 * @author Michal Janczykowski
 */
public class InvalidFileFormatException extends Exception {
    public InvalidFileFormatException(String message) {
        super(message);
    }

    public InvalidFileFormatException() {
        super("File format is invalid");
    }
}
