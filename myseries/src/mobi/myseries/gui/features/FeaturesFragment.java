package mobi.myseries.gui.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.Log;
import mobi.myseries.application.features.product.Product;
import mobi.myseries.application.features.store.Store;
import mobi.myseries.application.features.store.StoreListener;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class FeaturesFragment extends Fragment {
    private volatile ItemsAndAdapter mItemsAndAdapter;

    private View mNonEmptyStateView;
    private View mEmptyStateView;
    private TextView mLoadingErrorMessageView;
    private GridView mGridView;

    private AtomicReference<LoadTask> mLoadTask = new AtomicReference<LoadTask>();

    /* Fragment */

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

    /* Auxiliary */

    private void setUp() {
        findViews();
        setUpData();
        setUpViews();
    }

    private void findViews() {
        mEmptyStateView = getView().findViewById(R.id.empty_state);
        mNonEmptyStateView = getView().findViewById(R.id.non_empty_state);
        mLoadingErrorMessageView = (TextView) getView().findViewById(R.id.error_message_view);
        mGridView = (GridView) getView().findViewById(R.id.products_list);
    }

    private void setUpData() {
        Log.d(getClass().getCanonicalName(), "Loading products");

        // It starts with no items.
        if (mItemsAndAdapter == null) {
            Set<Product> productsWithoutPrice = App.store().productsWithoutAvilabilityInformation();
            mItemsAndAdapter = new ItemsAndAdapter(new ArrayList<Product>(productsWithoutPrice), true);
        }

        Log.d(getClass().getCanonicalName(), "Loading products' availability...");
        App.store().productsWithAvailabilityInformation(new Store.AvailableProductsResultListener() {
            @Override
            public void onSuccess(Set<Product> products) {
                // remove loading state
                mItemsAndAdapter = new ItemsAndAdapter(new ArrayList<Product>(products), false);
                setUpViews();

                Log.d(getClass().getCanonicalName(), "Loaded products: success");
            }

            @Override
            public void onFailure() {
                mItemsAndAdapter = new ItemsAndAdapter(mItemsAndAdapter.items(), false);  // <Stop loading
                setUpViews();

                showLoadingErrorMessage();

                Log.d(getClass().getCanonicalName(), "Loaded products: failure");
            }
        });
    }

    private void setUpViews() {
        setUpGridView();
        hideOrShowViews();
    }

    private void setUpGridView() {
        mGridView.setAdapter(mItemsAndAdapter.adapter());

        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = mItemsAndAdapter.mItems.get(position);

                startActivity(ProductDetailsActivity.newIntent(getActivity(), product.sku()));
            }
        });
    }

    private void hideOrShowViews() {
        if (!this.mItemsAndAdapter.items().isEmpty()) {
            mEmptyStateView.setVisibility(View.GONE);
            mNonEmptyStateView.setVisibility(View.VISIBLE);
        } else {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mNonEmptyStateView.setVisibility(View.GONE);
        }

        hideLoadingErrorMessage();
    }

    private void showLoadingErrorMessage() {
        mLoadingErrorMessageView.setVisibility(View.VISIBLE);
    }

    private void hideLoadingErrorMessage() {
        mLoadingErrorMessageView.setVisibility(View.GONE);
    }

    private void reload() {
        this.stopLoading();

        LoadTask newTask = new LoadTask();
        boolean newTaskIsTheCurrentTask = this.mLoadTask.compareAndSet(null, newTask);

        if (newTaskIsTheCurrentTask) {
            newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void stopLoading() {
        LoadTask currentTask = mLoadTask.getAndSet(null);

        if (currentTask != null) {
            currentTask.cancel(false);
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
            if (!isCancelled()) { setUpViews(); }
        }
    }

    /* StoreListener */

    private final StoreListener mStoreListener = new StoreListener() {
        @Override
        public void onProductsChanged() { reload(); }
    };

    /* ItemsAndAdapter */

    private class ItemsAndAdapter {
        private List<Product> mItems;
        private ProductAdapter mAdapter;

        public ItemsAndAdapter(List<Product> items, boolean isLoading) {
            mItems = items;
            mAdapter = new ProductAdapter(items, isLoading, getActivity());
        }

        public List<Product> items() {
            return mItems;
        }

        public ProductAdapter adapter() {
            return mAdapter;
        }
    }
}
