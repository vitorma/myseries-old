package mobi.myseries.application.update;

public class UpdateResult {
    private Exception error;

    public boolean success() { return this.error() == null; }
    public Exception error() { return this.error; }
    public UpdateResult withError(Exception e) {
        this.error = e;
        return this;
    }
}