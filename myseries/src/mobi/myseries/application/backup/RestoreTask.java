package mobi.myseries.application.backup;

import mobi.myseries.domain.repository.series.SeriesRepository;

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
            repository.restoreFrom(backupMode.getBackup().getAbsolutePath());
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
