package mobi.myseries.application.backup;

import java.io.File;
import java.util.Collection;

import mobi.myseries.application.App;
import mobi.myseries.application.backup.json.JsonHelper;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.shared.FilesUtil;

public class RestoreTask implements OperationTask {
    private BackupMode backupMode;
    private OperationResult result;
    private SeriesRepository repository;
    
    public RestoreTask(BackupMode backupMode, SeriesRepository repository) {
        this.backupMode = backupMode;
        this.repository = repository;
    }

    @Override
    public void run() {
        try {
            File backup = new File(App.context().getCacheDir(), "myseries.json");
            backupMode.downloadBackupToFile(backup);
            String filePath = backup.getAbsolutePath();
            String fileStringContent = FilesUtil.readFileAsString(filePath, null);
            Collection<Series> series = JsonHelper.fromJson(fileStringContent);
            repository.clear();
            for (Series s : series) {
                repository.insert(s);
            }
        } catch (Exception e) {
            this.result = new OperationResult().withError(e);
            e.printStackTrace();
            return;
        }
        this.result = new OperationResult();

    }

    @Override
    public OperationResult result() {
        return this.result;
    }

}
