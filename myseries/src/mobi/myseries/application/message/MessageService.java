package mobi.myseries.application.message;

import java.util.Collection;

import mobi.myseries.application.App;
import mobi.myseries.application.follow.SeriesFollowingListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.update.UpdateListener;

public class MessageService implements SeriesFollowingListener, UpdateListener{

    private ListenerSet<MessageServiceListener> listeners;

    public MessageService() {
        this.listeners = new ListenerSet<MessageServiceListener>();
        App.followSeriesService().registerSeriesFollowingListener(this);
        App.updateSeriesService().register(this);
    }

    public boolean registerListener(MessageServiceListener l) {
        return this.listeners.register(l);
    }

    public boolean deregisterListener(MessageServiceListener l) {
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

    public void notifyUpdateError(Exception e) {
        for (MessageServiceListener l : listeners) {
            l.onUpdateError(e);
        }
    }

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

    @Override
    public void onFollowingFailure(Series series, Exception e) {
        notifyFollowingError(series, e);
    }

}
