package mobi.myseries.gui.myseries;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.addseries.AddSeriesActivity;
import mobi.myseries.gui.series.SeriesActivity;
import mobi.myseries.gui.shared.UniversalImageLoader;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

public class MySeriesFragment extends Fragment {
    private GridView showsGrid;
    private View empty;
    private ProgressBar progressIndicator;

    private MySeriesAdapter adapter;

    /* Life cycle */

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

        App.preferences().forActivities().register(this.preferencesListener);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        this.adapter.reloadData();
    }

    @Override
    public void onStop() {
        super.onStop();

        this.adapter.deregister(this.adapterListener);
        App.preferences().forActivities().register(this.preferencesListener);
    }

    private void prepareViews() {
        this.showsGrid = (GridView) this.getActivity().findViewById(R.id.showsGrid);
        this.empty = this.getActivity().findViewById(R.id.empty);
        this.progressIndicator = (ProgressBar) this.getActivity().findViewById(R.id.progressIndicator);

        this.showsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Series series = (Series) parent.getItemAtPosition(position);
                Intent intent = SeriesActivity.newIntent(App.context(), series.id());

                MySeriesFragment.this.startActivity(intent);
            }
        });

        this.showsGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int seriesId = ((Series) parent.getItemAtPosition(position)).id();

                MySeriesFragment.this.adapterListener.onItemContextRequest(seriesId);

                return true;
            }
        });
        
        setupOnScrollListener();

        if (this.adapter == null) {
            this.adapter = new MySeriesAdapter();
        }

        this.showsGrid.setAdapter(this.adapter);
    }

    private void setupOnScrollListener() {
        boolean pauseOnScroll = false;
        boolean pauseOnFling = true;
        PauseOnScrollListener listener = new PauseOnScrollListener(UniversalImageLoader.loader(), pauseOnScroll, pauseOnFling);
        this.showsGrid.setOnScrollListener(listener);
    }

    /* MySeriesAdapter.Listener */

    private MySeriesAdapter.Listener adapterListener = new MySeriesAdapter.Listener() {
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

                this.prepareEmptyView();
            } else {
                MySeriesFragment.this.showsGrid.setVisibility(View.VISIBLE);
                MySeriesFragment.this.empty.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onItemContextRequest(int seriesId) {
            MySeriesContextMenuDialogFragment.showDialog(seriesId, MySeriesFragment.this.getFragmentManager());
        }

        private void prepareEmptyView() {
            View addSeries = MySeriesFragment.this.empty.findViewById(R.id.addSeries);
            TextView hiddenSeries = (TextView) MySeriesFragment.this.empty.findViewById(R.id.hiddenSeries);
            View filterSeries = MySeriesFragment.this.empty.findViewById(R.id.filterSeries);

            addSeries.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MySeriesFragment.this.startActivity(AddSeriesActivity.newIntent(MySeriesFragment.this.getActivity()));
                }
            });

            int numberOfFollowedSeries = App.seriesFollowingService().getAllFollowedSeries().size();

            if (numberOfFollowedSeries == 0) {
                hiddenSeries.setVisibility(View.GONE);
                filterSeries.setVisibility(View.GONE);

                addSeries.getLayoutParams().width = LayoutParams.WRAP_CONTENT;

            } else {
                hiddenSeries.setVisibility(View.VISIBLE);
                filterSeries.setVisibility(View.VISIBLE);

                hiddenSeries.setText(
                        App.resources().getQuantityString(
                                R.plurals.plural_there_are_hidden_series,
                                numberOfFollowedSeries,
                                numberOfFollowedSeries));

                filterSeries.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SeriesFilterDialogFragment().show(MySeriesFragment.this.getFragmentManager(), "seriesFilterDialog");
                    }
                });

                addSeries.getLayoutParams().width = LayoutParams.MATCH_PARENT;
            }
        }
    };

    /* SharedPreferences.OnSharedPreferenceChangeListener */

    private OnSharedPreferenceChangeListener preferencesListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            MySeriesFragment.this.adapter.reloadData();
        }
    };
}
