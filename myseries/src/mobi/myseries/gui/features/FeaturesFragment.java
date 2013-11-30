package mobi.myseries.gui.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.Log;
import mobi.myseries.application.features.Product;
import mobi.myseries.application.features.ProductDescription;
import mobi.myseries.gui.shared.UniversalImageLoader;
import mobi.myseries.shared.Validate;

import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FeaturesFragment extends Fragment {

    private List<Product> mItems;

    private ProductAdapter mAdapter;

    private ListView mListView;
    private View mEmptyStateView;

    private AtomicReference<LoadTask> loadTask = new AtomicReference<LoadTask>();

    private void stopLoading() {
        LoadTask currentTask = this.loadTask.getAndSet(null);

        if (currentTask != null) {
            currentTask.cancel(false);
        }
    }

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
        Validate.isNonNull(mListView, "mListView");

        mEmptyStateView = getView().findViewById(R.id.empty_state);
        Validate.isNonNull(mEmptyStateView, "mEmptyStateView");
    }

    private void setUpData() {
        Log.d(getClass().getCanonicalName(), "Loading products");
        mItems = new ArrayList<Product>((Set<Product>) App.store().productsAvailableForPurchase());
        mAdapter = new ProductAdapter(mItems, this.getActivity());
        Log.d(getClass().getCanonicalName(), "Loaded products");

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
        mListView.setAdapter(mAdapter);

        /* TODO
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
        if (!mItems.isEmpty()) {
            mEmptyStateView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

    private void reload() {
        this.stopLoading();

        LoadTask newTask = new LoadTask();

        boolean newTaskIsTheCurrentTask = this.loadTask.compareAndSet(null, newTask);

        if (newTaskIsTheCurrentTask) {
            newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class LoadTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //XXX mItems = (List<Product<?>>) App.store().productsAvailableForPurchase();
            setUpData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!isCancelled()) {
                setUpViews();
                Log.d(getClass().toString(), "Updated views.");
            } else {
                Log.d(getClass().toString(), "Views not updated.");
            }
        }
    }

    private void setUpOnScrollListener() {
        boolean pauseOnScroll = false;
        boolean pauseOnFling = true;
        PauseOnScrollListener listener = new PauseOnScrollListener(UniversalImageLoader.loader(), pauseOnScroll, pauseOnFling);
        // TODO this.mListView.setOnScrollListener(listener);
    }
}
