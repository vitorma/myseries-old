package mobi.myseries.gui.series;

import java.util.Collections;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.SeasonListener;
import mobi.myseries.gui.shared.SeasonComparator;
import mobi.myseries.gui.shared.SeenEpisodesBar;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.gui.shared.UnairedEpisodeSpecification;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SeasonsAdapter extends BaseAdapter implements SeasonListener, OnSharedPreferenceChangeListener {
    private int seriesId;
    private List<Season> seasons;

    public SeasonsAdapter(int seriesId) {
        this.seriesId = seriesId;
        this.loadSeasons();
    }

    private void loadSeasons() {
        this.seasons = App.seriesProvider().getSeries(this.seriesId).seasons().seasons();

        for (Season s : this.seasons) {
            s.register(this);
        }

        int sortMode = App.preferences().forSeriesDetails().sortMode();

        Collections.sort(this.seasons, SeasonComparator.fromSortMode(sortMode));
    }

    @Override
    public int getCount() {
        return this.seasons.size();
    }

    @Override
    public Object getItem(int position) {
        return this.seasons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = View.inflate(App.context(), R.layout.series_seasons_item, null);
        }

        TextView seasonNumber = (TextView) itemView.findViewById(R.id.seasonNumber);
        SeenEpisodesBar seenEpisodesBar = (SeenEpisodesBar) itemView.findViewById(R.id.seenEpisodesBar);
        final SeenMark seasonSeenMark = (SeenMark) itemView.findViewById(R.id.seenMark);

        final Season season = this.seasons.get(position);

        if (season.isSpecial()) {
            seasonNumber.setText(R.string.special_episodes);
            seasonNumber.setTextColor(App.resources().getColor(R.color.dark_red));
        } else {
            seasonNumber.setText(String.format(App.resources().getString(R.string.season_number_format_ext), season.number()));
            seasonNumber.setTextColor(App.resources().getColor(R.color.dark_gray));
        }

        seenEpisodesBar.updateWithEpisodesOf(season);

        seasonSeenMark.setChecked(season.wasSeen());
        seasonSeenMark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (seasonSeenMark.isChecked()) {
                    App.seriesProvider().markSeasonAsSeen(season);
                } else {
                    App.seriesProvider().markSeasonAsNotSeen(season);
                }
            }
        });

        TextView watchedEpisodes = (TextView) itemView.findViewById(R.id.watchedEpisodes);
        TextView allEpisodes = (TextView) itemView.findViewById(R.id.allEpisodes);
        TextView unairedEpisodes = (TextView) itemView.findViewById(R.id.unairedEpisodes);

        watchedEpisodes.setText(String.valueOf(season.numberOfSeenEpisodes()));
        allEpisodes.setText("/" + season.numberOfEpisodes());

        int numberOfUnairedEpisodes = season.numberOfEpisodes(new UnairedEpisodeSpecification());
        String pluralOfUnaired = App.resources().getQuantityString(R.plurals.plural_unaired, numberOfUnairedEpisodes);
        String allAired = App.resources().getString(R.string.all_aired);

        unairedEpisodes.setText(
            numberOfUnairedEpisodes > 0 ?
            numberOfUnairedEpisodes + " " + pluralOfUnaired :
            allAired);

        return itemView;
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
    public void onMarkAsSeenBySeries(Season season) { }

    @Override
    public void onMarkAsNotSeenBySeries(Season season) { }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        this.loadSeasons();
        this.notifyDataSetChanged();
    }
}
