package mobi.myseries.gui.addseries;

import java.util.Collection;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.following.SeriesFollowingListener;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.addseries.AddSeriesAdapter.AddSeriesAdapterListener;
import mobi.myseries.gui.shared.AsyncImageLoader;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.SearchResultPosterFetchingMethod;
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

public class AddSeriesAdapter extends ArrayAdapter<SearchResult> implements Publisher<AddSeriesAdapterListener> {
    private final LayoutInflater layoutInflater;
    private final AsyncImageLoader imageLoader;

    private static final Bitmap GENERIC_POSTER = Images.genericSeriesPosterFrom(App.resources());

    public AddSeriesAdapter(Context context, List<SearchResult> results, AsyncImageLoader imageLoader) {
        super(context, R.layout.addseries_item, results);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageLoader = imageLoader;
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
            SearchResultPosterFetchingMethod posterDownloader = new SearchResultPosterFetchingMethod(result);
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
