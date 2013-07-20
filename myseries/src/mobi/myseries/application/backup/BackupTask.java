package mobi.myseries.application.backup;

import java.io.File;
import java.util.Collection;

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
        try {
            File cachedFile = new File(App.context().getCacheDir(), "myseries.json");
            Collection<Series> series = repository.getAll();
            JsonHelper.toJson(series, cachedFile);
            backupMode.backupDB(cachedFile);
        } catch (Exception e) {
            this.result = new OperationResult().withError(e);
            return;
        }
        this.result = new OperationResult();

    }

    @Override
    public OperationResult result() {
        return this.result;
    }

}
