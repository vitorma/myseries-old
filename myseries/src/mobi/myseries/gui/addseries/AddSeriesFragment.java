package mobi.myseries.gui.addseries;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.SearchResult;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public abstract class AddSeriesFragment extends Fragment {
    protected EditText searchField;
    private View buttonPanel;
    private ImageButton clearButton;
    private ImageButton searchButton;
    private TextView sourceLabel;
    private TextView numberOfResultsLabel;
    private GridView resultsGrid;
    private ProgressBar progressIndicator;

    private AddSeriesAdapter adapter;
    private AsyncImageLoader imageLoader;

    private List<SearchResult> results;
    protected boolean isServiceRunning;

    /* Fragment life cycle */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);

        this.imageLoader = new AsyncImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.addseries_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.prepareViews();
    }

    @Override
    public void onStart() {
        super.onStart();

        this.registerListenerForService();

        if (this.hasResultsToShow()) {
            this.showResults();
        } else if (this.isServiceRunning) {
            this.onServiceStartRunning();
        } else if (this.shouldServiceRunOnStartLifeCycle()) {
            this.runService();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        this.deregisterListenerForService();
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
        this.prepareSearchPanel();
        this.prepareSourceLabel();
        this.prepareContentViews();
    }

    private void prepareSearchPanel() {
        if (this.hasSearchPanel()) {
            this.showSearchPanel();
            this.prepareSearchField();
            this.prepareButtonPanel();
        }
    }

    private void prepareSearchField() {
        this.searchField = (EditText) this.findView(R.id.searchField);

        this.searchField.addTextChangedListener(new TextWatcher() {
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

        this.searchField.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    AddSeriesFragment.this.runService();
                    return true;
                }

                return false;
            }
        });

        this.searchField.setOnKeyListener(new View.OnKeyListener() {
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
        this.buttonPanel = this.findView(R.id.buttonPanel);

        this.prepareClearButton();
        this.prepareSearchButton();
    }

    private void prepareClearButton() {
        this.clearButton = (ImageButton) this.findView(R.id.clearButton);

        this.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AddSeriesFragment.this.searchField.setText("");
            }
        });
    }

    private void prepareSearchButton() {
        this.searchButton = (ImageButton) this.findView(R.id.searchButton);

        this.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AddSeriesFragment.this.runService();
            }
        });
    }

    private void prepareSourceLabel() {
        this.sourceLabel = (TextView) this.findView(R.id.sourceLabel);

        this.sourceLabel.setText(this.sourceTextResource());
    }

    private void prepareContentViews() {
        this.prepareNumberOfResultsLabel();
        this.prepareProgressIndicator();
        this.prepareResultsGrid();
    }

    private void prepareNumberOfResultsLabel() {
        this.numberOfResultsLabel = (TextView) this.findView(R.id.numberOfResultsLabel);
    }

    private void prepareResultsGrid() {
        this.resultsGrid = (GridView) this.findView(R.id.resultsGrid);

        this.resultsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchResult selectedItem = ((SearchResult) parent.getItemAtPosition(position));

                SeriesFollowingDialogFragment.showDialog(selectedItem, getFragmentManager());
            }
        });

        if (this.adapter != null) {
            this.resultsGrid.setAdapter(this.adapter);

            this.resultsGrid.setOnScrollListener(new PauseImageLoaderOnScrollListener(this.imageLoader, false, true));
        }
    }

    private void prepareProgressIndicator() {
        this.progressIndicator = (ProgressBar) this.findView(R.id.progressIndicator);
    }

    private View findView(int resourceId) {
        return this.getView().findViewById(resourceId);
    }

    /* Update views */

    private void showSearchPanel() {
        this.findView(R.id.searchPanel).setVisibility(View.VISIBLE);
    }

    protected void hideButtons() {
        this.buttonPanel.setVisibility(View.INVISIBLE);
    }

    protected void showButtons() {
        this.buttonPanel.setVisibility(View.VISIBLE);
    }

    protected void disableSearch() {
        this.searchField.setEnabled(false);

        this.hideButtons();
    }

    protected void enableSearch(boolean showButtons) {
        this.searchField.setEnabled(true);

        if (showButtons) {
            this.showButtons();
        }
    }

    protected void showProgress() {
        this.progressIndicator.setVisibility(View.VISIBLE);

        this.hideResults();
    }

    protected void hideProgress() {
        this.progressIndicator.setVisibility(View.INVISIBLE);
    }

    protected void showResults() {
        this.setUpNumberOfResults();

        this.numberOfResultsLabel.setVisibility(View.VISIBLE);
        this.resultsGrid.setVisibility(View.VISIBLE);

        this.hideProgress();
    }

    protected void hideResults() {
        this.numberOfResultsLabel.setVisibility(View.INVISIBLE);
        this.resultsGrid.setVisibility(View.INVISIBLE);
    }

    protected boolean hasResultsToShow() {
        return this.results != null;
    }

    protected void setResults(List<SearchResult> results) {
        this.results = results;

        this.setUpNumberOfResults();
        this.setUpAdapter();

        this.resultsGrid.setAdapter(this.adapter);
    }

    private void setUpNumberOfResults() {
        String format = this.getString(this.numberOfResultsFormatResource());

        this.numberOfResultsLabel.setText(String.format(format, this.results.size()));
    }

    private void setUpAdapter() {
        if (this.adapter == null) {
            this.adapter = new AddSeriesAdapter(this.activity(), this.results, this.imageLoader);
            this.adapter.register(this.adapterListener);
        } else {
            this.adapter.clear();
            for (SearchResult result : this.results) {
                this.adapter.add(result);
            }
        }
    }

    /* Activity */

    protected AddSeriesActivity activity() {
        return (AddSeriesActivity) this.getActivity();
    }

    /* AddSeriesAdapterListener */

    private AddSeriesAdapterListener adapterListener = new AddSeriesAdapterListener() {
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
