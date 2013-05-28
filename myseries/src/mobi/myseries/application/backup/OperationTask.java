package mobi.myseries.application.backup;

public interface OperationTask extends Runnable {
    public OperationResult result();

}
