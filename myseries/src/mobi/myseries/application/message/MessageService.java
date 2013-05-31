package mobi.myseries.application.message;

import java.util.Collection;

import mobi.myseries.application.backup.BackupListener;
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

    private void notifyCheckingForUpdates() {
        for (MessageServiceListener l : this.listeners) {
            l.onCheckingForUpdates();
        }
    }

    private void notifyUpdateSuccess() {
        for (MessageServiceListener l : this.listeners) {
            l.onUpdateSuccess();
        }
    }

    private void notifyUpdateError(Exception e) {
        for (MessageServiceListener l : this.listeners) {
            l.onUpdateError(e);
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
    public void onCheckingForUpdates() {
        this.notifyCheckingForUpdates();
    }

    @Override
    public void onUpdateNotNecessary() {}


    @Override
    public void onUpdateProgress(int current, int total) {}

    @Override
    public void onUpdateFailure(Exception e) {
        this.notifyUpdateError(e);
    }

    @Override
    public void onUpdateSuccess() {
        this.notifyUpdateSuccess();
    }

    // Backup and restore

    @Override
    public void onBackupSucess() {
        this.notifyBackupSuccess();
    }

    @Override
    public void onBackupFailure(Exception e) {
        this.notifyBackupFailure(e);
    }

    @Override
    public void onRestoreSucess() {
        this.notifyRestoreSuccess();
    }

    @Override
    public void onRestoreFailure(Exception e) {
        this.notifyRestoreFailure(e);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub

    }
}
