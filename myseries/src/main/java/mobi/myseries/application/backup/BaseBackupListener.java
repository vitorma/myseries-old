package mobi.myseries.application.backup;

public class BaseBackupListener implements BackupListener {

    @Override
    public void onBackupSuccess() {}

    @Override
    public void onBackupFailure(BackupMode mode, Exception e) {}

    @Override
    public void onRestoreSuccess() {}

    @Override
    public void onRestoreFailure(BackupMode mode, Exception e) {}

    @Override
    public void onStart() {}

    @Override
    public void onBackupCompleted(BackupMode mode) {}

    @Override
    public void onBackupRunning(BackupMode mode) {}

    @Override
    public void onRestoreRunning(BackupMode mode) {}

    @Override
    public void onRestoreCompleted(BackupMode mode) {}

    @Override
    public void onBackupFailure(Exception e) {}

    @Override
    public void onRestoreProgress(int current, int total) {}

    @Override
    public void onRestorePosterDownloadProgress(int current, int total) {}

    @Override
    public void onBackupStart() {}

    @Override
    public void onRestoreCancelled() {}

}
