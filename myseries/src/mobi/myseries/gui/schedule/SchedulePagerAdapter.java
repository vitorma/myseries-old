package mobi.myseries.gui.schedule;

import java.util.Locale;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.DateFormats;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.gui.shared.UniversalImageLoader;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Objects;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class SchedulePagerAdapter extends PagerAdapter {

    private ScheduleMode mItems;
    private LayoutInflater mInflater;

    public SchedulePagerAdapter(ScheduleMode items) {
        mItems = items;
        mInflater = LayoutInflater.from(App.context());
    }

    @Override
    public int getCount() {
        return mItems.numberOfEpisodes();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle (int position) {
        Episode e = mItems.episodeAt(position);

        return DatesAndTimes.toString(
                e.airDate(),
                android.text.format.DateFormat.getDateFormat(App.context()),
                LocalText.get(R.string.unavailable_date));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mInflater.inflate(R.layout.myschedule_item_detail, null);

        TextView airDay = (TextView) view.findViewById(R.id.airDay);
        TextView airTime = (TextView) view.findViewById(R.id.airTime);
        TextView seriesTitle = (TextView) view.findViewById(R.id.seriesTitle);
        TextView episodeNumber = (TextView) view.findViewById(R.id.episodeNumber);
        TextView episodeTitle = (TextView) view.findViewById(R.id.episodeTitle);
        TextView episodeOverview = (TextView) view.findViewById(R.id.episodeOverview);
        final SeenMark watchMark = (SeenMark) view.findViewById(R.id.watchMark);
        final ImageView screen = (ImageView) view.findViewById(R.id.imageView);
        final ProgressBar screenLoadingProgress = (ProgressBar) view.findViewById(R.id.imageProgressSpinner);

        final Episode episode = mItems.episodeAt(position);
        final Series series = App.seriesFollowingService().getFollowedSeries(episode.seriesId());

        String formattedAirDay = DatesAndTimes.toString(
                episode.airDate(), DateFormats.forWeekDay(Locale.getDefault()), "");
        airDay.setText(formattedAirDay.toUpperCase(Locale.getDefault()));

        String formattedAirTime = DatesAndTimes.toString(
                episode.airTime(), android.text.format.DateFormat.getTimeFormat(App.context()), "");
        airTime.setText(formattedAirTime);

        seriesTitle.setText(series.name());

        String formattedNumber = episode.isSpecial() ?
                App.resources().getString(R.string.number_format_episode_full_special, episode.number()):
                App.resources().getString(R.string.number_format_episode_full, episode.seasonNumber(), episode.number());
        episodeNumber.setText(formattedNumber);

        episodeTitle.setText(Objects.nullSafe(episode.title(), App.resources().getString(R.string.to_be_announced)));
        episodeOverview.setText(episode.overview());

        watchMark.setChecked(episode.watched());
        watchMark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (watchMark.isChecked()) {
                    App.markingService().markAsWatched(episode);
                } else {
                    App.markingService().markAsUnwatched(episode);
                }
            }
        });
        String screenUrl = episode.screenUrl();
        String screenPath = null;

        if(screenUrl.isEmpty()) {
            screenPath = UniversalImageLoader.drawableURI(R.drawable.generic_episode_image);
        } else {
            screenPath = UniversalImageLoader.httpURI(episode.screenUrl());
        }

        UniversalImageLoader.loader().displayImage(screenPath, 
                screen, 
                UniversalImageLoader.defaultDisplayBuilder()
                .showImageOnFail(R.drawable.generic_episode_image)
                .build(), 
                new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                screenLoadingProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view,
                    Bitmap loadedImage) {
                screenLoadingProgress.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                    FailReason failReason) {
                screenLoadingProgress.setVisibility(View.GONE);
            }
        });
        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
