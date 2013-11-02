package mobi.myseries.gui.episodes;

import java.util.Locale;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.marking.MarkingListener;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.DateFormats;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.gui.shared.UniversalImageLoader;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Objects;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class EpisodeDetailsAdapter extends BaseAdapter {
    private Episode mEpisode;

    private TextView mAirDate;
    private TextView mAirDay;
    private TextView mAirTime;
    private TextView mTitle;
    private TextView mOverview;
    private SeenMark mWatchMark;
    private ImageView mScreen;
    private ProgressBar mProgress;

    public EpisodeDetailsAdapter(Episode episode) {
        mEpisode = episode;
    }

    /* BaseAdapter */

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return mEpisode;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) { itemView = View.inflate(App.context(), R.layout.episode_pager_item, null); }

        mAirDate = (TextView) itemView.findViewById(R.id.airDate);
        mAirDate.setText(DatesAndTimes.toString(
                mEpisode.airDate(),
                android.text.format.DateFormat.getDateFormat(App.context()),
                LocalText.get(R.string.unavailable_date)));

        mAirDay = (TextView) itemView.findViewById(R.id.airDay);
        mAirDay.setText(DatesAndTimes.toString(
                mEpisode.airDate(),
                DateFormats.forWeekDay(Locale.getDefault()),
                "").toUpperCase(Locale.getDefault()));

        mAirTime = (TextView) itemView.findViewById(R.id.airTime);
        mAirTime.setText(DatesAndTimes.toString(
                mEpisode.airTime(),
                android.text.format.DateFormat.getTimeFormat(App.context()),
                ""));

        mTitle = (TextView) itemView.findViewById(R.id.episodeTitle);
        mTitle.setText(Objects.nullSafe(
                mEpisode.title(),
                LocalText.get(R.string.to_be_announced)));

        mOverview = (TextView) itemView.findViewById(R.id.episodeOverview);
        mOverview.setText(mEpisode.overview());

        mWatchMark = (SeenMark) itemView.findViewById(R.id.watchMark);
        mWatchMark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWatchMark.isChecked()) {
                    App.markingService().markAsWatched(mEpisode);
                } else {
                    App.markingService().markAsUnwatched(mEpisode);
                }
            }
        });
        updateWatchMark();

        mScreen = (ImageView) itemView.findViewById(R.id.imageView);
        mProgress = (ProgressBar) itemView.findViewById(R.id.imageProgressSpinner);
        String screenUrl = mEpisode.screenUrl();
        String screenPath = null;
        if(screenUrl.isEmpty()) {
            screenPath = UniversalImageLoader.drawableURI(R.drawable.generic_episode_image);
        } else {
            screenPath = UniversalImageLoader.httpURI(screenUrl);
        }

        UniversalImageLoader.loader().displayImage(screenPath,
                mScreen,
                UniversalImageLoader.defaultDisplayBuilder()
                .showImageOnFail(R.drawable.generic_episode_image)
                .build(),
                new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view,
                    Bitmap loadedImage) {
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                    FailReason failReason) {
                mProgress.setVisibility(View.GONE);
            }
        });

        return itemView;
    }

    /* MarkingListener */

    public void registerServiceListeners() {
        App.markingService().register(mMarkingListener);
    }

    public void deregisterServiceListeners() {
        App.markingService().deregister(mMarkingListener);
    }

    private final MarkingListener mMarkingListener = new MarkingListener() {
        @Override
        public void onMarked(Episode e) {
            if (e.id() != mEpisode.id()) { return; }

            updateWatchMark();
        }

        @Override
        public void onMarked(Season s) {
            if (s.number() != mEpisode.seasonNumber()) { return; }

            updateWatchMark();
        }

        @Override
        public void onMarked(Series s) {
            if (s.id() != mEpisode.seriesId()) { return; }

            updateWatchMark();
        }
    };

    /* Auxiliary */

    private void updateWatchMark() {
        mWatchMark.setChecked(mEpisode.watched());
    }
}
