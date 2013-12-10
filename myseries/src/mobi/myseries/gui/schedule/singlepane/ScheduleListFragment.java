package mobi.myseries.gui.schedule.singlepane;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.broadcast.BroadcastAction;
import mobi.myseries.application.schedule.ScheduleListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.schedule.ScheduleListAdapter;
import mobi.myseries.gui.schedule.SeriesFilterDialogFragment;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.UniversalImageLoader;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

public class ScheduleListFragment extends Fragment implements ScheduleListener, OnSharedPreferenceChangeListener {
    private int mScheduleMode;
    private ScheduleMode mItems;

    private ScheduleListAdapter mAdapter;

    private ListView mListView;
    private View mEmptyStateView;
    private OnItemClickListener mOnItemClickListener;
    private AsyncTask<Void, Void, Void> loadTask;
    private boolean isLoading = false;
    private boolean mReloadOnResume;

    /* OnItemClickListener */

    public static interface OnItemClickListener {
        public void onItemClick(int scheduleMode, int position);
    }

    /* New instance */

    public static ScheduleListFragment newInstance(int scheduleMode) {
        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SCHEDULE_MODE, scheduleMode);

        ScheduleListFragment instance = new ScheduleListFragment();
        instance.setArguments(arguments);

        return instance;
    }

    /* Fragment */

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnItemClickListener = (OnItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException("activity " + activity.getClass().getName() + " should implement OnItemClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mScheduleMode = getArguments().getInt(Extra.SCHEDULE_MODE);

        App.preferences().forSchedule().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.myschedule_fragment_singlepane_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUp();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mReloadOnResume) {
            mReloadOnResume = false;
            reload();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        mItems.deregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        App.preferences().forSchedule().deregister(this);
    }

    /* ScheduleListener */

    @Override
    public void onScheduleStateChanged() {
        mAdapter.resetViewStates();
        mAdapter.notifyDataSetChanged();
        hideOrshowViews();
    }

    @Override
    public void onScheduleStructureChanged() {
        reload();
    }

    /* Auxiliary */

    private void setUp() {
        findViews();
        setUpData();
        setUpViews();
    }

    private void findViews() {
        mListView = (ListView) getView().findViewById(R.id.list);
        mEmptyStateView = getView().findViewById(R.id.empty_state);
    }

    private void setUpData() {
        if (mItems != null) { mItems.deregister(this); }

        mItems = App.schedule().mode(mScheduleMode, App.preferences().forSchedule().fullSpecification());
        mAdapter = new ScheduleListAdapter(mItems);

        mItems.register(this);
    }

    private void setUpViews() {
        setUpEmptyStateView();
        setUpListView();
        hideOrshowViews();
    }

    private void hideOrshowViews() {
        if (mItems.numberOfEpisodes() > 0) {
            mEmptyStateView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

    private void setUpListView() {
        mListView.setAdapter(mAdapter);
        setUpOnScrollListener();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnItemClickListener.onItemClick(mScheduleMode, position);
            }
        });
    }

    private void setUpEmptyStateView() {
        boolean thereAreFollowedSeries = !App.seriesFollowingService().getAllFollowedSeries().isEmpty();

        ScheduleSpecification specification = App.preferences().forSchedule().fullSpecification();
        boolean showHiddenEpisodesWarning = false;

        Button unhideSpecialEpisodes = (Button) mEmptyStateView.findViewById(R.id.unhideSpecialEpisodes);
        if (thereAreFollowedSeries && !specification.isSatisfiedBySpecialEpisodes()) {
            showHiddenEpisodesWarning = true;
            unhideSpecialEpisodes.setVisibility(View.VISIBLE);
            unhideSpecialEpisodes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.preferences().forSchedule().putIfShowSpecialEpisodes(true);
                }
            });
        } else {
            unhideSpecialEpisodes.setVisibility(View.GONE);
        }

        Button unhideWatchedEpisodes = (Button) mEmptyStateView.findViewById(R.id.unhideWatchedEpisodes);
        if (thereAreFollowedSeries && mScheduleMode != ScheduleMode.TO_WATCH && !specification.isSatisfiedByWatchedEpisodes()) {
            showHiddenEpisodesWarning = true;
            unhideWatchedEpisodes.setVisibility(View.VISIBLE);
            unhideWatchedEpisodes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.preferences().forSchedule().putIfShowWatchedEpisodes(true);
                }
            });
        } else {
            unhideWatchedEpisodes.setVisibility(View.GONE);
        }

        Button unhideEpisodesOfSomeSeries = (Button) mEmptyStateView.findViewById(R.id.unhideEpisodesOfSomeSeries);
        boolean isSatisfiedByEpisodesOfAllSeries = true;
        for (Series s : App.seriesFollowingService().getAllFollowedSeries()) {
            if (!specification.isSatisfiedByEpisodesOfSeries(s.id())) {
                isSatisfiedByEpisodesOfAllSeries = false;
                break;
            }
        }
        if (thereAreFollowedSeries && !isSatisfiedByEpisodesOfAllSeries) {
            showHiddenEpisodesWarning = true;
            unhideEpisodesOfSomeSeries.setVisibility(View.VISIBLE);
            unhideEpisodesOfSomeSeries.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SeriesFilterDialogFragment().show(getFragmentManager(), "seriesFilterDialog");
                }
            });
        } else {
            unhideEpisodesOfSomeSeries.setVisibility(View.GONE);
        }

        View hiddenEpisodesWarning = mEmptyStateView.findViewById(R.id.hiddenEpisodes);
        if (showHiddenEpisodesWarning) {
            hiddenEpisodesWarning.setVisibility(View.VISIBLE);
        } else {
            hiddenEpisodesWarning.setVisibility(View.GONE);
        }
    }

    private void reload() {
        if(isLoading) {
            loadTask.cancel(true);
        }

        loadTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                isLoading = true;
            }

            @Override
            protected Void doInBackground(Void... params) {
                setUpData();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if(!isCancelled()) { setUpViews(); }
                isLoading = false;
            }
        }.execute();
    }

    private void setUpOnScrollListener() {
        boolean pauseOnScroll = false;
        boolean pauseOnFling = true;
        PauseOnScrollListener listener = new PauseOnScrollListener(UniversalImageLoader.loader(), pauseOnScroll, pauseOnFling);
        this.mListView.setOnScrollListener(listener);
    }

    ListView listView() {
        return mListView;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (isVisible()) {
            mReloadOnResume = false;
            reload();
            App.context().sendBroadcast(new Intent(BroadcastAction.UPDATE));
        } else {
            mReloadOnResume = true;
        }
    }
}
