package mobi.myseries.gui.detail;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.ImageProvider;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Dates;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class SeriesDetailsFragment extends SherlockFragment {
    private static final String SERIES_ID = "seriesId";
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final ImageProvider IMAGE_PROVIDER = App.environment().imageProvider();

    private int seriesId;

    public static SeriesDetailsFragment newInstance(int seriesId) {
        SeriesDetailsFragment seriesDetailsFragment = new SeriesDetailsFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(SERIES_ID, seriesId);
        seriesDetailsFragment.setArguments(arguments);

        return seriesDetailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.seriesId = this.getArguments().getInt(SERIES_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        return inflater.inflate(R.layout.series_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView seriesOverview = (TextView) this.getActivity().findViewById(R.id.seriesOverviewTextView);
        TextView seriesStatus = (TextView) this.getActivity().findViewById(R.id.statusTextView);
        TextView seriesAirTime = (TextView) this.getActivity().findViewById(R.id.airTimeTextView);
        TextView seriesAirDays = (TextView) this.getActivity().findViewById(R.id.airDaysTextView);
        TextView seriesActors = (TextView) this.getActivity().findViewById(R.id.actorsTextView);
        TextView seriesFirstAirDay = (TextView) this.getActivity().findViewById(R.id.firstAiredTextView);
        TextView seriesNetwork = (TextView) this.getActivity().findViewById(R.id.networkTextView);
        TextView seriesGenre = (TextView) this.getActivity().findViewById(R.id.genreTextView);
        TextView seriesRuntime = (TextView) this.getActivity().findViewById(R.id.runtimeTextView);

        Series series = SERIES_PROVIDER.getSeries(this.seriesId);

        seriesOverview.setText(series.overview());
        seriesStatus.setText(series.status().toString());
        seriesAirTime.setText(series.airTime());
        seriesAirDays.setText(series.airDay());
        seriesActors.setText(series.actors());
        seriesFirstAirDay.setText(Dates.toString(series.airDate(), App.environment().localization().dateFormat(), ""));
        seriesNetwork.setText(series.network());
        seriesGenre.setText(series.genres());
        seriesRuntime.setText(String.format(this.getString(R.string.runtime_minutes_format), series.runtime()));

        Bitmap bmp = IMAGE_PROVIDER.getPosterOf(series);

        if (bmp != null) {
            ImageView view = (ImageView) this.getActivity().findViewById(R.id.seriesPosterImageView);
            view.setImageBitmap(bmp);
        }
    }
}
