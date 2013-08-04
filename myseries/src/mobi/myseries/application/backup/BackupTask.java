package mobi.myseries.application.backup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Map;

import android.content.Context;

import mobi.myseries.application.App;
import mobi.myseries.application.backup.json.JsonHelper;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.SeriesRepository;

public class BackupTask implements OperationTask {
    
    private BackupMode backupMode;
    private OperationResult result;
    private SeriesRepository repository;

    public BackupTask(BackupMode backupMode, SeriesRepository repository) {
        this.backupMode = backupMode;
        this.repository = repository;
    }

    @Override
    public void run() {
        File seriesCacheFile = null;
        File preferencesCacheFile = null;
        try {
            seriesCacheFile = new File(App.context().getCacheDir(), "myseries.json");
            Collection<Series> series = repository.getAll();
            JsonHelper.writeSeriesJsonStream(new BufferedOutputStream(new FileOutputStream(seriesCacheFile)), series);
            backupMode.backupDB(seriesCacheFile);
            preferencesCacheFile = new File(App.context().getCacheDir(), "preferences.json");
            Map<String, ?> preferences = App.context().getSharedPreferences("mobi.myseries.preferences", Context.MODE_PRIVATE).getAll();
            JsonHelper.preferencesToJson(preferences, preferencesCacheFile);
            backupMode.backupDB(preferencesCacheFile);
        } catch (Exception e) {
            this.result = new OperationResult().withError(e);
            return;
        } finally {
            if(seriesCacheFile != null)
                seriesCacheFile.delete();
            if(preferencesCacheFile != null)
                preferencesCacheFile.delete();
        }
        this.result = new OperationResult();
    }

    @Override
    public OperationResult result() {
        return this.result;
    }

}
