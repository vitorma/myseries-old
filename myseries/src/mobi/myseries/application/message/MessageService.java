package mobi.myseries.application.message;

import java.util.Collection;
import java.util.Map;

import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.application.backup.BackupService;
import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.application.follow.SeriesFollowingListener;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.application.update.listener.UpdateProgressListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;

public class MessageService implements
        Publisher<MessageServiceListener>,
        SeriesFollowingListener, UpdateProgressListener, BackupListener {

    private ListenerSet<MessageServiceListener> listeners;

    public MessageService(
            FollowSeriesService followSeriesService,
            UpdateService updateService,
            BackupService backupService) {

        this.listeners = new ListenerSet<MessageServiceListener>();

        followSeriesService.register(this);
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

    private void notifyFollowingStart(Series series){
        for (MessageServiceListener l : this.listeners) {
            l.onFollowingStart(series);
        }
    }

    private void notifyFollowingSuccess(Series series) {
        for (MessageServiceListener l : this.listeners) {
            l.onFollowingSuccess(series);
        }
    }

    private void notifyFollowingError(Series series, Exception e) {
        for (MessageServiceListener l : this.listeners) {
            l.onFollowingError(series, e);
        }
    }

    private void notifyUpdateSuccess() {
        for (MessageServiceListener l : this.listeners) {
            l.onUpdateSuccess();
        }
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

    // Following and stop following

    @Override
    public void onFollowingStart(Series seriesToFollow) {
        this.notifyFollowingStart(seriesToFollow);
    }

    @Override
    public void onFollowing(Series followedSeries) {
        this.notifyFollowingSuccess(followedSeries);
    }

    @Override
    public void onStopFollowing(Series unfollowedSeries) {}

    @Override
    public void onStopFollowingAll(Collection<Series> allUnfollowedSeries) {}

    @Override
    public void onFollowingFailure(Series series, Exception e) {
        this.notifyFollowingError(series, e);
    }

    // Update

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

    // Backup and restore

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

}
