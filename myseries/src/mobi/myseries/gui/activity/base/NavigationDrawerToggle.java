package mobi.myseries.gui.activity.base;

import mobi.myseries.R;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

public class NavigationDrawerToggle extends ActionBarDrawerToggle {
    private BaseActivity activity;

    public NavigationDrawerToggle(BaseActivity activity, DrawerLayout drawerLayout) {
        super(activity, drawerLayout, R.drawable.ic_drawer, R.string.sidemenu_accessibility_open, R.string.sidemenu_accessibility_close);

        this.activity = activity;
    }

    @Override
    public void onDrawerClosed(View view) {
        this.activity.getActionBar().setTitle(this.activity.title());
        this.activity.invalidateOptionsMenu();
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        this.activity.getActionBar().setTitle(R.string.app);
        this.activity.invalidateOptionsMenu();
    }
}
