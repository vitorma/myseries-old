package mobi.myseries.gui.schedule;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Objects;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class EpisodeListAdapter extends ArrayAdapter<Episode> implements EpisodeListener {
    private static final int EPISODE_ITEM_RESOURCE_ID = R.layout.episode_alone_list_item;
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

    private LayoutInflater layoutInflater;

    public EpisodeListAdapter(Context context, List<Episode> objects) {
        super(context, EPISODE_ITEM_RESOURCE_ID, objects);

        this.layoutInflater = LayoutInflater.from(context);

        for (Episode e : objects) {
            e.register(this);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = this.itemViewFrom(convertView);

        Episode episode = this.getItem(position);
        Series series = SERIES_PROVIDER.getSeries(episode.seriesId());
        Season season = series.seasons().season(episode.seasonNumber());

        this.showData(episode, season, series, itemView);
        this.setUpSeenEpisodeCheckBoxListener(episode, itemView);

        return itemView;
    }

    private View itemViewFrom(View convertView) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = this.layoutInflater.inflate(EPISODE_ITEM_RESOURCE_ID, null);
        }

        return itemView;
    }

    private void showData(Episode episode, Season season, Series series, View itemView) {
        TextView nameTextView = (TextView) itemView.findViewById(R.id.episodeNameTextView);
        TextView seriesTextView = (TextView) itemView.findViewById(R.id.episodeSeriesTextView);
        TextView seasonTextView = (TextView) itemView.findViewById(R.id.episodeSeasonEpisodeTextView);
        TextView dateTextView = (TextView) itemView.findViewById(R.id.episodeDateTextView);
        CheckBox isViewedCheckBox = (CheckBox) itemView.findViewById(R.id.episodeIsViewedCheckBox);

        String safe_name = this.getContext().getString(R.string.unnamed_episode);
        nameTextView.setText(Objects.nullSafe(episode.name(), safe_name));

        seriesTextView.setText(series.name());

        String format = this.getContext().getString(R.string.season_and_episode_format);
        seasonTextView.setText(String.format(format, season.number(), episode.number()));

        java.text.DateFormat dateFormat = App.environment().localization().dateFormat();
        dateTextView.setText(Dates.toString(episode.airDate(), dateFormat, ""));

        isViewedCheckBox.setChecked(episode.wasSeen());
    }

    private void setUpSeenEpisodeCheckBoxListener(final Episode episode, View itemView) {
        final CheckBox isViewedCheckBox = (CheckBox) itemView.findViewById(R.id.episodeIsViewedCheckBox);

        isViewedCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isViewedCheckBox.isChecked()) {
                    SERIES_PROVIDER.markEpisodeAsSeen(episode);
                } else {
                    SERIES_PROVIDER.markEpisodeAsNotSeen(episode);
                }
            }
        });
    }

    @Override
    public void onMarkAsSeen(Episode e) {
        this.remove(e);
    }

    @Override
    public void onMarkAsNotSeen(Episode e) {
        //All episodes here are already marked as not seen
    }

    @Override
    public void onMerge(Episode e) {
        this.notifyDataSetChanged();
    }
}
