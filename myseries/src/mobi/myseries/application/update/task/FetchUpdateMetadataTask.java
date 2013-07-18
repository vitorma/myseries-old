package mobi.myseries.application.update.task;

import mobi.myseries.application.update.UpdateResult;
import mobi.myseries.domain.source.SeriesSource;
import android.util.Log;

public class FetchUpdateMetadataTask implements UpdateTask {
    private final SeriesSource seriesSource;
    private final long lastSuccessfulUpdate;

    private UpdateResult result;

    public FetchUpdateMetadataTask(SeriesSource seriesSource, long lastSuccessfulUpdate) {
        this.seriesSource = seriesSource;
        this.lastSuccessfulUpdate = lastSuccessfulUpdate;
    }

    @Override
    public void run() {
        try {
            boolean updateIsAvailable = seriesSource.fetchUpdateMetadataSince(lastSuccessfulUpdate);
            Log.d(getClass().getName(), "Is update metadata available? " + updateIsAvailable);

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
