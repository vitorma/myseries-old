package mobi.myseries.gui.features;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.gui.shared.UniversalImageLoader;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ProductDetailsItemAdapter extends PagerAdapter {
    private final List<Integer> mItems;
    private final LayoutInflater mInflater;

    public ProductDetailsItemAdapter(List<Integer> items) {
        mItems = items;
        mInflater = LayoutInflater.from(App.context());
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView view = (ImageView) mInflater.inflate(R.layout.features_product_details_item, null);

        UniversalImageLoader.loader().displayImage(
                UniversalImageLoader.drawableURI(mItems.get(position)),
                view);

        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
