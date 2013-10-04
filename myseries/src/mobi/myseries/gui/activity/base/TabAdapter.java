package mobi.myseries.gui.activity.base;

import mobi.myseries.R;
import mobi.myseries.gui.shared.TabDefinition;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class TabAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener, ActionBar.TabListener, Publisher<TabAdapter.Listener> {
    private ActionBar actionBar;
    private ViewPager viewPager;
    private TabDefinition[] definitions;

    public TabAdapter(Activity activity, TabDefinition[] definitions, int selectedTab) {
        super(activity.getFragmentManager());

        this.definitions = definitions;

        this.viewPager = (ViewPager) activity.findViewById(R.id.viewPager);
        this.viewPager.setAdapter(this);
        this.viewPager.setOnPageChangeListener(this);

        this.actionBar = activity.getActionBar();
        for (TabDefinition td : this.definitions) {
            this.actionBar.addTab(this.newTab(td.title()), false);
        }
        this.notifyDataSetChanged();
        this.actionBar.setSelectedNavigationItem(selectedTab);
    }

    private ActionBar.Tab newTab(int nameResource) {
        return this.actionBar.newTab().setText(nameResource).setTabListener(this);
    }

    /* FragmentPagerAdapter */

    @Override
    public int getCount() {
        return this.definitions.length;
    }

    @Override
    public Fragment getItem(int position) {
        return this.definitions[position].fragment();
    }

    /* ViewPager.OnPageChangeListener */

    @Override
    public void onPageSelected(int position) {
        this.actionBar.setSelectedNavigationItem(position);
        this.notifyListeners(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) { }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    /* ActionBar.TabListener */

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        this.viewPager.setCurrentItem(tab.getPosition());
        this.notifyListeners(tab.getPosition());
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) { }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) { }

    /* Publisher<TabAdapter.Listener> */

    public static interface Listener {
        public void onSelected(int position);
    }

    private ListenerSet<TabAdapter.Listener> listeners = new ListenerSet<TabAdapter.Listener>();

    @Override
    public boolean register(TabAdapter.Listener listener) {
        return this.listeners.register(listener);
    }

    @Override
    public boolean deregister(TabAdapter.Listener listener) {
        return this.listeners.deregister(listener);
    }

    private void notifyListeners(int position) {
        for (TabAdapter.Listener l : this.listeners) {
            l.onSelected(position);
        }
    }
}
