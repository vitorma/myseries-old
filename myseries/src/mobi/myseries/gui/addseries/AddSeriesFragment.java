package mobi.myseries.gui.addseries;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.application.NetworkUnavailableException;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.source.InvalidSearchCriteriaException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.gui.addseries.AddSeriesAdapter.AddSeriesAdapterListener;
import mobi.myseries.gui.shared.AsyncImageLoader;
import mobi.myseries.gui.shared.PauseImageLoaderOnScrollListener;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public abstract class AddSeriesFragment extends Fragment {
    protected EditText mSearchField;
    private View mButtonPanel;
    private ImageButton clearButton;
    private ImageButton searchButton;
    private TextView sourceLabel;
    private TextView mNumberOfResultsLabel;
    private GridView mResultsGrid;
    private ProgressBar mProgressIndicator;
    private LinearLayout mErrorView;
    private TextView mErrorTitle;
    private TextView mErrorMessage;
    private Button mTryAgainButton;

    private AddSeriesAdapter mAdapter;
    private AsyncImageLoader mImageLoader;

    private List<SearchResult> mResults;
    protected boolean mIsServiceRunning;

    /* Fragment life cycle */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mImageLoader = new AsyncImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.addseries_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prepareViews();
    }

    @Override
    public void onStart() {
        super.onStart();

        registerListenerForService();

        if (hasResultsToShow()) {
            showResults();
        } else if (mIsServiceRunning) {
            onServiceStartRunning();
        } else if (shouldServiceRunOnStartLifeCycle()) {
            runService();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        deregisterListenerForService();
    }

    /* Abstract methods */

    protected abstract boolean hasSearchPanel();
    protected abstract int sourceTextResource();
    protected abstract int numberOfResultsFormatResource();
    protected abstract boolean shouldServiceRunOnStartLifeCycle();
    protected abstract void runService();
    protected abstract void registerListenerForService();
    protected abstract void deregisterListenerForService();
    protected abstract void onServiceStartRunning();

    /* Prepare views */

    private void prepareViews() {
        prepareSearchPanel();
        prepareSourceLabel();
        prepareContentViews();
    }

    private void prepareSearchPanel() {
        if (hasSearchPanel()) {
            showSearchPanel();
            prepareSearchField();
            prepareButtonPanel();
        }
    }

    private void prepareSearchField() {
        mSearchField = (EditText) findView(R.id.searchField);

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    AddSeriesFragment.this.showButtons();
                } else {
                    AddSeriesFragment.this.hideButtons();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mSearchField.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    AddSeriesFragment.this.runService();
                    return true;
                }

                return false;
            }
        });

        mSearchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    AddSeriesFragment.this.runService();
                    return true;
                }

                return false;
            }
        });
    }

    private void prepareButtonPanel() {
        mButtonPanel = findView(R.id.buttonPanel);

        prepareClearButton();
        prepareSearchButton();
    }

    private void prepareClearButton() {
        clearButton = (ImageButton) findView(R.id.clearButton);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mSearchField.setText("");
            }
        });
    }

    private void prepareSearchButton() {
        searchButton = (ImageButton) findView(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AddSeriesFragment.this.runService();
            }
        });
    }

    private void prepareSourceLabel() {
        sourceLabel = (TextView) findView(R.id.sourceLabel);

        sourceLabel.setText(sourceTextResource());
    }

    private void prepareContentViews() {
        prepareNumberOfResultsLabel();
        prepareProgressIndicator();
        prepareResultsGrid();
        prepareErrorView();
    }

    private void prepareNumberOfResultsLabel() {
        mNumberOfResultsLabel = (TextView) findView(R.id.numberOfResultsLabel);
    }

    private void prepareResultsGrid() {
        mResultsGrid = (GridView) findView(R.id.resultsGrid);

        mResultsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchResult selectedItem = ((SearchResult) parent.getItemAtPosition(position));

                SeriesFollowingDialogFragment.showDialog(selectedItem, getFragmentManager());
            }
        });

        if (mAdapter != null) {
            mResultsGrid.setAdapter(mAdapter);

            mResultsGrid.setOnScrollListener(new PauseImageLoaderOnScrollListener(mImageLoader, false, true));
        }
    }

    private void prepareProgressIndicator() {
        mProgressIndicator = (ProgressBar) findView(R.id.progressIndicator);
    }

    private void prepareErrorView() {
        mErrorView = (LinearLayout) findView(R.id.errorView);
        mErrorTitle = (TextView) findView(R.id.errorTitle);
        mErrorMessage = (TextView) findView(R.id.errorMessage);

        mTryAgainButton = (Button) findView(R.id.tryAgain);

        mTryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSeriesFragment.this.runService();
            }
        });
    }

    private View findView(int resourceId) {
        return getView().findViewById(resourceId);
    }

    /* Update views */

    private void showSearchPanel() {
        findView(R.id.searchPanel).setVisibility(View.VISIBLE);
    }

    protected void hideButtons() {
        mButtonPanel.setVisibility(View.INVISIBLE);
    }

    protected void showButtons() {
        mButtonPanel.setVisibility(View.VISIBLE);
    }

    protected void disableSearch() {
        mSearchField.setEnabled(false);

        hideButtons();
    }

    protected void enableSearch(boolean showButtons) {
        mSearchField.setEnabled(true);

        if (showButtons) {
            showButtons();
        }
    }

    protected void showProgress() {
        mProgressIndicator.setVisibility(View.VISIBLE);

        hideResults();
        hideError();
    }

    protected void hideProgress() {
        mProgressIndicator.setVisibility(View.INVISIBLE);
    }

    protected void showError(boolean showTryAgainButton) {
        mErrorView.setVisibility(View.VISIBLE);
        mTryAgainButton.setVisibility(showTryAgainButton? View.VISIBLE : View.INVISIBLE);

        hideProgress();
        hideResults();
    }

    protected void hideError() {
        mErrorView.setVisibility(View.INVISIBLE);
    }

    protected void setError(CharSequence title, CharSequence message) {
        mErrorTitle.setText(title);
        mErrorMessage.setText(message);
    }

    protected void setError(int titleResourceId, int messageResourceId) {
        this.setError(
                activity().getResources().getText(titleResourceId),
                activity().getResources().getText(messageResourceId));
    }

    protected void setError(Exception exception) {
        if (exception instanceof InvalidSearchCriteriaException) {
            this.setError(
                    R.string.invalid_criteria_title,
                    R.string.invalid_criteria_message);
        } else if (exception instanceof ConnectionFailedException) {
            this.setError(
                    R.string.connection_failed_title,
                    R.string.connection_failed_message);
        } else if (exception instanceof NetworkUnavailableException) {
            this.setError(
                    R.string.network_unavailable_title,
                    R.string.network_unavailable_message);
        } else if (exception instanceof ParsingFailedException) {
            this.setError(
                    R.string.parsing_failed_title,
                    R.string.parsing_failed_message);
        } else {
            // Just in case... but it should not reach here.
            this.setError(exception.getClass().getSimpleName(), "");
        }
    }

    protected void showResults() {
        setUpNumberOfResults();

        mNumberOfResultsLabel.setVisibility(View.VISIBLE);
        mResultsGrid.setVisibility(View.VISIBLE);

        hideProgress();
        hideError();
    }

    protected void hideResults() {
        mNumberOfResultsLabel.setVisibility(View.INVISIBLE);
        mResultsGrid.setVisibility(View.INVISIBLE);
    }

    protected boolean hasResultsToShow() {
        return mResults != null;
    }

    protected void setResults(List<SearchResult> results) {
        mResults = results;

        setUpNumberOfResults();
        setUpAdapter();

        mResultsGrid.setAdapter(mAdapter);
    }

    private void setUpNumberOfResults() {
        String format = this.getString(numberOfResultsFormatResource());

        mNumberOfResultsLabel.setText(String.format(format, mResults.size()));
    }

    private void setUpAdapter() {
        if (mAdapter == null) {
            mAdapter = new AddSeriesAdapter(activity(), mResults, mImageLoader);
            mAdapter.register(mAdapterListener);
        } else {
            mAdapter.clear();
            for (SearchResult result : mResults) {
                mAdapter.add(result);
            }
        }
    }

    /* Activity */

    protected AddSeriesActivity activity() {
        return (AddSeriesActivity) getActivity();
    }

    /* AddSeriesAdapterListener */

    private final AddSeriesAdapterListener mAdapterListener = new AddSeriesAdapterListener() {
        @Override
        public void onRequestAdd(SearchResult series) {
            App.seriesFollowingService().follow(series);
        }

        @Override
        public void onRequestRemove(SearchResult series) {
            SeriesRemovalConfirmationDialogFragment.newInstance(series)
            .show(AddSeriesFragment.this.getFragmentManager(), "SeriesRemovalConfirmationDialog");
        }
    };
}
