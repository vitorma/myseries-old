package mobi.myseries.gui.shared;

import java.util.ArrayList;

import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class TabsAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener, ActionBar.TabListener, Publisher<TabsAdapter.Listener> {
    private final Context context;
    private final ActionBar actionBar;
    private final ViewPager viewPager;

    private final ArrayList<String> fragmentClassNames = new ArrayList<String>();
    private final ArrayList<Bundle> fragmentExtras = new ArrayList<Bundle>();

    private final ListenerSet<TabsAdapter.Listener> listeners = new ListenerSet<TabsAdapter.Listener>();

    public TabsAdapter(Activity activity, ActionBar actionBar, ViewPager viewPager) {
        super(activity.getFragmentManager());

        this.context = activity;
        this.actionBar = actionBar;
        this.viewPager = viewPager;

        this.viewPager.setAdapter(this);
        this.viewPager.setOnPageChangeListener(this);
    }

    public void addTab(ActionBar.Tab tab, Class<?> clazz, Bundle extras, int position, boolean selected) {
        this.fragmentClassNames.add(clazz.getName());
        this.fragmentExtras.add(extras);
        this.actionBar.addTab(tab.setTabListener(this), position, selected);

        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.fragmentClassNames.size();
    }

    @Override
    public Fragment getItem(int position) {
        return Fragment.instantiate(
                this.context, this.fragmentClassNames.get(position), this.fragmentExtras.get(position));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        this.actionBar.setSelectedNavigationItem(position);
        this.notifyListeners(position);
    }

    private void notifyListeners(int position) {
        for (Listener l : this.listeners) {
            l.onSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        this.viewPager.setCurrentItem(tab.getPosition());
        this.notifyListeners(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {}

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {}

    public static interface Listener {
        public void onSelected(int position);
    }

    @Override
    public boolean register(TabsAdapter.Listener listener) {
        return this.listeners.register(listener);
    }

    @Override
    public boolean deregister(TabsAdapter.Listener listener) {
        return this.listeners.deregister(listener);
    }
}
