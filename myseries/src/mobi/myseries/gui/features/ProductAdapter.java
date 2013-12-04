package mobi.myseries.gui.features;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.features.Product;
import mobi.myseries.application.features.ProductDescription;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ProductAdapter extends BaseAdapter {
    private List<Product> mItems;

    private final Activity mActivity;

    public ProductAdapter(List<Product> items, Activity activity) {
        mItems = items;
        mActivity = activity;

        //TODO sortItems();
    }

    /*
    public void sortItems() {
        Collections.sort(
                mItems,
                SeasonComparator.fromSortMode(App.preferences().forSeriesDetails().sortMode()));
    }
    */

    public Product getSeason(int position) {
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
            view = View.inflate(App.context(), R.layout.features_product_item, null);
            viewHolder = new ViewHolder(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Product product = mItems.get(position);
        ProductDescription productDescription = product.description();

        viewHolder.mProductName.setText(productDescription.name());

        if (product.price().isAvailable()) {
            viewHolder.mBuyButton.setVisibility(View.VISIBLE);
            viewHolder.mBuyButton.setText(product.price().value());
            viewHolder.mBuyButton.setOnClickListener(viewHolder.buyButtonOnClickListener(product));
        } else {
            viewHolder.mBuyButton.setVisibility(View.GONE);
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

    private class ViewHolder {
        private TextView mProductName;
        private TextView mDescription;
        private Button mBuyButton;

        private ViewHolder(View view) {
            mProductName = (TextView) view.findViewById(R.id.productName);
            mBuyButton = (Button) view.findViewById(R.id.buyButton);
            /* TODO
            mDescription = (TextView) view.findViewById(R.id.description);
            */

            view.setTag(this);
        }

        private OnClickListener buyButtonOnClickListener(final Product product) {
            return new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // XXX(Gabriel) verify if the product is owned.

                    //if (App..isChecked()) {
                        App.store().buy(product, mActivity);
                    //}
                }
            };
        }
    }
}
