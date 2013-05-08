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

public class EpisodeAdapter extends ArrayAdapter<Episode> {
    private static final SeriesProvider SERIES_PROVIDER = App.seriesProvider();
    private static final ImageService IMAGE_SERVICE = App.imageService();
    private static final Bitmap GENERIC_IMAGE = Images.genericEpisodeImageFrom(App.resources());
    private static final int ITEM_LAYOUT = R.layout.episodes_item;

    private final EpisodeImageDownloadListener downloadListener = new EpisodeImageDownloadListener() {

        @Override
        public void onStartDownloadingImageOf(Episode episode) {
            if (episode.equals(EpisodeAdapter.this.episode)) {
                EpisodeAdapter.this.startedLoadingImage();
            }
        }

        @Override
        public void onFinishDownloadingImageOf(Episode episode) {
            if (episode.equals(EpisodeAdapter.this.episode)) {
                EpisodeAdapter.this.loadEpisodeImage();
            }
        }
    };

    private EpisodeListener seenMarkListener = new EpisodeListener() {

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
    private SeenMark isViewed;
    private ProgressBar progressSpinner;

    private LayoutInflater layoutInflater;

    public EpisodeAdapter(Context context, Episode e) {
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

        // The episode name TextBox is the one that belongs to the seen mark CheckBox
        this.episodeFirstAired = (TextView) itemView.findViewById(R.id.episodeFirstAiredTextView);
        this.episodeName = (TextView) itemView.findViewById(R.id.episodeName);
        this.episodeOverview = (TextView) itemView.findViewById(R.id.episodeOverviewTextView);
        this.episodeDirector = (TextView) itemView.findViewById(R.id.episodeDirectorsTextView);
        this.episodeWriter = (TextView) itemView.findViewById(R.id.episodeWritersTextView);
        this.episodeGuestStars = (TextView) itemView.findViewById(R.id.episodeGuestStarsTextView);
        this.isViewed = (SeenMark) itemView.findViewById(R.id.isEpisodeViewedCheckBox);
        this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
        this.progressSpinner = (ProgressBar) itemView.findViewById(R.id.imageProgressSpinner);

        this.episode = this.getItem(position);

        DateFormat dateformat = android.text.format.DateFormat.getDateFormat(App.context());
        String unavailable = LocalText.get(R.string.unavailable_date);
        String formattedDate = DatesAndTimes.toString(this.episode.airDate(), dateformat, unavailable);
        this.episodeFirstAired.setText(formattedDate);

        this.episodeName.setText(Objects.nullSafe(this.episode.name(), this.getContext().getString(R.string.to_be_announced)));
        this.episodeDirector.setText(this.episode.directors());
        this.episodeWriter.setText(this.episode.writers());
        this.episodeGuestStars.setText(this.episode.guestStars());
        this.episodeOverview.setText(this.episode.overview());
        this.updateSeenCheckbox();

        this.loadEpisodeImage();

        if (this.image == null) {
            IMAGE_SERVICE.downloadImageOf(this.episode);
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
        this.image = IMAGE_SERVICE.getImageOf(this.episode);

        this.imageView.setImageBitmap(Objects.nullSafe(this.image, GENERIC_IMAGE));
        this.finishedLoadingImage();
    }

    private void updateSeenCheckbox() {
        this.isViewed.setChecked(this.episode.wasSeen());
    }
}
