package mobi.myseries.gui.activity.base;

import mobi.myseries.R;
import mobi.myseries.application.App;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NavigationDrawerAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private BaseActivity mActivity;

    private CharSequence[] mTitles;
    private Drawable[] mDrawables;

    public NavigationDrawerAdapter(BaseActivity baseActivity) {
        mLayoutInflater = LayoutInflater.from(baseActivity);
        mActivity = baseActivity;

        loadTitles();
        loadDrawables();
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return mTitles[position];
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
        mTitles = App.resources().getStringArray(R.array.sidemenu_items_array);
    }

    private void loadDrawables() {
        mDrawables = new Drawable[] {
                App.resources().getDrawable(R.drawable.ic_nav_library),
                App.resources().getDrawable(R.drawable.ic_nav_schedule),
                App.resources().getDrawable(R.drawable.ic_nav_statistics)
        };
    }

    private void setUpItemView(int position, TextView view) {
        view.setText(mTitles[position]);
        view.setCompoundDrawablesWithIntrinsicBounds(mDrawables[position], null, null, null);

        if (shouldHighLight(position)) {
            view.setBackgroundColor(App.resources().getColor(R.color.bg_navigation_drawer_item_selected));
        } else {
            view.setBackgroundColor(App.resources().getColor(R.color.bg_navigation_drawer_item_unselected));
        }
    }

    private boolean shouldHighLight(int position) {
        return mActivity.isTopLevel() && mActivity.titleForSideMenu().equals(mTitles[position]);
    }

    private static class ViewHolder {
        private TextView mView;

        private ViewHolder(View convertView) {
            mView = (TextView) convertView.findViewById(R.id.item);

            convertView.setTag(this);
        }
    }
}
