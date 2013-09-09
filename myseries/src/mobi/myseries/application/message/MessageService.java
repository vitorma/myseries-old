package mobi.myseries.application.message;

import java.util.Collection;
import java.util.Map;

import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.application.backup.BackupService;
import mobi.myseries.application.following.BaseSeriesFollowingListener;
import mobi.myseries.application.following.SeriesFollowingListener;
import mobi.myseries.application.following.SeriesFollowingService;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.application.update.listener.UpdateProgressListener;
import mobi.myseries.domain.model.ParcelableSeries;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;

public class MessageService implements
        Publisher<MessageServiceListener>,
        UpdateProgressListener, BackupListener {

    private ListenerSet<MessageServiceListener> listeners;

    public MessageService(
            SeriesFollowingService seriesFollowingService,
            UpdateService updateService,
            BackupService backupService) {

        this.listeners = new ListenerSet<MessageServiceListener>();

        seriesFollowingService.register(mSeriesFollowingListener);
        updateService.register(this);
        backupService.register(this);
    }

    @Override
    public boolean register(MessageServiceListener l) {
        return this.listeners.register(l);
    }

    @Override
    public boolean deregister(MessageServiceListener l) {
        return this.listeners.deregister(l);
    }

    /* SeriesFollowingListener */

    private SeriesFollowingListener mSeriesFollowingListener = new BaseSeriesFollowingListener() {
        @Override
        public void onFailToFollow(ParcelableSeries series, Exception e) {
            for (MessageServiceListener l : listeners) {
                //FIXME Method bellow should receive ParcelableSeries instead of Series
                l.onFollowingError(series.toSeries(), e);
            }
        }

        @Override
        public void onFailToUnfollow(Series seriesToUnfollow, Exception e) {
            //XXX Implement me
        }

        @Override
        public void onFailToUnfollowAll(Collection<Series> allSeriesToUnfollow, Exception e) {
            //XXX Implement me
        }
    };

    /* UpdateProgressListener */

    @Override
    public void onCheckingForUpdates() {}

    @Override
    public void onUpdateNotNecessary() {}

    @Override
    public void onUpdateProgress(int current, int total, Series currentSeries) {}

    @Override
    public void onUpdateFailure(Exception cause) {}

    @Override
    public void onUpdateSeriesFailure(Map<Series, Exception> causes) {}

    @Override
    public void onUpdateSuccess() {
        this.notifyUpdateSuccess();
    }

    private void notifyUpdateSuccess() {
        for (MessageServiceListener l : this.listeners) {
            l.onUpdateSuccess();
        }
    }

    /* BackupListener */

    @Override
    public void onBackupSucess() {
        //this.notifyBackupSuccess();
    }

    @Override
    public void onBackupFailure(BackupMode mode, Exception e) {
        //this.notifyBackupFailure(e);
    }

    @Override
    public void onRestoreSucess() {
        //this.notifyRestoreSuccess();
    }

    @Override
    public void onStart() {
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

    private void notifyBackupSuccess() {
        for (MessageServiceListener l : this.listeners) {
            l.onBackupSucess();
        }
    }

    private void notifyBackupFailure(Exception e) {
        for (MessageServiceListener l : this.listeners) {
            l.onBackupFailure(e);
        }
    }

    private void notifyRestoreSuccess() {
        for (MessageServiceListener l : this.listeners) {
            l.onRestoreSucess();
        }
    }

    private void notifyRestoreFailure(Exception e) {
        for (MessageServiceListener l : this.listeners) {
            l.onRestoreFailure(e);
        }
    }
}
