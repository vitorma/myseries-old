package mobi.myseries.gui.backup;

import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupMode;

public class BackupServiceListener implements BackupListener {

    @Override
    public void onBackupSucess() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRestoreSucess() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onBackupFailure(BackupMode mode, Exception e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onBackupCompleted(BackupMode mode) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onBackupRunning(BackupMode mode) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRestoreFailure(BackupMode mode, Exception e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRestoreRunning(BackupMode mode) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRestoreCompleted(BackupMode mode) {
        // TODO Auto-generated method stub
        
    }

}
