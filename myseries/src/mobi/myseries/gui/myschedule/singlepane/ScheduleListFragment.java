package mobi.myseries.gui.myschedule.singlepane;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.MySchedulePreferencesListener;
import mobi.myseries.application.schedule.ScheduleListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.myschedule.ScheduleListAdapter;
import mobi.myseries.gui.myschedule.SeriesFilterDialogFragment;
import mobi.myseries.gui.shared.AsyncImageLoader;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.PauseImageLoaderOnScrollListener;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class ScheduleListFragment extends Fragment implements ScheduleListener, MySchedulePreferencesListener {
    private int mScheduleMode;
    private ScheduleMode mItems;

    private ScheduleListAdapter mAdapter;
    private AsyncImageLoader mPosterLoader;

    private ListView mListView;
    private View mEmptyStateView;
    private OnItemClickListener mOnItemClickListener;
    private AsyncTask<Void, Void, Void> loadTask;
    private boolean isLoading = false;

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
        mPosterLoader = new AsyncImageLoader();
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
    public void onStart() {
        super.onStart();

        App.preferences().forMySchedule(mScheduleMode).register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        App.preferences().forMySchedule(mScheduleMode).deregister(this);
        mItems.deregister(this);
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

        mItems = App.schedule().mode(mScheduleMode, App.preferences().forMySchedule(mScheduleMode).fullSpecification());
        mAdapter = new ScheduleListAdapter(mItems, mPosterLoader);

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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnItemClickListener.onItemClick(mScheduleMode, position);
            }
        });

        mListView.setOnScrollListener(new PauseImageLoaderOnScrollListener(mPosterLoader, false, true));
    }

    private void setUpEmptyStateView() {
        ScheduleSpecification specification = App.preferences().forMySchedule(mScheduleMode).fullSpecification();
        boolean showHiddenEpisodesWarning = false;

        Button unhideSpecialEpisodes = (Button) mEmptyStateView.findViewById(R.id.unhideSpecialEpisodes);
        if (!specification.isSatisfiedBySpecialEpisodes()) {
            showHiddenEpisodesWarning = true;
            unhideSpecialEpisodes.setVisibility(View.VISIBLE);
            unhideSpecialEpisodes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.preferences().forMySchedule(mScheduleMode).putIfShowSpecialEpisodes(true);
                }
            });
        } else {
            unhideSpecialEpisodes.setVisibility(View.GONE);
        }

        Button unhideWatchedEpisodes = (Button) mEmptyStateView.findViewById(R.id.unhideWatchedEpisodes);
        if (mScheduleMode != ScheduleMode.TO_WATCH && !specification.isSatisfiedByWatchedEpisodes()) {
            showHiddenEpisodesWarning = true;
            unhideWatchedEpisodes.setVisibility(View.VISIBLE);
            unhideWatchedEpisodes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.preferences().forMySchedule(mScheduleMode).putIfShowWatchedEpisodes(true);
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
        if (!isSatisfiedByEpisodesOfAllSeries) {
            showHiddenEpisodesWarning = true;
            unhideEpisodesOfSomeSeries.setVisibility(View.VISIBLE);
            unhideEpisodesOfSomeSeries.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesFilterDialogFragment.newInstance(mScheduleMode).show(getFragmentManager(), "seriesFilterDialog");
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
        if(isLoading)
            loadTask.cancel(true);

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

    @Override
    public void onSeriesToShowChange() {
        reload();
    }

    @Override
    public void onEpisodesToShowChange() {
        reload();
    }

    @Override
    public void onSortingChange() {
        reload();
    }
}
