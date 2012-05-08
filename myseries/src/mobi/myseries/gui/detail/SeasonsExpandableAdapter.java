package mobi.myseries.gui.detail;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.SeasonListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Objects;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SeasonsExpandableAdapter extends BaseExpandableListAdapter implements SeasonListener, EpisodeListener {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

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

    public int seasonNumber(int groupPosition) {
        return this.series.hasSpecialEpisodes() ? groupPosition : groupPosition + 1;
    }

    public int episodeNumber(int childPosition) {
        return childPosition + 1;
    }

    public Season season(int groupPosition) {
        return this.series.season(this.seasonNumber(groupPosition));
    }

    public Episode episode(int groupPosition, int childPosition) {
        return this.season(groupPosition).episode(this.episodeNumber(childPosition));
    }

    public int groupPosition(Episode episode) {
        return this.series.hasSpecialEpisodes() ? episode.seasonNumber() : episode.seasonNumber() - 1;
    }

    public int childPosition(Episode episode) {
        return episode.number() - 1;
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
            itemView = LayoutInflater.from(this.context).inflate(R.layout.episode_list_item, null);
        }

        TextView nameTextView = (TextView) itemView.findViewById(R.id.episodeNameTextView);
        TextView numberTextView = (TextView) itemView.findViewById(R.id.episodeNumberTextView);
        TextView dateTextView = (TextView) itemView.findViewById(R.id.episodeDateTextView);
        final CheckBox isViewedCheckBox = (CheckBox) itemView.findViewById(R.id.episodeIsViewedCheckBox);

        final Episode episode = this.episode(groupPosition, childPosition);

        nameTextView.setText(Objects.nullSafe(episode.name(), this.context.getString(R.string.unnamed_episode)));
        numberTextView.setText(String.format(this.context.getString(R.string.episode_number_format), episode.number()));
        dateTextView.setText(Dates.toString(episode.airDate(), App.environment().localization().dateFormat(), ""));
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
            itemView = LayoutInflater.from(this.context).inflate(R.layout.season_list_item, null);
        }

        final Season season = this.season(groupPosition);

        TextView name = (TextView) itemView.findViewById(R.id.itemName);
        final CheckBox isSeasonViewed = (CheckBox) itemView.findViewById(R.id.isSeasonViewedCheckBox);

        if (season.number() == 0) {
            name.setText(this.context.getString(R.string.special_episodes));
        } else {
            name.setText(String.format(this.context.getString(R.string.season_number_format), season.number()));
        }

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
        //It's not my problem
    }

    @Override
    public void onChangeNextEpisodeToSee(Season season) {
        //It's not my problem
    }

    @Override
    public void onMerge(Season season) {
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
    public void onMerge(Episode episode) {
        //It's not my problem
    }
}
