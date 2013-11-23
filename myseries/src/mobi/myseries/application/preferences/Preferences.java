package mobi.myseries.application.preferences;

import mobi.myseries.shared.Validate;
import android.content.Context;

public class Preferences {
    private Context mContext;

    public Preferences(Context context) {
        Validate.isNonNull(context, "context");

        mContext = context;
    }

    public LibraryPreferences forLibrary() {
        return new LibraryPreferences(mContext);
    }

    public SchedulePreferences forSchedule() {
        return new SchedulePreferences(mContext);
    }

    public StatisticsPreferences forStatistics() {
        return new StatisticsPreferences(mContext);
    }

    public UpdatePreferences forUpdate() {
        return new UpdatePreferences(mContext);
    }

    public SeriesPreferences forSeries() {
        return new SeriesPreferences(mContext);
    }

    public EpisodesPreferences forEpisodes() {
        return new EpisodesPreferences(mContext);
    }

    public ScheduleWidgetPreferences forScheduleWidget(int widgetId) {
        return new ScheduleWidgetPreferences(mContext, widgetId);
    }

    public void removeEntriesRelatedToSeries(int seriesId) {
        forLibrary().removeEntriesRelatedToSeries(seriesId);
        forSchedule().removeEntriesRelatedToSeries(seriesId);
        forStatistics().removeEntriesRelatedToSeries(seriesId);
    }

    public void removeEntriesRelatedToAllSeries(int[] seriesIds) {
        forLibrary().removeEntriesRelatedToAllSeries(seriesIds);
        forSchedule().removeEntriesRelatedToAllSeries(seriesIds);
        forStatistics().removeEntriesRelatedToAllSeries(seriesIds);
    }

    public void removeEntriesRelatedToScheduleWidget(int widgetId) {
        forScheduleWidget(widgetId).clear();
    }
}
