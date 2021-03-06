package mobi.myseries.gui.features;

import java.util.Collections;
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
    private final List<Product> mItems;
    private final boolean mIsLoading;

    private final Activity mActivity;

    public ProductAdapter(List<Product> items, boolean isLoading, Activity activity) {
        mItems = items;
        mIsLoading = isLoading;
        mActivity = activity;

        sortItems();
    }

    private void sortItems() {
        Collections.sort(mItems);
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

        if (mIsLoading) {
            // XXX(Gabriel): animate with bouncing/hopping ellipsis
            viewHolder.mBuyButton.setBackgroundColor(mActivity.getResources().getColor(R.color.light_gray));
            viewHolder.mBuyButton.setText(". . .");
            viewHolder.mBuyButton.setOnClickListener(null);
        } else {
            if (product.isOwned()) {
                viewHolder.mBuyButton.setBackgroundColor(mActivity.getResources().getColor(R.color.yellow));
                viewHolder.mBuyButton.setText(R.string.features_price_purchased);
                viewHolder.mBuyButton.setOnClickListener(null);
            } else {
                if (product.price().isAvailable()) {
                    viewHolder.mBuyButton.setBackgroundResource(R.drawable.bg_button_green);
                    viewHolder.mBuyButton.setText(product.price().value());

                    viewHolder.mBuyButton.setOnClickListener(viewHolder.buyButtonOnClickListener(product));
                } else {
                    viewHolder.mBuyButton.setBackgroundColor(mActivity.getResources().getColor(R.color.gray));
                    viewHolder.mBuyButton.setText(R.string.features_price_not_available);
                    viewHolder.mBuyButton.setOnClickListener(null);
                }
            }
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
