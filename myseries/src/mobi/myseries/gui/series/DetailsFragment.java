package mobi.myseries.gui.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.shared.Objects;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class DetailsFragment extends SherlockFragment {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final ImageService IMAGE_PROVIDER = App.imageProvider();

    private int seriesId;

    public static DetailsFragment newInstance(int seriesId) {
        DetailsFragment seriesDetailsFragment = new DetailsFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SERIES_ID, seriesId);
        seriesDetailsFragment.setArguments(arguments);

        return seriesDetailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.seriesId = this.getArguments().getInt(Extra.SERIES_ID);
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

        Bitmap poster = App.imageProvider().getPosterOf(series);
        Bitmap genericPoster = Images.genericSeriesPosterFrom(App.resources());
        Bitmap ensuredPoster = Objects.nullSafe(poster, genericPoster);

        ImageView seriesPoster = (ImageView) this.getActivity().findViewById(R.id.seriesPosterImageView);
        seriesPoster.setImageBitmap(ensuredPoster);

        ImageView background = (ImageView) this.getActivity().findViewById(R.id.background);
        BitmapDrawable drawable = new BitmapDrawable(this.getResources(), ensuredPoster);
        drawable.setAlpha(30);
        background.setImageDrawable(drawable);
    }
}
