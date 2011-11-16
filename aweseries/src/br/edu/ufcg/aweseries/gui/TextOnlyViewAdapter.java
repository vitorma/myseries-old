package br.edu.ufcg.aweseries.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.model.Series;

public final class TextOnlyViewAdapter extends ArrayAdapter<Series> {
    private final SeriesSearchActivity seriesSearchView;

    public TextOnlyViewAdapter(SeriesSearchActivity seriesSearchView, Context context,
            int seriesItemResourceId, Series[] objects) {
        super(context, seriesItemResourceId, objects);
        this.seriesSearchView = seriesSearchView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        // if no view was passed, create one for the item
        if (itemView == null) {
            final LayoutInflater vi = (LayoutInflater) this.seriesSearchView
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = vi.inflate(R.layout.text_only_list_item, null);
        }

        // get views for the series fields
        final TextView name = (TextView) itemView.findViewById(R.id.itemName);

        // load series data
        final Series item = this.getItem(position);

        name.setText(item.getName());

        return itemView;
    }
}
