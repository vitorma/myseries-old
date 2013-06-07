package mobi.myseries.gui.myseries;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.series.SeriesActivity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MySeriesFragment extends Fragment {
    private GridView showsGrid;
    private TextView empty;
    private ProgressBar progressIndicator;

    private MySeriesAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.myseries_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.prepareViews();
    }

    @Override
    public void onStart() {
        super.onStart();

        this.adapter.register(this.adapterListener);

        if (this.adapter.isLoading()) {
            this.adapterListener.onStartLoading();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        this.adapter.deregister(this.adapterListener);
    }

    private void prepareViews() {
        this.showsGrid = (GridView) this.getActivity().findViewById(R.id.showsGrid);
        this.empty = (TextView) this.getActivity().findViewById(R.id.empty);
        this.progressIndicator = (ProgressBar) this.getActivity().findViewById(R.id.progressIndicator);

        this.showsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Series series = (Series) parent.getItemAtPosition(position);
                Intent intent = SeriesActivity.newIntent(App.context(), series.id());

                MySeriesFragment.this.startActivity(intent);
            }
        });

        if (this.adapter == null) {
            this.adapter = new MySeriesAdapter();
        }

        this.showsGrid.setAdapter(this.adapter);
    }

    /* MySeriesAdapter.Listener */

    MySeriesAdapter.Listener adapterListener = new MySeriesAdapter.Listener() {
        @Override
        public void onStartLoading() {
            MySeriesFragment.this.showsGrid.setVisibility(View.INVISIBLE);
            MySeriesFragment.this.empty.setVisibility(View.INVISIBLE);
            MySeriesFragment.this.progressIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinishLoading() {
            MySeriesFragment.this.progressIndicator.setVisibility(View.INVISIBLE);

            if (MySeriesFragment.this.adapter.isEmpty()) {
                MySeriesFragment.this.showsGrid.setVisibility(View.INVISIBLE);
                MySeriesFragment.this.empty.setVisibility(View.VISIBLE);
            } else {
                MySeriesFragment.this.showsGrid.setVisibility(View.VISIBLE);
                MySeriesFragment.this.empty.setVisibility(View.INVISIBLE);
            }
        }
    };
}
