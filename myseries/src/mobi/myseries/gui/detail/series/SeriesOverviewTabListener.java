package mobi.myseries.gui.detail.series;

import mobi.myseries.R;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;

public class SeriesOverviewTabListener implements ActionBar.TabListener {
    private SherlockFragment fragment;

    public SeriesOverviewTabListener(SherlockFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) { }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.overview_container, this.fragment);
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        ft.remove(this.fragment);
    }
}
