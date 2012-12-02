package mobi.myseries.application.update;

public interface UpdateTask extends Runnable {
    public UpdateResult result();
}