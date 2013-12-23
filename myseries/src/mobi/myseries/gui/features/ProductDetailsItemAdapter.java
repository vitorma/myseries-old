package mobi.myseries.gui.features;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.shared.Validate;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ProductDetailsItemAdapter extends PagerAdapter {

    private final List<Integer> mItems;
    private final LayoutInflater mInflater;

    public ProductDetailsItemAdapter(List<Integer> items) {
        Validate.isNonNull(items, "items");
        mItems = items;
        mInflater = LayoutInflater.from(App.context());
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mInflater.inflate(R.layout.features_product_details_item, null);

//        ImageView productImage = (ImageView) view.findViewById(R.id.productImage);
        ImageView productImage = (ImageView) view;
        Validate.isNonNull(productImage, "productImage");

        productImage.setImageResource(mItems.get(position));

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
