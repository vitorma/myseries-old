package mobi.myseries.gui.series;

import java.util.Locale;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.AsyncImageLoader;
import mobi.myseries.gui.shared.DateFormats;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.NormalPosterFetchingMethod;
import mobi.myseries.gui.shared.PosterFetchingMethod;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Strings;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class OverviewFragment extends Fragment {
    private static final Bitmap GENERIC_POSTER = Images.genericSeriesPosterFrom(App.resources());

    private int seriesId;

    public static OverviewFragment newInstance(int seriesId) {
        OverviewFragment seriesDetailsFragment = new OverviewFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SERIES_ID, seriesId);
        seriesDetailsFragment.setArguments(arguments);

        return seriesDetailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        seriesId = getArguments().getInt(Extra.SERIES_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.series_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView seriesName = (TextView) getActivity().findViewById(R.id.seriesName);
        TextView seriesStatus = (TextView) getActivity().findViewById(R.id.statusTextView);
        TextView seriesAirDays = (TextView) getActivity().findViewById(R.id.airDaysTextView);
        TextView seriesRuntime = (TextView) getActivity().findViewById(R.id.runtimeTextView);

        TextView seriesGenre = (TextView) getActivity().findViewById(R.id.genreTextView);
        TextView seriesActors = (TextView) getActivity().findViewById(R.id.actorsTextView);

        TextView seriesOverview = (TextView) getActivity().findViewById(R.id.seriesOverviewTextView);

        Series series = App.seriesFollowingService().getFollowedSeries(seriesId);

        seriesName.setText(series.name());
        seriesStatus.setText(LocalText.of(series.status(), this.getString(R.string.unavailable_status)));

        String airDay = DatesAndTimes.toString(series.airDay(), DateFormats.forWeekDay(Locale.getDefault()), "");
        String airtime = DatesAndTimes.toString(series.airtime(), DateFormat.getTimeFormat(App.context()), "");
        String network = series.network();
        String airInfo = Strings.concat(airDay, airtime, ", ");
        airInfo = Strings.concat(airInfo, network, " - ");
        String unavailableInfo = this.getString(R.string.unavailable_air_info);

        seriesAirDays.setText(airInfo.isEmpty() ? unavailableInfo : airInfo);

        String runtime = series.runtime().isEmpty() ? this.getString(R.string.unavailable_runtime) : String.format(
                this.getString(R.string.runtime_minutes_format), series.runtime());

        seriesRuntime.setText(runtime);

        seriesGenre.setText(series.genres());

        TextView overviewLabel = (TextView) getActivity().findViewById(R.id.overviewLabel);
        if (series.overview().isEmpty()) {
            overviewLabel.setVisibility(View.GONE);
            seriesOverview.setVisibility(View.GONE);
        } else {
            overviewLabel.setVisibility(View.VISIBLE);
            seriesOverview.setVisibility(View.VISIBLE);
            seriesOverview.setText(series.overview());
        }

        TextView actorsLabel = (TextView) getActivity().findViewById(R.id.actorsLabel);
        if (series.actors().isEmpty()) {
            actorsLabel.setVisibility(View.GONE);
            seriesActors.setVisibility(View.GONE);
        } else {
            actorsLabel.setVisibility(View.VISIBLE);
            seriesActors.setVisibility(View.VISIBLE);
            seriesActors.setText(series.actors());
        }

        ImageView seriesPoster = (ImageView) getActivity().findViewById(R.id.seriesPosterImageView);
        ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.loadProgress);
        AsyncImageLoader.loadBitmapOn(new NormalPosterFetchingMethod(series, App.imageService()), GENERIC_POSTER, seriesPoster, progressBar);

        boolean isTablet = App.resources().getBoolean(R.bool.isTablet);

        if (isTablet) {
            ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.scrollView);
            scrollView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
        }
    }
}
