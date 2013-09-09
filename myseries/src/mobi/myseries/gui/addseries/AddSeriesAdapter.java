package mobi.myseries.gui.addseries;

import java.util.Collection;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.following.SeriesFollowingListener;
import mobi.myseries.domain.model.ParcelableSeries;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.addseries.AddSeriesAdapter.AddSeriesAdapterListener;
import mobi.myseries.gui.shared.ImageDownloader;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Strings;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AddSeriesAdapter extends ArrayAdapter<ParcelableSeries> implements Publisher<AddSeriesAdapterListener> {
    private LayoutInflater layoutInflater;
    private ImageDownloader imageDownloader;

    private static final Bitmap GENERIC_POSTER = Images.genericSeriesPosterFrom(App.resources());

    public AddSeriesAdapter(Context context, List<ParcelableSeries> results) {
        super(context, R.layout.addseries_item, results);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageDownloader = ImageDownloader.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ParcelableSeries result = this.getItem(position);

        final ViewHolder viewHolder;

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
                viewHolder.onStartToFollow(result);

                for (AddSeriesAdapterListener listener : AddSeriesAdapter.this.listeners) {
                    listener.onRequestAdd(result);
                }
            }
        });

        viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (AddSeriesAdapterListener listener : AddSeriesAdapter.this.listeners) {
                    listener.onRequestRemove(result);
                }
            }
        });

        if (App.seriesFollowingService().follows(result.toSeries())) {
            viewHolder.addButton.setVisibility(View.INVISIBLE);
            viewHolder.removeButton.setVisibility(View.VISIBLE);
            viewHolder.progressView.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.addButton.setVisibility(View.VISIBLE);
            viewHolder.removeButton.setVisibility(View.INVISIBLE);
            viewHolder.progressView.setVisibility(View.INVISIBLE);
        }

        viewHolder.seriesId = result.tvdbId();
        App.seriesFollowingService().register(viewHolder);

        if (App.seriesFollowingService().isTryingToFollowSeries(result.tvdbIdAsInt())) {
            viewHolder.addButton.setVisibility(View.INVISIBLE);
            viewHolder.removeButton.setVisibility(View.INVISIBLE);
            viewHolder.progressView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder implements SeriesFollowingListener {
        private TextView name;
        private ImageView image;
        private ImageButton addButton;
        private ImageButton removeButton;
        private ProgressBar progressView;

        private String seriesId;

        private ViewHolder(View convertView) {
            this.name = (TextView) convertView.findViewById(R.id.itemName);
            this.image = (ImageView) convertView.findViewById(R.id.seriesPoster);
            this.addButton = (ImageButton) convertView.findViewById(R.id.addButton);
            this.removeButton = (ImageButton) convertView.findViewById(R.id.removeButton);
            this.progressView = (ProgressBar) convertView.findViewById(R.id.progressView);

            convertView.setTag(this);
        }

        @Override
        public void onStartToFollow(ParcelableSeries series) {
            if (series.tvdbId().equals(this.seriesId)) {
                ViewHolder.this.addButton.setVisibility(View.INVISIBLE);
                ViewHolder.this.removeButton.setVisibility(View.INVISIBLE);
                ViewHolder.this.progressView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onSuccessToFollow(Series series) {
            if (series.id() == Integer.valueOf(this.seriesId)) {
                ViewHolder.this.addButton.setVisibility(View.INVISIBLE);
                ViewHolder.this.removeButton.setVisibility(View.VISIBLE);
                ViewHolder.this.progressView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onFailToFollow(ParcelableSeries series, Exception e) {
            if (series.tvdbId().equals(this.seriesId)) {
                ViewHolder.this.addButton.setVisibility(View.VISIBLE);
                ViewHolder.this.removeButton.setVisibility(View.INVISIBLE);
                ViewHolder.this.progressView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onSuccessToUnfollow(Series series) {
            if (series.id() == Integer.valueOf(this.seriesId)) {
                ViewHolder.this.addButton.setVisibility(View.VISIBLE);
                ViewHolder.this.removeButton.setVisibility(View.INVISIBLE);
                ViewHolder.this.progressView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onSuccessToUnfollowAll(Collection<Series> allSeries) {
            for (Series s : allSeries) {
                if (s.id() == Integer.valueOf(this.seriesId)) {
                    this.onSuccessToUnfollow(s);
                    break;
                }
            }
        }

        @Override
        public void onStartToUnfollow(Series seriesToUnfollow) {
            //XXX Implement me
        }

        @Override
        public void onStartToUnfollowAll(Collection<Series> allSeriesToUnfollow) {
            //XXX Implement me
        }

        @Override
        public void onFailToUnfollow(Series seriesToUnfollow, Exception e) {
            //XXX Implement me
        }

        @Override
        public void onFailToUnfollowAll(Collection<Series> allSeriesToUnfollow, Exception e) {
            //XXX Implement me
        }
    }

    /* AddSeriesAdapterListener */

    public static interface AddSeriesAdapterListener {
        public void onRequestAdd(ParcelableSeries series);
        public void onRequestRemove(ParcelableSeries series);
    }

    private ListenerSet<AddSeriesAdapterListener> listeners = new ListenerSet<AddSeriesAdapterListener>();

    @Override
    public boolean register(AddSeriesAdapterListener listener) {
        return this.listeners.register(listener);
    }

    @Override
    public boolean deregister(AddSeriesAdapterListener listener) {
        return this.listeners.deregister(listener);
    }
}
