package mobi.myseries.gui.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.Log;
import mobi.myseries.application.features.Store;
import mobi.myseries.application.features.StoreListener;
import mobi.myseries.application.features.product.Product;
import mobi.myseries.gui.shared.UniversalImageLoader;
import mobi.myseries.shared.Validate;

import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class FeaturesFragment extends Fragment {

    private volatile ItemsAndAdapter itemsAndAdapter;

    private class ItemsAndAdapter {
        private List<Product> mItems;
        private ProductAdapter mAdapter;

        public ItemsAndAdapter(List<Product> items, boolean isLoading) {
            mItems = items;
            mAdapter = new ProductAdapter(items, isLoading, FeaturesFragment.this.getActivity());
        }

        public List<Product> items() {
            return mItems;
        }

        public ProductAdapter adapter() {
            return mAdapter;
        }
    }

    private GridView mGridView;
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
        App.store().register(mStoreListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        App.store().deregister(mStoreListener);
    }

    private final StoreListener mStoreListener = new StoreListener() {
        @Override
        public void onProductsChanged() {
            Log.d(getClass().getCanonicalName(), "FeaturesFragment onProductsChanged called");
            reload();
        }
    };

    /* Auxiliary */

    private void setUp() {
        findViews();
        setUpData();
        setUpViews();
    }

    private void findViews() {
        mGridView = (GridView) getView().findViewById(R.id.products_list);
        Validate.isNonNull(mGridView, "mListView");

        mEmptyStateView = getView().findViewById(R.id.empty_state);
        Validate.isNonNull(mEmptyStateView, "mEmptyStateView");
    }

    private void setUpData() {
        Log.d(getClass().getCanonicalName(), "Loading products");

        // TODO: empty case
        // TODO: order items: alphabetically? owned first? owned last? by price?

        // The fragment has no items at the first time it is loaded.
        if (this.itemsAndAdapter == null) {
            Set<Product> productsWithoutPrice = App.store().productsWithoutAvilabilityInformation();
            this.itemsAndAdapter = new ItemsAndAdapter(new ArrayList<Product>(productsWithoutPrice), true);
        }

        Log.d(getClass().getCanonicalName(), "Loading products' availability...");
        App.store().productsWithAvailabilityInformation(new Store.AvailableProductsResultListener() {
            @Override
            public void onSuccess(Set<Product> products) {
                // TODO Auto-generated method stub
                itemsAndAdapter = new ItemsAndAdapter(new ArrayList<Product>(products), false);
                // remove loading state
                // draw list
                setUpViews();
                Log.d(getClass().getCanonicalName(), "Loaded products: success");
            }

            @Override
            public void onFailure() {
                // TODO Auto-generated method stub
                itemsAndAdapter = new ItemsAndAdapter(itemsAndAdapter.items(), false);  // <Stop loading
                // set error state
                // draw error view
                setUpViews();
                Log.d(getClass().getCanonicalName(), "Loaded products: failure");
            }
        });
    }

    private void setUpViews() {
        setUpEmptyStateView();
        setUpListView();
        hideOrshowViews();
    }

    private void setUpListView() {
        mGridView.setAdapter(this.itemsAndAdapter.adapter());

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
        if (!this.itemsAndAdapter.items().isEmpty()) {
            mEmptyStateView.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
        } else {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
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
