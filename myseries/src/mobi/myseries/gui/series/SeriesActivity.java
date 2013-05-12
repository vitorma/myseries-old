package mobi.myseries.gui.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.gui.shared.BaseActivity;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.TabsAdapter;
import net.simonvt.menudrawer.MenuDrawer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;

public class SeriesActivity extends BaseActivity {
    private static final SeriesProvider SERIES_PROVIDER = App.seriesProvider();

    private static final String SELECTED_TAB = "selectedTab";
    private static final int DETAILS = 0;
    private static final int SEASONS = 1;

    private int seriesId;
    private int selectedTab;

    public static Intent newIntent(Context context, int seriesId) {
        Intent intent = new Intent(context, SeriesActivity.class);
        intent.putExtra(Extra.SERIES_ID, seriesId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.series);
        this.setUpAttributesFrom(savedInstanceState);
        this.setUpActionBar();

        this.getMenu().setTouchMode(
                this.selectedTab == DETAILS ?
                MenuDrawer.TOUCH_MODE_FULLSCREEN :
                MenuDrawer.TOUCH_MODE_BEZEL);
    }

    private void setUpAttributesFrom(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            this.seriesId = this.getIntent().getExtras().getInt(Extra.SERIES_ID);
            this.selectedTab = SEASONS;
        } else {
            this.seriesId = savedInstanceState.getInt(Extra.SERIES_ID);
            this.selectedTab = savedInstanceState.getInt(SELECTED_TAB);
        }
    }

    private void setUpActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        actionBar.setTitle(SERIES_PROVIDER.getSeries(this.seriesId).name());
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.setUpNavigationFor(actionBar);
    }

    private void setUpNavigationFor(ActionBar actionBar) {
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ViewPager viewPager = (ViewPager) this.findViewById(R.id.viewPager);
        TabsAdapter tabsAdapter = new TabsAdapter(this, actionBar, viewPager);

        ActionBar.Tab detailsTab = actionBar.newTab().setText(R.string.details);
        ActionBar.Tab seasonsTab = actionBar.newTab().setText(R.string.seasons);

        Bundle extras = new Bundle();
        extras.putInt(Extra.SERIES_ID, this.seriesId);

        tabsAdapter.addTab(detailsTab, DetailsFragment.class, extras, DETAILS, false);
        tabsAdapter.addTab(seasonsTab, SeasonsFragment.class, extras, SEASONS, false);

        actionBar.setSelectedNavigationItem(this.selectedTab);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Extra.SERIES_ID, this.seriesId);
        outState.putInt(SELECTED_TAB, this.selectedTab);
        super.onSaveInstanceState(outState);
    }
}
