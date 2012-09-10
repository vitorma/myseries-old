package mobi.myseries.application.schedule;

public class ScheduleParameters {
    private int sortMode;
    private boolean includingSpecialEpisodes;
    private boolean includingSeenEpisodes;

    public ScheduleParameters() {
        this.sortMode = SortMode.OLDEST_FIRST;
        this.includingSpecialEpisodes = true;
        this.includingSeenEpisodes = false;
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

//    public ScheduleParameters copy() {
//        return new ScheduleParameters()
//            .setSortMode(this.sortMode)
//            .setInclusionOfSpecialEpisodes(this.includingSpecialEpisodes)
//            .setInclusionOfSeenEpisodes(this.includingSeenEpisodes);
//    }
}
