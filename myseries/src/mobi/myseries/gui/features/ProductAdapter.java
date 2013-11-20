package mobi.myseries.gui.features;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ProductAdapter extends BaseAdapter {
    private List</*TODO Product*/ String> mItems;

    public ProductAdapter(List</*TODO Product*/ String> items) {
        mItems = items;

        //TODO sortItems();
    }

    /*
    public void sortItems() {
        Collections.sort(
                mItems,
                SeasonComparator.fromSortMode(App.preferences().forSeriesDetails().sortMode()));
    }
    */

    public /*TODO Product*/ String getSeason(int position) {
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

        /* TODO
        Season season = mItems.get(position);

        int numberOfEpisodes = season.numberOfEpisodes();
        int numberOfWatchedEpisodes = season.numberOfEpisodes(new EpisodeWatchMarkSpecification(true));
        int numberOfUnairedEpisodes = season.numberOfEpisodes(new UnairedEpisodeSpecification());
        String pluralOfUnaired = App.resources().getQuantityString(R.plurals.plural_unaired, numberOfUnairedEpisodes);
        String allAired = App.resources().getString(R.string.all_aired);

        viewHolder.mSeasonNumber.setText(LocalText.of(season));
        viewHolder.mNumberOfEpisodes.setText("/" + String.valueOf(numberOfEpisodes));
        viewHolder.mWatchMark.setChecked(numberOfWatchedEpisodes == season.numberOfEpisodes());
        viewHolder.mWatchMark.setOnClickListener(viewHolder.watchMarkOnClickListener(season));
        */

        return view;
    }

    private static class ViewHolder {
        private TextView mProductName;
        private TextView mDescription;
        private Button mBuyButton;

        private ViewHolder(View view) {
            /* TODO
            mProductName = (TextView) view.findViewById(R.id.name);
            mDescription = (TextView) view.findViewById(R.id.description);
            mBuyButton= (Button) view.findViewById(R.id.buy);
            */

            view.setTag(this);
        }

        /* TODO
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
        */
    }
}
