package mobi.myseries.gui.features;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.features.product.Product;
import mobi.myseries.application.features.product.ProductDescription;

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
        viewHolder.mDescription.setText(productDescription.description());

        if (product.isOwned()) {
            viewHolder.mBuyButton.setBackgroundColor(mActivity.getResources().getColor(R.color.green));
            viewHolder.mBuyButton.setText("Purchased");
        } else {
            viewHolder.mBuyButton.setBackgroundColor(mActivity.getResources().getColor(R.color.light_gray));
            if (product.price().isAvailable()) {
                viewHolder.mBuyButton.setText(product.price().value());
            } else {
                // XXX Find a good text for this
                viewHolder.mBuyButton.setText("N/A");
            }

            viewHolder.mBuyButton.setOnClickListener(viewHolder.buyButtonOnClickListener(product));
        }

        return view;
    }

    private class ViewHolder {
        private TextView mProductName;
        private TextView mDescription;
        private Button mBuyButton;

        private ViewHolder(View view) {
            mProductName = (TextView) view.findViewById(R.id.productName);
            mDescription = (TextView) view.findViewById(R.id.productDescription);
            mBuyButton = (Button) view.findViewById(R.id.buyButton);

            view.setTag(this);
        }

        private OnClickListener buyButtonOnClickListener(final Product product) {
            return new OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.store().buy(product, mActivity);
                }
            };
        }
    }
}
