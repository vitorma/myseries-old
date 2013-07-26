package mobi.myseries.gui.episodes;

import java.text.DateFormat;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.image.EpisodeImageDownloadListener;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
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

public class EpisodeDetailsAdapter extends ArrayAdapter<Episode> {
    private static final SeriesProvider SERIES_PROVIDER = App.seriesProvider();
    private static final ImageService IMAGE_SERVICE = App.imageService();
    private static final Bitmap GENERIC_IMAGE = Images.genericEpisodeImageFrom(App.resources());
    private static final int ITEM_LAYOUT = R.layout.episode_pager_item;

    private final EpisodeImageDownloadListener downloadListener = new EpisodeImageDownloadListener() {

        @Override
        public void onStartDownloadingImageOf(Episode episode) {
            if (episode.equals(EpisodeDetailsAdapter.this.episode)) {
                EpisodeDetailsAdapter.this.startedLoadingImage();
            }
        }

        @Override
        public void onFinishDownloadingImageOf(Episode episode) {
            if (episode.equals(EpisodeDetailsAdapter.this.episode)) {
                EpisodeDetailsAdapter.this.loadEpisodeImage();
            }
        }
    };

    private EpisodeListener seenMarkListener = new EpisodeListener() {

        @Override
        public void onMarkAsSeenBySeason(Episode episode) {
            EpisodeDetailsAdapter.this.updateSeenCheckbox();
        }

        @Override
        public void onMarkAsSeen(Episode episode) {
            EpisodeDetailsAdapter.this.updateSeenCheckbox();
        }

        @Override
        public void onMarkAsNotSeenBySeason(Episode episode) {
            EpisodeDetailsAdapter.this.updateSeenCheckbox();
        }

        @Override
        public void onMarkAsNotSeen(Episode episode) {
            EpisodeDetailsAdapter.this.updateSeenCheckbox();
        }
    };

    private Episode episode;
    private TextView episodeName;
    private TextView episodeAirDate;
    private TextView episodeOverview;
    private TextView episodeGuestStars;
    private TextView episodeWriters;
    private TextView episodeDirectors;
    private Bitmap image;
    private ImageView imageView;
    private SeenMark isViewed;
    private ProgressBar progressSpinner;

    private LayoutInflater layoutInflater;

    public EpisodeDetailsAdapter(Context context, Episode e) {
        super(context, ITEM_LAYOUT, new Episode[] {e});

        this.layoutInflater = LayoutInflater.from(context);

        IMAGE_SERVICE.register(this.downloadListener);
        e.register(this.seenMarkListener);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = this.layoutInflater.inflate(ITEM_LAYOUT, null);
        }

        this.episodeAirDate = (TextView) itemView.findViewById(R.id.episodeFirstAiredTextView);
        this.episodeName = (TextView) itemView.findViewById(R.id.episodeName);
        this.episodeOverview = (TextView) itemView.findViewById(R.id.episodeOverviewTextView);
        this.episodeDirectors = (TextView) itemView.findViewById(R.id.episodeDirectorsTextView);
        this.episodeWriters = (TextView) itemView.findViewById(R.id.episodeWritersTextView);
        this.episodeGuestStars = (TextView) itemView.findViewById(R.id.episodeGuestStarsTextView);
        this.isViewed = (SeenMark) itemView.findViewById(R.id.isEpisodeViewedCheckBox);
        this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
        this.progressSpinner = (ProgressBar) itemView.findViewById(R.id.imageProgressSpinner);

        this.episode = this.getItem(position);

        DateFormat dateformat = android.text.format.DateFormat.getMediumDateFormat(App.context());
        String unavailable = LocalText.get(R.string.unavailable_date);
        String formattedDate = DatesAndTimes.toString(this.episode.airDate(), dateformat, unavailable);
        this.episodeAirDate.setText(formattedDate.toUpperCase());

        this.episodeName.setText(Objects.nullSafe(this.episode.name(), this.getContext().getString(R.string.to_be_announced)));

        if (this.episode.directors().trim().isEmpty()) {
            this.episodeDirectors.setVisibility(View.GONE);
            itemView.findViewById(R.id.episodeDirectorsLabel).setVisibility(View.GONE);
        } else {
            this.episodeDirectors.setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.episodeDirectorsLabel).setVisibility(View.VISIBLE);
            this.episodeDirectors.setText(this.episode.directors());
        }

        if (this.episode.writers().trim().isEmpty()) {
            this.episodeWriters.setVisibility(View.GONE);
            itemView.findViewById(R.id.episodeWritersLabel).setVisibility(View.GONE);
        } else {
            this.episodeWriters.setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.episodeWritersLabel).setVisibility(View.VISIBLE);
            this.episodeWriters.setText(this.episode.writers());
        }

        if (this.episode.guestStars().trim().isEmpty()) {
            this.episodeGuestStars.setVisibility(View.GONE);
            itemView.findViewById(R.id.episodeGuestStarsLabel).setVisibility(View.GONE);
        } else {
            this.episodeGuestStars.setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.episodeGuestStarsLabel).setVisibility(View.VISIBLE);
            this.episodeGuestStars.setText(this.episode.guestStars());
        }

        if (this.episode.overview().trim().isEmpty()) {
            this.episodeOverview.setVisibility(View.GONE);
            itemView.findViewById(R.id.episodeOverviewLabel).setVisibility(View.GONE);
        } else {
            this.episodeOverview.setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.episodeOverviewLabel).setVisibility(View.VISIBLE);
            this.episodeOverview.setText(this.episode.overview());
        }

        this.updateSeenCheckbox();

        this.loadEpisodeImage();

        if (this.image == null) {
            IMAGE_SERVICE.downloadImageOf(this.episode);
        }

        this.isViewed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EpisodeDetailsAdapter.this.isViewed.isChecked()) {
                    SERIES_PROVIDER.markEpisodeAsSeen(EpisodeDetailsAdapter.this.episode);
                } else {
                    SERIES_PROVIDER.markEpisodeAsNotSeen(EpisodeDetailsAdapter.this.episode);
                }
            }
        });

        return itemView;
    }

    private void startedLoadingImage() {
        this.progressSpinner.setVisibility(View.VISIBLE);
        this.imageView.setImageBitmap(null);
    }

    private void finishedLoadingImage() {
        this.progressSpinner.setVisibility(View.GONE);
    }

    private void loadEpisodeImage() {
        this.image = IMAGE_SERVICE.getImageOf(this.episode);

        this.imageView.setImageBitmap(Objects.nullSafe(this.image, GENERIC_IMAGE));
        this.finishedLoadingImage();
    }

    private void updateSeenCheckbox() {
        this.isViewed.setChecked(this.episode.wasSeen());
    }
}
