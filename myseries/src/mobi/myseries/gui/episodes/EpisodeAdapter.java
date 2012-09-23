package mobi.myseries.gui.episodes;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.image.EpisodeImageDownloadListener;
import mobi.myseries.application.image.ImageProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Objects;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EpisodeAdapter extends ArrayAdapter<Episode> {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final ImageProvider IMAGE_PROVIDER = App.imageProvider();

    private static final Resources RESOURCES = App.environment().context().getResources();
    private static final Bitmap GENERIC_IMAGE = BitmapFactory.decodeResource(RESOURCES, R.drawable.clapperboard);
    private static final int ITEM_LAYOUT = R.layout.episodes_item;

    private final EpisodeImageDownloadListener downloadListener = new EpisodeImageDownloadListener() {

        @Override
        public void onStartDownloadingImageOf(Episode episode) {
            if (episode.equals(EpisodeAdapter.this.episode)) {
                EpisodeAdapter.this.startedLoadingImage();
            }
        }

        @Override
        public void onDownloadImageOf(Episode episode) {
            if (episode.equals(EpisodeAdapter.this.episode)) {
                EpisodeAdapter.this.loadEpisodeImage();
            }
        }

        @Override
        public void onFailureWhileSavingImageOf(Episode episode) {
            if (episode.equals(EpisodeAdapter.this.episode)) {
                EpisodeAdapter.this.setUpForUnavailableImage();
            }
        }

        @Override
        public void onConnectionFailureWhileDownloadingImageOf(Episode episode) {
            if (episode.equals(EpisodeAdapter.this.episode)) {
                EpisodeAdapter.this.setUpForUnavailableImage();
            }
        }
    };

    private EpisodeListener seenMarkListener = new EpisodeListener() {

        @Override
        @Deprecated
        public void onMerge(Episode episode) {}  // TODO remove this deprecated method

        @Override
        public void onMarkAsSeenBySeason(Episode episode) {
            EpisodeAdapter.this.updateSeenCheckbox();
        }

        @Override
        public void onMarkAsSeen(Episode episode) {
            EpisodeAdapter.this.updateSeenCheckbox();
        }

        @Override
        public void onMarkAsNotSeenBySeason(Episode episode) {
            EpisodeAdapter.this.updateSeenCheckbox();
        }

        @Override
        public void onMarkAsNotSeen(Episode episode) {
            EpisodeAdapter.this.updateSeenCheckbox();
        }
    };

    private Episode episode;
    private TextView episodeDirector;
    private TextView episodeFirstAired;
    private TextView episodeGuestStars;
    private TextView episodeName;
    private TextView episodeOverview;
    private TextView episodeWriter;
    private Bitmap image;
    private ImageView imageView;
    private CheckBox isViewed;
    private ProgressBar progressSpinner;

    private LayoutInflater layoutInflater;

    public EpisodeAdapter(Context context, Episode e) {
        super(context, ITEM_LAYOUT, new Episode[] {e});

        this.layoutInflater = LayoutInflater.from(context);

        IMAGE_PROVIDER.register(this.downloadListener);
        e.register(this.seenMarkListener);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = this.layoutInflater.inflate(ITEM_LAYOUT, null);
        }

        // The episode name TextBox is the one that belongs to the seen mark CheckBox
        this.episodeName = (TextView) itemView.findViewById(R.id.isEpisodeViewedCheckBox);
        this.episodeFirstAired = (TextView) itemView.findViewById(R.id.episodeFirstAiredTextView);
        this.episodeOverview = (TextView) itemView.findViewById(R.id.episodeOverviewTextView);
        this.episodeDirector = (TextView) itemView.findViewById(R.id.episodeDirectorsTextView);
        this.episodeWriter = (TextView) itemView.findViewById(R.id.episodeWritersTextView);
        this.episodeGuestStars = (TextView) itemView.findViewById(R.id.episodeGuestStarsTextView);
        this.isViewed = (CheckBox) itemView.findViewById(R.id.isEpisodeViewedCheckBox);
        this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
        this.progressSpinner = (ProgressBar) itemView.findViewById(R.id.imageProgressSpinner);

        this.episode = this.getItem(position);

        this.episodeName.setText(Objects.nullSafe(this.episode.name(), this.getContext().getString(R.string.to_be_announced)));
        this.episodeFirstAired.setText(Dates.toString(this.episode.airDate(), App.environment().localization().dateFormat(), ""));
        this.episodeDirector.setText(this.episode.directors());
        this.episodeWriter.setText(this.episode.writers());
        this.episodeGuestStars.setText(this.episode.guestStars());
        this.episodeOverview.setText(this.episode.overview());
        this.updateSeenCheckbox();

        this.loadEpisodeImage();

        if (this.image == null) {
            IMAGE_PROVIDER.downloadImageOf(this.episode);
        }

        this.isViewed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EpisodeAdapter.this.isViewed.isChecked()) {
                    SERIES_PROVIDER.markEpisodeAsSeen(EpisodeAdapter.this.episode);
                } else {
                    SERIES_PROVIDER.markEpisodeAsNotSeen(EpisodeAdapter.this.episode);
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
        this.image = IMAGE_PROVIDER.getImageOf(this.episode);

        if (this.image != null) {
            this.setUpForAvailableImage();
        } else {
            this.setUpForUnavailableImage();
        }
    }

    private void setUpForAvailableImage() {
        this.imageView.setImageBitmap(this.image);
        this.finishedLoadingImage();
    }

    private void setUpForUnavailableImage() {
        this.imageView.setImageBitmap(GENERIC_IMAGE);
        this.finishedLoadingImage();
    }

    private void updateSeenCheckbox() {
        this.isViewed.setChecked(this.episode.wasSeen());
    }
}
