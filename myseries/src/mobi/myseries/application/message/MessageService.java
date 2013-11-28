package mobi.myseries.application.message;

import java.util.Collection;

import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.application.backup.BackupService;
import mobi.myseries.application.following.BaseSeriesFollowingListener;
import mobi.myseries.application.following.SeriesFollowingListener;
import mobi.myseries.application.following.SeriesFollowingService;
import mobi.myseries.application.update.BaseUpdateListener;
import mobi.myseries.application.update.UpdateListener;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;

public class MessageService implements Publisher<MessageServiceListener> {

    private ListenerSet<MessageServiceListener> listeners;

    public MessageService(
            SeriesFollowingService seriesFollowingService,
            UpdateService updateService,
            BackupService backupService) {

        this.listeners = new ListenerSet<MessageServiceListener>();

        seriesFollowingService.register(mSeriesFollowingListener);
        updateService.register(mUpdateListener);
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
        public void onFailToFollow(SearchResult series, Exception e) {
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

    private UpdateListener mUpdateListener = new BaseUpdateListener() {
        @Override
        public void onUpdateSuccess() {
            notifyUpdateSuccess();
        }
    };

    private void notifyUpdateSuccess() {
        for (MessageServiceListener l : this.listeners) {
            l.onUpdateSuccess();
        }
    }

}
