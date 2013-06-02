package mobi.myseries.gui.myschedule;

import java.util.Map;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.gui.preferences.Preferences;
import mobi.myseries.gui.preferences.SchedulePreferences.MySchedulePreferences;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.SeriesFilterDialogBuilder;
import mobi.myseries.gui.shared.SeriesFilterDialogBuilder.OnFilterListener;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.gui.shared.SortingDialogBuilder;
import mobi.myseries.gui.shared.SortingDialogBuilder.OptionListener;
import android.app.Dialog;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

public class ScheduleFragment extends ListFragment implements ScheduleAdapter.Listener {
    private int scheduleMode;
    private ScheduleAdapter adapter;
    private MySchedulePreferences preferences;

    public static ScheduleFragment newInstance(int scheduleMode) {
        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SCHEDULE_MODE, scheduleMode);

        ScheduleFragment instance = new ScheduleFragment();
        instance.setArguments(arguments);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);
        this.scheduleMode = this.getArguments().getInt(Extra.SCHEDULE_MODE);
        this.preferences = Preferences.forMySchedule(this.scheduleMode);

        if (this.adapter == null) {
            this.adapter = new ScheduleAdapter(this.scheduleMode, Preferences.forMySchedule(this.scheduleMode));
            this.setListAdapter(this.adapter);
        }

        this.setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpEmptyText();
        this.setUpItemClickListener();
    }

    @Override
    public void onStart() {
        super.onStart();

        this.adapter.register(this);

        if (this.adapter.isLoading()) {
            this.onStartLoading();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        this.adapter.deregister(this);
    }

    @Override
    public void onStartLoading() {
        this.setListShown(false);
    }

    @Override
    public void onFinishLoading() {
        this.setListShown(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem hideShowSpecialEpisodes = menu.findItem(R.id.hideShowSpecialEpisodes);
        MenuItem hideShowSeenEpisodes = menu.findItem(R.id.hideShowSeenEpisodes);
        MenuItem seriesToShow = menu.findItem(R.id.filterSeries);
        MenuItem sortEpisodes = menu.findItem(R.id.sorting);

        boolean showOptions = !this.activity().isDrawerOpen();

        hideShowSpecialEpisodes.setVisible(showOptions);
        hideShowSeenEpisodes.setVisible(showOptions && this.scheduleMode != ScheduleMode.NEXT);
        seriesToShow.setVisible(showOptions);
        sortEpisodes.setVisible(showOptions);

        boolean isShowingSpecialEpisodes = this.preferences.showSpecialEpisodes();
        boolean isShowingSeenEpisodes = this.preferences.showSeenEpisodes();

        hideShowSpecialEpisodes.setTitle(isShowingSpecialEpisodes ? R.string.hideSpecialEpisodes : R.string.showSpecialEpisodes);
        hideShowSeenEpisodes.setTitle(isShowingSeenEpisodes ? R.string.hideSeenEpisodes : R.string.showSeenEpisodes);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sorting:
                this.showSortingDialog();
                return true;
            case R.id.hideShowSpecialEpisodes:
                this.hideOrShowSpecialEpisodes();
                return true;
            case R.id.hideShowSeenEpisodes:
                this.hideOrShowSeenEpisodes();
                return true;
            case R.id.filterSeries:
                this.showFilterDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideOrShowSpecialEpisodes() {
        boolean isShowingSpecialEpisodes = this.preferences.showSpecialEpisodes();

        this.adapter.hideOrShowSpecialEpisodes(!isShowingSpecialEpisodes);
    }

    private void hideOrShowSeenEpisodes() {
        boolean isShowingSeenEpisodes = this.preferences.showSeenEpisodes();

        this.adapter.hideOrShowSeenEpisodes(!isShowingSeenEpisodes);
    }

    private void showSortingDialog() {
        Dialog dialog = new SortingDialogBuilder(this.getActivity())
            .setTitleArgument(R.string.episodes)
            .setDefaultSortMode(this.preferences.sortMode())
            .setNewestFirstOptionListener(new OptionListener() {
                @Override
                public void onClick() {
                    ScheduleFragment.this.adapter.sortBy(SortMode.NEWEST_FIRST);
                }
            })
            .setOldestFirstOptionListener(new OptionListener() {
                @Override
                public void onClick() {
                    ScheduleFragment.this.adapter.sortBy(SortMode.OLDEST_FIRST);
                }
            })
            .build();

        this.showDialog(dialog);
    }

    private void showFilterDialog() {
        final Map<Series, Boolean> filterOptions = this.preferences.seriesToShow();

        Dialog dialog = new SeriesFilterDialogBuilder(this.getActivity())
            .setDefaultFilterOptions(filterOptions)
            .setOnFilterListener(new OnFilterListener() {
                @Override
                public void onFilter() {
                    ScheduleFragment.this.adapter.hideOrShowSeries(filterOptions);
                }
            })
            .build();

        this.showDialog(dialog);
    }

    private void showDialog(Dialog dialog) {
        this.activity().showDialog(dialog);
    }

    private MyScheduleActivity activity() {
        return (MyScheduleActivity) this.getActivity();
    }

    private void setUpEmptyText() {
        this.setEmptyText(this.getString(R.string.no_episodes_to_see));
    }

    private void setUpItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Episode e = (Episode) parent.getItemAtPosition(position);

                Intent intent = EpisodesActivity.newIntent(
                        view.getContext(), e.seriesId(), e.seasonNumber(), e.number());

                ScheduleFragment.this.startActivity(intent);
            }
        });
    }
}
