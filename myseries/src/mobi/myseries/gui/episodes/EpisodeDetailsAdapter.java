package mobi.myseries.gui.episodes;

import java.util.Locale;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

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
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//TODO (Cleber) extend BaseAdapter instead of ArrayAdapter
public class EpisodeDetailsAdapter extends ArrayAdapter<Episode> {
    private Episode mEpisode;
    private LayoutInflater mLayoutInflater;

    private TextView mAirDate;
    private TextView mAirDay;
    private TextView mAirTime;
    private TextView mTitle;
    private TextView mOverview;
    private SeenMark mWatchMark;

    private ImageView mScreen;
    private DisplayImageOptions mDisplayImageOptions;
 
    public EpisodeDetailsAdapter(Context context, Episode episode) {
        super(context, R.layout.episode_pager_item, new Episode[] {episode});

        mEpisode = episode;
        mLayoutInflater = LayoutInflater.from(context);

        App.markingService().register(mMarkingListener);
 
        mDisplayImageOptions = imageLoaderOptions();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) { itemView = mLayoutInflater.inflate(R.layout.episode_pager_item, null); }

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
        updateWatchMark();
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

        mScreen = (ImageView) itemView.findViewById(R.id.imageView);
        UniversalImageLoader.loader().displayImage(mEpisode.screenUrl(), mScreen, mDisplayImageOptions);

        return itemView;
    }

    private void updateWatchMark() {
        this.mWatchMark.setChecked(this.mEpisode.watched());
    }

    /* MarkingListener */

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
    
    private DisplayImageOptions imageLoaderOptions() {
        return new DisplayImageOptions.Builder()
        .cacheOnDisc(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
        .resetViewBeforeLoading(true)
        .showImageOnFail(R.drawable.generic_episode_image)
        .build();
    }
}
