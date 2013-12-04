package mobi.myseries.application.backup;

public interface BackupListener {

    public void onBackupSuccess();

    public void onBackupFailure(BackupMode mode, Exception e);

    public void onRestoreSuccess();

    public void onRestoreFailure(BackupMode mode, Exception e);

    public void onStart();

    public void onBackupCompleted(BackupMode mode);

    public void onBackupRunning(BackupMode mode);

    public void onRestoreRunning(BackupMode mode);

    public void onRestoreCompleted(BackupMode mode);

    public void onBackupFailure(Exception e);

    public void onRestoreProgress(int current, int total);

    public void onRestorePosterDownloadProgress(int current, int total);

    public void onBackupStart();

    public void onRestoreCancelled();

}
