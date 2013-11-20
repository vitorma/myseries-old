package mobi.myseries.gui.features;

import mobi.myseries.R;
import mobi.myseries.gui.shared.UniversalImageLoader;
import mobi.myseries.shared.Validate;

import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FeaturesFragment extends Fragment {
    //TODO private ScheduleMode mItems;

    private ProductAdapter mAdapter;

    private ListView mListView;
    private View mEmptyStateView;

    /* TODO
    private AsyncTask<Void, Void, Void> loadTask;
    private boolean isLoading = false;
    */

    /* Fragment */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO (Gabriel) should we use it? setRetainInstance(true);

        // TODO mScheduleMode = getArguments().getInt(Extra.SCHEDULE_MODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.features_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUp();
    }

    @Override
    public void onStart() {
        super.onStart();

        /* TODO
        App.preferences().forMySchedule(mScheduleMode).register(this);
        */
    }

    @Override
    public void onStop() {
        super.onStop();

        /* TODO
        App.preferences().forMySchedule(mScheduleMode).deregister(this);
        mItems.deregister(this);
        */
    }

    /* Auxiliary */

    private void setUp() {
        findViews();
        setUpData();
        setUpViews();
    }

    private void findViews() {
        mListView = (ListView) getView().findViewById(R.id.products_list);
        mEmptyStateView = getView().findViewById(R.id.empty_state);
        Validate.isNonNull(mListView, "mListView");
        Validate.isNonNull(mEmptyStateView, "mEmptyStateView");
    }

    private void setUpData() {
        /* TODO
        if (mItems != null) { mItems.deregister(this); }

        mItems = App.schedule().mode(mScheduleMode, App.preferences().forMySchedule(mScheduleMode).fullSpecification());
        mAdapter = new ScheduleListAdapter(mItems);

        mItems.register(this);
        */
    }

    private void setUpViews() {
        setUpEmptyStateView();
        setUpListView();
        hideOrshowViews();
    }

    private void setUpListView() {
        /* TODO
        mListView.setAdapter(mAdapter);
        setUpOnScrollListener();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnItemClickListener.onItemClick(mScheduleMode, position);
            }
        });
        */
    }

    private void setUpEmptyStateView() {
        /* TODO
        View hiddenEpisodesWarning = mEmptyStateView.findViewById(R.id.hiddenEpisodes);
        if (showHiddenEpisodesWarning) {
            hiddenEpisodesWarning.setVisibility(View.VISIBLE);
        } else {
            hiddenEpisodesWarning.setVisibility(View.GONE);
        }
        */
    }

    private void hideOrshowViews() {
        /* TODO
        if (mItems.numberOfEpisodes() > 0) {
            mEmptyStateView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
        */

        mEmptyStateView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
    }

    private void reload() {
        /* TODO
        if (isLoading) {
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
                if (!isCancelled()) { setUpViews(); }
                isLoading = false;
            }
        }.execute();
        */
    }

    private void setUpOnScrollListener() {
        boolean pauseOnScroll = false;
        boolean pauseOnFling = true;
        PauseOnScrollListener listener = new PauseOnScrollListener(UniversalImageLoader.loader(), pauseOnScroll, pauseOnFling);
        // TODO this.mListView.setOnScrollListener(listener);
    }
}
