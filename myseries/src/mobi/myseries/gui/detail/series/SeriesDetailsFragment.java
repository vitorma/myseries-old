package mobi.myseries.gui.detail.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.ImageProvider;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Series;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

        return inflater.inflate(R.layout.series_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView seriesStatus = (TextView) this.getActivity().findViewById(R.id.statusTextView);
        TextView seriesAirDays = (TextView) this.getActivity().findViewById(R.id.airDaysTextView);
        TextView seriesRuntime = (TextView) this.getActivity().findViewById(R.id.runtimeTextView);

        TextView seriesGenre = (TextView) this.getActivity().findViewById(R.id.genreTextView);
        TextView seriesActors = (TextView) this.getActivity().findViewById(R.id.actorsTextView);

        TextView seriesOverview = (TextView) this.getActivity().findViewById(R.id.seriesOverviewTextView);

        Series series = SERIES_PROVIDER.getSeries(this.seriesId);

        seriesStatus.setText(series.status().toString());
        seriesAirDays.setText(series.airDay() + " " + series.airTime() + " (" + series.network() + ")");
        seriesRuntime.setText(String.format(this.getString(R.string.runtime_minutes_format), series.runtime()));

        seriesGenre.setText(series.genres());
        seriesActors.setText(series.actors());
        seriesOverview.setText(series.overview());

        Bitmap bmp = IMAGE_PROVIDER.getPosterOf(series);

        ImageView seriesPoster = (ImageView) this.getActivity().findViewById(R.id.seriesPosterImageView);
        seriesPoster.setImageBitmap(bmp);

        ImageView background = (ImageView) getActivity().findViewById(R.id.background);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bmp);
        drawable.setAlpha(30);
        background.setImageDrawable(drawable);
    }
}
