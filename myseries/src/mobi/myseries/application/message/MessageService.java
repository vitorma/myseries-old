package mobi.myseries.application.message;

import java.util.Collection;

import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupService;
import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.application.follow.SeriesFollowingListener;
import mobi.myseries.application.update.UpdateListener;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;

public class MessageService implements
        Publisher<MessageServiceListener>,
        SeriesFollowingListener, UpdateListener, BackupListener {

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

    public boolean register(MessageServiceListener l) {
        return this.listeners.register(l);
    }

    public boolean deregister(MessageServiceListener l) {
        return this.listeners.deregister(l);
    }

    private void notifyFollowingStart(Series series){
        for (MessageServiceListener l : listeners) {
            l.onFollowingStart(series);
        }
    }

    private void notifyFollowingSuccess(Series series) {
        for (MessageServiceListener l : listeners) {
            l.onFollowingSuccess(series);
        }
    }

    private void notifyFollowingError(Series series, Exception e) {
        for (MessageServiceListener l : listeners) {
            l.onFollowingError(series, e);
        }
    }

    private void notifyUpdateStart() {
        for (MessageServiceListener l : listeners) {
            l.onUpdateStart();
        }

    }

    private void notifyUpdateSuccess() {
        for (MessageServiceListener l : listeners) {
            l.onUpdateSuccess();
        }
    }

    private void notifyUpdateError(Exception e) {
        for (MessageServiceListener l : listeners) {
            l.onUpdateError(e);
        }
    }

    private void notifyBackupSuccess() {
        for (MessageServiceListener l : listeners) {
            l.onBackupSucess();
        }
    }

    private void notifyBackupFailure(Exception e) {
        for (MessageServiceListener l : listeners) {
            l.onBackupFailure(e);
        }
    }

    private void notifyRestoreSuccess() {
        for (MessageServiceListener l : listeners) {
            l.onRestoreSucess();
        }
    }

    private void notifyRestoreFailure(Exception e) {
        for (MessageServiceListener l : listeners) {
            l.onRestoreFailure(e);
        }
    }

    // Following and stop following

    @Override
    public void onFollowingStart(Series seriesToFollow) {
        notifyFollowingStart(seriesToFollow);
    }

    @Override
    public void onFollowing(Series followedSeries) {
        notifyFollowingSuccess(followedSeries);
    }

    @Override
    public void onStopFollowing(Series unfollowedSeries) {}

    @Override
    public void onStopFollowingAll(Collection<Series> allUnfollowedSeries) {}

    @Override
    public void onFollowingFailure(Series series, Exception e) {
        notifyFollowingError(series, e);
    }

    // Update

    @Override
    public void onUpdateStart() {
        notifyUpdateStart();
    }

    @Override
    public void onUpdateNotNecessary() {}

    @Override
    public void onUpdateFailure(Exception e) {
        notifyUpdateError(e);
    }

    @Override
    public void onUpdateSuccess() {
        notifyUpdateSuccess();
    }

    // Backup and restore

    @Override
    public void onBackupSucess() {
        notifyBackupSuccess();
    }

    @Override
    public void onBackupFailure(Exception e) {
        notifyBackupFailure(e);
    }

    @Override
    public void onRestoreSucess() {
        notifyRestoreSuccess();
    }

    @Override
    public void onRestoreFailure(Exception e) {
        notifyRestoreFailure(e);
    }
}
