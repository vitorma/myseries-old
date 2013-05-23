package mobi.myseries.gui.addseries;

import java.util.List;

import mobi.myseries.domain.model.Series;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockFragment;

public abstract class AddSeriesFragment extends SherlockFragment {

    private GridView grid;
    private AddAdapter adapter;

    protected boolean isLoading;
    protected List<Series> results;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(this.layoutResource(), container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpGrid();
        this.setUp();
    }

    @Override
    public void onStart() {
        super.onStart();

        this.onStartFired();
    }

    @Override
    public void onStop() {
        super.onStop();

        this.onStopFired();
    }

    //-------------------------------------------------------------------------------------------------------

    protected AddSeriesActivity activity() {
        return (AddSeriesActivity) this.getSherlockActivity();
    }

    protected abstract int layoutResource();
    protected abstract void setUp();
    protected abstract void onStartFired();
    protected abstract void onStopFired();

    protected void setResults(List<Series> results) {
        this.results = results;
        this.adapter = new AddAdapter(this.activity(), results);
        this.grid.setAdapter(this.adapter);
    }

    private void setUpGrid() {
        this.grid = (GridView) this.getView().findViewById(android.R.id.list);

        this.grid.setEmptyView(this.getView().findViewById(android.R.id.empty));

        if (this.adapter != null) {
            this.grid.setAdapter(this.adapter);
        }

        this.grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Series selectedItem = (Series) parent.getItemAtPosition(position);
                AddSeriesFragment.this.activity().onRequestAdd(selectedItem);
            }
        });
    }
}
