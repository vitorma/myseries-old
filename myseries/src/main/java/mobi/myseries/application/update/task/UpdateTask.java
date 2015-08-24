package mobi.myseries.application.update.task;

import mobi.myseries.application.update.UpdateResult;

public interface UpdateTask extends Runnable {
    public UpdateResult result();
}