package mobi.myseries.gui.addseries;

import java.util.Collection;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.following.SeriesFollowingListener;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.addseries.AddSeriesAdapter.AddSeriesAdapterListener;
import mobi.myseries.gui.shared.UniversalImageLoader;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class AddSeriesAdapter extends ArrayAdapter<SearchResult> implements Publisher<AddSeriesAdapterListener> {
    private final LayoutInflater layoutInflater;
    private final DisplayImageOptions mDisplayImageOptions;

    public AddSeriesAdapter(Context context, List<SearchResult> results) {
        super(context, R.layout.addseries_item, results);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDisplayImageOptions = imageLoaderOptions();
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
        UniversalImageLoader.loader().displayImage(App.imageService().getPosterOf(result.toSeries()), viewHolder.image, mDisplayImageOptions, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                UniversalImageLoader.loader().displayImage(result.poster(), viewHolder.image, mDisplayImageOptions);
            }

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ImageView imageView = (ImageView) view;
                imageView.setImageBitmap(null);
             }
        });

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
        private ImageButton addButton;
        private ImageButton removeButton;
        private ProgressBar progressAdd;
        private ProgressBar progressRemove;

        private String seriesId;

        private ViewHolder(View convertView) {
            this.name = (TextView) convertView.findViewById(R.id.itemName);
            this.image = (ImageView) convertView.findViewById(R.id.seriesPoster);
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

    private DisplayImageOptions imageLoaderOptions() {
        return new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
        .resetViewBeforeLoading(true)
        .showImageOnFail(R.drawable.generic_poster)
        .build();
    }
}
