package mobi.myseries.gui.detail;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.EpisodeImageDownloadListener;
import mobi.myseries.application.ImageProvider;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Objects;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EpisodeAdapter extends ArrayAdapter<Episode> implements EpisodeImageDownloadListener, EpisodeListener {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final ImageProvider IMAGE_PROVIDER = App.environment().imageProvider();
    private static final int ITEM_LAYOUT = R.layout.episode;

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
    private ProgressBar progressBar;

    private LayoutInflater layoutInflater;

    public EpisodeAdapter(Context context, Episode e) {
        super(context, ITEM_LAYOUT, new Episode[] {e});

        this.layoutInflater = LayoutInflater.from(context);

        IMAGE_PROVIDER.register(this);
        e.register(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = this.layoutInflater.inflate(ITEM_LAYOUT, null);
        }

        this.episodeName = (TextView) itemView.findViewById(R.id.episodeNameTextView);
        this.episodeFirstAired = (TextView) itemView.findViewById(R.id.episodeFirstAiredTextView);
        this.episodeOverview = (TextView) itemView.findViewById(R.id.episodeOverviewTextView);
        this.episodeDirector = (TextView) itemView.findViewById(R.id.episodeDirectorTextView);
        this.episodeWriter = (TextView) itemView.findViewById(R.id.episodeWriterTextView);
        this.episodeGuestStars = (TextView) itemView.findViewById(R.id.episodeGuestStarsTextView);
        this.isViewed = (CheckBox) itemView.findViewById(R.id.isEpisodeViewedCheckBox);
        this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
        this.progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);

        this.episode = this.getItem(position);

        this.episodeName.setText(Objects.nullSafe(episode.name(), this.getContext().getString(R.string.unnamed_episode)));
        this.episodeFirstAired.setText(Dates.toString(episode.airDate(), App.environment().localization().dateFormat(), ""));
        this.episodeDirector.setText(episode.directors());
        this.episodeWriter.setText(episode.writers());
        this.episodeGuestStars.setText(episode.guestStars());
        this.episodeOverview.setText(episode.overview());
        this.isViewed.setChecked(episode.wasSeen());

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

    @Override
    public void onConnectionFailureWhileDownloadingImageOf(Episode episode) {
        if (episode.equals(this.episode)) {
            this.setupForUnavailableImage();
        }
    }

    @Override
    public void onDownloadImageOf(Episode episode) {
        if (episode.equals(this.episode)) {
            this.loadEpisodeImage();
            this.setupForLoadedImage();
        }
    }

    @Override
    public void onFailureWhileSavingImageOf(Episode episode) {
        if (episode.equals(this.episode)) {
            this.setupForUnavailableImage();
        }
    }

    @Override
    public void onStartDownloadingImageOf(Episode episode) {
        if (episode.equals(this.episode)) {
            this.setupForLoadingImage();
        }
    }

    private void loadEpisodeImage() {
        this.image = IMAGE_PROVIDER.getImageOf(episode);

        this.imageView.setImageBitmap(this.image);

        if (this.image != null) {
            this.setupForLoadedImage();
        } else {
            this.setupForUnavailableImage();
        }
    }

    private void setupForLoadedImage() {
        this.progressBar.setVisibility(View.GONE);
        this.imageView.setVisibility(View.VISIBLE);
    }

    private void setupForLoadingImage() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.imageView.setVisibility(View.GONE);
    }

    private void setupForUnavailableImage() {
        this.progressBar.setVisibility(View.GONE);
        this.imageView.setVisibility(View.GONE);
    }

    @Override
    public void onMarkAsSeen(Episode episode) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onMarkAsNotSeen(Episode episode) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onMerge(Episode episode) {
        //TODO This method should be removed from the interface EpisodeListener
    }
}
