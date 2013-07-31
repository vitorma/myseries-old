package mobi.myseries.application.backup;

public interface BackupListener {

    public void onBackupSucess();

    public void onBackupFailure(BackupMode mode, Exception e);

    public void onRestoreSucess();

    public void onRestoreFailure(BackupMode mode, Exception e);

    public void onStart();

    public void onBackupCompleted(BackupMode mode);

    public void onBackupRunning(BackupMode mode);

    public void onRestoreRunning(BackupMode mode);

    public void onRestoreCompleted(BackupMode mode);

}
