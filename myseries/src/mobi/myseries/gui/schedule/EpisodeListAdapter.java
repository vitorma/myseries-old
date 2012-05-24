package mobi.myseries.gui.schedule;

import java.util.Comparator;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesFollowingListener;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Validate;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class EpisodeListAdapter extends ArrayAdapter<Episode> implements EpisodeListener {
    private static final int EPISODE_ITEM_RESOURCE_ID = R.layout.myschedule_item;
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

    private LayoutInflater layoutInflater;

    private EpisodeListFactory episodeListFactory;

    /**
     * Necessary because it must there be a strong reference to the listener so it cannot be collected.
     */
    private SeriesFollowingListener seriesFollowingListener = new SeriesFollowingListener() {

        @Override
        public void onFollowing(Series followedSeries) {
            EpisodeListAdapter.this.reloadData();
        }

        @Override
        public void onStopFollowing(Series unfollowedSeries) {
            // Remove all the unfollowedSeries' episodes from the adapter
            for (Episode e : unfollowedSeries.episodes()) {
                EpisodeListAdapter.this.remove(e);
            }
        }
    };

    public EpisodeListAdapter(Context context,
                              EpisodeListFactory episodeListFactory) {
        super(context, EPISODE_ITEM_RESOURCE_ID);
        Validate.isNonNull(episodeListFactory, "episodeListFactory");

        this.episodeListFactory = episodeListFactory;
        this.layoutInflater = LayoutInflater.from(context);

        App.registerSeriesFollowingListener(this.seriesFollowingListener);

        this.reloadData();
    }

    private List<Episode> episodes() {
        return this.episodeListFactory.episodes();
    }

    private Comparator<Episode> episodesComparator() {
        return this.episodeListFactory.episodesComparator();
    }

    private void reloadData() {
        this.clear();

        List<Episode> episodes = this.episodes();

        for (Episode e : episodes) {
            e.register(this);
            this.add(e);
        }

        this.sort(this.episodesComparator());
        this.notifyDataSetChanged();
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
        TextView seriesTextView = (TextView) itemView.findViewById(R.id.episodeSeriesTextView);
        TextView seasonTextView = (TextView) itemView.findViewById(R.id.episodeSeasonEpisodeTextView);
        TextView dateTextView = (TextView) itemView.findViewById(R.id.episodeDateTextView);
        CheckBox isViewedCheckBox = (CheckBox) itemView.findViewById(R.id.episodeIsViewedCheckBox);

        seriesTextView.setText(series.name());

        String format = this.getContext().getString(R.string.season_and_episode_format_short);
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
        this.reloadData();
    }

    @Override
    public void onMerge(Episode e) {
        this.notifyDataSetChanged();
    }
}
