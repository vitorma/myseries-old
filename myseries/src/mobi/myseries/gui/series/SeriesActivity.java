package mobi.myseries.gui.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.myseries.MySeriesActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class SeriesActivity extends SherlockFragmentActivity {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final String SERIES_ID = "seriesId";
    private static final String CURRENT_TAB = "currentTab";
    private static final int SEASONS = 0;

    private int seriesId;
    private int currentTab;

    private Series series;

    private ActionBar.Tab seasonsTab;
    private ActionBar.Tab detailsTab;

    public static Intent newIntent(Context context, int seriesId) {
        Intent intent = new Intent(context, SeriesActivity.class);
        intent.putExtra(SERIES_ID, seriesId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.series);

        if (savedInstanceState == null) {
            this.seriesId = this.getIntent().getExtras().getInt(SERIES_ID);
            this.currentTab = SEASONS;
        } else {
            this.seriesId = savedInstanceState.getInt(SERIES_ID);
            this.currentTab = savedInstanceState.getInt(CURRENT_TAB);
        }

        this.series = SERIES_PROVIDER.getSeries(this.seriesId);

        ActionBar ab = this.getSupportActionBar();
        ab.setTitle(this.series.name());
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        this.seasonsTab = ab.newTab().setText(R.string.seasons);
        this.seasonsTab.setTabListener(new SeriesOverviewTabListener(SeasonsFragment.newInstance(this.seriesId)));

        this.detailsTab = ab.newTab().setText(R.string.details);
        this.detailsTab.setTabListener(new SeriesOverviewTabListener(DetailsFragment.newInstance(this.seriesId)));

        ab.addTab(this.seasonsTab, false);
        ab.addTab(this.detailsTab, false);

        ab.setSelectedNavigationItem(this.currentTab);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SERIES_ID, this.seriesId);
        outState.putInt(CURRENT_TAB, this.currentTab);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MySeriesActivity.class);
                this.startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class SeriesOverviewTabListener implements ActionBar.TabListener {
        private SherlockFragment fragment;

        public SeriesOverviewTabListener(SherlockFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) { }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            SeriesActivity.this.currentTab = tab.getPosition();
            ft.replace(R.id.overview_container, this.fragment);
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            ft.remove(this.fragment);
        }
    }
}
