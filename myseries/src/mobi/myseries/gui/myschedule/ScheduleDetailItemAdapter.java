package mobi.myseries.gui.myschedule;

import java.text.DateFormat;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.image.EpisodeImageDownloadListener;
import mobi.myseries.application.marking.MarkingListener;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.SeenMark;
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
import android.widget.ProgressBar;
import android.widget.TextView;

public class ScheduleDetailItemAdapter extends ArrayAdapter<Episode> {
    private static final int ITEM_LAYOUT = R.layout.myschedule_detail_item;
    private static final Bitmap GENERIC_IMAGE = Images.genericEpisodeImageFrom(App.resources());

    private Episode mEpisode;
    private TextView mEpisodeTitle;
    private TextView mEpisodeAirDate;
    private TextView mEpisodeOverview;
    private TextView mEpisodeGuestStars;
    private TextView mEpisodeWriters;
    private TextView mEpisodeDirectors;
    private Bitmap mImage;
    private ImageView mEpisodeScreen;
    private ProgressBar mScreenLoadingProgress;
    private SeenMark mEpisodeWatchMark;

    private LayoutInflater mLayoutInflater;

    public ScheduleDetailItemAdapter(Context context, Episode e) {
        super(context, ITEM_LAYOUT, new Episode[] {e});

        mLayoutInflater = LayoutInflater.from(context);

        App.imageService().register(mScreenDownloadListener);
        App.markingService().register(mMarkingListener);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = mLayoutInflater.inflate(ITEM_LAYOUT, null);
        }

        mEpisodeAirDate = (TextView) itemView.findViewById(R.id.episodeFirstAiredTextView);
        mEpisodeTitle = (TextView) itemView.findViewById(R.id.episodeName);
        mEpisodeOverview = (TextView) itemView.findViewById(R.id.episodeOverviewTextView);
        mEpisodeDirectors = (TextView) itemView.findViewById(R.id.episodeDirectorsTextView);
        mEpisodeWriters = (TextView) itemView.findViewById(R.id.episodeWritersTextView);
        mEpisodeGuestStars = (TextView) itemView.findViewById(R.id.episodeGuestStarsTextView);
        mEpisodeWatchMark = (SeenMark) itemView.findViewById(R.id.isEpisodeViewedCheckBox);
        mEpisodeScreen = (ImageView) itemView.findViewById(R.id.imageView);
        mScreenLoadingProgress = (ProgressBar) itemView.findViewById(R.id.imageProgressSpinner);

        mEpisode = this.getItem(position);

        DateFormat dateformat = android.text.format.DateFormat.getMediumDateFormat(App.context());
        String unavailable = LocalText.get(R.string.unavailable_date);
        String formattedDate = DatesAndTimes.toString(mEpisode.airDate(), dateformat, unavailable);
        mEpisodeAirDate.setText(formattedDate.toUpperCase());

        mEpisodeTitle.setText(Objects.nullSafe(this.mEpisode.title(), this.getContext().getString(R.string.to_be_announced)));

        if (mEpisode.directors().trim().isEmpty()) {
            mEpisodeDirectors.setVisibility(View.GONE);
            itemView.findViewById(R.id.episodeDirectorsLabel).setVisibility(View.GONE);
        } else {
            mEpisodeDirectors.setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.episodeDirectorsLabel).setVisibility(View.VISIBLE);
            mEpisodeDirectors.setText(mEpisode.directors());
        }

        if (mEpisode.writers().trim().isEmpty()) {
            mEpisodeWriters.setVisibility(View.GONE);
            itemView.findViewById(R.id.episodeWritersLabel).setVisibility(View.GONE);
        } else {
            mEpisodeWriters.setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.episodeWritersLabel).setVisibility(View.VISIBLE);
            mEpisodeWriters.setText(mEpisode.writers());
        }

        if (mEpisode.guestStars().trim().isEmpty()) {
            mEpisodeGuestStars.setVisibility(View.GONE);
            itemView.findViewById(R.id.episodeGuestStarsLabel).setVisibility(View.GONE);
        } else {
            mEpisodeGuestStars.setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.episodeGuestStarsLabel).setVisibility(View.VISIBLE);
            mEpisodeGuestStars.setText(this.mEpisode.guestStars());
        }

        if (mEpisode.overview().trim().isEmpty()) {
            mEpisodeOverview.setVisibility(View.GONE);
            itemView.findViewById(R.id.episodeOverviewLabel).setVisibility(View.GONE);
        } else {
            mEpisodeOverview.setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.episodeOverviewLabel).setVisibility(View.VISIBLE);
            mEpisodeOverview.setText(mEpisode.overview());
        }

        this.updateWatchMark();

        this.loadEpisodeImage();

        if (mImage == null) {
            App.imageService().downloadImageOf(mEpisode);
        }

        mEpisodeWatchMark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEpisodeWatchMark.isChecked()) {
                    App.markingService().markAsWatched(mEpisode);
                } else {
                    App.markingService().markAsUnwatched(mEpisode);
                }
            }
        });

        return itemView;
    }

    private void loadEpisodeImage() {
        mImage = App.imageService().getImageOf(mEpisode);

        mEpisodeScreen.setImageBitmap(Objects.nullSafe(mImage, GENERIC_IMAGE));
        finishedLoadingImage();
    }

    private void startedLoadingImage() {
        mScreenLoadingProgress.setVisibility(View.VISIBLE);
        mEpisodeScreen.setImageBitmap(null);
    }

    private void finishedLoadingImage() {
        mScreenLoadingProgress.setVisibility(View.GONE);
    }

    private void updateWatchMark() {
        mEpisodeWatchMark.setChecked(mEpisode.watched());
    }

    private final MarkingListener mMarkingListener = new MarkingListener() {
        @Override
        public void onMarked(Episode e) {
            if (e.id() == mEpisode.id()) {
                updateWatchMark();
            }
        }

        @Override
        public void onMarked(Season s) {
            if (s.number() == mEpisode.seasonNumber()) {
                updateWatchMark();
            }
        }

        @Override
        public void onMarked(Series s) {
            if (s.id() == mEpisode.seriesId()) {
                updateWatchMark();
            }
        }
    };

    private final EpisodeImageDownloadListener mScreenDownloadListener = new EpisodeImageDownloadListener() {
        @Override
        public void onStartDownloadingImageOf(Episode episode) {
            if (episode.equals(mEpisode)) {
                startedLoadingImage();
            }
        }

        @Override
        public void onFinishDownloadingImageOf(Episode episode) {
            if (episode.equals(mEpisode)) {
                loadEpisodeImage();
            }
        }
    };
}
