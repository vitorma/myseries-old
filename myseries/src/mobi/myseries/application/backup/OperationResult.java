package mobi.myseries.application.backup;

public class OperationResult {
    private Exception error;

    public boolean success() { return this.error() == null; }
    public Exception error() { return this.error; }
    public OperationResult withError(Exception e) {
        this.error = e;
        return this;
    }
}
