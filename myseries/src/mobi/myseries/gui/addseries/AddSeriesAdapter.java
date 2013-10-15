package mobi.myseries.gui.addseries;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.following.SeriesFollowingListener;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.addseries.AddSeriesAdapter.AddSeriesAdapterListener;
import mobi.myseries.gui.shared.AndroidUtils;
import mobi.myseries.gui.shared.AsyncImageLoader;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Strings;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AddSeriesAdapter extends ArrayAdapter<SearchResult> implements Publisher<AddSeriesAdapterListener> {
    private final LayoutInflater layoutInflater;
    private final AsyncImageLoader imageLoader;

    private static final Bitmap GENERIC_POSTER = Images.genericSeriesPosterFrom(App.resources());

    public AddSeriesAdapter(Context context, List<SearchResult> results, AsyncImageLoader imageLoader) {
        super(context, R.layout.addseries_item, results);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageLoader = imageLoader;
    }

    /*
     * An InputStream that skips the exact number of bytes provided, unless it
     * reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = this.in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = this.read();
                    if (b < 0) {
                        break; // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SearchResult result = this.getItem(position);

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
            //this.imageDownloader.download(result.poster(), viewHolder.image, false); XXX
            AsyncImageLoader.BitmapFetchingMethod posterDownloader
                    = new AsyncImageLoader.BitmapFetchingMethod() {
                @Override
                public Bitmap loadCachedBitmap() {
                    return null;
                }

                @Override
                public Bitmap loadBitmap() {
                    try {
                        return BitmapFactory.decodeStream(new FlushedInputStream(AndroidUtils.downloadUrl(result.poster())));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // TODO Auto-generated method stub
                    return null;
                }
            };
            imageLoader.loadBitmapOn(posterDownloader, GENERIC_POSTER, viewHolder.image, viewHolder.imageProgress);
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
            viewHolder.showRemoveButton();
        } else {
            viewHolder.showAddButton();
        }

        viewHolder.seriesId = result.tvdbId();
        App.seriesFollowingService().register(viewHolder);

        if (App.seriesFollowingService().isTryingToFollowSeries(result.tvdbIdAsInt())) {
            viewHolder.showProgressAdd();
        } else if (App.seriesFollowingService().isTryingToUnfollowSeries(result.tvdbIdAsInt())) {
            viewHolder.showProgressRemove();
        }

        return convertView;
    }

    /* ViewHolder */

    private static class ViewHolder implements SeriesFollowingListener {
        private TextView name;
        private ImageView image;
        private ProgressBar imageProgress;
        private ImageButton addButton;
        private ImageButton removeButton;
        private ProgressBar progressAdd;
        private ProgressBar progressRemove;

        private String seriesId;

        private ViewHolder(View convertView) {
            this.name = (TextView) convertView.findViewById(R.id.itemName);
            this.image = (ImageView) convertView.findViewById(R.id.seriesPoster);
            this.imageProgress = (ProgressBar) convertView.findViewById(R.id.loadProgress);
            this.addButton = (ImageButton) convertView.findViewById(R.id.addButton);
            this.removeButton = (ImageButton) convertView.findViewById(R.id.removeButton);
            this.progressAdd = (ProgressBar) convertView.findViewById(R.id.progressAdd);
            this.progressRemove = (ProgressBar) convertView.findViewById(R.id.progressRemove);

            convertView.setTag(this);
        }

        private void showAddButton() {
            this.removeButton.setVisibility(View.INVISIBLE);
            this.progressAdd.setVisibility(View.INVISIBLE);
            this.progressRemove.setVisibility(View.INVISIBLE);
            this.addButton.setVisibility(View.VISIBLE);
        }

        private void showRemoveButton() {
            this.addButton.setVisibility(View.INVISIBLE);
            this.progressAdd.setVisibility(View.INVISIBLE);
            this.progressRemove.setVisibility(View.INVISIBLE);
            this.removeButton.setVisibility(View.VISIBLE);
        }

        private void showProgressAdd() {
            this.addButton.setVisibility(View.INVISIBLE);
            this.removeButton.setVisibility(View.INVISIBLE);
            this.progressRemove.setVisibility(View.INVISIBLE);
            this.progressAdd.setVisibility(View.VISIBLE);
        }

        private void showProgressRemove() {
            this.addButton.setVisibility(View.INVISIBLE);
            this.removeButton.setVisibility(View.INVISIBLE);
            this.progressAdd.setVisibility(View.INVISIBLE);
            this.progressRemove.setVisibility(View.VISIBLE);
        }

        @Override
        public void onStartToFollow(SearchResult series) {
            if (series.tvdbId().equals(this.seriesId)) {
                this.showProgressAdd();
            }
        }

        @Override
        public void onSuccessToFollow(Series series) {
            if (series.id() == Integer.valueOf(this.seriesId)) {
                this.showRemoveButton();
            }
        }

        @Override
        public void onFailToFollow(SearchResult series, Exception e) {
            if (series.tvdbId().equals(this.seriesId)) {
                this.showAddButton();
            }
        }

        @Override
        public void onSuccessToUnfollow(Series series) {
            if (series.id() == Integer.valueOf(this.seriesId)) {
                this.showAddButton();
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
        public void onStartToUnfollow(Series series) {
            if (series.id() == Integer.valueOf(this.seriesId)) {
                this.showProgressRemove();
            }
        }

        @Override
        public void onStartToUnfollowAll(Collection<Series> allSeries) {
            for (Series s : allSeries) {
                if (s.id() == Integer.valueOf(this.seriesId)) {
                    this.onStartToUnfollow(s);
                    break;
                }
            }
        }

        @Override
        public void onFailToUnfollow(Series series, Exception e) {
            if (series.id() == Integer.valueOf(this.seriesId)) {
                this.showAddButton();
            }
        }

        @Override
        public void onFailToUnfollowAll(Collection<Series> allSeries, Exception e) {
            for (Series s : allSeries) {
                if (s.id() == Integer.valueOf(this.seriesId)) {
                    this.onFailToUnfollow(s, e);
                    break;
                }
            }
        }
    }

    /* AddSeriesAdapterListener */

    public static interface AddSeriesAdapterListener {
        public void onRequestAdd(SearchResult series);
        public void onRequestRemove(SearchResult series);
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
