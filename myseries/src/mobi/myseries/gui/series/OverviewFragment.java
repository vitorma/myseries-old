package mobi.myseries.gui.series;

import java.util.Locale;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.DateFormats;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.LocalText;
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
import android.widget.ScrollView;
import android.widget.TextView;

public class OverviewFragment extends Fragment {
    private DisplayImageOptions mDisplayImageOptions;

    public static OverviewFragment newInstance(int seriesId) {
        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SERIES_ID, seriesId);

        OverviewFragment seriesDetailsFragment = new OverviewFragment();
        seriesDetailsFragment.setArguments(arguments);
        
        return seriesDetailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.series_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDisplayImageOptions = imageLoaderOptions();
        setUp();
    }

    private void setUp() {
        TextView seriesTitle = (TextView) getView().findViewById(R.id.seriesName);
        TextView seriesGenres = (TextView) getView().findViewById(R.id.genreTextView);
        TextView seriesStatus = (TextView) getView().findViewById(R.id.statusTextView);
        TextView seriesAirDay = (TextView) getView().findViewById(R.id.airDaysTextView);
        TextView seriesRuntime = (TextView) getView().findViewById(R.id.runtimeTextView);
        TextView seriesOverview = (TextView) getView().findViewById(R.id.seriesOverviewTextView);
        TextView seriesActors = (TextView) getView().findViewById(R.id.actorsTextView);

        Series series = App.seriesFollowingService().getFollowedSeries(getArguments().getInt(Extra.SERIES_ID));

        seriesTitle.setText(series.name());
        seriesGenres.setText(series.genres());
        seriesStatus.setText(LocalText.of(series.status(), LocalText.get(R.string.unavailable_status)));

        String airDay = DatesAndTimes.toString(series.airtime(), DateFormats.forWeekDay(Locale.getDefault()), "");
        String airtime = DatesAndTimes.toString(series.airtime(), DateFormat.getTimeFormat(App.context()), "");
        String network = series.network();
        String airInfo = Strings.concat(airDay, airtime, ", ");
        airInfo = Strings.concat(airInfo, network, " - ");
        String unavailableInfo = LocalText.get(R.string.unavailable_air_info);
        seriesAirDay.setText(airInfo.isEmpty() ? unavailableInfo : airInfo);

        seriesRuntime.setText(
                series.runtime().isEmpty() ?
                LocalText.get(R.string.unavailable_runtime) :
                LocalText.get(R.string.runtime_minutes_format, series.runtime()));

        TextView overviewLabel = (TextView) getView().findViewById(R.id.overviewLabel);
        if (series.overview().isEmpty()) {
            overviewLabel.setVisibility(View.GONE);
            seriesOverview.setVisibility(View.GONE);
        } else {
            overviewLabel.setVisibility(View.VISIBLE);
            seriesOverview.setVisibility(View.VISIBLE);
            seriesOverview.setText(series.overview());
        }

        TextView actorsLabel = (TextView) getView().findViewById(R.id.actorsLabel);
        if (series.actors().isEmpty()) {
            actorsLabel.setVisibility(View.GONE);
            seriesActors.setVisibility(View.GONE);
        } else {
            actorsLabel.setVisibility(View.VISIBLE);
            seriesActors.setVisibility(View.VISIBLE);
            seriesActors.setText(series.actors());
        }

        ImageView seriesPoster = (ImageView) getView().findViewById(R.id.seriesPosterImageView);
        ImageLoader.getInstance().displayImage(App.imageService().getPosterOf(series), seriesPoster, mDisplayImageOptions);

        if (App.resources().getBoolean(R.bool.isTablet)) {
            ScrollView scrollView = (ScrollView) getView().findViewById(R.id.scrollView);
            scrollView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
        }
    }

    private static DisplayImageOptions imageLoaderOptions() {
        return new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
        .resetViewBeforeLoading(true)
        .showImageOnFail(R.drawable.generic_poster)
        .build();
    }
}
