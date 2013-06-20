package mobi.myseries.gui.season;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.shared.Extra;
import android.app.Activity;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class SeasonFragment extends ListFragment implements OnSharedPreferenceChangeListener {

    public static interface OnEpisodeSelectedListener {
        public void onSelected(Episode e);
    }

    public static SeasonFragment newInstance(int seriesId, int seasonNumber) {
        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SERIES_ID, seriesId);
        arguments.putInt(Extra.SEASON_NUMBER, seasonNumber);

        SeasonFragment instance = new SeasonFragment();
        instance.setArguments(arguments);

        return instance;
    }

    private int seriesId;
    private int seasonNumber;
    private SeasonAdapter adapter;
    private OnEpisodeSelectedListener onEpisodeSelectedListener;
    private int selected = -1;
    private int sortMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);

        this.seriesId = this.getArguments().getInt(Extra.SERIES_ID);
        this.seasonNumber = this.getArguments().getInt(Extra.SEASON_NUMBER);
        this.sortMode = App.preferences().forSeason().sortMode();

        if (this.adapter == null) {
            this.adapter = new SeasonAdapter(this.seriesId, this.seasonNumber);
            this.setListAdapter(this.adapter);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpEmptyText();

        //XXX If is dual pane
        this.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        this.getListView().setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.onEpisodeSelectedListener = (OnEpisodeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnEpisodeSelectedListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        App.preferences().forActivities().register(this);
        App.preferences().forActivities().register(this.adapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        App.preferences().forActivities().deregister(this);
        App.preferences().forActivities().deregister(this.adapter);
    }

    private void setUpEmptyText() {
        this.setEmptyText(this.getString(R.string.no_episodes_to_see));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        this.selected = position;
        Log.d(this.getClass().toString(), "selected=" + this.selected);

        this.getListView().setItemChecked(position, true);
        Log.d(this.getClass().toString(), "checked=" + this.getListView().getCheckedItemPosition());

        Episode e = (Episode) this.getListView().getItemAtPosition(position);

        this.onEpisodeSelectedListener.onSelected(e);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        int currentSortMode = App.preferences().forSeason().sortMode();

        if (this.sortMode == currentSortMode) { return; }

        this.sortMode = currentSortMode;

        if (this.selected == -1) { return; }

        this.getListView().setItemChecked(this.selected, false);

        this.selected = this.getListView().getCount() - 1 - this.selected;

        this.getListView().setItemChecked(this.selected, true);
    }
}
