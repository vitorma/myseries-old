package mobi.myseries.gui.schedule.singlepane;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.activity.base.TabActivity;
import mobi.myseries.gui.schedule.EpisodeFilterDialogFragment;
import mobi.myseries.gui.schedule.EpisodeSortingDialogFragment;
import mobi.myseries.gui.schedule.SeriesFilterDialogFragment;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.TabDefinition;
import mobi.myseries.gui.shared.ToastBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ScheduleDetailActivity extends TabActivity {
    private static final int FIRST_POSITION = 0;

    /* Intent */

    public static Intent newIntent(Context context, int scheduleMode, int selectedItem) {
        return new Intent(context, ScheduleDetailActivity.class)
            .putExtra(Extra.SCHEDULE_MODE, scheduleMode)
            .putExtra(Extra.POSITION, selectedItem);
    }

    /* Menu */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.myschedule, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (isDrawerOpen()) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }

            return true;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_series:
                showSeriesFilterDialog();
                return true;
            case R.id.filter_episodes:
                showEpisodeFilterDialog();
                return true;
            case R.id.sort:
                showSortDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSeriesFilterDialog() {
        if (App.seriesFollowingService().getAllFollowedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_show).build().show();
        } else {
            new SeriesFilterDialogFragment().show(getFragmentManager(), "seriesFilterDialog");
        }
    }

    private void showEpisodeFilterDialog() {
        EpisodeFilterDialogFragment
            .newInstance(selectedTab())
            .show(getFragmentManager(), "episodeFilterDialog");
    }

    private void showSortDialog() {
        EpisodeSortingDialogFragment
            .newInstance(selectedTab())
            .show(getFragmentManager(), "seriesSortingDialog");
    }

    /* BaseActivity */

    @Override
    protected void init(Bundle savedInstanceState) { /* There is nothing to initialize here */ }

    @Override
    protected CharSequence title() {
        return getText(R.string.ab_title_schedule);
    }

    @Override
    protected boolean isTopLevel() {
        return true;
    }

    @Override
    protected CharSequence titleForSideMenu() {
        return getText(R.string.nav_schedule);
    }

    @Override
    protected TabDefinition[] tabDefinitions() {
        return new TabDefinition[] {
              new TabDefinition(
                  R.string.tab_next_to_watch,
                  ScheduleDetailFragment.newInstance(ScheduleMode.TO_WATCH, selectedItem(ScheduleMode.TO_WATCH))),
              new TabDefinition(
                  R.string.tab_aired,
                  ScheduleDetailFragment.newInstance(ScheduleMode.AIRED, selectedItem(ScheduleMode.AIRED))),
              new TabDefinition(
                  R.string.tab_unaired,
                  ScheduleDetailFragment.newInstance(ScheduleMode.UNAIRED, selectedItem(ScheduleMode.UNAIRED)))
          };
    }

    @Override
    protected int defaultSelectedTab() {
        return getIntent().getExtras().getInt(Extra.SCHEDULE_MODE);
    }

    private int selectedItem(int tab) {
        return tab == defaultSelectedTab() ?
                getIntent().getExtras().getInt(Extra.POSITION) :
                FIRST_POSITION;
    }
}
