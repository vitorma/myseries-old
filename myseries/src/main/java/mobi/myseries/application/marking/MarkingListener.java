package mobi.myseries.application.marking;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;

public interface MarkingListener {
    public void onMarked(Series markedSeries);
    public void onMarked(Season markedSeason);
    public void onMarked(Episode markedEpisode);
}
