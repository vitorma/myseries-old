package mobi.myseries.gui.series;

import java.util.Locale;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.Strings;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
    private static final SeriesProvider SERIES_PROVIDER = App.seriesProvider();
    private static final ImageService IMAGE_SERVICE = App.imageService();
    private static final Context CONTEXT = App.context();

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

        TextView seriesName = (TextView) this.getActivity().findViewById(R.id.seriesName);
        TextView seriesStatus = (TextView) this.getActivity().findViewById(R.id.statusTextView);
        TextView seriesAirDays = (TextView) this.getActivity().findViewById(R.id.airDaysTextView);
        TextView seriesRuntime = (TextView) this.getActivity().findViewById(R.id.runtimeTextView);

        TextView seriesGenre = (TextView) this.getActivity().findViewById(R.id.genreTextView);
        TextView seriesActors = (TextView) this.getActivity().findViewById(R.id.actorsTextView);

        TextView seriesOverview = (TextView) this.getActivity().findViewById(R.id.seriesOverviewTextView);

        Series series = SERIES_PROVIDER.getSeries(this.seriesId);

        seriesName.setText(series.name());
        seriesStatus.setText(LocalText.of(series.status(), ""));

        String airDay = DatesAndTimes.toString(series.airDay(), Locale.getDefault(), "");
        String airtime = DatesAndTimes.toString(series.airtime(), DateFormat.getTimeFormat(App.context()), "");
        String network = series.network();
        String airInfo = Strings.concat(airDay, airtime, ", ");
        airInfo = Strings.concat(airInfo, network, " - ");
        seriesAirDays.setText(airInfo);
        seriesRuntime.setText(String.format(this.getString(R.string.runtime_minutes_format), series.runtime()));

        seriesGenre.setText(series.genres());
        seriesActors.setText(series.actors());
        seriesOverview.setText(series.overview());

        Bitmap poster = IMAGE_SERVICE.getPosterOf(series);
        Bitmap genericPoster = Images.genericSeriesPosterFrom(App.resources());
        Bitmap ensuredPoster = Objects.nullSafe(poster, genericPoster);

        ImageView seriesPoster = (ImageView) this.getActivity().findViewById(R.id.seriesPosterImageView);
        seriesPoster.setImageBitmap(ensuredPoster);

//        ImageView background = (ImageView) this.getActivity().findViewById(R.id.background);
//        BitmapDrawable drawable = new BitmapDrawable(this.getResources(), ensuredPoster);
//        drawable.setAlpha(30);
//        background.setImageDrawable(drawable);
    }
}
