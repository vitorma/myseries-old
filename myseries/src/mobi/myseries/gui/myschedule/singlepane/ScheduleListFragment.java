package mobi.myseries.gui.myschedule.singlepane;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.myschedule.ScheduleListAdapter;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.PauseOnScrollListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class ScheduleListFragment extends Fragment implements ScheduleListener, OnSharedPreferenceChangeListener {
    private int mScheduleMode;
    private ScheduleMode mItems;
    private ScheduleListAdapter mAdapter;
    private ListView mListView;
    private OnItemClickListener mOnItemClickListener;

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

        App.preferences().forActivities().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        App.preferences().forActivities().register(this);
        mItems.deregister(this);
    }

    /* ScheduleListener */

    @Override
    public void onScheduleStateChanged() {
        mAdapter.resetViewStates();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScheduleStructureChanged() {
        reload();
    }

    /* SharedPreferences.OnSharedPreferenceChangeListener */

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
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
    }

    private void setUpData() {
        if (mItems != null) { mItems.deregister(this); }

        mItems = App.schedule().mode(mScheduleMode, App.preferences().forMySchedule(mScheduleMode).fullSpecification());
        mAdapter = new ScheduleListAdapter(mItems);

        mItems.register(this);
    }

    private void setUpViews() {
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnItemClickListener.onItemClick(mScheduleMode, position);
            }
        });

        mListView.setOnScrollListener(new PauseOnScrollListener(false, true));
    }

    private void reload() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                setUpData();

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                setUpViews();
            }
        }.execute();
    }
}
