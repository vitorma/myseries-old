package mobi.myseries.application.message;

import mobi.myseries.domain.model.Series;

public interface MessageServiceListener {

    public void onFollowingStart(Series series);
    public void onFollowingSuccess(Series series); 
    public void onFollowingError(Series series, Exception e);

    public void onCheckingForUpdates();
    public void onUpdateSuccess();
    public void onUpdateError(Exception e);

    public void onBackupSucess();
    public void onBackupFailure(Exception e);
    public void onRestoreSucess();
    public void onRestoreFailure(Exception e);
}
