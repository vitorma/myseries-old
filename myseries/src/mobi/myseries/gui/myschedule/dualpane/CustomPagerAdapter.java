package mobi.myseries.gui.myschedule.dualpane;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.image.EpisodeImageDownloadListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
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

public class CustomPagerAdapter extends PagerAdapter {
    private static final Bitmap GENERIC_IMAGE = Images.genericEpisodeImageFrom(App.resources());

    private ScheduleMode mItems;
    private LayoutInflater mInflater;

    public CustomPagerAdapter(ScheduleMode items) {
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
                android.text.format.DateFormat.getMediumDateFormat(App.context()),
                LocalText.get(R.string.unavailable_date));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mInflater.inflate(R.layout.myschedule_detail_item, null);

        TextView number = (TextView) view.findViewById(R.id.episodeFirstAiredTextView);
        TextView title = (TextView) view.findViewById(R.id.episodeName);
        TextView overview = (TextView) view.findViewById(R.id.episodeOverviewTextView);
        TextView directors = (TextView) view.findViewById(R.id.episodeDirectorsTextView);
        TextView writers = (TextView) view.findViewById(R.id.episodeWritersTextView);
        TextView guestStars = (TextView) view.findViewById(R.id.episodeGuestStarsTextView);
        final SeenMark watchMark = (SeenMark) view.findViewById(R.id.isEpisodeViewedCheckBox);
        final ImageView screen = (ImageView) view.findViewById(R.id.imageView);
        final ProgressBar screenLoadingProgress = (ProgressBar) view.findViewById(R.id.imageProgressSpinner);

        final Episode episode = mItems.episodeAt(position);

        String numberFormat = App.resources().getString(R.string.episode_number_format_ext);
        number.setText(String.format(numberFormat, episode.number()));

        title.setText(Objects.nullSafe(episode.title(), App.resources().getString(R.string.to_be_announced)));

        if (episode.directors().trim().isEmpty()) {
            directors.setVisibility(View.GONE);
            view.findViewById(R.id.episodeDirectorsLabel).setVisibility(View.GONE);
        } else {
            directors.setVisibility(View.VISIBLE);
            view.findViewById(R.id.episodeDirectorsLabel).setVisibility(View.VISIBLE);
            directors.setText(episode.directors());
        }

        if (episode.writers().trim().isEmpty()) {
            writers.setVisibility(View.GONE);
            view.findViewById(R.id.episodeWritersLabel).setVisibility(View.GONE);
        } else {
            writers.setVisibility(View.VISIBLE);
            view.findViewById(R.id.episodeWritersLabel).setVisibility(View.VISIBLE);
            writers.setText(episode.writers());
        }

        if (episode.guestStars().trim().isEmpty()) {
            guestStars.setVisibility(View.GONE);
            view.findViewById(R.id.episodeGuestStarsLabel).setVisibility(View.GONE);
        } else {
            guestStars.setVisibility(View.VISIBLE);
            view.findViewById(R.id.episodeGuestStarsLabel).setVisibility(View.VISIBLE);
            guestStars.setText(episode.guestStars());
        }

        if (episode.overview().trim().isEmpty()) {
            overview.setVisibility(View.GONE);
            view.findViewById(R.id.episodeOverviewLabel).setVisibility(View.GONE);
        } else {
            overview.setVisibility(View.VISIBLE);
            view.findViewById(R.id.episodeOverviewLabel).setVisibility(View.VISIBLE);
            overview.setText(episode.overview());
        }

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
