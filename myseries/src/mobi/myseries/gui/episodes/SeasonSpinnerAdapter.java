package mobi.myseries.gui.episodes;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SeasonSpinnerAdapter extends BaseAdapter {
    private Series series;
    private LayoutInflater inflater;

    public SeasonSpinnerAdapter(Series series) {
        this.series = series;
        this.inflater = LayoutInflater.from(App.context());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = this.inflater.inflate(R.layout.ab_spinner_item, null);

        TextView title = (TextView) item.findViewById(R.id.title);
        title.setText(this.seriesTitle());

        TextView subtitle = (TextView) item.findViewById(R.id.subtitle);
        subtitle.setText(this.seasonTitle(position));

        return item;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View item = this.inflater.inflate(R.layout.ab_spinner_dropdown_item, null);

        TextView title = (TextView) item.findViewById(R.id.title);
        title.setText(this.seasonTitle(position));

        return item;
    }

    @Override
    public int getCount() {
        return this.series.seasons().numberOfSeasons();
    }

    @Override
    public Object getItem(int position) {
        return this.seasonAt(position);
    }

    @Override
    public long getItemId(int position) {
        return this.seasonNumber(position);
    }

    public int seasonNumber(int position) {
        return this.seasonAt(position).number();
    }

    public Season seasonAt(int position) {
        return this.series.seasonAt(position);
    }

    /* Auxiliary */

    private String seriesTitle() {
        return this.series.name();
    }

    private String seasonTitle(int position) {
        Season season = this.seasonAt(position);

        if (season.isSpecial()) {
            return App.resources().getString(R.string.special_episodes_upper);
        }

        return App.resources().getString(R.string.season_number_format_ext, season.number());
    }
}
