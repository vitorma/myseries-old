package mobi.myseries.gui.shared;

import java.util.ArrayList;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class TabPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener, ActionBar.TabListener, Publisher<TabPagerAdapter.Listener> {
    private ActionBar actionBar;
    private List<Fragment> fragments;
    private ViewPager viewPager;

    public TabPagerAdapter(Activity activity) {
        super(activity.getFragmentManager());

        this.actionBar = activity.getActionBar();
        this.fragments = new ArrayList<Fragment>();

        this.viewPager = (ViewPager) activity.findViewById(R.id.viewPager);
        this.viewPager.setAdapter(this);
        this.viewPager.setOnPageChangeListener(this);

    }

    /* Tabs */

    public TabPagerAdapter addTab(int nameResource, Fragment fragment) {
        this.fragments.add(fragment);
        this.actionBar.addTab(this.newTab(nameResource), false);

        this.notifyDataSetChanged();

        return this;
    }

    private ActionBar.Tab newTab(int nameResource) {
        return this.actionBar.newTab().setText(nameResource).setTabListener(this);
    }

    /* FragmentPagerAdapter */

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
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

    private ListenerSet<TabPagerAdapter.Listener> listeners = new ListenerSet<TabPagerAdapter.Listener>();

    @Override
    public boolean register(TabPagerAdapter.Listener listener) {
        return this.listeners.register(listener);
    }

    @Override
    public boolean deregister(TabPagerAdapter.Listener listener) {
        return this.listeners.deregister(listener);
    }

    private void notifyListeners(int position) {
        for (TabPagerAdapter.Listener l : this.listeners) {
            l.onSelected(position);
        }
    }
}
