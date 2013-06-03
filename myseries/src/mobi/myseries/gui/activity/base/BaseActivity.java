package mobi.myseries.gui.activity.base;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.myschedule.MyScheduleActivity;
import mobi.myseries.gui.myseries.MySeriesActivity;
import mobi.myseries.gui.shared.MessageLauncher;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

//TODO (Cleber) Clean up

public abstract class BaseActivity extends Activity {
    private static final int MENU_ITEM_MYSERIES = 0;
    private static final int MENU_ITEM_MYSCHEDULE = 1;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] sideMenuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //layout
        this.setContentView(R.layout.activity_base);
        this.includeChildView();

        //state
        this.loadState();

        //init
        this.init();

        //actionBar
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(true);
        this.setTitle(this.title());

        //sideMenu
        this.mDrawerTitle = this.getText(R.string.app);
        this.sideMenuItems = this.getResources().getStringArray(R.array.sidemenu_items_array);
        this.mDrawerLayout = (DrawerLayout) this.findViewById(R.id.layout);
        this.mDrawerList = (ListView) this.findViewById(R.id.drawer);
        this.mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        this.mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.activity_base_sidemenu_item, this.sideMenuItems));
        this.mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        this.mDrawerToggle = new ActionBarDrawerToggle(
                this,
                this.mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.sidemenu_accessibility_open,
                R.string.sidemenu_accessibility_close
                ) {
            @Override
            public void onDrawerClosed(View view) {
                int color = BaseActivity.this.getResources().getColor(R.color.transparent);

                for (int i=0; i < BaseActivity.this.sideMenuItems.length; i++) {
                    BaseActivity.this.mDrawerList.getChildAt(i).setBackgroundColor(color);
                }

                BaseActivity.this.getActionBar().setTitle(BaseActivity.this.mTitle);
                BaseActivity.this.invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                int color = BaseActivity.this.getResources().getColor(R.color.blue);

                if (BaseActivity.this.mTitle.equals(BaseActivity.this.sideMenuItems[MENU_ITEM_MYSERIES])) {
                    BaseActivity.this.mDrawerList.getChildAt(MENU_ITEM_MYSERIES).setBackgroundColor(color);
                } else if (BaseActivity.this.mTitle.equals(BaseActivity.this.sideMenuItems[MENU_ITEM_MYSCHEDULE])) {
                    BaseActivity.this.mDrawerList.getChildAt(MENU_ITEM_MYSCHEDULE).setBackgroundColor(color);
                }

                BaseActivity.this.getActionBar().setTitle(BaseActivity.this.mDrawerTitle);
                BaseActivity.this.invalidateOptionsMenu();
            }
        };
        if (!this.isTopLevel()) { this.mDrawerToggle.setDrawerIndicatorEnabled(false); }
        this.mDrawerLayout.setDrawerListener(this.mDrawerToggle);
    }

    private void includeChildView() {
        LinearLayout root = (LinearLayout) this.findViewById(R.id.content);
        this.getLayoutInflater().inflate(this.layoutResource(), root);
    }

    protected abstract void init();
    protected abstract int layoutResource();
    protected abstract boolean isTopLevel();
    protected abstract CharSequence title();

    public boolean isDrawerOpen() {
        return this.mDrawerLayout.isDrawerOpen(this.mDrawerList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                if (this.isTopLevel()) {
                    return this.mDrawerToggle.onOptionsItemSelected(item);
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BaseActivity.this.selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch (position) {
            case MENU_ITEM_MYSERIES:
                this.startActivity(MySeriesActivity.newIntent(this));
                break;
            case MENU_ITEM_MYSCHEDULE:
                this.startActivity(MyScheduleActivity.newIntent(this, ScheduleMode.NEXT));
                break;
        }

        this.mDrawerList.setItemChecked(position, true);
        this.mDrawerLayout.closeDrawer(this.mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.mTitle = title;

        this.getActionBar().setTitle(this.mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        this.mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        this.mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //TODO (Cleber) Remove all the code below ASAP -----------------------------------------------------------------------------------------

    public void showDialog(Dialog dialog) {
        this.state.dialog = dialog;
        this.state.dialog.show();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return this.state;
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.state.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        this.state.onStop();
    }

    private void loadState() {
        Object retainedState = this.getLastNonConfigurationInstance();

        if (retainedState != null) {
            this.state = (State) retainedState;
        } else {
            this.state = new State();
            this.state.messageLauncher = new MessageLauncher(this);
        }
    }

    private State state;

    private static class State {
        private Dialog dialog;
        private boolean isShowingDialog;
        private MessageLauncher messageLauncher;

        private void onStart() {
            if (this.isShowingDialog) {
                this.dialog.show();
            }

            this.messageLauncher.loadState();
        }

        private void onStop() {
            if (this.dialog != null && this.dialog.isShowing()) {
                this.isShowingDialog = true;
                this.dialog.dismiss();
            } else {
                this.isShowingDialog = false;
            }

            this.messageLauncher.onStop();
        }
    }
}
