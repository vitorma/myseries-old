package mobi.myseries.gui.myschedule;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.episode.EpisodeActivity;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.ToastBuilder;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

public class ScheduleFragment extends ListFragment implements ScheduleAdapter.Listener {
    private int scheduleMode;
    private ScheduleAdapter adapter;

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

        if (this.adapter == null) {
            this.adapter = new ScheduleAdapter(this.scheduleMode, App.preferences().forMySchedule(this.scheduleMode));
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

        App.preferences().forActivities().register(this.preferencesListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        this.adapter.deregister(this);

        App.preferences().forActivities().register(this.preferencesListener);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_series:
                this.showSeriesFilterDialog();
                return true;
            case R.id.filter_episodes:
                this.showEpisodeFilterDialog();
                return true;
            case R.id.sort:
                this.showSortDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSeriesFilterDialog() {
        if (this.adapter.isEmpty()) {
            new ToastBuilder(this.getActivity()).setMessage(R.string.no_series_to_show).build().show();
        } else {
            SeriesFilterDialogFragment.newInstance(this.scheduleMode).show(this.getFragmentManager(), "seriesFilterDialog");
        }
    }

    private void showEpisodeFilterDialog() {
        if (this.adapter.isEmpty()) {
            new ToastBuilder(this.getActivity()).setMessage(R.string.no_episodes_to_show).build().show();
        } else {
            EpisodeFilterDialogFragment.newInstance(this.scheduleMode).show(this.getFragmentManager(), "episodeFilterDialog");
        }
    }

    private void showSortDialog() {
        if (this.adapter.isEmpty()) {
            new ToastBuilder(this.getActivity()).setMessage(R.string.no_episodes_to_sort).build().show();
        } else {
            SeriesSortingDialogFragment.newInstance(this.scheduleMode).show(this.getFragmentManager(), "seriesSortingDialog");
        }
    }

    private void setUpEmptyText() {
        this.setEmptyText(this.getString(R.string.no_episodes_to_see));
    }

    private void setUpItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Episode e = (Episode) parent.getItemAtPosition(position);

                Intent intent = EpisodeActivity.newIntent(
                        view.getContext(), e.seriesId(), e.seasonNumber(), e.number());

                ScheduleFragment.this.startActivity(intent);
            }
        });
    }

    /* SharedPreferences.OnSharedPreferenceChangeListener */

    private OnSharedPreferenceChangeListener preferencesListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            ScheduleFragment.this.adapter.reload();
        }
    };
}
