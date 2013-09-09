package mobi.myseries.application.message;

import mobi.myseries.domain.model.Series;

public interface MessageServiceListener {

    @Deprecated
    public void onFollowingStart(Series series);
    @Deprecated
    public void onFollowingSuccess(Series series);
    public void onFollowingError(Series series, Exception e);

    public void onUpdateSuccess();

    public void onBackupSucess();
    public void onBackupFailure(Exception e);
    public void onRestoreSucess();
    public void onRestoreFailure(Exception e);
}
