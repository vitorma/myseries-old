package mobi.myseries.gui.addseries;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.ImageDownloader;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.shared.Strings;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddSeriesAdapter extends ArrayAdapter<SearchResult> {
    private LayoutInflater layoutInflater;
    private ImageDownloader imageDownloader;

    private static final Bitmap GENERIC_POSTER = Images.genericSeriesPosterFrom(App.resources());

    public AddSeriesAdapter(Context context, List<SearchResult> results) {
        super(context, R.layout.addseries_item, results);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageDownloader = ImageDownloader.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SearchResult result = this.getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = this.layoutInflater.inflate(R.layout.addseries_item, null);
            viewHolder = new ViewHolder(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(result.title());

        if (Strings.isNullOrBlank(result.poster())) {
            viewHolder.image.setImageBitmap(GENERIC_POSTER);
        } else {
            this.imageDownloader.download(result.poster(), viewHolder.image, false);
        }

        viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Series series = result.toSeries(); //XXX Let FollowSeriesService receive a seriesId in its methods ¬¬

                if (App.followSeriesService().follows(series)) {
                    String message = App.resources().getString(R.string.add_already_followed_series_message, result.title());

                    Toast.makeText(App.context(), message, Toast.LENGTH_SHORT).show();
                } else {
                    App.followSeriesService().follow(series);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        private TextView name;
        private ImageView image;
        private ImageButton addButton;

        private ViewHolder(View convertView) {
            this.name = (TextView) convertView.findViewById(R.id.itemName);
            this.image = (ImageView) convertView.findViewById(R.id.seriesPoster);
            this.addButton = (ImageButton) convertView.findViewById(R.id.addButton);

            convertView.setTag(this);
        }
    }
}
