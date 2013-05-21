package mobi.myseries.gui.shared;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.myschedule.MyScheduleActivity;
import mobi.myseries.gui.myseries.MySeriesActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SlidingMenuFragment extends ListFragment {
    private MenuAdapter menuAdapter;

    private static final int MENU_ITEM_MYSERIES_ID = 0;
    private static final int MENU_ITEM_MYSCHEDULE_ID = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.menuAdapter = new MenuAdapter(this.getActivity());
        this.menuAdapter.add(new MenuItem(this.getString(R.string.my_series), R.drawable.ic_action_menu, MENU_ITEM_MYSERIES_ID));
        this.menuAdapter.add(new MenuItem(this.getString(R.string.my_schedule), R.drawable.actionbar_calendar, MENU_ITEM_MYSCHEDULE_ID));

        this.setListAdapter(this.menuAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        this.closeMenu();

        int itemId = ((MenuItem) this.menuAdapter.getItem(position)).mId;
        switch (itemId) {
            case MENU_ITEM_MYSERIES_ID:
                this.startActivity(new Intent(this.getActivity(), MySeriesActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case MENU_ITEM_MYSCHEDULE_ID:
                this.startActivity(MyScheduleActivity.newIntent(this.getActivity(), ScheduleMode.NEXT)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                break;
        }
    }

    private void closeMenu() {
        if (this.getActivity() instanceof BaseActivity) {
            final BaseActivity activity = (BaseActivity) this.getActivity();
            Handler h = new Handler();
            h.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        activity.getMenu().closeMenu();
                    }
                },
                200
            );
        }
    }

    private class MenuItem {
        String mTitle;
        int mIconRes;
        int mId;

        public MenuItem(String title, int iconRes, int id) {
            this.mTitle = title;
            this.mIconRes = iconRes;
            this.mId = id;
        }
    }

    private class MenuCategory {
        public MenuCategory() { }
    }

    public class MenuAdapter extends ArrayAdapter<Object> {
        public MenuAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public int getItemViewType(int position) {
            return this.getItem(position) instanceof MenuItem ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEnabled(int position) {
            return this.getItem(position) instanceof MenuItem;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object item = this.getItem(position);

            if (item instanceof MenuItem) {
                ViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.sliding_menu_row_item, parent, false);
                    holder = new ViewHolder();
                    holder.attach(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                MenuItem menuItem = (MenuItem) item;
                holder.icon.setImageResource(menuItem.mIconRes);
                holder.title.setText(menuItem.mTitle);
            } else {
                if (convertView == null) {
                    convertView = LayoutInflater.from(this.getContext()).inflate(
                            R.layout.sliding_menu_row_category, parent, false);
                }
            }

            return convertView;
        }
    }

    static class ViewHolder {
        public TextView title;
        public ImageView icon;

        public void attach(View v) {
            this.icon = (ImageView) v.findViewById(R.id.menu_icon);
            this.title = (TextView) v.findViewById(R.id.menu_title);
        }
    }
}
