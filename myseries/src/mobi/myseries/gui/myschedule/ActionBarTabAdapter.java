package mobi.myseries.gui.myschedule;

import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class ActionBarTabAdapter extends FragmentPagerAdapter implements Publisher<ActionBarTabAdapter.Listener> {
    private ActionBar mActionBar;
    private ViewPager mViewPager;
    private TabDefinition[] mTabDefinitions;

    public ActionBarTabAdapter(
            FragmentManager fragmentManager,
            ActionBar actionBar,
            ViewPager viewPager,
            TabDefinition[] tabDefinitions,
            int selectedTab) {
        super(fragmentManager);

        mTabDefinitions = tabDefinitions;

        mViewPager = viewPager;
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);

        mActionBar = actionBar;
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (TabDefinition td : mTabDefinitions) {
            mActionBar.addTab(newTab(td.title()), false);
        }

        notifyDataSetChanged();

        mActionBar.setSelectedNavigationItem(selectedTab);
    }

    private ActionBar.Tab newTab(int nameResource) {
        return mActionBar.newTab().setText(nameResource).setTabListener(mTabListener);
    }

    /* FragmentPagerAdapter */

    @Override
    public int getCount() {
        return mTabDefinitions.length;
    }

    @Override
    public Fragment getItem(int position) {
        return mTabDefinitions[position].fragment();
    }

    /* Publisher<TabAdapter.Listener> */

    public static interface Listener {
        public void onTabSelected(int position);
    }

    private ListenerSet<ActionBarTabAdapter.Listener> mListeners = new ListenerSet<ActionBarTabAdapter.Listener>();

    @Override
    public boolean register(ActionBarTabAdapter.Listener listener) {
        return mListeners.register(listener);
    }

    @Override
    public boolean deregister(ActionBarTabAdapter.Listener listener) {
        return mListeners.deregister(listener);
    }

    private void notifyListeners(int position) {
        for (ActionBarTabAdapter.Listener listener : mListeners) {
            listener.onTabSelected(position);
        }
    }

    /* ActionBar.TabListener */

    private final ActionBar.TabListener mTabListener = new ActionBar.TabListener() {
        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            mViewPager.setCurrentItem(tab.getPosition());

            notifyListeners(tab.getPosition());
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) { }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) { }
    };

    /* ViewPager.OnPageChangeListener */

    private final ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);

            notifyListeners(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

        @Override
        public void onPageScrollStateChanged(int state) { }
    };
}
