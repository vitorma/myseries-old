package mobi.myseries.gui.addseries;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Strings;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AddAdapter extends ArrayAdapter<Series> {
    private LayoutInflater layoutInflater;
    private ImageDownloader imageDownloader;

    public AddAdapter(Context context, List<Series> objects) {
        super(context, R.layout.addseries_item, objects);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageDownloader = ImageDownloader.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Series item = this.getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = this.layoutInflater.inflate(R.layout.addseries_item, null);
            viewHolder = new ViewHolder(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(item.name().toUpperCase());

        if (!Strings.isNullOrBlank(item.posterFileName())) {
            this.imageDownloader.download(item.posterFileName(), viewHolder.image, false);
        }

        return convertView;
    }

    private static class ViewHolder {
        private TextView name;
        private ImageView image;

        private ViewHolder(View convertView) {
            this.name = (TextView) convertView.findViewById(R.id.itemName);
            this.image = (ImageView) convertView.findViewById(R.id.seriesPoster);

            convertView.setTag(this);
        }
    }
}
