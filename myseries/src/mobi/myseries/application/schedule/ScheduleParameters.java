package mobi.myseries.application.schedule;

import java.util.Collection;
import java.util.HashSet;

import mobi.myseries.domain.model.Series;

public class ScheduleParameters {
    private int sortMode;
    private boolean includingSpecialEpisodes;
    private boolean includingSeenEpisodes;
    private HashSet<Integer> seriesIds;

    public ScheduleParameters() {
        this.sortMode = SortMode.OLDEST_FIRST;
        this.includingSpecialEpisodes = true;
        this.includingSeenEpisodes = false;
        this.seriesIds = new HashSet<Integer>();
    }

    public int sortMode() {
        return this.sortMode;
    }

    public boolean includesSpecialEpisodes() {
        return this.includingSpecialEpisodes;
    }

    public boolean includesSeenEpisodes() {
        return this.includingSeenEpisodes;
    }

    public boolean includesEpisodesOfSeries(int seriesId) {
        return this.seriesIds.contains(seriesId);
    }

    public ScheduleParameters setSortMode(int sortMode) {
        this.sortMode = sortMode;
        return this;
    }

    public ScheduleParameters setInclusionOfSpecialEpisodes(boolean includingSpecialEpisodes) {
        this.includingSpecialEpisodes = includingSpecialEpisodes;
        return this;
    }

    public ScheduleParameters setInclusionOfSeenEpisodes(boolean includingSeenEpisodes) {
        this.includingSeenEpisodes = includingSeenEpisodes;
        return this;
    }

    public ScheduleParameters setInclusionOfEpisodesOfAllSeries(Collection<Series> collection) {
        for (Series s : collection) {
            this.seriesIds.add(s.id());
        }

        return this;
    }
}
