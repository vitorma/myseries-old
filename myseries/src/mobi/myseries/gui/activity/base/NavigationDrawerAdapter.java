package mobi.myseries.gui.activity.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.features.product.Feature;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NavigationDrawerAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private BaseActivity mActivity;

    private List<CharSequence> mTitles;
    private List<Drawable> mDrawables;

    public NavigationDrawerAdapter(BaseActivity baseActivity) {
        mLayoutInflater = LayoutInflater.from(baseActivity);
        mActivity = baseActivity;

        loadTitles();
        loadDrawables();
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return mTitles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.activity_base_sidemenu_item, null);
            viewHolder = new ViewHolder(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setUpItemView(position, viewHolder.mView);

        return convertView;
    }

    private void loadTitles() {
        List<CharSequence> titles = new ArrayList<CharSequence>(Arrays.asList(
                App.resources().getString(R.string.nav_library),
                App.resources().getString(R.string.nav_schedule),
                App.resources().getString(R.string.nav_statistics)
        ));
        if (App.features().isVisible(Feature.FEATURE_SHOP)) {
            titles.add(
                    App.resources().getString(R.string.nav_features)
            );
        }
        mTitles = titles;
    }

    private void loadDrawables() {
        List<Drawable> drawables = new ArrayList<Drawable>(Arrays.asList(
                App.resources().getDrawable(R.drawable.ic_nav_library),
                App.resources().getDrawable(R.drawable.ic_nav_schedule),
                App.resources().getDrawable(R.drawable.ic_nav_statistics)
        ));
        if (App.features().isVisible(Feature.FEATURE_SHOP)) {
            drawables.add(
                    App.resources().getDrawable(R.drawable.ic_nav_shop)
            );
        }
        mDrawables = drawables;
    }

    private void setUpItemView(int position, TextView view) {
        view.setText(mTitles.get(position));
        view.setCompoundDrawablesWithIntrinsicBounds(mDrawables.get(position), null, null, null);

        if (shouldHighLight(position)) {
            view.setBackgroundColor(App.resources().getColor(R.color.bg_navigation_drawer_item_selected));
        } else {
            view.setBackgroundColor(App.resources().getColor(R.color.bg_navigation_drawer_item_unselected));
        }
    }

    private boolean shouldHighLight(int position) {
        return mActivity.isTopLevel() && mActivity.titleForSideMenu().equals(mTitles.get(position));
    }

    private static class ViewHolder {
        private TextView mView;

        private ViewHolder(View convertView) {
            mView = (TextView) convertView.findViewById(R.id.item);

            convertView.setTag(this);
        }
    }
}
