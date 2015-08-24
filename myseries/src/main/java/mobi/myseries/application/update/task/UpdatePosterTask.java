package mobi.myseries.application.update.task;

import mobi.myseries.application.image.ImageService;
import mobi.myseries.application.update.UpdateResult;
import mobi.myseries.domain.model.Series;
import android.util.Log;

public class UpdatePosterTask implements UpdateTask {

    private ImageService imageService;
    private Series series;
    private UpdateResult result;

    public UpdatePosterTask(ImageService imageService, Series series) {
        this.series = series;
        this.imageService = imageService;
    }

    @Override
    public void run() {
        Log.d(getClass().getName(), "Downloading poster of " + series);
        this.imageService.downloadAndSavePosterOf(series);
        Log.d(getClass().getName(), "Poster of " + series + " downloaded");
        this.result = new UpdateResult();
    }

    @Override
    public UpdateResult result() {
        return this.result;
    }

}
