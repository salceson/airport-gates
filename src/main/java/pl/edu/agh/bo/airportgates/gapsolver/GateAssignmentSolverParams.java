package pl.edu.agh.bo.airportgates.gapsolver;


public class GateAssignmentSolverParams {
    public int timeout;
    public int iterations;
    public double modificationRate;
    public int threadPoolSize;
    public int beesCount;
    public int abandonmentLimit;
    public int scoutBeesNumber;

    public GateAssignmentSolverParams(int timeout, int iterations, double modificationRate, int threadPoolSize,
                                      int beesCount, int abandomentLimit, int scoutBeesNumber) {
        this.timeout = timeout;
        this.iterations = iterations;
        this.modificationRate = modificationRate;
        this.threadPoolSize = threadPoolSize;
        this.beesCount = beesCount;
        this.abandonmentLimit = abandomentLimit;
        this.scoutBeesNumber = scoutBeesNumber;
    }

    public static GateAssignmentSolverParams defaultParams() {
        return new GateAssignmentSolverParams(100, 1000, 12, 15, 1234, 123, 321);
    }
}
