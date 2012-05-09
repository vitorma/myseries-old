package mobi.myseries.gui.detail;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.MySeriesActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class SeriesOverviewActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final String SERIES_ID = "seriesId";

    private int seriesId;
    private Series series;

    public static Intent newIntent(Context context, int seriesId) {
        Intent intent = new Intent(context, SeriesOverviewActivity.class);
        intent.putExtra(SERIES_ID, seriesId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.overview);

        if (savedInstanceState == null) {
            this.seriesId = this.getIntent().getExtras().getInt(SERIES_ID);
        } else {
            this.seriesId = savedInstanceState.getInt(SERIES_ID);
        }

        this.series = SERIES_PROVIDER.getSeries(this.seriesId);

        ActionBar ab = this.getSupportActionBar();

        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                ab.getThemedContext(),
                R.layout.sherlock_spinner_item,
                this.spinnerItems());

        adapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        ab.setListNavigationCallbacks(adapter, this);
        ab.setSelectedNavigationItem(1);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SERIES_ID, this.seriesId);
        super.onSaveInstanceState(outState);
    }

    private String[] spinnerItems() {
        return new String[] {
                this.getString(R.string.details) + " " + this.series.name(),
                this.getString(R.string.seasons_of_series) + " " + this.series.name()};
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

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();

        Fragment f = this.getSupportFragmentManager().findFragmentById(R.id.overview_details);

        if (f != null) {
            ft.remove(f);
        }

        switch (itemPosition) {
            case 0:
                ft.replace(R.id.overview_container, SeriesDetailsFragment.newInstance(this.seriesId));
                break;
            default:
                ft.replace(R.id.overview_container, SeasonsFragment.newInstance(this.seriesId));
        }

        ft.commit();

        return true;
    }
}
