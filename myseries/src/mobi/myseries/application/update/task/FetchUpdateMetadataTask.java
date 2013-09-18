package mobi.myseries.application.update.task;

import mobi.myseries.application.Environment;
import mobi.myseries.application.update.UpdateResult;

@Deprecated
public class FetchUpdateMetadataTask implements UpdateTask {
    private final Environment environment;
    private final long lastSuccessfulUpdate;

    private UpdateResult result;

    public FetchUpdateMetadataTask(Environment environment, long lastSuccessfulUpdate) {
        this.environment = environment;
        this.lastSuccessfulUpdate = lastSuccessfulUpdate;
    }

    @Override
    public void run() {
        try {
//            boolean updateIsAvailable = environment.seriesSource().fetchUpdateMetadataSince(lastSuccessfulUpdate);
//            Log.d(getClass().getName(), "Is update metadata available? " + updateIsAvailable);

            this.result = new UpdateResult();
        } catch (Exception e) {

            this.result = new UpdateResult().withError(e);
        }
    }

    @Override
    public UpdateResult result() {
        return this.result;
    }
}
