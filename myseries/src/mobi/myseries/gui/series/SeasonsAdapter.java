package mobi.myseries.gui.series;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.SeasonListener;
import mobi.myseries.gui.shared.SeenEpisodesBar;
import mobi.myseries.gui.shared.SeenMark;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SeasonsAdapter extends BaseAdapter implements SeasonListener {
    private List<Season> seasons;

    public SeasonsAdapter(int seriesId) {
        this.seasons = App.seriesProvider().getSeries(seriesId).seasons().seasons();

        for (Season s : this.seasons) {
            s.register(this);
        }
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

        if (season.number() == 0) {
            seasonNumber.setText(App.resources().getString(R.string.special_episodes));
        } else {
            seasonNumber.setText(String.format(App.resources().getString(R.string.season_number_format), season.number()));
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
        TextView unwatchedEpisodes = (TextView) itemView.findViewById(R.id.unwatchedEpisodes);

        int numberOfUnwatchedEpisodes = season.numberOfEpisodes() - season.numberOfSeenEpisodes();
        String pluralOfUnwatched = App.resources().getQuantityString(R.plurals.plural_unwatched, numberOfUnwatchedEpisodes);
        String allWatched = App.resources().getString(R.string.all_watched);

        watchedEpisodes.setText(season.numberOfSeenEpisodes() + "/" + season.numberOfEpisodes());
        unwatchedEpisodes.setText(
            numberOfUnwatchedEpisodes > 0 ?
            numberOfUnwatchedEpisodes + " " + pluralOfUnwatched :
            allWatched);

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
}
