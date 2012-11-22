package mobi.myseries.gui.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.SeasonListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.SeenEpisodesBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SeasonsExpandableAdapter extends BaseExpandableListAdapter implements SeasonListener, EpisodeListener {
    private static final SeriesProvider SERIES_PROVIDER = App.seriesProvider();

    private Context context;
    private Series series;

    public SeasonsExpandableAdapter(Context context, Series series) {
        this.context = context;
        this.series = series;

        for (Season s : series.seasons().seasons()) {
            s.register(this);
        }

        for (Episode e : series.episodes()) {
            e.register(this);
        }
    }

    private int descendingPosition(int position, int numberOfPositions) {
        return numberOfPositions - 1 - position;
    }

    public Season season(int groupPosition) {
        int numberOfSeasons = this.series.seasons().numberOfSeasons();
        return this.series.seasons().seasonAt(this.descendingPosition(groupPosition, numberOfSeasons));
    }

    public Episode episode(int groupPosition, int childPosition) {
        Season season = this.season(groupPosition);
        return season.episodeAt(this.descendingPosition(childPosition, season.numberOfEpisodes()));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.episode(groupPosition, childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null){
            itemView = LayoutInflater.from(this.context).inflate(R.layout.series_seasons_item_episode, null);
        }

        TextView numberTextView = (TextView) itemView.findViewById(R.id.episodeNumberTextView);
        TextView nameTextView = (TextView) itemView.findViewById(R.id.episodeNameTextView);
        final CheckBox isViewedCheckBox = (CheckBox) itemView.findViewById(R.id.episodeIsViewedCheckBox);

        final Episode episode = this.episode(groupPosition, childPosition);

        numberTextView.setText(String.format("%02d", episode.number()));
        nameTextView.setText(episode.name());
        isViewedCheckBox.setChecked(episode.wasSeen());
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

        return itemView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.season(groupPosition).numberOfEpisodes();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.season(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.series.seasons().numberOfSeasons();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = LayoutInflater.from(this.context).inflate(R.layout.series_seasons_item_season, null);
        }

        final Season season = this.season(groupPosition);

        TextView name = (TextView) itemView.findViewById(R.id.itemName);
        SeenEpisodesBar seenEpisodesBar = (SeenEpisodesBar) itemView.findViewById(R.id.seen_episodes);
        final CheckBox isSeasonViewed = (CheckBox) itemView.findViewById(R.id.isSeasonViewedCheckBox);

        if (season.number() == 0) {
            name.setText(this.context.getString(R.string.special_episodes));
        } else {
            name.setText(String.format(this.context.getString(R.string.season_number_format), season.number()));
        }

        seenEpisodesBar.updateWithEpisodesOf(season);

        isSeasonViewed.setChecked(season.wasSeen());
        isSeasonViewed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isSeasonViewed.isChecked()) {
                    SERIES_PROVIDER.markSeasonAsSeen(season);
                } else {
                    SERIES_PROVIDER.markSeasonAsNotSeen(season);
                }
            }
        });

        return itemView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onMarkAsSeen(Season season) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onMarkAsNotSeen(Season season) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onChangeNumberOfSeenEpisodes(Season season) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onChangeNextEpisodeToSee(Season season) {
        //It's not my problem
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
    public void onMarkAsSeenBySeason(Episode episode) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onMarkAsNotSeenBySeason(Episode episode) {
        this.notifyDataSetChanged();
    }
}
