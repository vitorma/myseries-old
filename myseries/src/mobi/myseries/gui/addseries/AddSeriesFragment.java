package mobi.myseries.gui.addseries;

import mobi.myseries.domain.model.Series;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.actionbarsherlock.app.SherlockListFragment;

public abstract class AddSeriesFragment extends SherlockListFragment {

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

        this.setUpItemClickListener();
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

    protected AddSeriesActivity activity() {
        return (AddSeriesActivity) this.getSherlockActivity();
    }

    protected abstract int layoutResource();
    protected abstract void setUp();
    protected abstract void onStartFired();
    protected abstract void onStopFired();

    private void setUpItemClickListener() {
        this.getListView().setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Series selectedItem = (Series) parent.getItemAtPosition(position);
                    AddSeriesFragment.this.activity().onRequestAdd(selectedItem);
                }
            }
        );
    }
}
