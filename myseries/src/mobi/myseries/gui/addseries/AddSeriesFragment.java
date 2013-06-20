package mobi.myseries.gui.addseries;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.search.SeriesSearchListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.ConfirmationDialogBuilder;
import mobi.myseries.gui.shared.DialogButtonOnClickListener;
import mobi.myseries.gui.shared.FailureDialogBuilder;
import android.app.Dialog;
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

    private AddAdapter adapter;

    private List<Series> results;
    protected boolean isSearching;
    protected SeriesSearchListener seriesSearchListener;

    /* Fragment life cycle */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.addseries_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.prepareViews();
        this.seriesSearchListener = this.seriesSearchListener();
    }

    @Override
    public void onStart() {
        super.onStart();

        this.registerListenerForSeriesSearch();

        if (this.hasResultsToShow()) {
            this.showResults();
        } else if (this.isSearching) {
            this.seriesSearchListener.onStart();
        } else if (this.shouldPerformSearchOnStartLifeCycle()) {
            this.performSearch();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        this.deregisterListenerForSeriesSearch();
    }

    /* Abstract methods */

    protected abstract boolean hasSearchPanel();
    protected abstract int sourceTextResource();
    protected abstract int numberOfResultsFormatResource();
    protected abstract boolean shouldPerformSearchOnStartLifeCycle();
    protected abstract void performSearch();
    protected abstract void registerListenerForSeriesSearch();
    protected abstract void deregisterListenerForSeriesSearch();
    protected abstract SeriesSearchListener seriesSearchListener();

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
                    AddSeriesFragment.this.performSearch();
                    return true;
                }

                return false;
            }
        });

        this.searchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    AddSeriesFragment.this.performSearch();
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
                AddSeriesFragment.this.performSearch();
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
                Series selectedItem = (Series) parent.getItemAtPosition(position);
                AddSeriesFragment.this.onRequestAdd(selectedItem);
            }
        });

        if (this.adapter != null) {
            this.resultsGrid.setAdapter(this.adapter);
        }
    }

    private void onRequestAdd(Series seriesToAdd) {
        Dialog dialog;

        if (App.followSeriesService().follows(seriesToAdd)) {
            String messageFormat = this.getString(R.string.add_already_followed_series_message);

            dialog = new FailureDialogBuilder(this.getActivity())
                .setMessage(String.format(messageFormat, seriesToAdd.name()))
                .build();
        } else {
            dialog = new ConfirmationDialogBuilder(this.getActivity())
                .setTitle(seriesToAdd.name())
                .setMessage(seriesToAdd.overview())
                .setSurrogateMessage(R.string.overview_unavailable)
                .setPositiveButton(R.string.add, this.addButtonOnClickListener(seriesToAdd))
                .setNegativeButton(R.string.dont_add, null)
                .build();
        }

        this.activity().showDialog(dialog);
    }

    private DialogButtonOnClickListener addButtonOnClickListener(final Series seriesToAdd) {
        return new DialogButtonOnClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                App.followSeriesService().follow(seriesToAdd);

                dialog.dismiss();
            }
        };
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

    protected void setResults(List<Series> results) {
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
            this.adapter = new AddAdapter(this.activity(), this.results);
        } else {
            this.adapter.clear();
            for (Series s : this.results) {
                this.adapter.add(s);
            }
        }
    }

    /* Activity */

    protected AddSeriesActivity activity() {
        return (AddSeriesActivity) this.getActivity();
    }
}
