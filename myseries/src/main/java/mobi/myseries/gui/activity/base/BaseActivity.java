package mobi.myseries.gui.activity.base;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.features.FeaturesActivity;
import mobi.myseries.gui.library.LibraryActivity;
import mobi.myseries.gui.schedule.dualpane.ScheduleDualPaneActivity;
import mobi.myseries.gui.schedule.singlepane.ScheduleListActivity;
import mobi.myseries.gui.shared.MessageLauncher;
import mobi.myseries.gui.statistics.StatisticsActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

//TODO (Cleber) Clean up: extract methods, objects, etc.
public abstract class BaseActivity extends Activity {
    private static final int MENU_ITEM_MYSERIES = 0;
    private static final int MENU_ITEM_MYSCHEDULE = 1;
    private static final int MENU_ITEM_MYSTATISTICS = 2;
    private static final int MENU_ITEM_FEATURES = 3;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //layout
        this.setContentView(R.layout.activity_base);
        this.includeChildView();

        //state
        this.loadState();

        //init
        this.init(savedInstanceState);

        //actionBar
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(true);
        this.setTitle(this.title());

        //navigationDrawer
        this.mDrawerLayout = (DrawerLayout) this.findViewById(R.id.layout);
        this.mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        this.mDrawerList = (ListView) this.findViewById(R.id.drawer);
        this.mDrawerList.setAdapter(new NavigationDrawerAdapter(this));
        this.mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        this.mDrawerToggle = new NavigationDrawerToggle(this, this.mDrawerLayout);
        if (!this.isTopLevel()) { this.mDrawerToggle.setDrawerIndicatorEnabled(false); }
        this.mDrawerLayout.setDrawerListener(this.mDrawerToggle);
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

    @Override
    public void setTitle(CharSequence title) {
        this.getActionBar().setTitle(title);
    }

    protected abstract void init(Bundle savedInstanceState);
    protected abstract CharSequence title();
    protected abstract int layoutResource();
    protected abstract boolean isTopLevel();

    protected Intent upIntent() {
        return NavUtils.getParentActivityIntent(this).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    protected CharSequence titleForSideMenu() {
        return "";
    }

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
                    NavUtils.navigateUpTo(this, upIntent());
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void includeChildView() {
        LinearLayout root = (LinearLayout) this.findViewById(R.id.content);
        this.getLayoutInflater().inflate(this.layoutResource(), root);
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
                if (this.getClass() != LibraryActivity.class) {
                    this.startActivity(topActivityIntentFrom(LibraryActivity.newIntent(this)));
                }

                break;
            case MENU_ITEM_MYSCHEDULE:
                boolean isTablet = this.getResources().getBoolean(R.bool.isTablet);

                if (!isTablet && this.getClass() != ScheduleListActivity.class) {
                    this.startActivity(topActivityIntentFrom(ScheduleListActivity.newIntent(this, ScheduleMode.TO_WATCH)));
                }

                if (isTablet && this.getClass() != ScheduleDualPaneActivity.class) {
                    this.startActivity(topActivityIntentFrom(ScheduleDualPaneActivity.newIntent(this, ScheduleMode.TO_WATCH)));
                }

                break;
            case MENU_ITEM_MYSTATISTICS:
                if (this.getClass() != StatisticsActivity.class) {
                    this.startActivity(topActivityIntentFrom(StatisticsActivity.newIntent(this)));
                }

                break;
            case MENU_ITEM_FEATURES:
                if (this.getClass() != FeaturesActivity.class) {
                    this.startActivity(topActivityIntentFrom(FeaturesActivity.newIntent(this)));
                }

                break;
            default:
                break;
        }

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        this.mDrawerList.setItemChecked(position, true);
        this.mDrawerLayout.closeDrawer(this.mDrawerList);
    }

    private Intent topActivityIntentFrom(Intent intent) {
        return intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        App.activityEvents().notifyOnActivityResult(requestCode, resultCode, data);
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
