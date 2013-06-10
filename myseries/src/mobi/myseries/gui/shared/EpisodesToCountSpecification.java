package mobi.myseries.gui.shared;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.DatesAndTimes;

public class EpisodesToCountSpecification extends AbstractSpecification<Episode> {
    private boolean countSpecialEpisodes;
    private boolean countUnairedEpisodes;

    public EpisodesToCountSpecification(boolean countSpecialEpisodes, boolean countUnairedEpisodes) {
        this.countSpecialEpisodes = countSpecialEpisodes;
        this.countUnairedEpisodes = countUnairedEpisodes;
    }

    @Override
    public boolean isSatisfiedBy(Episode e) {
        if (e == null) { return false; }

        boolean unaired = DatesAndTimes.compareByNullLast(e.airDate(), DatesAndTimes.now()) > 0;

        return (!e.isSpecial() || this.countSpecialEpisodes) && (!unaired || this.countUnairedEpisodes);
    }
}
