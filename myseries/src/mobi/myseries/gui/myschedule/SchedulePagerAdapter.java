package mobi.myseries.gui.myschedule;

import java.util.Locale;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.image.EpisodeImageDownloadListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.DateFormats;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.SeenMark;
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

public class SchedulePagerAdapter extends PagerAdapter {
    private static final Bitmap GENERIC_IMAGE = Images.genericEpisodeImageFrom(App.resources());

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
                series.airtime(), DateFormats.forWeekDay(Locale.getDefault()), "");
        airDay.setText(formattedAirDay.toUpperCase());

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

        Bitmap image = App.imageService().getImageOf(episode);
        if (image == null) {
            App.imageService().register(new EpisodeImageDownloadListener() {
                @Override
                public void onStartDownloadingImageOf(Episode e) {
                    if (e.equals(episode)) {
                        screenLoadingProgress.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFinishDownloadingImageOf(Episode e) {
                    if (e.equals(episode)) {
                        Bitmap image = App.imageService().getImageOf(episode);
                        screen.setImageBitmap(Objects.nullSafe(image, GENERIC_IMAGE));
                        screenLoadingProgress.setVisibility(View.GONE);
                    }
                }
            });

            App.imageService().downloadImageOf(episode);
        } else {
            screen.setImageBitmap(image);
            screenLoadingProgress.setVisibility(View.GONE);
        }

        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}