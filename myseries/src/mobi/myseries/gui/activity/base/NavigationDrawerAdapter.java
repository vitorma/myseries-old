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
    private LayoutInflater layoutInflater;
    private BaseActivity activity;

    private CharSequence[] titles;
    private Drawable[] drawables;
    private Drawable[] drawablesSelected;

    public NavigationDrawerAdapter(BaseActivity baseActivity) {
        this.layoutInflater = LayoutInflater.from(baseActivity);
        this.activity = baseActivity;

        this.loadTitles();
        this.loadDrawables();
    }

    @Override
    public int getCount() {
        return this.titles.length;
    }

    @Override
    public Object getItem(int position) {
        return this.titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = this.layoutInflater.inflate(R.layout.activity_base_sidemenu_item, null);
            viewHolder = new ViewHolder(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        this.setUpItemView(position, viewHolder.view);

        return convertView;
    }

    private void loadTitles() {
        this.titles = App.resources().getStringArray(R.array.sidemenu_items_array);
    }

    private void loadDrawables() {
        this.drawables = new Drawable[] {
                App.resources().getDrawable(R.drawable.ic_nav_grid),
                App.resources().getDrawable(R.drawable.ic_nav_calendar),
                App.resources().getDrawable(R.drawable.ic_nav_stats)
        };

        this.drawablesSelected = new Drawable[] {
                App.resources().getDrawable(R.drawable.ic_nav_grid_selected),
                App.resources().getDrawable(R.drawable.ic_nav_calendar_selected),
                App.resources().getDrawable(R.drawable.ic_nav_stats_selected)
        };
    }

    private void setUpItemView(int position, TextView view) {
        view.setText(this.titles[position]);

        if (this.shouldHighLight(position)) {
            view.setTextColor(App.resources().getColor(R.color.white));
            view.setCompoundDrawablesWithIntrinsicBounds(this.drawablesSelected[position], null, null, null);
            view.setBackgroundColor(App.resources().getColor(R.color.blue));
        } else {
            view.setTextColor(App.resources().getColor(R.color.gray));
            view.setCompoundDrawablesWithIntrinsicBounds(this.drawables[position], null, null, null);
            view.setBackgroundColor(App.resources().getColor(R.color.transparent));
        }
    }

    private boolean shouldHighLight(int position) {
        return this.activity.isTopLevel() && this.activity.titleForSideMenu().equals(this.titles[position]);
    }

    private static class ViewHolder {
        private TextView view;

        private ViewHolder(View convertView) {
            this.view = (TextView) convertView.findViewById(R.id.item);

            convertView.setTag(this);
        }
    }
}
