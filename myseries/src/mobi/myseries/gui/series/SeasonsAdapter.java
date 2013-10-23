package mobi.myseries.gui.series;

import java.util.Collections;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Season;
import mobi.myseries.gui.shared.EpisodeWatchMarkSpecification;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.SeasonComparator;
import mobi.myseries.gui.shared.SeenEpisodesBar;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.gui.shared.UnairedEpisodeSpecification;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SeasonsAdapter extends BaseAdapter {
    private List<Season> mItems;

    public SeasonsAdapter(List<Season> items) {
        mItems = items;

        sortItems();
    }

    public void sortItems() {
        Collections.sort(
                mItems,
                SeasonComparator.fromSortMode(App.preferences().forSeriesDetails().sortMode()));
    }

    public Season getSeason(int position) {
        return mItems.get(position);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = View.inflate(App.context(), R.layout.series_seasons_item, null);
            viewHolder = new ViewHolder(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Season season = mItems.get(position);

        int numberOfEpisodes = season.numberOfEpisodes();
        int numberOfWatchedEpisodes = season.numberOfEpisodes(new EpisodeWatchMarkSpecification(true));
        int numberOfUnairedEpisodes = season.numberOfEpisodes(new UnairedEpisodeSpecification());
        String pluralOfUnaired = App.resources().getQuantityString(R.plurals.plural_unaired, numberOfUnairedEpisodes);
        String allAired = App.resources().getString(R.string.all_aired);

        viewHolder.mSeasonNumber.setText(LocalText.of(season));
        viewHolder.mNumberOfEpisodes.setText("/" + String.valueOf(numberOfEpisodes));
        viewHolder.mNumberOfWatchedEpisodes.setText(String.valueOf(numberOfWatchedEpisodes));
        viewHolder.mNumberOfUnairedEpisodes.setText(
            numberOfUnairedEpisodes > 0 ?
            numberOfUnairedEpisodes + " " + pluralOfUnaired :
            allAired);
        viewHolder.mWatchProgress.updateWithEpisodesOf(season);
        viewHolder.mWatchMark.setChecked(numberOfWatchedEpisodes == season.numberOfEpisodes());
        viewHolder.mWatchMark.setOnClickListener(viewHolder.watchMarkOnClickListener(season));

        return view;
    }

    private static class ViewHolder {
        private TextView mSeasonNumber;
        private TextView mNumberOfEpisodes;
        private TextView mNumberOfWatchedEpisodes;
        private TextView mNumberOfUnairedEpisodes;
        private SeenEpisodesBar mWatchProgress;
        private SeenMark mWatchMark;

        private ViewHolder(View view) {
            mSeasonNumber = (TextView) view.findViewById(R.id.seasonNumber);
            mNumberOfEpisodes = (TextView) view.findViewById(R.id.allEpisodes);
            mNumberOfWatchedEpisodes = (TextView) view.findViewById(R.id.watchedEpisodes);
            mNumberOfUnairedEpisodes = (TextView) view.findViewById(R.id.unairedEpisodes);
            mWatchProgress = (SeenEpisodesBar) view.findViewById(R.id.seenEpisodesBar);
            mWatchMark = (SeenMark) view.findViewById(R.id.seenMark);

            view.setTag(this);
        }

        private OnClickListener watchMarkOnClickListener(final Season season) {
            return new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mWatchMark.isChecked()) {
                        App.markingService().markAsWatched(season);
                    } else {
                        App.markingService().markAsUnwatched(season);
                    }
                }
            };
        }
    }
}
