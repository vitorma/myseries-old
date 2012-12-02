package mobi.myseries.application.backup;

public interface BackupListener {

    public void onBackupSucess();

    public void onBackupFailure(Exception e);

    public void onRestoreSucess();

    public void onRestoreFailure(Exception e);

}
